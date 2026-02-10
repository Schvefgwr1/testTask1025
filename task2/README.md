# File Service - Full Stack приложение

Веб-приложение для загрузки и управления файлами с аутентификацией пользователей.

## Описание

**Backend**: Java SE 17, PostgreSQL, JWT (общий фреймворк common-core)  
**Frontend**: Vanilla JavaScript, статика через Apache httpd

### Возможности

- Регистрация и вход пользователей
- Загрузка файлов (до 10 MB)
- Статистика файлов пользователя
- JWT токены и сессии
- Система миграций базы данных
- Автоматическая очистка старых файлов

---

## Быстрый старт

```bash
cd task2
docker-compose up -d
```

Приложение будет доступно:
- **Frontend:** http://localhost:3001
- **Backend API:** http://localhost:8080
- **PostgreSQL:** localhost:5433

---

## Страницы

- `/` - Вход и регистрация
- `/upload` - Загрузка файлов (требуется авторизация)
- `/stats` - Статистика файлов (требуется авторизация)

---

## API Endpoints

```
POST /api/register       # Регистрация
POST /api/login          # Вход
POST /api/files/upload   # Загрузка файла (JWT)
GET  /api/stats/files/   # Статистика (JWT)
GET  /api/files/{uuid}   # Скачать файл
```

---

## Конфигурация (переменные окружения Docker)

| Переменная | Описание | По умолчанию |
|------------|----------|--------------|
| DB_URL | JDBC URL | jdbc:postgresql://postgres:5432/file_service |
| DB_USER | Пользователь БД | postgres |
| DB_PASSWORD | Пароль БД | postgres |
| JWT_SECRET | Секрет для JWT | change-me-in-production |
| FILE_STORAGE_PATH | Путь для загрузок | /app/uploads |
| FILE_RETENTION_DAYS | Дней хранения файлов | 30 |

---

## Структура

```
task2/
├── docker-compose.yml
├── backend/                 # Использует common-core
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/.../com/fileservice/
│       ├── handler/
│       ├── service/
│       ├── repository/
│       ├── middleware/
│       └── ...
└── frontend/
    ├── Dockerfile
    ├── httpd.conf
    ├── pages/
    ├── css/
    └── js/
```
