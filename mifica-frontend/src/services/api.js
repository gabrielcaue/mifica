import axios from 'axios';

const resolveApiBaseUrl = () => {
  const envUrl = (import.meta.env.VITE_API_URL || '').trim();

  if (envUrl) {
    return envUrl.replace(/\/$/, '');
  }

  if (typeof window !== 'undefined') {
    const isLocalhost = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
    return isLocalhost ? 'http://localhost:8080' : window.location.origin;
  }

  return 'http://localhost:8080';
};

const api = axios.create({
  baseURL: `${resolveApiBaseUrl()}/api`,
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
