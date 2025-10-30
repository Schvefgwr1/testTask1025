import { getWeatherForCity } from './api/weather.js';
import Chart from 'chart.js';

// Глобальные переменные
let currentCity = null;
let temperatureChart = null;
let updateTimer = null;

// DOM элементы (будут инициализированы после загрузки DOM)
let weatherForm;
let cityInput;
let messageEl;
let weatherInfoEl;
let cityNameEl;
let coordinatesEl;
let updateTimeEl;
let nextUpdateEl;
let statsGridEl;
let chartCanvas;

/**
 * Обработчик отправки формы
 */
async function handleFormSubmit(e) {
    e.preventDefault();

    const city = cityInput.value.trim();
    if (!city) {
        showMessage('Пожалуйста, введите название города', 'error');
        return;
    }

    currentCity = city;
    await loadWeatherData(city);
}

/**
 * Загрузка данных о погоде
 */
async function loadWeatherData(city) {
    try {
        hideMessage();
        showLoading();

        const data = await getWeatherForCity(city);

        displayWeatherData(data);
        hideMessage();
        setupAutoUpdate();

    } catch (error) {
        console.error('Error loading weather:', error);
        showMessage(`Ошибка загрузки данных: ${error.message}`, 'error');
        hideWeatherInfo();
    }
}

/**
 * Отображение данных о погоде
 */
function displayWeatherData(data) {
    cityNameEl.textContent = data.city;
    coordinatesEl.textContent = `Координаты: ${data.coordinates.latitude.toFixed(4)}, ${data.coordinates.longitude.toFixed(4)}`;
    updateTimeEl.textContent = new Date().toLocaleString('ru-RU');

    // Готовим данные для графика
    const hours = data.hourlyData.map(item => {
        const date = new Date(item.hour);
        return date.toLocaleString('ru-RU', {
            day: '2-digit',
            month: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    });

    const temperatures = data.hourlyData.map(item =>  item.temperature.toFixed(1));

    renderChart(hours, temperatures);
    displayStats(temperatures);
    weatherInfoEl.style.display = 'block';
}

/**
 * Отрисовка графика температуры
 */
function renderChart(labels, data) {
    if (temperatureChart) {
        temperatureChart.destroy();
    }

    const ctx = chartCanvas.getContext('2d');

    temperatureChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Температура (°C)',
                data: data,
                borderColor: '#667eea',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                borderWidth: 3,
                tension: 0.4,
                fill: true,
                pointRadius: 4,
                pointHoverRadius: 6,
                pointBackgroundColor: '#667eea',
                pointBorderColor: '#fff',
                pointBorderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        font: {
                            size: 14,
                            family: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto'
                        },
                        padding: 15
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    titleFont: {
                        size: 14
                    },
                    bodyFont: {
                        size: 13
                    },
                    callbacks: {
                        label: function(context) {
                            return `Температура: ${context.parsed.y.toFixed(1)}°C`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: false,
                    ticks: {
                        callback: function(value) {
                            return `${value.toFixed(1)} °C`;
                        },
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    ticks: {
                        maxRotation: 45,
                        minRotation: 45,
                        font: {
                            size: 11
                        }
                    },
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
}

/**
 * Отображение статистики
 */
function displayStats(temperatures) {
    const min = Math.min(...temperatures);
    const max = Math.max(...temperatures);
    console.log(temperatures);
    const avg = temperatures.reduce((a, b) => a + Number(b), 0) / temperatures.length;

    statsGridEl.innerHTML = `
        <div class="stat-card">
            <div class="stat-number">${min.toFixed(1)}°C</div>
            <div class="stat-label">Минимальная</div>
        </div>
        <div class="stat-card">
            <div class="stat-number">${avg.toFixed(1)}°C</div>
            <div class="stat-label">Средняя</div>
        </div>
        <div class="stat-card">
            <div class="stat-number">${max.toFixed(1)}°C</div>
            <div class="stat-label">Максимальная</div>
        </div>
    `;
}

/**
 * Настройка автоматического обновления данных на каждый новый час
 */
function setupAutoUpdate() {
    if (updateTimer) {
        clearTimeout(updateTimer);
    }

    const now = new Date();
    const nextHour = new Date(now);
    nextHour.setHours(nextHour.getHours() + 1);
    nextHour.setMinutes(0);
    nextHour.setSeconds(0);
    nextHour.setMilliseconds(0);

    const msUntilNextHour = nextHour - now;

    nextUpdateEl.textContent = nextHour.toLocaleString('ru-RU', {
        hour: '2-digit',
        minute: '2-digit'
    });

    console.log(`Следующее обновление через ${Math.round(msUntilNextHour / 1000 / 60)} минут`);

    updateTimer = setTimeout(() => {
        console.log('Автоматическое обновление данных...');
        if (currentCity) {
            loadWeatherData(currentCity);
        }
    }, msUntilNextHour);
}

/**
 * Показать сообщение
 */
function showMessage(text, type = 'info') {
    messageEl.textContent = text;
    messageEl.className = `message ${type}`;
}

/**
 * Скрыть сообщение
 */
function hideMessage() {
    messageEl.textContent = '';
    messageEl.className = 'message';
}

/**
 * Показать состояние загрузки
 */
function showLoading() {
    messageEl.textContent = 'Загрузка данных';
    messageEl.className = 'message info loading';
}

/**
 * Скрыть блок с информацией о погоде
 */
function hideWeatherInfo() {
    weatherInfoEl.style.display = 'none';
}

/**
 * Инициализация приложения
 */
document.addEventListener('DOMContentLoaded', () => {
    // Инициализируем DOM элементы после загрузки страницы
    weatherForm = document.getElementById('weatherForm');
    cityInput = document.getElementById('city-input');
    messageEl = document.getElementById('message');
    weatherInfoEl = document.getElementById('weather-info');
    cityNameEl = document.getElementById('city-name');
    coordinatesEl = document.getElementById('coordinates');
    updateTimeEl = document.getElementById('update-time');
    nextUpdateEl = document.getElementById('next-update');
    statsGridEl = document.getElementById('stats-grid');
    chartCanvas = document.getElementById('temperatureChart');

    if (weatherForm) {
        weatherForm.addEventListener('submit', handleFormSubmit);
    } else {
        console.error('Форма weatherForm не найдена!');
    }
});