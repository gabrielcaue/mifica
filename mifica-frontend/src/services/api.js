import axios from 'axios';

const fallbackOrigin = typeof window !== 'undefined'
  ? (window.location.hostname === 'localhost' ? 'http://localhost:8080' : window.location.origin)
  : 'http://localhost:8080';

const configuredApiUrl = (import.meta.env.VITE_API_URL || fallbackOrigin).trim();
const normalizedApiUrl = configuredApiUrl.replace(/\/$/, '');

const api = axios.create({
  baseURL: `${normalizedApiUrl}/api`,
});

// Interceptor único para adicionar token e Content-Type
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    if (config.method !== 'get' && !config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json';
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;
