# 🔐 План: Access Control

**Дата:** 2026-07-02
**Цель:** заменить размазанные по сервисам ручные проверки владельца на декларативную централизованную авторизацию (`@PreAuthorize` + `PermissionEvaluator`), закрыть открытые GET-эндпоинты и класс IDOR-багов.

**Почему сейчас:** авторизация императивна и разбросана (`if (!insectBean.getUserId().equals(userId)) throw ...`), на этом уже был пойман IDOR в `deletePhoto`. Плюс все GET открыты — любой читает чужую коллекцию.

**Можно начинать до Kafka и до Social:** AC-1 самодостаточен.

---

## 🧩 Механизм

- [ ] `@EnableMethodSecurity` (Spring Security 6; не устаревший `@EnableGlobalMethodSecurity`).
- [ ] `@PreAuthorize(...)` на методах → AOP-прокси считает SpEL до входа → false кидает `AccessDeniedException` → уже маппится в 403 в `GlobalExceptionHandler` (дописывать не нужно).
- [ ] Логика — в отдельном bean / `PermissionEvaluator`.

### Стиль реализации (развилка)
- **A. bean-метод в SpEL:** `@PreAuthorize("@access.canEdit(#insectId)")` — проще, типобезопаснее, читаемее.
- **B. `PermissionEvaluator`:** `@PreAuthorize("hasPermission(#insectId, 'Insect', 'edit')")` + `InsectPermissionEvaluator implements PermissionEvaluator`, регистрация в `MethodSecurityExpressionHandler`. Каноничнее, сильнее как сигнал зрелости, но stringly-typed (`'Insect'`, `'edit'` — компилятор не ловит опечатки).
- **Рекомендация:** ядро на **B** (портфолио-сигнал), от опечаток страховаться тестами матрицы; сложные сценарии (`canView`) можно оставить bean-методом. Допустим гибрид.

### Ключевой принцип
Не таскать `#userId` параметром (вызывающий может подать чужой). Evaluator **сам** берёт actor из `SecurityContextHolder.getContext().getAuthentication()`. Сигнатуры — только по ресурсу: `canEdit(#insectId)`, `canDelete(#photoId)`, `canView(#targetUserId)`.

---

## ⚙️ AC-1 — Ownership (без новых данных, максимум пользы)

- [ ] Включить `@EnableMethodSecurity`.
- [ ] Реализовать evaluator: `canEdit(insectId)` (actor владеет насекомым), `canDelete(photoId)` (actor владеет насекомым, к которому привязано фото — та самая IDOR-проверка).
- [ ] Повесить `@PreAuthorize` на `updateInsect` / `deleteInsect` / `addPhoto` / `deletePhoto`.
- [ ] **Удалить** ручные `if (!...getUserId().equals(...))` из сервисов (иначе два расходящихся слоя проверок).
- [ ] Проверить self-invocation: аннотированный метод не должен вызываться изнутри того же бина (прокси не сработает).

### ✅ Тесты AC-1
- [ ] Матрица `{владелец, чужой, аноним} × {edit, delete}` → `200/403`.
- [ ] Регресс на IDOR: чужой `photoId` под своим `insectId` → 403/404.

---

## 👁 AC-2 — Видимость коллекции (PUBLIC / PRIVATE)

- [ ] Миграция: `collection_visibility` на `users` (enum `PUBLIC / FRIENDS / PRIVATE`) — по tasks.md видимость на уровне пользователя, не отдельного насекомого.
- [ ] `canView(targetUserId)`: `PUBLIC` всем, `PRIVATE` только владельцу; `FRIENDS` временно как `false` (стаб до Social).
- [ ] View-эндпоинты: `GET /users/{id}/specimens`, `GET /users/{id}/specimens/{specimenId}` под `@PreAuthorize("@access.canView(#id)")`.
- [ ] 403 если коллекция закрыта.

### ✅ Тесты AC-2
- [ ] `{владелец, чужой, аноним} × {PUBLIC, PRIVATE}` → ожидаемые статусы.

---

## 🤝 AC-3 — Тир FRIENDS (зависит от Social Graph)

- [ ] Реализовать `isFriend(actorId, targetId)` в evaluator поверх Social.
- [ ] Включить `FRIENDS` в `canView`.

### ✅ Тесты AC-3
- [ ] `{друг, не-друг} × FRIENDS` → 200/403.

---

## 🧠 Куда вешать и грабли

- **Слой:** сервисный (ближе к данным, труднее обойти в обход контроллера).
- **Self-invocation:** `@PreAuthorize` работает через прокси — вызов аннотированного метода изнутри того же бина проверку пропускает.
- **Дублирование:** после переноса убрать ручные проверки, не держать два слоя.
- **Async / Kafka:** `SecurityContext` — thread-local, **не пробрасывается** в `@KafkaListener`. Консьюмер превью работает как система, не как пользователь — там `@PreAuthorize` неуместен; авторизация уже сделана продюсером в момент HTTP-запроса, событие «доверенное».

---

## 🔗 Зависимости и порядок
- AC-1 — независим, делать первым (закрывает IDOR-класс сразу).
- AC-2 — нужна миграция видимости; `FRIENDS` стабится.
- AC-3 — после Social Graph.
- View Collections — поверх AC-2.

---

## 🔓 Открытые решения
- Стиль: `PermissionEvaluator` (B) vs bean-метод (A) vs гибрид.
- Видимость на уровне пользователя или per-insect.
- Делать AC-1 до Kafka (рекомендация — да, самодостаточно и чинит реальную дыру).
