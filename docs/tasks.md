# 🧭 Project Backlog

## 📌 Общая стратегия

Разработка идёт по этапам:

1. MVP (ядро продукта)
2. Access Control
3. Social Graph
4. Exchange
5. Kafka / Async
6. Рефакторинг и улучшения

---

# 🚀 MVP

## 🔐 EPIC: Auth

**Цель:** пользователь может зарегистрироваться и войти

### Issues
- [ ] Создать таблицу users
- [ ] Добавить поля: email, password_hash
- [ ] Реализовать регистрацию (POST /auth/register)
- [ ] Реализовать логин (POST /auth/login)
- [ ] Хеширование пароля
- [ ] Генерация JWT
- [ ] Middleware авторизации (user_id из токена)
- [ ] Обработка ошибок авторизации

---

## 👤 EPIC: User

**Цель:** есть профиль пользователя

### Issues
- [ ] Endpoint GET /me
- [ ] Endpoint PATCH /me
- [ ] Добавить поля username, avatar_url
- [ ] Валидация данных
- [ ] Обновление пользователя в БД

---

## 🐞 EPIC: Specimen

**Цель:** CRUD коллекции

### Issues
- [ ] Таблица specimen (id, owner_id, name, description)
- [ ] Создание specimen
- [ ] Получение списка своих specimen
- [ ] Получение одного specimen
- [ ] Обновление specimen
- [ ] Удаление specimen
- [ ] Проверка owner_id при изменении

---

## 🖼 EPIC: Storage (S3)

**Цель:** загрузка файлов

### Issues
- [ ] Подключить S3 клиент
- [ ] Конфигурация (ключи, бакет)
- [ ] Метод upload(file)
- [ ] Генерация уникального имени файла
- [ ] Возврат URL
- [ ] Метод delete(file_key)
- [ ] Обработка ошибок

---

## 📸 EPIC: Specimen Photos

**Цель:** фото работают end-to-end

### Issues

#### Модель
- [ ] Таблица specimen_photo (id, specimen_id, url)

#### Upload
- [ ] Endpoint POST /specimens/{id}/photos
- [ ] Прием multipart файла
- [ ] Проверка владельца specimen
- [ ] Загрузка в S3
- [ ] Сохранение URL в БД

#### Чтение
- [ ] Получение списка фото specimen

#### Удаление
- [ ] Endpoint удаления фото
- [ ] Проверка прав
- [ ] Удаление из S3
- [ ] Удаление из БД

---

# 🔐 ACCESS CONTROL

## 🔐 EPIC: Access Control

**Цель:** централизованные проверки

### Issues
- [ ] Создать сервис access_control
- [ ] canEditSpecimen(userId, specimenId)
- [ ] canDeletePhoto(userId, photoId)
- [ ] canViewCollection(userId, targetUserId)
- [ ] Проверка владельца
- [ ] Проверка приватности
- [ ] Интеграция в specimen и photos

---

# 👥 SOCIAL

## 👥 EPIC: Social Graph

**Цель:** дружба

### Issues
- [ ] Таблица friendships
- [ ] Таблица friend_requests
- [ ] Отправка заявки
- [ ] Принятие заявки
- [ ] Отклонение заявки
- [ ] Список друзей
- [ ] Входящие заявки
- [ ] Проверка дружбы

---

# 🔍 VIEW

## 🔍 EPIC: View Collections

**Цель:** просмотр чужих коллекций

### Issues
- [ ] GET /users/{id}/specimens
- [ ] Проверка доступа (public / friend)
- [ ] Ограничение доступа
- [ ] Ошибка "нет доступа"

---

# 🔄 EXCHANGE

## 🔄 EPIC: Exchange

**Цель:** обмен экземплярами

### Issues
- [ ] Таблица exchange
- [ ] Таблица exchange_items
- [ ] Создание обмена
- [ ] Проверка дружбы
- [ ] Принятие обмена
- [ ] Отклонение обмена
- [ ] Передача владения specimen
- [ ] История обменов

---

# ⚡ ASYNC

## ⚡ EPIC: Kafka

**Цель:** асинхронные события

### Issues
- [ ] Поднять Kafka (docker-compose)
- [ ] Настроить producer
- [ ] Настроить consumer
- [ ] Базовый event класс
- [ ] PhotoUploadedEvent
- [ ] Публикация события
- [ ] Consumer обработки
- [ ] Retry / error handling

---

# 🧱 INFRASTRUCTURE

## 🧱 EPIC: Infrastructure

**Цель:** базовая тех. основа

### Issues
- [ ] Общий exception handler
- [ ] DTO структура
- [ ] Repository слой (MyBatis)
- [ ] Конфигурация проекта
- [ ] Логирование
- [ ] Валидация
- [ ] Базовый response формат

---

# 🧭 Порядок разработки

```text
MVP:
Auth → User → Specimen → Storage → Photos

Потом:
Access Control → View

Потом:
Social → Exchange

Потом:
Kafka

Потом:
Refactor