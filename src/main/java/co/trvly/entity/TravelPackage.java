package co.trvly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un paquete turístico
 */
@Entity
@Table(name = "travel_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 200)
    private String destination;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "additional_services", columnDefinition = "TEXT")
    private String additionalServices;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método que se ejecuta antes de persistir la entidad
     * Establece las fechas de creación y actualización
     */
    @PrePersist
    protected void onCreate() {
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
}

