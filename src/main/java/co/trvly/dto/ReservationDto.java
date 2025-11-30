package co.trvly.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para transferir datos de reservas
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private Long id;
    private String reservationNumber;

    @NotNull(message = "El ID del cliente es requerido")
    private Long clientId;

    @NotNull(message = "El ID del paquete es requerido")
    private Long packageId;

    private LocalDateTime reservationDate;

    @NotNull(message = "La cantidad de pasajeros es requerida")
    @Min(value = 1, message = "La cantidad de pasajeros debe ser al menos 1")
    private Integer passengerCount;

    private String status;

    private BigDecimal totalPrice;

    @NotEmpty(message = "Se requiere al menos un pasajero")
    @Size(min = 1, message = "Se requiere al menos un pasajero")
    private List<PassengerDto> passengers = new ArrayList<>();

    // Para respuesta
    private ClientDto client;
    private TravelPackageDto travelPackage;
}

