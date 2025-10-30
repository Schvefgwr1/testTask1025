# Weather Service - Full Stack приложение

Веб-приложение для получения прогноза погоды с интерактивными графиками и автоматическим обновлением данных.

## 📋 Описание

**Backend**: Java SE 17 (без фреймворков), Redis, Open-Meteo API  
**Frontend**: Vanilla JavaScript (ES6 Modules), Chart.js, CSS, Node.js сервер

### Возможности

- 🌤️ Получение прогноза погоды на 24 часа для любого города
- 📊 Интерактивные графики температуры (Chart.js)
- 🔄 Автоматическое обновление данных каждый час
- 💾 Кэширование данных (Redis)
- 🌍 Геокодирование городов через Open-Meteo Geocoding API
- 📈 Статистика: минимальная, средняя и максимальная температура

---

## 🚀 Быстрый старт

### Предварительные требования

- **Java 17+** ([скачать](https://adoptium.net/))
- **Maven 3.8+** ([скачать](https://maven.apache.org/download.cgi))
- **Redis 7+** ([скачать](https://redis.io/download))
- **Node.js 18+** ([скачать](https://nodejs.org/))

### 1️⃣ Настройка Redis

```bash
# Запустите Redis (Windows - через WSL или установщик)
redis-server
```

### 2️⃣ Запуск Backend

```bash
# Перейдите в папку backend
cd backend_t3

# Настройте application.properties (если нужно)
# backend_t3/src/main/resources/application.properties

# Соберите проект
mvn clean package

# Запустите сервер
java -cp target/classes com.meteoservice.Main
```

Backend запустится на **http://localhost:8080**

### 3️⃣ Запуск Frontend

```bash
# Откройте новый терминал и перейдите в папку frontend
cd frontend_t3

# Установите зависимости (первый раз)
npm install

# Запустите сервер
npm start
```

Frontend запустится на **http://localhost:3002**

---

## 🌐 Доступ к приложению

Откройте браузер и перейдите на:

**http://localhost:3002**

### Использование

1. Введите название города (на английском, например: Moscow, London, Tokyo)
2. Нажмите "Получить прогноз"
3. Просмотрите график температуры и статистику
4. Данные автоматически обновятся в начале следующего часа

---

## ⚙️ Конфигурация

### Backend (`backend_t3/src/main/resources/application.properties`)

```properties
# Сервер
server.port=8080

# Redis
redis.host=localhost
redis.port=6379
redis.password=

# Кэш (TTL в секундах)
cache.timeout.seconds=172800

# Внешние API
api.geocoding.url=https://geocoding-api.open-meteo.com/v1/search
api.weather.url=https://api.open-meteo.com/v1/forecast
api.timeout.seconds=10
```

### Frontend (`frontend_t3/server.js`)

```javascript
const PORT = 3002;  // Порт frontend сервера
```

### Frontend API URL (`frontend_t3/js/config.js`)

```javascript
export const API_BASE_URL = 'http://localhost:8080';
```

---

## 📁 Структура проекта

```
task3/
├── backend_t3/                    # Backend (Java)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/meteoservice/
│   │   │   │   ├── core/         # Ядро (HTTP сервер, роутер, кэш)
│   │   │   │   │   ├── cache/    # Redis кэш
│   │   │   │   │   ├── config/   # Конфигурация
│   │   │   │   │   └── http/     # HTTP сервер и роутинг
│   │   │   │   ├── handler/      # HTTP обработчики
│   │   │   │   ├── service/      # Бизнес-логика
│   │   │   │   ├── client/       # HTTP клиенты для внешних API
│   │   │   │   ├── model/        # Модели данных (Lombok)
│   │   │   │   ├── dto/          # Data Transfer Objects
│   │   │   │   ├── middleware/   # CORS, Exception handling
│   │   │   │   ├── exception/    # Кастомные исключения
│   │   │   │   ├── router/       # Конфигурация маршрутов
│   │   │   │   └── Main.java     # Точка входа
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── pom.xml                   # Maven конфигурация
│   └── target/                   # Скомпилированные классы
│
├── frontend_t3/                  # Frontend (JavaScript)
│   ├── pages/                    # HTML страницы
│   │   └── index.html           # Главная страница
│   ├── js/
│   │   ├── api/                 # HTTP запросы к backend
│   │   │   └── weather.js       # API погоды
│   │   ├── main.js              # Основная логика + Chart.js
│   │   └── config.js            # Конфигурация (API URL)
│   ├── css/                     # Стили
│   │   ├── style.css            # Главный (CSS Variables)
│   │   ├── main.css             # Основные стили
│   │   ├── forms.css            # Формы
│   │   └── media.css            # Медиа-запросы
│   ├── node_modules/            # npm зависимости
│   │   └── chart.js/            # Chart.js для графиков
│   ├── package.json             # npm конфигурация
│   └── server.js                # Минимальный HTTP сервер
│
└── README.md                     # Этот файл
```

---

## 🎯 API Endpoints

### Погода

```
GET /weather?city={city_name}    # Получить прогноз погоды на 24 часа
```