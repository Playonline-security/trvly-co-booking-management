/**
 * Mensajes de error desde respuestas Axios — RF-05 (refactorización)
 */
export const getApiErrorMessage = (error, defaultMessage = 'Ocurrió un error inesperado') => {
  const msg = error?.response?.data?.message;
  return typeof msg === 'string' && msg.length > 0 ? msg : defaultMessage;
};
