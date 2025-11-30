import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import AdvisorDashboard from './components/AdvisorDashboard';
import SupervisorDashboard from './components/SupervisorDashboard';
import AdminDashboard from './components/AdminDashboard';
import ClientsPage from './components/ClientsPage';
import ReservationsPage from './components/ReservationsPage';
import PackagesPage from './components/PackagesPage';
import UsersPage from './components/UsersPage';
import { AuthProvider, useAuth } from './context/AuthContext';
import './App.css';

/**
 * Componente de ruta privada que requiere autenticación
 * Opcionalmente puede requerir un rol específico
 * @param {Object} props - Propiedades del componente
 * @param {ReactNode} props.children - Componentes hijos a renderizar
 * @param {string} props.requiredRole - Rol requerido (opcional)
 */
const PrivateRoute = ({ children, requiredRole }) => {
  const { user, isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  if (requiredRole && !user?.roles?.includes(requiredRole)) {
    return <Navigate to="/" />;
  }

  return children;
};

/**
 * Componente que define las rutas de la aplicación
 * Maneja el enrutamiento según el estado de autenticación y roles del usuario
 */
const AppRoutes = () => {
  const { user, isAuthenticated } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={!isAuthenticated ? <Login /> : <Navigate to="/" />} />
      <Route
        path="/"
        element={
          <PrivateRoute>
            {user?.roles?.includes('ADMIN') ? (
              <AdminDashboard />
            ) : user?.roles?.includes('SUPERVISOR') ? (
              <SupervisorDashboard />
            ) : (
              <AdvisorDashboard />
            )}
          </PrivateRoute>
        }
      />
      <Route
        path="/clients"
        element={
          <PrivateRoute>
            <ClientsPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/reservations"
        element={
          <PrivateRoute>
            <ReservationsPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/packages"
        element={
          <PrivateRoute requiredRole="ADMIN">
            <PackagesPage />
          </PrivateRoute>
        }
      />
      <Route
        path="/users"
        element={
          <PrivateRoute requiredRole="ADMIN">
            <UsersPage />
          </PrivateRoute>
        }
      />
    </Routes>
  );
};

/**
 * Componente principal de la aplicación
 * Configura el proveedor de autenticación y el enrutador
 */
function App() {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;

