package co.trvly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una reserva de viaje
 */
@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_number", nullable = false, unique = true, length = 50)
    private String reservationNumber;

    // Relación muchos a uno con cliente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Relación muchos a uno con paquete turístico
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_id", nullable = false)
    private TravelPackage travelPackage;

    @Column(name = "reservation_date", nullable = false)
    private LocalDateTime reservationDate;

    @Column(name = "passenger_count", nullable = false)
    private Integer passengerCount;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING_PAYMENT;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    // Relación uno a muchos con pasajeros
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passenger> passengers = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método que se ejecuta antes de persistir la entidad
     * Establece las fechas de creación, actualización y reserva
     */
    @PrePersist
    protected void onCreate() {
        reservationDate = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Método que se ejecuta antes de actualizar la entidad
     * Actualiza la fecha de modificación
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Enum que representa los estados posibles de una reserva
     */
    public enum ReservationStatus {
        PENDING_PAYMENT,    // Pendiente de pago
        PARTIAL_PAYMENT,   // Pago parcial
        FULL_PAYMENT,      // Pago completo
        CANCELLED          // Cancelada
    }
}

