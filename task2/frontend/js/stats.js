import LogoutScript from "./common.js";
import getFileStats from "./api/stats.js";
import { API_BASE_URL } from "./config.js";
import Cookies from "./lib/cookies.js";

document.addEventListener('DOMContentLoaded', () => {
    // Logout functionality
    document.getElementById('logoutBtn').addEventListener('click', LogoutScript);

    // Load statistics on page load
    loadStats();
});

async function loadStats() {
    try {
        // Check authentication
        const token = Cookies.get('token');
        if (!token) {
            window.location.href = '/';
            return;
        }

        // Получаем статистику через API
        const data = await getFileStats(token);

        // Отображаем статистику
        displayStats(data);

    } catch (error) {
        showMessage(error.message, 'error');

        // Check if unauthorized
        if (error.message.includes('token') || error.message.includes('Session') || error.message.includes('401')) {
            setTimeout(() => {
                Cookies.remove('token');
                Cookies.remove('username');
                window.location.href = '/';
            }, 2000);
        }
    }
}

function displayStats(data) {
    const totalFiles = data.totalFiles || 0;
    const files = data.files || [];

    // Update summary
    document.getElementById('totalFiles').textContent = totalFiles;

    if (files.length === 0) {
        // Show empty state
        document.querySelector('.files-table-container table').classList.add('hidden');
        document.getElementById('emptyState').classList.remove('hidden');
    } else {
        // Show table
        document.querySelector('.files-table-container table').classList.remove('hidden');
        document.getElementById('emptyState').classList.add('hidden');

        // Populate table
        const tbody = document.getElementById('filesTableBody');
        tbody.innerHTML = '';

        files.forEach(file => {
            const row = createFileRow(file);
            tbody.appendChild(row);
        });
    }
}

function createFileRow(file) {
    const tr = document.createElement('tr');

    // File name
    const tdName = document.createElement('td');
    tdName.textContent = file.fileName;
    tr.appendChild(tdName);

    // Created at
    const tdCreated = document.createElement('td');
    tdCreated.textContent = formatDate(file.createdAt);
    tr.appendChild(tdCreated);

    // Download count
    const tdCount = document.createElement('td');
    tdCount.textContent = file.downloadCount;
    tr.appendChild(tdCount);

    // Last download
    const tdLastDownload = document.createElement('td');
    tdLastDownload.textContent = file.lastDownloadAt ? formatDate(file.lastDownloadAt) : 'Никогда';
    tr.appendChild(tdLastDownload);

    // Actions
    const tdActions = document.createElement('td');
    const downloadBtn = document.createElement('a');
    downloadBtn.href = `${API_BASE_URL}${file.downloadUrl}`;
    downloadBtn.className = 'btn btn-small';
    downloadBtn.textContent = 'Скачать';
    downloadBtn.target = '_blank';

    const copyBtn = document.createElement('button');
    copyBtn.className = 'btn btn-small btn-secondary';
    copyBtn.textContent = 'Копировать ссылку';
    copyBtn.addEventListener('click', () => {
        copyToClipboard(`${API_BASE_URL}${file.downloadUrl}`);
        copyBtn.textContent = 'Скопировано!';
        setTimeout(() => {
            copyBtn.textContent = 'Копировать ссылку';
        }, 2000);
    });

    tdActions.appendChild(downloadBtn);
    tdActions.appendChild(copyBtn);
    tr.appendChild(tdActions);

    return tr;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const options = {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    };
    return date.toLocaleString('ru-RU', options);
}

function copyToClipboard(text) {
    const textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand('copy');
    document.body.removeChild(textarea);
}

function showMessage(text, type = 'info') {
    const messageEl = document.getElementById('message');
    messageEl.textContent = text;
    messageEl.className = `message ${type}`;
    messageEl.classList.remove('hidden');

    setTimeout(() => {
        messageEl.classList.add('hidden');
    }, 5000);
}

