package co.trvly.repository;

import co.trvly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestionar usuarios
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Busca un usuario por su nombre de usuario
     * @param username Nombre de usuario
     * @return Usuario encontrado
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Busca un usuario por su correo electrónico
     * @param email Correo electrónico
     * @return Usuario encontrado
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     * @param username Nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el email dado
     * @param email Correo electrónico
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
}

