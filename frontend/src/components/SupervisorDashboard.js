import React from 'react';
import { useAuth } from '../context/AuthContext';
import Sidebar from './Sidebar';
import DashboardCard from './common/DashboardCard'; // [RF-03]

/**
 * Componente del dashboard para supervisores
 */
const SupervisorDashboard = () => {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <div className="ml-64 p-8">
        <div className="max-w-7xl mx-auto">
          <h1 className="text-3xl font-bold text-gray-800 mb-2">
            Bienvenido, {user?.username}
          </h1>
          <p className="text-gray-600 mb-8">Panel de control - Supervisor</p>

          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
            <p className="text-blue-800">
              <strong>Rol de supervisor:</strong> Tienes acceso a funciones adicionales de eliminación de clientes y reservas.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* [RF-03] */}
            <DashboardCard
              to="/clients"
              title="Gestión de clientes"
              description="Administrar información de clientes"
              icon="👥"
              borderColorClass="border-primary-500"
              iconColorClass="text-primary-500"
            />
            <DashboardCard
              to="/reservations"
              title="Gestión de reservas"
              description="Crear y gestionar reservas"
              icon="✈️"
              borderColorClass="border-green-500"
              iconColorClass="text-green-500"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default SupervisorDashboard;
