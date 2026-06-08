# Как Spring Security + JWT работает от начала до конца

## Общая схема потока

```
Клиент                    Сервер
  |                         |
  |-- POST /auth/login ----> |  1. Принимает credentials
  |                         |  2. AuthenticationManager проверяет их
  |                         |  3. Генерирует JWT
  |<-- { token: "..." } --- |
  |                         |
  |-- GET /api/data -------> |  4. JwtFilter перехватывает запрос
  |   Authorization:        |  5. Валидирует токен
  |   Bearer <token>        |  6. Кладёт Authentication в контекст
  |                         |  7. SecurityFilterChain пропускает дальше
  |<-- 200 OK ------------- |
```

---

## 1. `SecurityFilterChain` — главный конфиг

Это сердце Spring Security. Ты описываешь **правила**: кому куда можно, что отключить, какие фильтры добавить.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()  // открытые эндпоинты
            .anyRequest().authenticated()             // всё остальное — требует токен
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

`STATELESS` — говорит Spring: не создавай сессии, не храни состояние между запросами. Каждый запрос аутентифицируется заново через токен.

---

## 2. `AuthenticationManager` — кто проверяет credentials

Это интерфейс с одним методом `authenticate()`. Внутри он использует `DaoAuthenticationProvider`, который:
1. Вызывает `UserDetailsService.loadUserByUsername()`
2. Сравнивает пришедший пароль с хешем через `PasswordEncoder`

```java
@Bean
public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

Чтобы Spring автоматически связал `DaoAuthenticationProvider` с твоим `UserDetailsService` и `PasswordEncoder` — достаточно, чтобы оба были бинами `@Service`/`@Bean`. Spring сам их подхватит.

---

## 3. `JwtUtils` — генерация и валидация токенов

Токен JWT выглядит так:
```
eyJhbGciOiJIUzI1NiJ9  <-- header (алгоритм)
.eyJzdWIiOiJ1c2VyQG1haWwuY29tIiwiZXhwIjoxNzE1MDAwMDAwfQ  <-- payload (данные)
.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  <-- signature (подпись)
```

```java
@Component
public class JwtUtils {

    private final String secret = "your-secret-key-min-256-bits";
    private final long expirationMs = 86400000; // 24 часа

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSignKey())
            .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token); // бросит исключение если невалидный
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
```

---

## 4. `/login` — выдача токена

Контроллер теперь не проверяет пароль сам — он делегирует это `AuthenticationManager`.

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginDto dto) {
    // 1. Бросит исключение если credentials неверные
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
    );

    // 2. Из authentication достаём UserDetails
    UserDetails userDetails = (UserDetails) auth.getPrincipal();

    // 3. Генерируем токен
    String token = jwtUtils.generateToken(userDetails);

    return ResponseEntity.ok(Map.of("token", token));
}
```

Если `authenticate()` упал с `BadCredentialsException` — это нормально, Spring Security сам вернёт 401. Можно добавить `@ExceptionHandler` если хочешь кастомное сообщение.

---

## 5. `JwtFilter` — проверка токена на каждый запрос

Этот фильтр запускается **до** того, как запрос попадёт в контроллер. Он читает заголовок, валидирует токен и устанавливает пользователя в контекст.

```java
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // 1. Достаём токен из заголовка
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response); // нет токена — идём дальше (может быть /auth/login)
            return;
        }

        String token = header.substring(7); // убираем "Bearer "

        // 2. Валидируем
        if (jwtUtils.isTokenValid(token)) {
            String username = jwtUtils.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 3. Кладём аутентификацию в контекст
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
```

`SecurityContextHolder` — это thread-local хранилище. Spring Security смотрит туда, когда решает — аутентифицирован ли текущий запрос.

## Что такое Claims

JWT состоит из трех частей:

```
header.payload.signature
```

Payload и есть набор Claims.

Например токен может содержать:

```
{
  "sub": "leonid",
  "iat": 1717850000,
  "exp": 1717853600,
  "role": "ADMIN"
}
```

---

## Как всё это связано вместе

```
[Запрос]
    |
    v
[JwtFilter]  <-- твой фильтр, зарегистрирован через addFilterBefore()
    |  читает заголовок, валидирует JwtUtils, кладёт в SecurityContextHolder
    v
[SecurityFilterChain rules]  <-- проверяет: нужна ли аутентификация для этого URL?
    |  если нужна — смотрит SecurityContextHolder
    v
[Controller]  <-- до сюда доходит только аутентифицированный запрос
```

При логине путь другой — фильтр пропускает `/auth/**` насквозь, `AuthenticationManager` делает проверку сам внутри контроллера.
