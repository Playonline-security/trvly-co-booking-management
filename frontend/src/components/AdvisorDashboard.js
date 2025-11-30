import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Sidebar from './Sidebar';

/**
 * Componente del dashboard para asesores
 * Muestra un panel de control con acceso a gestión de clientes y reservas
 */
const AdvisorDashboard = () => {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <div className="ml-64 p-8">
        <div className="max-w-7xl mx-auto">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">
            Bienvenido, {user?.username}
          </h1>
          <p className="text-gray-600 mb-8">Panel de control - Asesor</p>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <Link
              to="/clients"
              className="bg-white rounded-xl shadow-md hover:shadow-xl transition-shadow p-6 border-l-4 border-primary-500"
            >
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-lg font-semibold text-gray-800">Gestión de clientes</h3>
                  <p className="text-gray-600 text-sm mt-1">Administrar información de clientes</p>
                </div>
                <div className="text-primary-500 text-3xl">👥</div>
              </div>
            </Link>

            <Link
              to="/reservations"
              className="bg-white rounded-xl shadow-md hover:shadow-xl transition-shadow p-6 border-l-4 border-green-500"
            >
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-lg font-semibold text-gray-800">Gestión de reservas</h3>
                  <p className="text-gray-600 text-sm mt-1">Crear y gestionar reservas</p>
                </div>
                <div className="text-green-500 text-3xl">✈️</div>
              </div>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdvisorDashboard;

