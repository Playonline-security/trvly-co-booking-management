/**
 * Permisos por rol en UI — RF-02 (refactorización)
 */
export const canDeleteRecords = (user) =>
  user?.roles?.includes('ADMIN') || user?.roles?.includes('SUPERVISOR');

export const canEditReservations = (user) =>
  user?.roles?.includes('ADMIN') ||
  user?.roles?.includes('SUPERVISOR') ||
  user?.roles?.includes('ADVISOR');
