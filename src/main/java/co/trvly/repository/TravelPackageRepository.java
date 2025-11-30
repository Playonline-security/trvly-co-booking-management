package co.trvly.repository;

import co.trvly.entity.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para gestionar paquetes turísticos
 */
@Repository
public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {
}

