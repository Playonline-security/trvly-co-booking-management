package co.trvly.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa un cliente de la agencia de viajes
 */
@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_names", nullable = false, length = 200)
    private String firstNames;

    @Column(name = "last_names", nullable = false, length = 200)
    private String lastNames;

    @Column(name = "document_id", nullable = false, unique = true, length = 50)
    private String documentId;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "mobile_phone", nullable = false, length = 20)
    private String mobilePhone;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

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

