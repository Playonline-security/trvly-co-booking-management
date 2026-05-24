package co.trvly.controller;

import co.trvly.dto.ClientDto;
import co.trvly.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar clientes
 */
@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientController {
    @Autowired
    private ClientService clientService;

    /**
     * Obtiene todos los clientes, opcionalmente filtrados por búsqueda
     * HU-03: normaliza espacios en el parámetro search
     * @param search Término de búsqueda opcional
     * @return Lista de clientes
     */
    @GetMapping
    @PreAuthorize("hasAuthority('client_management_r')")
    public ResponseEntity<List<ClientDto>> getAllClients(
            @RequestParam(required = false) String search) {
        if (search != null) {
            search = search.trim();
        }
        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(clientService.searchClients(search));
        }
        return ResponseEntity.ok(clientService.getAllClients());
    }


    /**
     * Obtiene un cliente por su ID
     * @param id ID del cliente
     * @return Cliente encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('client_management_r')")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    /**
     * Crea un nuevo cliente
     * @param clientDto Datos del cliente
     * @return Cliente creado
     */
    @PostMapping
    @PreAuthorize("hasAuthority('client_management_w')")
    public ResponseEntity<?> createClient(@Valid @RequestBody ClientDto clientDto) {
        try {
            ClientDto created = clientService.createClient(clientDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Actualiza un cliente existente
     * @param id ID del cliente
     * @param clientDto Datos actualizados del cliente
     * @return Cliente actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('client_management_w')")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @Valid @RequestBody ClientDto clientDto) {
        try {
            ClientDto updated = clientService.updateClient(id, clientDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Elimina un cliente
     * @param id ID del cliente a eliminar
     * @return Respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('client_management_d')")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        try {
            clientService.deleteClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Maneja errores de validación
     * @param ex Excepción de validación
     * @return Mapa con los errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Map<String, String> response = new HashMap<>();
        response.put("message", "Error de validación: " + errors.values().iterator().next());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

