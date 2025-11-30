import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Componente de barra lateral de navegación
 * Muestra el menú de navegación según el rol del usuario
 */
const Sidebar = () => {
  const { user, logout } = useAuth();
  const location = useLocation();
  const isAdmin = user?.roles?.includes('ADMIN');
  const isSupervisor = user?.roles?.includes('SUPERVISOR');

  const menuItems = [
    { path: '/', label: 'Dashboard', icon: '📊' },
    { path: '/clients', label: 'Clientes', icon: '👥' },
    { path: '/reservations', label: 'Reservas', icon: '✈️' },
    ...(isAdmin ? [
      { path: '/packages', label: 'Paquetes', icon: '🎒' },
      { path: '/users', label: 'Usuarios', icon: '👤' },
    ] : []),
  ];

  return (
    <div className="fixed left-0 top-0 h-full w-64 bg-white shadow-lg z-10">
      <div className="p-6 border-b border-gray-200">
        <h2 className="text-2xl font-bold text-primary-700">trvly.co</h2>
        <p className="text-sm text-gray-600 mt-1">
          {isAdmin ? 'Administrador' : isSupervisor ? 'Supervisor' : 'Asesor'}
        </p>
      </div>

      <nav className="mt-6">
        {menuItems.map((item) => (
          <Link
            key={item.path}
            to={item.path}
            className={`flex items-center px-6 py-3 text-gray-700 hover:bg-primary-50 hover:text-primary-700 transition-colors ${
              location.pathname === item.path ? 'bg-primary-50 text-primary-700 border-r-4 border-primary-500' : ''
            }`}
          >
            <span className="mr-3 text-xl">{item.icon}</span>
            <span className="font-medium">{item.label}</span>
          </Link>
        ))}
      </nav>

      <div className="absolute bottom-0 w-full p-6 border-t border-gray-200">
        <div className="mb-4">
          <p className="text-sm font-medium text-gray-800">{user?.username}</p>
          <p className="text-xs text-gray-500">{user?.email}</p>
        </div>
        <button
          onClick={logout}
          className="w-full bg-red-500 hover:bg-red-600 text-white font-semibold py-2 px-4 rounded-lg transition"
        >
          Cerrar sesión
        </button>
      </div>
    </div>
  );
};

export default Sidebar;

