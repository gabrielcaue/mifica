import axios from 'axios';

const api = axios.create({
  // 🔧 ALTERADO: antes era "http://localhost:8080/api"
  // agora basta "/api" porque o Traefik já roteia para o backend
  baseURL: '/api'
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
