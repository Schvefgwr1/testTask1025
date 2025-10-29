import { API_BASE_URL } from "../config.js";

/**
 * Загружает файл на сервер
 * @param {File} file - Файл для загрузки
 * @param {string} token - JWT токен авторизации
 * @returns {Promise<Object>} - Объект с информацией о загруженном файле
 * @throws {Error} - Ошибка при загрузке файла
 */
export default async function uploadFile(file, token) {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`${API_BASE_URL}/api/files/upload`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`
        },
        body: formData
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.error || 'Ошибка загрузки файла');
    }

    return data;
}