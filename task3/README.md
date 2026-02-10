# Weather Service - Full Stack приложение

Веб-приложение для получения прогноза погоды с интерактивными графиками.

## Описание

**Backend**: Java SE 17, Redis, Open-Meteo API (общий фреймворк common-core)  
**Frontend**: Vanilla JavaScript, Chart.js (CDN), статика через Apache httpd

### Возможности

- Прогноз погоды на 24 часа для любого города
- Интерактивные графики температуры (Chart.js)
- Автоматическое обновление каждый час
- Кэширование данных (Redis)
- Геокодирование городов

---

## Быстрый старт

### Вариант 1: Docker Compose (рекомендуется)

```bash
cd task3
docker-compose up -d
```

Приложение будет доступно:
- **Frontend:** http://localhost:3002
- **Backend API:** http://localhost:8081
- **Redis:** localhost:6379

### Вариант 2: Локальный запуск

**Требования:** Java 17+, Maven, Redis, веб-сервер для статики

```bash
# Сборка
mvn clean install

# Запуск backend
cd task3/backend_t3
mvn exec:java -Dexec.mainClass="com.meteoservice.Main"
```

---

## Использование

1. Введите название города (англ.: Moscow, London, Tokyo)
2. Нажмите "Получить прогноз"
3. Просмотрите график температуры и статистику
4. Данные обновятся автоматически в начале следующего часа

---

## API

```
GET /weather?city={city_name}   # Прогноз погоды на 24 часа
```

---

## Конфигурация (Docker)

| Переменная | Описание |
|------------|----------|
| REDIS_HOST | Хост Redis | redis |
| REDIS_PORT | Порт Redis | 6379 |
| CACHE_TIMEOUT_SECONDS | TTL кэша | 900 |
| API_GEOCODING_URL | Geocoding API | open-meteo |
| API_WEATHER_URL | Weather API | open-meteo |

---

## Структура

```
task3/
├── docker-compose.yml
├── backend_t3/              # Использует common-core
│   ├── Dockerfile
│   └── src/.../com/meteoservice/
└── frontend_t3/
    ├── Dockerfile
    ├── httpd.conf
    ├── pages/
    └── js/
```
