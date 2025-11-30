package co.trvly.repository;

import co.trvly.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar reservas
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    /**
     * Busca una reserva por su número de reserva
     * @param reservationNumber Número de reserva
     * @return Reserva encontrada
     */
    Optional<Reservation> findByReservationNumber(String reservationNumber);
    
    /**
     * Verifica si existe una reserva con el número dado
     * @param reservationNumber Número de reserva
     * @return true si existe, false en caso contrario
     */
    boolean existsByReservationNumber(String reservationNumber);
    
    /**
     * Busca reservas por término de búsqueda
     * Busca en número de reserva, nombre del cliente, nombre del paquete
     * @param search Término de búsqueda
     * @return Lista de reservas encontradas
     */
    @Query("SELECT r FROM Reservation r WHERE " +
           "LOWER(r.reservationNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.client.firstNames) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.client.lastNames) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.travelPackage.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.travelPackage.destination) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Reservation> searchReservations(@Param("search") String search);
}

