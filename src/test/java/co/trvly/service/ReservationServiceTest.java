package co.trvly.service;

import co.trvly.dto.QuotationResponse;
import co.trvly.entity.Client;
import co.trvly.entity.Reservation;
import co.trvly.entity.TravelPackage;
import co.trvly.repository.ClientRepository;
import co.trvly.repository.ReservationRepository;
import co.trvly.repository.TravelPackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TravelPackageRepository packageRepository;

    @InjectMocks
    private ReservationService reservationService;

    private TravelPackage travelPackage;

    @BeforeEach
    void setUp() {
        travelPackage = new TravelPackage();
        travelPackage.setId(1L);
        travelPackage.setName("Beach Paradise");
        travelPackage.setDestination("Cancun");
        travelPackage.setDescription("Amazing beach vacation");
        travelPackage.setBasePrice(new BigDecimal("1500.00"));
    }

    @Test
    void calculateCorrectQuotation() {
        // Given: Un paquete de viaje con precio base 1500.00 y 3 pasajeros
        Long packageId = 1L;
        Integer passengerCount = 3;
        when(packageRepository.findById(packageId)).thenReturn(Optional.of(travelPackage));

        // When: Calcular cotización
        QuotationResponse response = reservationService.calculateQuotation(packageId, passengerCount);

        // Then: El precio total debe ser el precio base * número de pasajeros
        assertEquals(new BigDecimal("1500.00"), response.getBasePrice());
        assertEquals(3, response.getPassengerCount());
        assertEquals(new BigDecimal("4500.00"), response.getTotalPrice());
        assertNotNull(response.getTravelPackage());
    }

    @Test
    void changeStatusToFullPaymentSuccessful() {
        // Given: Una reserva existente con estado PAGO_PENDIENTE
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(Reservation.ReservationStatus.PENDING_PAYMENT);
        reservation.setTravelPackage(travelPackage);
        reservation.setClient(new Client());

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        // When: Actualizar estado a PAGO_COMPLETO
        var result = reservationService.updateReservationStatus(1L, "FULL_PAYMENT");

        // Then: El estado debe actualizarse
        assertEquals("FULL_PAYMENT", result.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }
}

