import { API_BASE_URL } from "../config.js";
import Cookies from "../lib/cookies.js";

export default async function Login(e) {
    e.preventDefault();

    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    const errorEl = document.getElementById('login-error');

    errorEl.textContent = '';

    try {
        const response = await fetch(`${API_BASE_URL}/api/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                login: username,
                password: password
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || 'Ошибка входа');
        }

        // Save token to cookies
        Cookies.set('token', data.token, { expires: 1 }); // 1 day
        Cookies.set('username', data.username, { expires: 1 });

        // Redirect to upload page
        window.location.href = '/upload';

    } catch (error) {
        errorEl.textContent = error.message;
    }
}