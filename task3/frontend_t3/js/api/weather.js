import { API_BASE_URL } from '../config.js';

/**
 * Получает прогноз погоды для города
 * @param {string} city - название города
 * @returns {Promise<Object>} данные о погоде
 */
export async function getWeatherForCity(city) {
    const url = `${API_BASE_URL}/weather?city=${encodeURIComponent(city)}`;
    
    const response = await fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        const err = new Error(errorData.reason || `HTTP error! status: ${response.status}`);
        err.status = response.status;
        throw err;
    }

    return await response.json();
}

