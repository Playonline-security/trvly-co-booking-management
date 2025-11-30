import React, { createContext, useState, useContext, useEffect } from 'react';
import api from '../services/api';

/**
 * Contexto de autenticación para gestionar el estado del usuario
 */
const AuthContext = createContext();

/**
 * Hook personalizado para acceder al contexto de autenticación
 * @returns {Object} Contexto de autenticación con usuario, estado y funciones
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider');
  }
  return context;
};

/**
 * Proveedor de contexto de autenticación
 * Gestiona el estado de autenticación y las funciones de login/logout
 * @param {Object} props - Propiedades del componente
 * @param {ReactNode} props.children - Componentes hijos
 */
export const AuthProvider = ({ children }) => {
  // Estados para gestionar la autenticación
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  /**
   * Efecto para verificar si hay una sesión guardada al cargar la aplicación
   * Restaura el token y los datos del usuario desde el localStorage
   */
  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    if (token && userData) {
      setUser(JSON.parse(userData));
      setIsAuthenticated(true);
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
    setLoading(false);
  }, []);

  /**
   * Función para iniciar sesión
   * @param {string} username - Nombre de usuario
   * @param {string} password - Contraseña
   * @returns {Object} Resultado del login con success y message
   */
  const login = async (username, password) => {
    try {
      const response = await api.post('/api/auth/login', { username, password });
      const { token, ...userData } = response.data;
      
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(userData));
      
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      
      setUser(userData);
      setIsAuthenticated(true);
      
      return { success: true };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Error al iniciar sesión'
      };
    }
  };

  /**
   * Función para cerrar sesión
   * Elimina el token y los datos del usuario del localStorage
   */
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    delete api.defaults.headers.common['Authorization'];
    setUser(null);
    setIsAuthenticated(false);
  };

  const value = {
    user,
    isAuthenticated,
    loading,
    login,
    logout
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

