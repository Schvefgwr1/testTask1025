import Register from "./api/register.js";
import Login from "./api/login.js";
import Cookies from "./lib/cookies.js";

document.addEventListener('DOMContentLoaded', () => {
    // Tab switching
    const tabs = document.querySelectorAll('.tab');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const targetTab = tab.dataset.tab;

            // Update active tab
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // Show/hide forms
            if (targetTab === 'login') {
                loginForm.classList.remove('hidden');
                registerForm.classList.add('hidden');
            } else {
                loginForm.classList.add('hidden');
                registerForm.classList.remove('hidden');
            }

            // Clear error messages
            document.getElementById('login-error').textContent = '';
            document.getElementById('register-error').textContent = '';
        });
    });

    // Check if already logged in
    const token = Cookies.get('token');
    if (token) {
        window.location.href = '/upload';
    }

    loginForm.addEventListener('submit', Login);
    registerForm.addEventListener('submit', Register);
});