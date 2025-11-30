package co.trvly.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para transferir datos de usuarios
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El correo electrónico es requerido")
    @Email(message = "El correo electrónico debe ser válido")
    @Size(max = 100, message = "El correo electrónico no debe exceder 100 caracteres")
    private String email;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no debe exceder 100 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    @Size(max = 100, message = "El apellido no debe exceder 100 caracteres")
    private String lastName;

    private Boolean enabled = true;
    private Set<String> roles;
}

