package co.trvly.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para transferir datos de pasajeros
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDto {
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200, message = "El nombre no debe exceder 200 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    @Size(max = 200, message = "El apellido no debe exceder 200 caracteres")
    private String lastName;

    @NotBlank(message = "El documento de identidad es requerido")
    @Size(max = 50, message = "El documento de identidad no debe exceder 50 caracteres")
    private String documentId;

    @NotNull(message = "La fecha de nacimiento es requerida")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate birthDate;
}

