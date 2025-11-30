package co.trvly.repository;

import co.trvly.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar permisos
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    /**
     * Busca un permiso por su nombre
     * @param name Nombre del permiso
     * @return Permiso encontrado
     */
    Optional<Permission> findByName(String name);
}

