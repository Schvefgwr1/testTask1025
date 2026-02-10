# Test Task - Полный стек проектов

Набор из трёх самостоятельных приложений, демонстрирующих различные технологии и архитектурные подходы.

---

## Подробная документация

- [Task 2 - File Service](task2/README.md)
- [Task 3 - Weather Service](task3/README.md)

---

## Структура проекта

```
testTask1025/
├── pom.xml                    # Parent POM (Maven multi-module)
├── common-core/               # Общий модульный фреймворк
│   └── src/main/java/com/common/core/
│       ├── http/              # HTTP сервер, роутинг, middleware
│       ├── config/            # Конфигурация
│       ├── db/                # PostgreSQL
│       ├── migration/         # Миграции БД
│       ├── transaction/       # Транзакции
│       ├── cache/             # Redis кэширование
│       └── exception/         # Базовые исключения
├── task1/                     # Игра "Вода и колбочки"
│   └── src/
│       ├── GameTests.java    # Тесты 
│       ├── Main.java
│       ├── MachineStates.java
│       └── models/
├── task2/                     # File Service
│   ├── docker-compose.yml    # Apache + Java + PostgreSQL
│   ├── backend/              # Maven модуль
│   └── frontend/             # Статика для Apache
└── task3/                     # Weather Service
    ├── docker-compose.yml    # Apache + Java + Redis
    ├── backend_t3/           # Maven модуль
    └── frontend_t3/          # Статика для Apache
```

---

## Быстрый старт

### Сборка всех модулей

```bash
mvn clean install
```

### Task 1 - Игра

```bash
cd task1
javac -d out src/*.java src/models/*.java
java -cp out Main
```

При запуске выберите: **1** — ручной ввод, **2** — запуск тестов.

### Task 2 - File Service (Docker)

```bash
cd task2
docker-compose up -d
```

- Frontend: http://localhost:3001
- Backend API: http://localhost:8080

### Task 3 - Weather Service (Docker)

```bash
cd task3
docker-compose up -d
```

- Frontend: http://localhost:3002
- Backend API: http://localhost:8081

---

## Обзор заданий

### Task 1: "Сортировка жидкостей"

**Технологии:** Java SE 17, паттерн State Machine (A*)

Игра "Вода и колбочки" - реализация игровой логики с алгоритмом поиска решения.

**Тестирование:** `GameTests.java` - 10 тест-кейсов на чистой Java.

---

### Task 2: File Service

**Технологии:** Backend: Java 17, PostgreSQL, JWT | Frontend: Vanilla JS, Apache httpd

Веб-приложение для загрузки и управления файлами с аутентификацией.

**Запуск:** `cd task2 && docker-compose up -d`

---

### Task 3: Weather Service

**Технологии:** Backend: Java 17, Redis, Open-Meteo API | Frontend: Vanilla JS, Chart.js (CDN), Apache httpd

Прогноз погоды с интерактивными графиками.

**Запуск:** `cd task3 && docker-compose up -d`

---