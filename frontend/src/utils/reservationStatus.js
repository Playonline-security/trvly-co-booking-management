/**
 * Utilidades de estado de reserva — RF-01 (refactorización)
 */
const STATUS_LABELS = {
  PENDING_PAYMENT: 'Pendiente de pago',
  PARTIAL_PAYMENT: 'Pago parcial',
  FULL_PAYMENT: 'Pago completo',
  CANCELLED: 'Cancelada',
};

const STATUS_COLORS = {
  FULL_PAYMENT: 'bg-green-100 text-green-800',
  PARTIAL_PAYMENT: 'bg-yellow-100 text-yellow-800',
  CANCELLED: 'bg-red-100 text-red-800',
  PENDING_PAYMENT: 'bg-gray-100 text-gray-800',
};

export const getStatusLabel = (status) => STATUS_LABELS[status] || status;

export const getStatusColor = (status) =>
  STATUS_COLORS[status] || 'bg-gray-100 text-gray-800';
