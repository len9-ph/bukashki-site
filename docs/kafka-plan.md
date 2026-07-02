# 🧭 План: рефакторинг → Kafka (PhotoUploadedEvent)

**Дата:** 2026-07-02
**Цель:** пощупать Kafka на осмысленном доменном кейсе (асинхронная пост-обработка загруженных фото), предварительно почистив модель и подняв инженерную базу.

**Порядок:** инженерная база → фундамент (нейминг DTO, судьба bean-слоя, single-step register) → Kafka по нарастающей (hello → payoff → надёжность).

**Сквозное правило:** каждая фаза заканчивается тестами. Без них архитектура читается как junior вне зависимости от паттернов.

---

## ⚙️ Фаза 0 — Инженерная база

Разовая настройка, которая сильнее всего двигает восприятие middle → middle+.

- [ ] **Flyway** вместо SQL-в-initdb: перенести `01/02/03.sql` в `src/main/resources/db/migration/V1__init.sql`, `V2__insect_photos.sql`, `V3__avatar_object_key.sql`. Убрать монтирование SQL в compose и хрупкий `ddl-auto=validate` (оставить `validate`, но схему держит Flyway). Даёт версионирование и повторяемость.
- [ ] **Testcontainers harness:** базовый абстрактный `@SpringBootTest` с контейнерами Postgres (+ MinIO, + Kafka по мере фаз). Один раз настроить — дальше переиспользовать.
- [ ] **Actuator:** health/info эндпоинты, `management.endpoints`. Основа observability.
- [ ] **Конфиг:** вынести секреты (JWT, minio, kafka) из `application.properties` в env-переменные + профиль `dev`. Плейнтекст-секреты — junior-tell.

---

## 🧱 Фаза 1 — Фундамент (рефакторинг)

### 1.1. Нейминг read-DTO
- [ ] `InsectDto → InsectResponseDto`; поправить `InsectBean.toDto`, `InsectController` (getInsects / getInsect / getMy).
- [ ] Зафиксировать правило: `*ResponseDto` — исходящие, `*CreateDto` / `*UpdateDto` — входящие.

### 1.2. Решение по bean-слою
- [ ] Выбрать: убрать `InsectBean` или ввести beans везде. **Рекомендация — убрать** (нет доменной логики, только перекладка полей).
- [ ] Если убираем: `InsectService` оперирует `InsectBean` в сигнатурах — основной объём. Заменить на `Entity ↔ DTO` прямо в сервисе. Тронется `InsectService`, `InsectServiceImpl`, `InsectController`.
- ⚠️ Самый крупный кусок — отдельный PR, не смешивать с реймингом.

### 1.3. Single-step register
- [ ] `/auth/register` → `multipart/form-data`; `@RequestPart("data") @Valid UserRegisterDto` + `@RequestPart(value = "file", required = false) MultipartFile`.
- [ ] `AccountService.register` создаёт user + credentials, затем `if (file != null) avatarService.uploadAvatar(...)`.
- [ ] Решить: аватар best-effort или строгий (400 на плохой файл).
- [ ] Обновить `http/Bukashki.http`.

### ✅ Тесты Фазы 1
- [ ] Unit на `InsectService` / маппинг Entity↔DTO.
- [ ] `@WebMvcTest` на контроллеры (валидация, статусы, форма ответа).
- [ ] Интеграционный на register-с-файлом (Testcontainers: Postgres + MinIO).

---

## 👋 Фаза 2 — Kafka «hello»

- [ ] **compose:** `kafka` (KRaft, один брокер, replication-factor 1), опционально `kafka-ui`; healthcheck; `SPRING_KAFKA_BOOTSTRAP_SERVERS`.
- [ ] **pom:** `spring-kafka`.
- [ ] **application.properties:** `bootstrap-servers`, producer `JsonSerializer`, consumer `JsonDeserializer` + `spring.json.trusted.packages`, `group-id`, `auto-offset-reset=earliest`.
- [ ] **событие:** пакет `model/event`, `PhotoUploadedEvent` (см. ниже).
- [ ] **продюсер:** тонкий `PhotoEventPublisher` поверх `KafkaTemplate`; вызвать в `addPhoto` после `save`.
- [ ] **консьюмер:** `@KafkaListener`, пока просто логирует.
- [ ] **проверка:** загрузил фото → лог в консьюмере + сообщение в kafka-ui.

### ✅ Тесты Фазы 2
- [ ] Testcontainers Kafka: продюсер публикует → тестовый консьюмер получает корректный `PhotoUploadedEvent`.
- [ ] Проверка сериализации/десериализации события (round-trip).

---

## 🖼 Фаза 3 — Payoff (генерация превью)

- [ ] **миграция `V4__thumbnail.sql`:** `alter table insect_photos add column thumbnail_object_key text;` (nullable = не обработано).
- [ ] **pom:** библиотека ресайза (Thumbnailator / imgscalr).
- [ ] **консьюмер:** download оригинала по `objectKey` → resize → upload превью в MinIO → записать `thumbnail_object_key`.
- [ ] **идемпотентность:** детерминированный ключ превью + идемпотентный UPDATE (повторная доставка не плодит дублей).
- [ ] **наружу:** `thumbnailUrl` в `InsectPhotoResponseDto` (когда ключ есть).

### ✅ Тесты Фазы 3
- [ ] Полный e2e на Testcontainers (Postgres + MinIO + Kafka): upload → событие → консьюмер сгенерил превью → в БД появился `thumbnail_object_key`, в MinIO лежит объект.
- [ ] Тест идемпотентности: доставить событие дважды → одно превью, БД не сломалась.

---

## 🛡 Фаза 4 — Надёжность

- [ ] **retry + DLT:** `DefaultErrorHandler` + backoff + `DeadLetterPublishingRecoverer` → топик `photo.uploaded.DLT`.
- [ ] **transactional outbox:** таблица `outbox`; запись события в **той же** транзакции, что и строка фото; relay (`@Scheduled`-поллер) публикует и помечает `sent`. Убрать прямой `send` из `addPhoto`.
- [ ] **observability:** лог `eventId` сквозь продюсер → консьюмер → DLT.

### ✅ Тесты Фазы 4
- [ ] «Ядовитое» событие (битый `objectKey`) → после ретраев уходит в DLT, партиция не блокируется.
- [ ] Outbox: событие пишется в той же транзакции, что и фото; при откате фото — события нет (нет фантомных публикаций).

---

## 📨 Спецификация `PhotoUploadedEvent`

**Назначение:** уведомить «фото загружено», чтобы консьюмер сам скачал и обработал, не дёргая обратно API.

### Поля (v1)
| поле | тип | зачем |
|---|---|---|
| `eventId` | UUID | идемпотентность/дедуп, трейсинг в DLT |
| `occurredAt` | Instant | когда произошло (не когда доставлено) |
| `photoId` | Long | какую строку `insect_photos` обновлять |
| `insectId` | Long | владелец-сущность; **ключ партиции** |
| `ownerUserId` | Long | аудит / будущие нотификации |
| `objectKey` | String | откуда качать оригинал в MinIO |
| `contentType` | String | как обрабатывать |
| `version` | int | эволюция схемы (начать с `1`) |

Принцип: не суём весь entity и не суём URL (вычисляемый) — только идентификаторы + `objectKey`. `eventId` / `occurredAt` / `version` — обязательный «конверт» любого события.

### Топик и ключ
- **топик:** `photo.uploaded` (прошедшее время = факт).
- **партиций:** 3; replication-factor 1 на одном брокере.
- **ключ:** `insectId` → события одного насекомого в одной партиции → упорядочены на сущность.

### Где продюсится
- Фаза 2: в `addPhoto` после `save`.
- Фаза 4: переезжает в outbox — прямой `send` после коммита создаёт дыру dual-write (коммит прошёл, `send` упал → превью не будет никогда). Тот же класс проблемы, что storage↔db.

### Что делает консьюмер (Фаза 3)
1. idempotency-guard: `thumbnail_object_key` уже стоит → ack и выход.
2. download оригинала по `objectKey`.
3. resize → превью.
4. upload превью в MinIO (`insects/{insectId}/thumbnails/{origUuid}.jpg`).
5. `UPDATE insect_photos SET thumbnail_object_key = ...`.
6. ack (коммит оффсета).

### Ошибки
- транзиентные (MinIO мигнул) → retry с backoff;
- «битый» файл / несуществующий `objectKey` после N попыток → DLT.

### Про аватар
Отдельный флоу (нет строки `insect_photos`). Не тащить сразу — позже `AvatarUploadedEvent` либо обобщённый `ImageUploadedEvent` с `kind`.

---

## 🎯 Опционально: ещё один эпик ради «баллов»

Ширина эпиков даёт убывающую отдачу; глубина + новая компетенция — растущую.

- **Access Control** (`@PreAuthorize` + `PermissionEvaluator`) — 🟢 **рекомендация**. Закрывает реальную дыру (сейчас GET-эндпоинты открыты, читается чужая коллекция), демонстрирует владение Spring method security, идеально тестируется (матрица 200/403/404). Prerequisite для View.
- **Exchange** (передача владения) — 🟢 высокий сигнал: атомарный перенос specimen = корректный многострочный `@Transactional`, state-machine, гонки. Но требует Social сначала.
- **Social Graph** — 🟡 enabler, сам по себе «more of the same».
- **View Collections** — 🔴 смысл только поверх Access Control.

Правило: один отполированный + тестируемый + защищённый slice ценнее пяти полу-готовых эпиков.

---

## 🔓 Открытые решения
- Судьба `InsectBean` (убрать / оставить).
- Аватар в register: best-effort или строгий.
- Префикс топиков (`photo.uploaded` vs `bukashki.photo.uploaded`).
- Отдельное событие для аватара сейчас или потом.
- Добавлять ли Access Control до Kafka (он тоже двигает восприятие сильнее ещё одного CRUD).
