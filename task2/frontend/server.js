import http from 'http';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const PORT = 3001;

const MIME_TYPES = {
  '.html': 'text/html',
  '.css': 'text/css',
  '.js': 'text/javascript',
  '.mjs': 'text/javascript',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.gif': 'image/gif',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon'
};

// Роутинг
const ROUTES = {
  '/': '/pages/index.html',
  '/upload': '/pages/upload.html',
  '/stats': '/pages/stats.html'
};

const server = http.createServer((req, res) => {
  // CORS
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  
  if (req.method === 'OPTIONS') {
    res.writeHead(200);
    res.end();
    return;
  }

  let filePath = req.url;

  // Роутинг для чистых URL
  if (ROUTES[filePath]) {
    filePath = ROUTES[filePath];
  }

  // Убираем query string
  filePath = filePath.split('?')[0];

  // Полный путь к файлу
  const fullPath = path.join(__dirname, filePath);

  // Проверяем существование файла
  fs.access(fullPath, fs.constants.F_OK, (err) => {
    if (err) {
      res.writeHead(404);
      res.end('404 Not Found');
      return;
    }

    // Определяем MIME type
    const ext = path.extname(fullPath);
    const contentType = MIME_TYPES[ext] || 'application/octet-stream';

    // Читаем и отправляем файл
    fs.readFile(fullPath, (err, content) => {
      if (err) {
        res.writeHead(500);
        res.end('500 Internal Server Error');
        return;
      }

      res.writeHead(200, { 
        'Content-Type': contentType,
        'Cache-Control': 'no-cache'
      });
      res.end(content);
    });
  });
});

server.listen(PORT, () => {
  console.log(`\n Frontend сервер запущен!`);
  console.log(`http://localhost:${PORT}\n`);
  console.log(`Доступные страницы:`);
  console.log(`   → http://localhost:${PORT}/          (Вход)`);
  console.log(`   → http://localhost:${PORT}/upload    (Загрузка)`);
  console.log(`   → http://localhost:${PORT}/stats     (Статистика)\n`);
});

