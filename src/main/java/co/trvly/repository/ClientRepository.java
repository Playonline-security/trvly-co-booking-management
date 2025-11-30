package co.trvly.repository;

import co.trvly.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar clientes
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    /**
     * Busca un cliente por su documento de identidad
     * @param documentId Documento de identidad
     * @return Cliente encontrado
     */
    Optional<Client> findByDocumentId(String documentId);
    
    /**
     * Busca un cliente por su correo electrónico
     * @param email Correo electrónico
     * @return Cliente encontrado
     */
    Optional<Client> findByEmail(String email);
    
    /**
     * Verifica si existe un cliente con el documento dado
     * @param documentId Documento de identidad
     * @return true si existe, false en caso contrario
     */
    boolean existsByDocumentId(String documentId);
    
    /**
     * Verifica si existe un cliente con el email dado
     * @param email Correo electrónico
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca clientes por término de búsqueda
     * Busca en nombres, apellidos, documento, teléfono y email
     * @param search Término de búsqueda
     * @return Lista de clientes encontrados
     */
    @Query("SELECT c FROM Client c WHERE " +
           "LOWER(CONCAT(c.firstNames, ' ', c.lastNames)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.documentId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.mobilePhone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Client> searchClients(@Param("search") String search);
}

