package co.trvly.service;

import co.trvly.dto.TravelPackageDto;
import co.trvly.entity.TravelPackage;
import co.trvly.repository.TravelPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TravelPackageServiceTest {
    @Mock
    private TravelPackageRepository packageRepository;

    @InjectMocks
    private TravelPackageService packageService;

    private TravelPackage existingPackage;
    private TravelPackageDto updateDto;

    @BeforeEach
    void setUp() {
        existingPackage = new TravelPackage();
        existingPackage.setId(1L);
        existingPackage.setName("Original Package");
        existingPackage.setDestination("Original Destination");
        existingPackage.setDescription("Original Description");
        existingPackage.setBasePrice(new BigDecimal("1000.00"));

        updateDto = new TravelPackageDto();
        updateDto.setName("Updated Package");
        updateDto.setDestination("Updated Destination");
        updateDto.setDescription("Updated Description");
        updateDto.setBasePrice(new BigDecimal("1500.00"));
    }

    @Test
    void updateShouldPersistChanges() {
        // Given: Un paquete de viaje existente
        when(packageRepository.findById(1L)).thenReturn(Optional.of(existingPackage));
        when(packageRepository.save(any(TravelPackage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: Actualizar el paquete
        TravelPackageDto result = packageService.updatePackage(1L, updateDto);

        // Then: Los cambios deben persistir
        assertEquals("Updated Package", result.getName());
        assertEquals("Updated Destination", result.getDestination());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(new BigDecimal("1500.00"), result.getBasePrice());
        verify(packageRepository, times(1)).save(any(TravelPackage.class));
    }
}

