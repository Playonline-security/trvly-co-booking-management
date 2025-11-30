import axios from 'axios';

/**
 * Configuración de la instancia de Axios para las peticiones HTTP
 * Configura la URL base y los headers por defecto
 */
const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Agregar token JWT a las solicitudes si está disponible en el localStorage
 */
const token = localStorage.getItem('token');
if (token) {
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

/**
 * Interceptor de respuesta para el manejo de errores
 * Si se recibe un error 401 (no autorizado), elimina el token y redirige al login
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;

