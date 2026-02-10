// Backend API Configuration
// Task3 uses port 8081 when running via Docker (to avoid conflict with Task2)
export const API_BASE_URL = (typeof window !== 'undefined' && window.location.port === '3002')
    ? 'http://localhost:8081'
    : 'http://localhost:8080';

