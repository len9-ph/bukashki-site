# 🧭 План: рефакторинг → Kafka (PhotoUploadedEvent)

**Дата:** 2026-07-02
**Цель:** пощупать Kafka на осмысленном доменном кейсе (асинхронная пост-обработка загруженных фото), предварительно почистив модель.

**Порядок:** сначала фундамент (нейминг DTO, судьба bean-слоя, single-step register), потом Kafka по нарастающей — hello → payoff → надёжность.

---

## 🧱 Фаза 1 — Фундамент (до Kafka)

### 1.1. Нейминг read-DTO
- [ ] `InsectDto → InsectResponseDto`; поправить `InsectBean.toDto`, `InsectController` (getInsects / getInsect / getMy).
- [ ] Зафиксировать правило: `*ResponseDto` — только исходящие, `*CreateDto` / `*UpdateDto` — входящие. Проверить, что все DTO под него попадают.

### 1.2. Решение по bean-слою
- [ ] Выбрать: убрать `InsectBean` или ввести beans везде. **Рекомендация — убрать** (нет доменной логики, только перекладка полей).
- [ ] Если убираем: `InsectService` оперирует `InsectBean` в сигнатурах — это основной объём. Заменить на маппинг `Entity ↔ DTO` прямо в сервисе (как в фото/юзерах). Тронется `InsectService`, `InsectServiceImpl`, `InsectController`.
- ⚠️ Самый крупный кусок фазы — вынести в отдельный PR, не смешивать с реймингом.

### 1.3. Single-step register
- [ ] `/auth/register` → `consumes = multipart/form-data`; `@RequestPart("data") @Valid UserRegisterDto` + `@RequestPart(value = "file", required = false) MultipartFile`.
- [ ] `AccountService.register` создаёт user + credentials, затем `if (file != null) avatarService.uploadAvatar(newUser.getUserId(), file)` — переиспользуем готовый флоу.
- [ ] Решить: аватар best-effort (плохой/пустой файл не валит регистрацию) или строгий (валит 400).
- [ ] Обновить `http/Bukashki.http` примером multipart-регистрации.

---

## 👋 Фаза 2 — Kafka «hello» (выкидной спайк, отдельная ветка)

- [ ] **compose:** `kafka` (KRaft, один брокер, replication-factor 1), опционально `kafka-ui`; healthcheck; в `app` — `SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092`.
- [ ] **pom:** `spring-kafka`.
- [ ] **application.properties:** `spring.kafka.bootstrap-servers`, producer `JsonSerializer`, consumer `JsonDeserializer` + `spring.json.trusted.packages`, `group-id`, `auto-offset-reset=earliest`.
- [ ] **событие:** пакет `model/event`, `PhotoUploadedEvent` (см. раздел ниже).
- [ ] **продюсер:** тонкий `PhotoEventPublisher` поверх `KafkaTemplate`; вызвать в `addPhoto` после `save`.
- [ ] **консьюмер:** `@KafkaListener`, пока просто логирует.
- [ ] **проверка end-to-end:** загрузил фото → лог в консьюмере + сообщение в kafka-ui. Цель фазы — пощупать producer / consumer / consumer-group / сериализацию.

---

## 🖼 Фаза 3 — Payoff (генерация превью)

- [ ] **миграция `sql/04.sql`:** `alter table insect_photos add column thumbnail_object_key text;` (nullable = ещё не обработано).
- [ ] **pom:** библиотека ресайза (Thumbnailator или imgscalr).
- [ ] **консьюмер делает работу:** download оригинала по `objectKey` → resize → upload превью в MinIO → записать `thumbnail_object_key` в БД.
- [ ] **идемпотентность:** детерминированный ключ превью (производный от оригинала) + идемпотентный UPDATE → повторная доставка не плодит дублей, dedup-таблица не нужна.
- [ ] **отдать наружу:** в `InsectPhotoResponseDto` добавить `thumbnailUrl` (когда ключ есть).

---

## 🛡 Фаза 4 — Надёжность

- [ ] **retry + DLT:** `DefaultErrorHandler` с backoff (транзиентные сбои MinIO ретраятся) + `DeadLetterPublishingRecoverer` → топик `photo.uploaded.DLT` для «ядовитых» сообщений.
- [ ] **transactional outbox:** таблица `outbox`; запись события в **той же** транзакции, что и строка фото; отдельный relay (`@Scheduled`-поллер для учебки, не Debezium) публикует и помечает `sent`. Убрать прямой `send` из `addPhoto`.
- [ ] **наблюдаемость:** лог `eventId` сквозь продюсер → консьюмер → DLT.

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

Принцип: не суём весь entity и не суём URL (он вычисляемый) — только идентификаторы + `objectKey`. `eventId` / `occurredAt` / `version` — обязательный «конверт» любого события.

### Топик и ключ
- **топик:** `photo.uploaded` (прошедшее время = факт; при желании домен-префикс `bukashki.photo.uploaded`).
- **партиций:** 3 (почувствовать параллелизм); replication-factor 1 на одном брокере.
- **ключ:** `insectId` (как String) → события одного насекомого в одной партиции → **упорядочены на сущность**.

### Где продюсится
- Фаза 2: в `addPhoto` после `save`.
- Фаза 4: переезжает в outbox — прямой `send` после коммита создаёт дыру dual-write (коммит прошёл, `send` упал/креш → превью не будет никогда). Тот же класс проблемы, что storage↔db, только третья система.

### Что делает консьюмер (Фаза 3)
1. idempotency-guard: если `thumbnail_object_key` уже стоит (или превью лежит по детерминированному ключу) → ack и выход.
2. download оригинала по `objectKey`.
3. resize → превью.
4. upload превью в MinIO (ключ вроде `insects/{insectId}/thumbnails/{origUuid}.jpg`).
5. `UPDATE insect_photos SET thumbnail_object_key = ...`.
6. ack (коммит оффсета).

### Ошибки
- транзиентные (MinIO мигнул) → retry с backoff;
- «битый» файл / несуществующий `objectKey` после N попыток → DLT, не блокируя партицию.

### Про аватар
Отдельный флоу (нет строки `insect_photos`). Не тащить в это событие сразу — позже `AvatarUploadedEvent` либо обобщённый `ImageUploadedEvent` с дискриминатором `kind`. Для «пощупать» — только фото.

---

## 🔓 Открытые решения
- Судьба `InsectBean` (убрать / оставить).
- Аватар в register: best-effort или строгий.
- Префикс топиков (`photo.uploaded` vs `bukashki.photo.uploaded`).
- Отдельное событие для аватара сейчас или потом.
