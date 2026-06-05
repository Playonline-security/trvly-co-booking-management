import React from 'react';
import { Link } from 'react-router-dom';

/**
 * Tarjeta de navegación en dashboards — RF-03 (refactorización)
 */
const DashboardCard = ({ to, title, description, icon, borderColorClass, iconColorClass }) => (
  <Link
    to={to}
    className={`bg-white rounded-xl shadow-md hover:shadow-xl transition-shadow p-6 border-l-4 ${borderColorClass}`}
  >
    <div className="flex items-center justify-between">
      <div>
        <h3 className="text-lg font-semibold text-gray-800">{title}</h3>
        <p className="text-gray-600 text-sm mt-1">{description}</p>
      </div>
      <div className={`text-3xl ${iconColorClass}`}>{icon}</div>
    </div>
  </Link>
);

export default DashboardCard;
