# File Service - Full Stack приложение

Веб-приложение для загрузки и управления файлами с аутентификацией пользователей.

## 📋 Описание

**Backend**: Java SE 17 (без фреймворков), PostgreSQL, JWT аутентификация  
**Frontend**: Vanilla JavaScript (ES6 Modules), CSS Variables, Node.js сервер

### Возможности

- 🔐 Регистрация и вход пользователей
- 📁 Загрузка файлов (до 10 MB)
- 📊 Статистика файлов пользователя
- 🔒 JWT токены и сессии
- 🗄️ Система миграций базы данных
- 🎯 Автоматическая очистка старых файлов

---

## 🚀 Быстрый старт

### Предварительные требования

- **Java 17+** ([скачать](https://adoptium.net/))
- **Maven 3.8+** ([скачать](https://maven.apache.org/download.cgi))
- **PostgreSQL 14+** ([скачать](https://www.postgresql.org/download/))
- **Node.js 18+** ([скачать](https://nodejs.org/))

### 1️⃣ Настройка базы данных

```bash
# Запустите PostgreSQL и создайте базу данных
psql -U postgres

CREATE DATABASE fileservice;
CREATE USER fileservice_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE fileservice TO fileservice_user;
\q
```

### 2️⃣ Запуск Backend

```bash
# Перейдите в папку backend
cd backend

# Настройте application.properties (если нужно)
# backend/src/main/resources/application.properties

# Соберите проект
mvn clean package

# Запустите сервер
java -jar target/file-service-1.0.jar
```

Backend запустится на **http://localhost:8080**

### 3️⃣ Запуск Frontend

```bash
# Откройте новый терминал и перейдите в папку frontend
cd frontend

# Установите зависимости (первый раз)
npm install

# Запустите сервер
npm start
```

Frontend запустится на **http://localhost:3001**

---

## 🌐 Доступ к приложению

Откройте браузер и перейдите на:

**http://localhost:3001**

### Страницы

- `/` - Вход и регистрация
- `/upload` - Загрузка файлов (требуется авторизация)
- `/stats` - Статистика файлов (требуется авторизация)

---

## ⚙️ Конфигурация

### Backend (`backend/src/main/resources/application.properties`)

```properties
# База данных
db.url=jdbc:postgresql://localhost:5432/fileservice
db.user=fileservice_user
db.password=your_password

# Сервер
server.port=8080

# JWT
jwt.secret=your-secret-key-min-256-bits-long-for-hs256-algorithm-security
jwt.expiration=86400000

# Файлы
file.upload.dir=uploads
file.max.size=10485760
file.retention.days=30
```

### Frontend (`frontend/server.js`)

```javascript
const PORT = 3001;  // Порт frontend сервера
```

### Frontend API URL (`frontend/js/config.js`)

```javascript
export const API_BASE_URL = 'http://localhost:8080';
```

---

## 📁 Структура проекта

```
task2/
├── backend/                        # Backend (Java)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fileservice/
│   │   │   │   ├── core/          # Ядро (HTTP сервер, роутер)
│   │   │   │   ├── handler/       # HTTP обработчики
│   │   │   │   ├── service/       # Бизнес-логика
│   │   │   │   ├── repository/    # Работа с БД
│   │   │   │   ├── model/         # Модели данных (Lombok)
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── mapper/        # ResultSet → Model
│   │   │   │   ├── migration/     # Система миграций
│   │   │   │   ├── middleware/    # CORS, Auth, Exception
│   │   │   │   ├── exception/     # Кастомные исключения
│   │   │   │   ├── util/          # JWT, Password, Multipart
│   │   │   │   ├── config/        # Конфигурация
│   │   │   │   └── Main.java      # Точка входа (DI контейнер)
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── migrations.yaml
│   │   │       └── migrations/    # SQL миграции
│   │   └── test/
│   ├── pom.xml                    # Maven конфигурация
│   └── uploads/                   # Загруженные файлы (создается автоматически)
│
├── frontend/                      # Frontend (JavaScript)
│   ├── pages/                     # HTML страницы
│   │   ├── index.html            # Вход/Регистрация
│   │   ├── upload.html           # Загрузка файлов
│   │   └── stats.html            # Статистика
│   ├── js/
│   │   ├── api/                  # HTTP запросы к backend
│   │   │   ├── login.js
│   │   │   ├── register.js
│   │   │   ├── upload.js
│   │   │   └── stats.js
│   │   ├── lib/                  # Библиотеки (js-cookie)
│   │   │   └── cookies.js
│   │   ├── auth.js               # Логика авторизации
│   │   ├── upload.js             # Логика загрузки
│   │   ├── stats.js              # Логика статистики
│   │   ├── common.js             # Общие функции (logout)
│   │   └── config.js             # Конфигурация (API URL)
│   ├── css/                      # Стили
│   │   ├── style.css             # Главный (CSS Variables)
│   │   ├── main.css              # Основные стили
│   │   ├── forms.css             # Формы
│   │   └── media.css             # Медиа-запросы
│   ├── node_modules/             # npm зависимости
│   ├── package.json              # npm конфигурация
│   └── server.js                 # Минимальный HTTP сервер
│
└── README.md                      # Этот файл
```

---

## 🎯 API Endpoints

### Аутентификация

```
POST /api/register       # Регистрация пользователя
POST /api/login          # Вход в систему
```

### Файлы (требуется JWT токен)

```
POST   /api/files/upload    # Загрузить файл
GET    /api/stats/files/    # Статистика файлов
GET    /api/files/{uuid}    # Скачать файл
```

---

## 📝 Миграции базы данных

Миграции применяются автоматически при старте backend.

### Добавление новой миграции

1. Создайте файл `backend/src/main/resources/migrations/V2__description.sql`
2. Добавьте его в `backend/src/main/resources/migrations.yaml`:

```yaml
migrations:
  - V1__initial_schema.sql
  - V2__description.sql
```

3. Перезапустите backend - миграция применится автоматически

---