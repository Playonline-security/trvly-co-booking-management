import React, { useState, useEffect } from 'react';
import Sidebar from './Sidebar';
import api from '../services/api';

/**
 * Componente para gestionar paquetes turísticos
 * Permite crear, editar, eliminar y listar paquetes (solo administradores)
 */
const PackagesPage = () => {
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingPackage, setEditingPackage] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    destination: '',
    description: '',
    additionalServices: '',
    basePrice: '',
  });

  // Efecto para cargar paquetes al montar el componente
  useEffect(() => {
    fetchPackages();
  }, []);

  /**
   * Obtiene la lista de paquetes turísticos desde el servidor
   */
  const fetchPackages = async () => {
    try {
      setLoading(true);
      const response = await api.get('/api/admin/packages');
      setPackages(response.data);
    } catch (error) {
      console.error('Error al obtener paquetes:', error);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Maneja el envío del formulario para crear o editar un paquete
   * @param {Event} e - Evento del formulario
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const packageData = {
        ...formData,
        basePrice: parseFloat(formData.basePrice),
      };
      if (editingPackage) {
        await api.put(`/api/admin/packages/${editingPackage.id}`, packageData);
      } else {
        await api.post('/api/admin/packages', packageData);
      }
      setShowModal(false);
      setEditingPackage(null);
      resetForm();
      fetchPackages();
    } catch (error) {
      alert(error.response?.data?.message || 'Error al guardar paquete');
    }
  };

  /**
   * Prepara el formulario para editar un paquete existente
   * @param {Object} pkg - Paquete a editar
   */
  const handleEdit = (pkg) => {
    setEditingPackage(pkg);
    setFormData({
      name: pkg.name,
      destination: pkg.destination,
      description: pkg.description,
      additionalServices: pkg.additionalServices || '',
      basePrice: pkg.basePrice,
    });
    setShowModal(true);
  };

  /**
   * Elimina un paquete después de confirmación
   * @param {number} id - ID del paquete a eliminar
   */
  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar este paquete?')) return;
    try {
      await api.delete(`/api/admin/packages/${id}`);
      fetchPackages();
    } catch (error) {
      alert('Error al eliminar paquete');
    }
  };

  /**
   * Reinicia el formulario a sus valores iniciales
   */
  const resetForm = () => {
    setFormData({
      name: '',
      destination: '',
      description: '',
      additionalServices: '',
      basePrice: '',
    });
    setEditingPackage(null);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <div className="ml-64 p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex justify-between items-center mb-6">
            <h1 className="text-3xl font-bold text-gray-800">Gestión de paquetes turísticos</h1>
            <button
              onClick={() => {
                resetForm();
                setShowModal(true);
              }}
              className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 px-4 rounded-lg transition"
            >
              + Nuevo paquete
            </button>
          </div>

          {loading ? (
            <div className="text-center py-12">Cargando...</div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {packages.map((pkg) => (
                <div key={pkg.id} className="bg-white rounded-lg shadow-md p-6 hover:shadow-xl transition-shadow">
                  <h3 className="text-xl font-bold text-gray-800 mb-2">{pkg.name}</h3>
                  <p className="text-primary-600 font-semibold mb-2">📍 {pkg.destination}</p>
                  <p className="text-gray-600 text-sm mb-4 line-clamp-3">{pkg.description}</p>
                  <p className="text-2xl font-bold text-primary-600 mb-4">
                    ${parseFloat(pkg.basePrice).toLocaleString()}
                  </p>
                  {pkg.additionalServices && (
                    <p className="text-xs text-gray-500 mb-4">
                      <strong>Servicios adicionales:</strong> {pkg.additionalServices}
                    </p>
                  )}
                  <div className="flex space-x-2">
                    <button
                      onClick={() => handleEdit(pkg)}
                      className="flex-1 bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 px-4 rounded-lg transition"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => handleDelete(pkg.id)}
                      className="flex-1 bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded-lg transition"
                    >
                      Eliminar
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {showModal && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
              <div className="bg-white rounded-lg p-8 max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
                <h2 className="text-2xl font-bold mb-6">
                  {editingPackage ? 'Editar paquete' : 'Nuevo paquete'}
                </h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Nombre del paquete
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Destino
                    </label>
                    <input
                      type="text"
                      required
                      value={formData.destination}
                      onChange={(e) => setFormData({ ...formData, destination: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Descripción
                    </label>
                    <textarea
                      required
                      rows="4"
                      value={formData.description}
                      onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Servicios adicionales (opcional)
                    </label>
                    <textarea
                      rows="3"
                      value={formData.additionalServices}
                      onChange={(e) => setFormData({ ...formData, additionalServices: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Precio base
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      min="0"
                      required
                      value={formData.basePrice}
                      onChange={(e) => setFormData({ ...formData, basePrice: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>
                  <div className="flex justify-end space-x-4 pt-4">
                    <button
                      type="button"
                      onClick={() => {
                        setShowModal(false);
                        resetForm();
                      }}
                      className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
                    >
                      Cancelar
                    </button>
                    <button
                      type="submit"
                      className="px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                    >
                      Guardar
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PackagesPage;

