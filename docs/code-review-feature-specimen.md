# Code Review — feature/specimen

**Ветка:** feature/specimen  
**Дата:** 2026-06-22  
**Задачи:** #22–28 (таблица, CRUD для specimen, проверка owner_id)

---

## Критические баги (блокируют работу)

### 1. `UserNotFoundException` больше не перехватывается → 500 на GET /me

**Файл:** `exception/GlobalExceptionHandler.java:29`

`@ExceptionHandler(UserNotFoundException.class)` заменён на `InsectNotFoundException.class`. `UserServiceImpl.getMe()` кидает `UserNotFoundException` — теперь она уходит наверх как 500 Internal Server Error вместо 404.

**Фикс:** вернуть обработчик `UserNotFoundException` рядом с новым:
```java
@ExceptionHandler(UserNotFoundException.class)
public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
}
```

---

### 2. PATCH /insect всегда падает — `InsectBean` строится без `id`

**Файл:** `controller/InsectController.java:61`

```java
insectService.updateInsect(currUserId, new InsectBean(dto.getName(), dto.getDescription()));
```

Конструктор `InsectBean(String, String)` не заполняет `id`, поле остаётся `null`. В `InsectServiceImpl.updateInsect()`:
```java
insectRepository.findById(insectBean.getId()) // getId() == null → IllegalArgumentException
```

---

### 3. `InsectUpdateDto` не содержит поля `id` — нет способа указать какой insect обновлять

**Файл:** `model/dto/InsectUpdateDto.java:5`

Корень проблемы #2. PATCH /insect не принимает `{insectId}` в пути и не имеет `id` в теле. Клиент не может указать, какую запись обновить.

**Фикс (вариант A):** добавить `id` в `InsectUpdateDto` + передавать в `InsectBean`.  
**Фикс (вариант B):** изменить маршрут на `PATCH /insect/{insectId}` и брать id из `@PathVariable`.

---

### 4. `InsectEntity` нет no-arg конструктора — JPA/Hibernate не может загрузить сущности из БД

**Файл:** `model/entity/InsectEntity.java:13`

JPA (спецификация 2.2) требует `public` или `protected` конструктор без аргументов. В `InsectEntity` он отсутствует — есть только 4-аргументный. Hibernate упадёт с `InstantiationException` при любом SELECT (findById, findAllByUserId). Все GET и DELETE операции не работают.

**Фикс:** добавить пустой конструктор:
```java
protected InsectEntity() {}
```

---

## Безопасность

### 5. GET /insect?userId=X — IDOR

**Файл:** `controller/InsectController.java:67`

Endpoint принимает произвольный `userId` как `@RequestParam` и не сверяет его с аутентифицированным пользователем. Любой авторизованный пользователь может получить список насекомых любого другого. Задача #24 — «Получение списка **своих** specimen».

**Фикс:** убрать `@RequestParam`, использовать `authentication.getPrincipal()` как в других методах.

---

## Качество

### 6. Код ошибки `"USER_NOT_FOUND"` для `InsectNotFoundException`

**Файл:** `exception/GlobalExceptionHandler.java:33`

При 404 на насекомое клиент получает `{"code": "USER_NOT_FOUND"}`. Нужно `"INSECT_NOT_FOUND"`.

---

### 7. Лишний параметр `InsectServiceImpl` в конструкторе контроллера

**Файл:** `controller/InsectController.java:33`

```java
public InsectController(InsectService insectService, InsectServiceImpl insectServiceImpl) {
    this.insectService = insectService; // insectServiceImpl нигде не используется
}
```

Нарушает DI через интерфейс, создаёт лишнюю зависимость на конкретный класс. Удалить второй параметр.

---

### 8. `"userId"` строка вместо реального значения в сообщении исключения

**Файл:** `service/impl/UserServiceImpl.java:20`

```java
throw new UserNotFoundException("userId"); // передаётся строка "userId", не значение переменной
```

При `userId=42` сообщение будет «User not found for login: userId». Отладка по логам бесполезна.

**Фикс:**
```java
throw new UserNotFoundException(String.valueOf(userId));
```
