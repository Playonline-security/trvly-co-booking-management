package co.trvly.controller;

import co.trvly.dto.QuotationResponse;
import co.trvly.dto.ReservationDto;
import co.trvly.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar reservas
 */
@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    /**
     * Obtiene todas las reservas
     * @return Lista de reservas
     */
    @GetMapping
    @PreAuthorize("hasAuthority('reservation_management_r')")
    public ResponseEntity<List<ReservationDto>> getAllReservations(
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(reservationService.getAllReservations(search));
    }

    /**
     * Obtiene una reserva por su ID
     * @param id ID de la reserva
     * @return Reserva encontrada
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('reservation_management_r')")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    /**
     * Calcula la cotización de una reserva
     * @param packageId ID del paquete turístico
     * @param passengerCount Cantidad de pasajeros
     * @return Respuesta con la cotización calculada
     */
    @GetMapping("/quotation")
    @PreAuthorize("hasAuthority('reservation_management_r')")
    public ResponseEntity<QuotationResponse> calculateQuotation(
            @RequestParam Long packageId,
            @RequestParam Integer passengerCount) {
        return ResponseEntity.ok(reservationService.calculateQuotation(packageId, passengerCount));
    }

    /**
     * Crea una nueva reserva
     * @param reservationDto Datos de la reserva
     * @return Reserva creada
     */
    @PostMapping
    @PreAuthorize("hasAuthority('reservation_management_w')")
    public ResponseEntity<ReservationDto> createReservation(@Valid @RequestBody ReservationDto reservationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(reservationDto));
    }

    /**
     * Actualiza el estado de una reserva
     * @param id ID de la reserva
     * @param status Nuevo estado
     * @return Reserva actualizada
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('reservation_management_w')")
    public ResponseEntity<ReservationDto> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }

    /**
     * Actualiza la cantidad de pasajeros de una reserva
     * @param id ID de la reserva
     * @param passengerCount Nueva cantidad de pasajeros
     * @return Reserva actualizada
     */
    @PutMapping("/{id}/passengers")
    @PreAuthorize("hasAuthority('reservation_management_w')")
    public ResponseEntity<ReservationDto> updatePassengerCount(
            @PathVariable Long id,
            @RequestParam Integer passengerCount) {
        return ResponseEntity.ok(reservationService.updatePassengerCount(id, passengerCount));
    }

    /**
     * Elimina una reserva
     * @param id ID de la reserva a eliminar
     * @return Respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('reservation_management_d')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}

