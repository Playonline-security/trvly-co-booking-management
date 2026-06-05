import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import Sidebar from './Sidebar';
import api from '../services/api';
import { getStatusLabel, getStatusColor } from '../utils/reservationStatus'; // [RF-01]
import { canDeleteRecords, canEditReservations } from '../utils/permissions'; // [RF-02]

// status helpers moved to utils/reservationStatus.js
/**
 * Componente principal para gestionar reservas
 * Permite crear, editar, eliminar y buscar reservas
 */
const ReservationsPage = () => {
  const { user } = useAuth();
  
  // Estados para gestionar las reservas
  const [reservations, setReservations] = useState([]);
  const [clients, setClients] = useState([]);
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Estados para modales y formularios
  const [showModal, setShowModal] = useState(false);
  const [showQuotation, setShowQuotation] = useState(false);
  const [quotation, setQuotation] = useState(null);
  const [selectedClient, setSelectedClient] = useState(null);
  const [clientSearch, setClientSearch] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  
  // Estados para edición de cantidad de pasajeros
  const [editingPassengerCount, setEditingPassengerCount] = useState(null);
  const [newPassengerCount, setNewPassengerCount] = useState('');
  
  // Datos del formulario de nueva reserva
  const [formData, setFormData] = useState({
    clientId: '',
    packageId: '',
    passengerCount: '',
    passengers: [],
  });

  // Verificar permisos del usuario
  const canDelete = canDeleteRecords(user); // [RF-02]
  const canEdit = canEditReservations(user); // [RF-02]

  // Efecto para cargar reservas y paquetes al montar el componente
  useEffect(() => {
    fetchReservations();
    fetchPackages();
  }, []);

  // Efecto para buscar clientes cuando cambia el término de búsqueda
  useEffect(() => {
    if (clientSearch) {
      searchClients();
    } else {
      setClients([]);
    }
  }, [clientSearch]);

  /**
   * Filtrar reservas localmente mientras se busca
   * Busca en número de reserva, nombre del cliente, nombre y destino del paquete
   */
  const filteredReservations = searchTerm
    ? reservations.filter(r => 
        r.reservationNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        `${r.client?.firstNames} ${r.client?.lastNames}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
        r.travelPackage?.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        r.travelPackage?.destination?.toLowerCase().includes(searchTerm.toLowerCase())
      )
    : reservations;

  /**
   * Obtiene todas las reservas desde el servidor
   */
  const fetchReservations = async () => {
    try {
      setLoading(true);
      const response = await api.get('/api/reservations', {
        params: searchTerm ? { search: searchTerm } : {}
      });
      setReservations(response.data);
    } catch (error) {
      console.error('Error al obtener reservas:', error);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Obtiene todos los paquetes turísticos disponibles
   */
  const fetchPackages = async () => {
    try {
      const response = await api.get('/api/admin/packages');
      setPackages(response.data);
    } catch (error) {
      console.error('Error al obtener paquetes:', error);
    }
  };

  /**
   * Busca clientes por término de búsqueda
   */
  const searchClients = async () => {
    try {
      const response = await api.get('/api/clients', {
        params: { search: clientSearch },
      });
      setClients(response.data);
    } catch (error) {
      console.error('Error al buscar clientes:', error);
    }
  };

  /**
   * Calcula la cotización de una reserva basada en el paquete y cantidad de pasajeros
   */
  const calculateQuotation = async () => {
    if (!formData.packageId || !formData.passengerCount || formData.passengerCount < 1) {
      alert('Seleccione un paquete e ingrese la cantidad de pasajeros (mínimo 1)');
      return;
    }
    try {
      const response = await api.get('/api/reservations/quotation', {
        params: {
          packageId: formData.packageId,
          passengerCount: parseInt(formData.passengerCount),
        },
      });
      setQuotation(response.data);
      setShowQuotation(true);
    } catch (error) {
      console.error('Error al calcular cotización:', error);
      alert('Error al calcular cotización: ' + (error.response?.data?.message || 'Error desconocido'));
    }
  };

  /**
   * Maneja el envío del formulario para crear una nueva reserva
   * @param {Event} e - Evento del formulario
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    // Validaciones
    if (!selectedClient) {
      alert('Seleccione un cliente');
      return;
    }
    if (!formData.packageId) {
      alert('Seleccione un paquete turístico');
      return;
    }
    if (!formData.passengerCount || formData.passengerCount < 1) {
      alert('Ingrese una cantidad válida de pasajeros (mínimo 1)');
      return;
    }
    if (formData.passengers.length !== parseInt(formData.passengerCount)) {
      alert('Complete la información de todos los pasajeros');
      return;
    }
    try {
      const reservationData = {
        ...formData,
        clientId: selectedClient.id,
        packageId: parseInt(formData.packageId),
        passengerCount: parseInt(formData.passengerCount),
        status: 'PENDING_PAYMENT',
      };
      await api.post('/api/reservations', reservationData);
      setShowModal(false);
      resetForm();
      fetchReservations();
      alert('Reserva creada exitosamente');
    } catch (error) {
      alert(error.response?.data?.message || 'Error al crear reserva');
    }
  };

  /**
   * Elimina una reserva después de confirmación
   * @param {number} id - ID de la reserva a eliminar
   */
  const handleDelete = async (id) => {
    if (!window.confirm('¿Está seguro de eliminar esta reserva?')) return;
    try {
      await api.delete(`/api/reservations/${id}`);
      fetchReservations();
      alert('Reserva eliminada exitosamente');
    } catch (error) {
      alert('Error al eliminar reserva');
    }
  };

  /**
   * Actualiza el estado de una reserva
   * @param {number} id - ID de la reserva
   * @param {string} status - Nuevo estado
   */
  const updateStatus = async (id, status) => {
    try {
      await api.put(`/api/reservations/${id}/status`, null, {
        params: { status },
      });
      fetchReservations();
    } catch (error) {
      alert('Error al actualizar estado');
    }
  };

  /**
   * Actualiza la cantidad de pasajeros de una reserva existente
   * @param {number} id - ID de la reserva
   */
  const updatePassengerCount = async (id) => {
    if (!newPassengerCount || parseInt(newPassengerCount) < 1) {
      alert('Ingrese una cantidad válida de pasajeros (mínimo 1)');
      return;
    }
    try {
      await api.put(`/api/reservations/${id}/passengers`, null, {
        params: { passengerCount: parseInt(newPassengerCount) },
      });
      setEditingPassengerCount(null);
      setNewPassengerCount('');
      fetchReservations();
      alert('Cantidad de pasajeros actualizada exitosamente');
    } catch (error) {
      alert('Error al actualizar cantidad de pasajeros: ' + (error.response?.data?.message || ''));
    }
  };

  /**
   * Reinicia el formulario a sus valores iniciales
   */
  const resetForm = () => {
    setFormData({
      clientId: '',
      packageId: '',
      passengerCount: '',
      passengers: [],
    });
    setSelectedClient(null);
    setClientSearch('');
    setQuotation(null);
    setShowQuotation(false);
  };

  /**
   * Actualiza la cantidad de pasajeros en el formulario y ajusta la lista de pasajeros
   * @param {string} count - Nueva cantidad de pasajeros
   */
  const updatePassengerCountInForm = (count) => {
    const numCount = count === '' ? '' : Math.max(1, parseInt(count) || 1);
    const currentPassengers = formData.passengers || [];
    const newPassengers = [];
    
    const targetCount = numCount === '' ? 0 : numCount;
    for (let i = 0; i < targetCount; i++) {
      newPassengers.push(currentPassengers[i] || { firstName: '', lastName: '', documentId: '', birthDate: '' });
    }
    
    setFormData({ ...formData, passengerCount: numCount, passengers: newPassengers });
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <div className="ml-64 p-4 md:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-6 gap-4">
            <h1 className="text-2xl md:text-3xl font-bold text-gray-800">Gestión de reservas</h1>
            <div className="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
              <input
                type="text"
                placeholder="Buscar reservas..."
                value={searchTerm}
                onChange={(e) => {
                  setSearchTerm(e.target.value);
                  // Opcional: buscar en el servidor con debounce
                }}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 w-full md:w-64"
              />
              <button
                onClick={() => {
                  resetForm();
                  setShowModal(true);
                }}
                className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 px-4 rounded-lg transition whitespace-nowrap"
              >
                + Nueva reserva
              </button>
            </div>
          </div>

          {loading ? (
            <div className="text-center py-12">Cargando...</div>
          ) : (
            <div className="bg-white rounded-lg shadow-md overflow-hidden overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                      Número
                    </th>
                    <th className="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                      Cliente
                    </th>
                    <th className="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                      Paquete
                    </th>
                    <th className="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                      Pasajeros
                    </th>
                    <th className="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                      Total
                    </th>
                    <th className="px-4 md:px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                      Estado / Acciones
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {filteredReservations.length === 0 ? (
                    <tr>
                      <td colSpan="6" className="px-6 py-4 text-center text-gray-500">
                        No se encontraron reservas
                      </td>
                    </tr>
                  ) : (
                    filteredReservations.map((reservation) => (
                      <tr key={reservation.id} className="hover:bg-gray-50">
                        <td className="px-4 md:px-6 py-4 whitespace-nowrap font-mono text-sm">
                          {reservation.reservationNumber}
                        </td>
                        <td className="px-4 md:px-6 py-4 whitespace-nowrap text-sm">
                          {reservation.client?.firstNames} {reservation.client?.lastNames}
                        </td>
                        <td className="px-4 md:px-6 py-4 whitespace-nowrap text-sm">
                          {reservation.travelPackage?.name}
                        </td>
                        <td className="px-4 md:px-6 py-4 whitespace-nowrap text-center text-sm">
                          {editingPassengerCount === reservation.id ? (
                            <div className="flex items-center gap-2">
                              <input
                                type="number"
                                min="1"
                                value={newPassengerCount}
                                onChange={(e) => setNewPassengerCount(e.target.value)}
                                className="w-20 px-2 py-1 border border-gray-300 rounded text-sm"
                                onKeyPress={(e) => {
                                  if (e.key === 'Enter') {
                                    updatePassengerCount(reservation.id);
                                  }
                                }}
                              />
                              <button
                                onClick={() => updatePassengerCount(reservation.id)}
                                className="text-green-600 hover:text-green-800 text-sm"
                              >
                                ✓
                              </button>
                              <button
                                onClick={() => {
                                  setEditingPassengerCount(null);
                                  setNewPassengerCount('');
                                }}
                                className="text-red-600 hover:text-red-800 text-sm"
                              >
                                ✕
                              </button>
                            </div>
                          ) : (
                            <div className="flex items-center gap-2">
                              <span>{reservation.passengerCount}</span>
                              {canEdit && (
                                <button
                                  onClick={() => {
                                    setEditingPassengerCount(reservation.id);
                                    setNewPassengerCount(reservation.passengerCount.toString());
                                  }}
                                  className="text-blue-600 hover:text-blue-800 text-xs"
                                  title="Editar cantidad"
                                >
                                  ✏️
                                </button>
                              )}
                            </div>
                          )}
                        </td>
                        <td className="px-4 md:px-6 py-4 whitespace-nowrap text-sm">
                          ${reservation.totalPrice?.toLocaleString()}
                        </td>
                        <td className="px-4 md:px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <div className="flex flex-col gap-2">
                            {/* Selector de estado - clickeable para cambiar el estado */}
                            <select
                              value={reservation.status}
                              onChange={(e) => updateStatus(reservation.id, e.target.value)}
                              className="text-xs sm:text-sm border border-gray-300 rounded px-2 py-1 cursor-pointer hover:bg-gray-50"
                              title="Haga clic para cambiar el estado"
                            >
                              <option value="PENDING_PAYMENT">Pendiente de pago</option>
                              <option value="PARTIAL_PAYMENT">Pago parcial</option>
                              <option value="FULL_PAYMENT">Pago completo</option>
                              <option value="CANCELLED">Cancelada</option>
                            </select>
                            {/* Botón de eliminar solo para administradores y supervisores */}
                            {canDelete && (
                              <button
                                onClick={() => handleDelete(reservation.id)}
                                className="text-red-600 hover:text-red-900 text-xs sm:text-sm text-left"
                              >
                                Eliminar
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}

          {showModal && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 overflow-y-auto p-4">
              <div className="bg-white rounded-lg p-4 md:p-8 max-w-4xl w-full mx-4 my-8 max-h-[90vh] overflow-y-auto">
                <h2 className="text-xl md:text-2xl font-bold mb-4 md:mb-6">Nueva reserva</h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Buscar cliente
                    </label>
                    <input
                      type="text"
                      value={clientSearch}
                      onChange={(e) => setClientSearch(e.target.value)}
                      placeholder="Buscar por nombre, documento, teléfono o email..."
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                    {clientSearch && clients.length > 0 && (
                      <div className="mt-2 border border-gray-200 rounded-lg max-h-40 overflow-y-auto">
                        {clients.map((client) => (
                          <div
                            key={client.id}
                            onClick={() => {
                              setSelectedClient(client);
                              setClientSearch(`${client.firstNames} ${client.lastNames}`);
                              setClients([]);
                            }}
                            className="p-3 hover:bg-gray-50 cursor-pointer border-b border-gray-100"
                          >
                            {client.firstNames} {client.lastNames} - {client.documentId}
                          </div>
                        ))}
                      </div>
                    )}
                    {selectedClient && (
                      <p className="mt-2 text-sm text-green-600">
                        Cliente seleccionado: {selectedClient.firstNames} {selectedClient.lastNames}
                      </p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Paquete turístico
                    </label>
                    <select
                      required
                      value={formData.packageId}
                      onChange={(e) => setFormData({ ...formData, packageId: e.target.value })}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    >
                      <option value="">Seleccione un paquete</option>
                      {packages.map((pkg) => (
                        <option key={pkg.id} value={pkg.id}>
                          {pkg.name} - {pkg.destination} (${pkg.basePrice?.toLocaleString()})
                        </option>
                      ))}
                    </select>
                    {packages.length === 0 && (
                      <p className="mt-1 text-sm text-yellow-600">
                        No hay paquetes disponibles. Contacte al administrador.
                      </p>
                    )}
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Cantidad de pasajeros
                    </label>
                    <input
                      type="number"
                      min="1"
                      required
                      value={formData.passengerCount}
                      onChange={(e) => updatePassengerCountInForm(e.target.value)}
                      placeholder="Ingrese la cantidad"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                    />
                  </div>

                  {formData.passengerCount && parseInt(formData.passengerCount) > 0 && (
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Información de pasajeros
                      </label>
                      <div className="space-y-3 max-h-96 overflow-y-auto">
                        {formData.passengers.map((passenger, index) => (
                          <div key={index} className="p-3 md:p-4 border border-gray-200 rounded-lg bg-gray-50">
                            <h4 className="font-semibold mb-2 text-sm md:text-base">Pasajero {index + 1}</h4>
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                              <div>
                                <label className="block text-xs text-gray-600 mb-1">Nombre</label>
                                <input
                                  type="text"
                                  required
                                  value={passenger.firstName}
                                  onChange={(e) => {
                                    const newPassengers = [...formData.passengers];
                                    newPassengers[index].firstName = e.target.value;
                                    setFormData({ ...formData, passengers: newPassengers });
                                  }}
                                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
                                />
                              </div>
                              <div>
                                <label className="block text-xs text-gray-600 mb-1">Apellido</label>
                                <input
                                  type="text"
                                  required
                                  value={passenger.lastName}
                                  onChange={(e) => {
                                    const newPassengers = [...formData.passengers];
                                    newPassengers[index].lastName = e.target.value;
                                    setFormData({ ...formData, passengers: newPassengers });
                                  }}
                                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
                                />
                              </div>
                              <div>
                                <label className="block text-xs text-gray-600 mb-1">Documento</label>
                                <input
                                  type="text"
                                  required
                                  value={passenger.documentId}
                                  onChange={(e) => {
                                    const newPassengers = [...formData.passengers];
                                    newPassengers[index].documentId = e.target.value;
                                    setFormData({ ...formData, passengers: newPassengers });
                                  }}
                                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
                                />
                              </div>
                              <div>
                                <label className="block text-xs text-gray-600 mb-1">Fecha nacimiento</label>
                                <input
                                  type="date"
                                  required
                                  value={passenger.birthDate}
                                  onChange={(e) => {
                                    const newPassengers = [...formData.passengers];
                                    newPassengers[index].birthDate = e.target.value;
                                    setFormData({ ...formData, passengers: newPassengers });
                                  }}
                                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm"
                                />
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  <div className="flex flex-col sm:flex-row justify-between pt-4 gap-2">
                    <button
                      type="button"
                      onClick={calculateQuotation}
                      className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
                    >
                      Calcular cotización
                    </button>
                    <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-4">
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
                        Crear reserva
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          )}

          {showQuotation && quotation && (
            <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
              <div className="bg-white rounded-lg p-6 md:p-8 max-w-md w-full mx-4">
                <h3 className="text-xl font-bold mb-4">Cotización</h3>
                <div className="space-y-2 mb-4">
                  <p><strong>Paquete:</strong> {quotation.travelPackage?.name}</p>
                  <p><strong>Destino:</strong> {quotation.travelPackage?.destination}</p>
                  <p><strong>Pasajeros:</strong> {quotation.passengerCount}</p>
                  <p><strong>Precio base:</strong> ${quotation.basePrice?.toLocaleString()}</p>
                  <p className="text-xl font-bold text-primary-600">
                    <strong>Total:</strong> ${quotation.totalPrice?.toLocaleString()}
                  </p>
                </div>
                <button
                  onClick={() => setShowQuotation(false)}
                  className="w-full px-6 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700"
                >
                  Cerrar
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ReservationsPage;
