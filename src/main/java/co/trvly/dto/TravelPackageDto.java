package co.trvly.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para transferir datos de paquetes turísticos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelPackageDto {
    private Long id;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200, message = "El nombre no debe exceder 200 caracteres")
    private String name;

    @NotBlank(message = "El destino es requerido")
    @Size(max = 200, message = "El destino no debe exceder 200 caracteres")
    private String destination;

    @NotBlank(message = "La descripción es requerida")
    private String description;

    private String additionalServices;

    @NotNull(message = "El precio base es requerido")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio base debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "El precio base debe tener como máximo 8 dígitos enteros y 2 decimales")
    private BigDecimal basePrice;
}

