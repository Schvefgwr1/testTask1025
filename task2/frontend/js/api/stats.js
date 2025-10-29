import { API_BASE_URL } from "../config.js";

export default async function getFileStats(token) {
    const response = await fetch(`${API_BASE_URL}/api/stats/files/`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.error || 'Ошибка загрузки статистики');
    }

    return data;
}

