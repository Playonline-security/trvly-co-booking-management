import React, {useState, useEffect} from 'react';
import {useAuth} from '../context/AuthContext';
import Sidebar from './Sidebar';
import api from '../services/api';

/**
 * Componente para gestionar clientes
 * Permite crear, editar, eliminar y buscar clientes
 */
const ClientsPage = () => {
    const {user} = useAuth();
    const [clients, setClients] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [editingClient, setEditingClient] = useState(null);
    const [formData, setFormData] = useState({
        firstNames: '',
        lastNames: '',
        documentId: '',
        birthDate: '',
        mobilePhone: '',
        email: '',
    });

    // Verificar si el usuario tiene permisos para eliminar clientes
    const canDelete = user?.roles?.includes('ADMIN') || user?.roles?.includes('SUPERVISOR');

    // Efecto para cargar clientes cuando cambia el término de búsqueda
    useEffect(() => {
        fetchClients();
    }, [searchTerm]);

    /**
     * Obtiene la lista de clientes desde el servidor
     */
    const fetchClients = async () => {
        try {
            setLoading(true);
            const response = await api.get('/api/clients', {
                params: searchTerm ? {search: searchTerm} : {},
            });
            setClients(response.data);
        } catch (error) {
            console.error('Error al obtener clientes:', error);
        } finally {
            setLoading(false);
        }
    };

    /**
     * Maneja el envío del formulario para crear o editar un cliente
     * @param {Event} e - Evento del formulario
     */
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingClient) {
                // Actualizar cliente existente
                await api.put(`/api/clients/${editingClient.id}`, formData);
            } else {
                // Crear nuevo cliente
                await api.post('/api/clients', formData);
            }
            setShowModal(false);
            setEditingClient(null);
            resetForm();
            fetchClients();
        } catch (error) {
            // Mostrar mensaje de error del servidor o mensaje genérico
            alert(error.response?.data?.message || 'Error al guardar cliente');
        }
    };

    /**
     * Prepara el formulario para editar un cliente existente
     * @param {Object} client - Cliente a editar
     */
    const handleEdit = (client) => {
        setEditingClient(client);
        setFormData({
            firstNames: client.firstNames,
            lastNames: client.lastNames,
            documentId: client.documentId,
            birthDate: client.birthDate,
            mobilePhone: client.mobilePhone,
            email: client.email,
        });
        setShowModal(true);
    };

    /**
     * Elimina un cliente después de confirmación
     * @param {number} id - ID del cliente a eliminar
     */
    const handleDelete = async (id) => {
        if (!window.confirm('¿Está seguro de eliminar este cliente?')) return;
        try {
            await api.delete(`/api/clients/${id}`);
            fetchClients();
        } catch (error) {
            alert('Error al eliminar cliente');
        }
    };

    /**
     * Reinicia el formulario a sus valores iniciales
     */
    const resetForm = () => {
        setFormData({
            firstNames: '',
            lastNames: '',
            documentId: '',
            birthDate: '',
            mobilePhone: '',
            email: '',
        });
        setEditingClient(null);
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <Sidebar/>
            <div className="ml-64 p-8">
                <div className="max-w-7xl mx-auto">
                    <div className="flex justify-between items-center mb-6">
                        <h1 className="text-3xl font-bold text-gray-800">Gestión de clientes</h1>
                        <button
                            onClick={() => {
                                resetForm();
                                setShowModal(true);
                            }}
                            className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 px-4 rounded-lg transition"
                        >
                            + Nuevo cliente
                        </button>
                    </div>

                    <div className="bg-white rounded-lg shadow-md p-6 mb-6">
                        <input
                            type="text"
                            placeholder="Buscar por nombre, documento, teléfono o email..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent outline-none"
                        />
                    </div>

                    {loading ? (
                        <div className="text-center py-12">Cargando...</div>
                    ) : (
                        <div className="bg-white rounded-lg shadow-md p-12 text-center">
                            <p className="text-gray-500 text-lg mb-2">
                                {searchTerm
                                    ? 'No se encontraron clientes para la búsqueda realizada.'
                                    : 'Aún no hay clientes registrados en el sistema.'}
                            </p>
                            <p className="text-gray-400 text-sm mb-6">
                                {searchTerm
                                    ? 'Intente con otro nombre, documento, teléfono o correo.'
                                    : 'Puede crear el primer cliente con el botón Nuevo cliente.'}
                            </p>
                            {!searchTerm && (
                                <button
                                    type="button"
                                    onClick={() => {
                                        resetForm();
                                        setShowModal(true);
                                    }}
                                    className="bg-primary-600 hover:bg-primary-700 text-white font-semibold py-2 px-4 rounded-lg transition"
                                >
                                    + Nuevo cliente
                                </button>
                            )}
                        </div>
                        <div className="bg-white rounded-lg shadow-md overflow-hidden">
                        <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                        <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Nombre completo
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Documento
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Teléfono
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Email
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Acciones
                        </th>
                        </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                    {clients.map((client) => (
                        <tr key={client.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                        {client.firstNames} {client.lastNames}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">{client.documentId}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{client.mobilePhone}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{client.email}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button
                            onClick={() => handleEdit(client)}
                            className="text-primary-600 hover:text-primary-900 mr-4"
                        >
                            Editar
                        </button>
                        {canDelete && (
                            <button
                                onClick={() => handleDelete(client.id)}
                                className="text-red-600 hover:text-red-900"
                            >
                                Eliminar
                            </button>
                        )}
                    </td>
                </tr>
                ))}
            </tbody>
        </table>
</div>
)}

{
    showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-8 max-w-2xl w-full mx-4">
                <h2 className="text-2xl font-bold mb-6">
                    {editingClient ? 'Editar cliente' : 'Nuevo cliente'}
                </h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Nombres
                            </label>
                            <input
                                type="text"
                                required
                                value={formData.firstNames}
                                onChange={(e) => setFormData({...formData, firstNames: e.target.value})}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Apellidos
                            </label>
                            <input
                                type="text"
                                required
                                value={formData.lastNames}
                                onChange={(e) => setFormData({...formData, lastNames: e.target.value})}
                                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                            />
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Documento de identidad
                        </label>
                        <input
                            type="text"
                            required
                            value={formData.documentId}
                            onChange={(e) => setFormData({...formData, documentId: e.target.value})}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Fecha de nacimiento
                        </label>
                        <input
                            type="date"
                            required
                            value={formData.birthDate}
                            onChange={(e) => setFormData({...formData, birthDate: e.target.value})}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Teléfono móvil
                        </label>
                        <input
                            type="tel"
                            required
                            value={formData.mobilePhone}
                            onChange={(e) => setFormData({...formData, mobilePhone: e.target.value})}
                            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                        <input
                            type="email"
                            required
                            value={formData.email}
                            onChange={(e) => setFormData({...formData, email: e.target.value})}
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
    )
}
</div>
</div>
</div>
)
    ;
};

export default ClientsPage;

