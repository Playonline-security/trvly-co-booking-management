package co.trvly.controller;

import co.trvly.dto.TravelPackageDto;
import co.trvly.service.TravelPackageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar paquetes turísticos
 * Los endpoints de lectura están disponibles para asesores y supervisores
 * Los endpoints de escritura solo para administradores
 */
@RestController
@RequestMapping("/api/admin/packages")
@CrossOrigin(origins = "http://localhost:3000")
public class TravelPackageController {
    @Autowired
    private TravelPackageService packageService;

    /**
     * Obtiene todos los paquetes turísticos
     * @return Lista de paquetes
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('package_management', 'reservation_management_r', 'reservation_management_w')")
    public ResponseEntity<List<TravelPackageDto>> getAllPackages() {
        return ResponseEntity.ok(packageService.getAllPackages());
    }

    /**
     * Obtiene un paquete turístico por su ID
     * @param id ID del paquete
     * @return Paquete encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('package_management', 'reservation_management_r', 'reservation_management_w')")
    public ResponseEntity<TravelPackageDto> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(packageService.getPackageById(id));
    }

    /**
     * Crea un nuevo paquete turístico (solo administradores)
     * @param packageDto Datos del paquete
     * @return Paquete creado
     */
    @PostMapping
    @PreAuthorize("hasAuthority('package_management')")
    public ResponseEntity<TravelPackageDto> createPackage(@Valid @RequestBody TravelPackageDto packageDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(packageService.createPackage(packageDto));
    }

    /**
     * Actualiza un paquete turístico existente (solo administradores)
     * @param id ID del paquete
     * @param packageDto Datos actualizados del paquete
     * @return Paquete actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('package_management')")
    public ResponseEntity<TravelPackageDto> updatePackage(@PathVariable Long id, @Valid @RequestBody TravelPackageDto packageDto) {
        return ResponseEntity.ok(packageService.updatePackage(id, packageDto));
    }

    /**
     * Elimina un paquete turístico (solo administradores)
     * @param id ID del paquete a eliminar
     * @return Respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('package_management')")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        packageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
