package co.trvly.controller;

import co.trvly.dto.UserDto;
import co.trvly.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar usuarios (solo administradores)
 */
@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasAuthority('user_management')")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * Obtiene todos los usuarios
     * @return Lista de usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Crea un nuevo usuario
     * @param userDto Datos del usuario
     * @return Usuario creado
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }

    /**
     * Actualiza un usuario existente
     * @param id ID del usuario
     * @param userDto Datos actualizados del usuario
     * @return Usuario actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    /**
     * Elimina un usuario
     * @param id ID del usuario a eliminar
     * @return Respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

