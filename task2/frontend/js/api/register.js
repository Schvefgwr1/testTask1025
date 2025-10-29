import { API_BASE_URL } from "../config.js";

export default async function Register(e) {
    e.preventDefault();

    const username = document.getElementById('register-username').value;
    const password = document.getElementById('register-password').value;
    const passwordConfirm = document.getElementById('register-password-confirm').value;
    const errorEl = document.getElementById('register-error');

    errorEl.textContent = '';

    if (password !== passwordConfirm) {
        errorEl.textContent = 'Пароли не совпадают';
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/register`, {
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
            throw new Error(data.error || 'Ошибка регистрации');
        }

        alert(`Регистрация успешна! Теперь войдите с логином: ${data.login}`);

        document.getElementById('register-form').classList.add('hidden');
        document.getElementById('login-form').classList.remove('hidden');

        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        document.querySelector('.tab[data-tab="login"]').classList.add('active');

        document.getElementById('login-username').value = data.login;
    } catch (error) {
        errorEl.textContent = error.message;
    }
}