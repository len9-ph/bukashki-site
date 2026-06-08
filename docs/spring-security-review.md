# Spring Security — ревью реализации (feature/jwt)

## Критические проблемы

### 1. `JwtUtils` — поля не инжектируются (NPE в рантайме)

`jwtSecret` и `jwtExpirationMs` объявлены, но нет аннотаций `@Value`. При вызове `getKey()` будет NullPointerException.

```java
@Value("${jwt.secret}")
private String jwtSecret;

@Value("${jwt.expiration-ms}")
private long jwtExpirationMs;
```

В `application.properties` добавить:

```properties
jwt.secret=some-long-secret-key-at-least-256-bits
jwt.expiration-ms=86400000
```

---

### 2. `JwtFilter` — неправильная обрезка токена

`HEADER_BEARER = "Bearer"` — при `header.substring(HEADER_BEARER.length())` получится `" eyJ..."` (с пробелом в начале). Нужно `"Bearer "` с пробелом в конце.

```java
private static final String HEADER_BEARER = "Bearer ";
```

---

### 3. `AuthController` — вызывает несуществующий метод

`userService.login(...)` вызывается, но в `UserService` интерфейсе этого метода нет — код не скомпилируется.

Нужно добавить метод `login` в интерфейс и реализацию, либо переделать логику прямо в контроллере через `AuthenticationManager`.

---

### 4. `SecurityConfig` — отсутствует `AuthenticationManager` бин

Для `/auth/login` нужно проверять пароль через `AuthenticationManager`. Без него программная аутентификация в логине не работает.

```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

---

## Нужно добавить

### 5. `SecurityConfig` — нет `SessionCreationPolicy.STATELESS`

Для JWT-архитектуры обязательно — иначе Spring создаёт HTTP-сессии:

```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

---

### 6. `CustomUserDetailsService` — неправильный тип исключения

`UserNotFoundException extends RuntimeException` — Spring Security ожидает `UsernameNotFoundException`. При ненайденном пользователе придёт 500 вместо 401.

```java
return userMapper.findByEmail(username)
    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
```

---

### 7. `AuthController.login()` — не возвращает токен

Сейчас возвращает `ResponseEntity<Void>`. Должен возвращать JWT токен (строку или DTO).

---

## Что сделано правильно

- `JwtFilter` extends `OncePerRequestFilter` — правильно
- `parseClaims` вынесен отдельно, exception handling в `isTokenValid` — хорошо
- `SecurityFilterChain` с lambda DSL вместо устаревшего `WebSecurityConfigurerAdapter` — современно
- `PasswordEncoder` как бин в `SecurityConfig` — правильное место

---

## Приоритет исправлений

| # | Файл | Проблема | Эффект |
|---|------|----------|--------|
| 1 | `AuthController` | вызов несуществующего `login()` | не компилируется |
| 2 | `JwtUtils` | нет `@Value` на полях | NPE в рантайме |
| 3 | `JwtFilter` | пробел в токене | невалидный токен всегда |
| 4 | `SecurityConfig` | нет `AuthenticationManager` | логин не работает |
| 5 | `SecurityConfig` | нет `STATELESS` | ненужные сессии |
| 6 | `CustomUserDetailsService` | не тот тип исключения | 500 вместо 401 |
| 7 | `AuthController` | не возвращает токен | логин бесполезен |
