package co.trvly.repository;

import co.trvly.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar roles
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Busca un rol por su nombre
     * @param name Nombre del rol
     * @return Rol encontrado
     */
    Optional<Role> findByName(String name);
}

