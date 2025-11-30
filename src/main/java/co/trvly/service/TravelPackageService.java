package co.trvly.service;

import co.trvly.dto.TravelPackageDto;
import co.trvly.entity.TravelPackage;
import co.trvly.repository.TravelPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar paquetes turísticos
 */
@Service
@Transactional
public class TravelPackageService {
    @Autowired
    private TravelPackageRepository packageRepository;

    /**
     * Crea un nuevo paquete turístico
     * @param packageDto Datos del paquete
     * @return Paquete creado
     */
    public TravelPackageDto createPackage(TravelPackageDto packageDto) {
        TravelPackage travelPackage = new TravelPackage();
        travelPackage.setName(packageDto.getName());
        travelPackage.setDestination(packageDto.getDestination());
        travelPackage.setDescription(packageDto.getDescription());
        travelPackage.setAdditionalServices(packageDto.getAdditionalServices());
        travelPackage.setBasePrice(packageDto.getBasePrice());

        TravelPackage saved = packageRepository.save(travelPackage);
        return convertToDto(saved);
    }

    /**
     * Obtiene todos los paquetes turísticos
     * @return Lista de paquetes
     */
    public List<TravelPackageDto> getAllPackages() {
        return packageRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un paquete turístico por su ID
     * @param id ID del paquete
     * @return Paquete encontrado
     */
    public TravelPackageDto getPackageById(Long id) {
        TravelPackage travelPackage = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paquete turístico no encontrado"));
        return convertToDto(travelPackage);
    }

    /**
     * Actualiza un paquete turístico existente
     * @param id ID del paquete
     * @param packageDto Datos actualizados del paquete
     * @return Paquete actualizado
     */
    public TravelPackageDto updatePackage(Long id, TravelPackageDto packageDto) {
        TravelPackage travelPackage = packageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paquete turístico no encontrado"));

        travelPackage.setName(packageDto.getName());
        travelPackage.setDestination(packageDto.getDestination());
        travelPackage.setDescription(packageDto.getDescription());
        travelPackage.setAdditionalServices(packageDto.getAdditionalServices());
        travelPackage.setBasePrice(packageDto.getBasePrice());

        TravelPackage updated = packageRepository.save(travelPackage);
        return convertToDto(updated);
    }

    /**
     * Elimina un paquete turístico
     * @param id ID del paquete a eliminar
     */
    public void deletePackage(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new RuntimeException("Paquete turístico no encontrado");
        }
        packageRepository.deleteById(id);
    }

    /**
     * Convierte una entidad TravelPackage a DTO
     * @param travelPackage Entidad TravelPackage
     * @return TravelPackageDto
     */
    private TravelPackageDto convertToDto(TravelPackage travelPackage) {
        TravelPackageDto dto = new TravelPackageDto();
        dto.setId(travelPackage.getId());
        dto.setName(travelPackage.getName());
        dto.setDestination(travelPackage.getDestination());
        dto.setDescription(travelPackage.getDescription());
        dto.setAdditionalServices(travelPackage.getAdditionalServices());
        dto.setBasePrice(travelPackage.getBasePrice());
        return dto;
    }
}

