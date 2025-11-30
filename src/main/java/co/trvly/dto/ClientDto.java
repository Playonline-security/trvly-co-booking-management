package co.trvly.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para transferir datos de clientes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private Long id;

    @NotBlank(message = "Los nombres son requeridos")
    @Size(max = 200, message = "Los nombres no deben exceder 200 caracteres")
    private String firstNames;

    @NotBlank(message = "Los apellidos son requeridos")
    @Size(max = 200, message = "Los apellidos no deben exceder 200 caracteres")
    private String lastNames;

    @NotBlank(message = "El documento de identidad es requerido")
    @Size(max = 50, message = "El documento de identidad no debe exceder 50 caracteres")
    private String documentId;

    @NotNull(message = "La fecha de nacimiento es requerida")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate birthDate;

    @NotBlank(message = "El teléfono móvil es requerido")
    @Size(max = 20, message = "El teléfono móvil no debe exceder 20 caracteres")
    private String mobilePhone;

    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "El correo electrónico debe ser válido")
    @Size(max = 100, message = "El correo electrónico no debe exceder 100 caracteres")
    private String email;
}

