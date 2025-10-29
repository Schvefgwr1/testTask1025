import { API_BASE_URL } from "./config.js";
import LogoutScript from "./common.js";
import uploadFile from "./api/upload.js";
import Cookies from "./lib/cookies.js";

document.addEventListener('DOMContentLoaded', () => {
    // Logout functionality
    document.getElementById('logoutBtn').addEventListener('click', LogoutScript);

    // File input handling
    const fileInput = document.getElementById('file-input');
    const fileInfo = document.getElementById('file-info');
    const fileName = document.getElementById('file-name');

    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            fileName.textContent = e.target.files[0].name;
            fileInfo.classList.remove('hidden');
        } else {
            fileInfo.classList.add('hidden');
        }
    });

    // Drag and drop
    const fileLabel = document.querySelector('.file-label');
    fileLabel.addEventListener('dragover', (e) => {
        e.preventDefault();
        fileLabel.classList.add('drag-over');
    });
    fileLabel.addEventListener('dragleave', () => {
        fileLabel.classList.remove('drag-over');
    });
    fileLabel.addEventListener('drop', (e) => {
        e.preventDefault();
        fileLabel.classList.remove('drag-over');

        if (e.dataTransfer.files.length > 0) {
            fileInput.files = e.dataTransfer.files;
            fileName.textContent = e.dataTransfer.files[0].name;
            fileInfo.classList.remove('hidden');
        }
    });

    // Upload form submission
    document.getElementById('uploadForm').addEventListener('submit', handleUpload);

    // Copy link button
    document.getElementById('copyLinkBtn').addEventListener('click', () => {
        const linkInput = document.getElementById('download-link');
        linkInput.select();
        document.execCommand('copy');

        const btn = document.getElementById('copyLinkBtn');
        const originalText = btn.textContent;
        btn.textContent = 'Скопировано!';

        setTimeout(() => {
            btn.textContent = originalText;
        }, 2000);
    });
});

async function handleUpload(e) {
    e.preventDefault();

    // Check authentication
    const token = Cookies.get('token');
    if (!token) {
        window.location.href = '/';
        return;
    }

    const file = document.getElementById('file-input').files[0];
    if (!file) {
        showMessage('Пожалуйста, выберите файл', 'error');
        return;
    }

    const uploadBtn = document.getElementById('uploadBtn');
    uploadBtn.disabled = true;
    uploadBtn.textContent = 'Загрузка...';

    try {
        // Загружаем файл через API
        const data = await uploadFile(file, token);

        // Показываем результат
        showUploadResult(data);

        // Сбрасываем форму
        document.getElementById('uploadForm').reset();
        document.getElementById('file-info').classList.add('hidden');

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
    } finally {
        uploadBtn.disabled = false;
        uploadBtn.textContent = 'Загрузить';
    }
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

function showUploadResult(data) {
    const resultEl = document.getElementById('uploadResult');
    const downloadLink = `${API_BASE_URL}${data.downloadUrl}`;

    document.getElementById('file-uuid').textContent = data.uuid;
    document.getElementById('download-link').value = downloadLink;

    resultEl.classList.remove('hidden');

    // Scroll to result
    resultEl.scrollIntoView({ behavior: 'smooth' });
}
