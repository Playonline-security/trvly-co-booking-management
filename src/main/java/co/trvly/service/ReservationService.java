package co.trvly.service;

import co.trvly.dto.*;
import co.trvly.entity.*;
import co.trvly.repository.ClientRepository;
import co.trvly.repository.ReservationRepository;
import co.trvly.repository.TravelPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar reservas
 */
@Service
@Transactional
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TravelPackageRepository packageRepository;

    /**
     * Calcula la cotización de una reserva
     * @param packageId ID del paquete turístico
     * @param passengerCount Cantidad de pasajeros
     * @return Respuesta con la cotización calculada
     */
    public QuotationResponse calculateQuotation(Long packageId, Integer passengerCount) {
        TravelPackage travelPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Paquete turístico no encontrado"));

        BigDecimal basePrice = travelPackage.getBasePrice();
        BigDecimal totalPrice = basePrice.multiply(BigDecimal.valueOf(passengerCount));

        TravelPackageDto packageDto = convertPackageToDto(travelPackage);

        QuotationResponse response = new QuotationResponse();
        response.setTravelPackage(packageDto);
        response.setPassengerCount(passengerCount);
        response.setBasePrice(basePrice);
        response.setTotalPrice(totalPrice);
        response.setMessage("Cotización calculada exitosamente");

        return response;
    }

    /**
     * Crea una nueva reserva
     * @param reservationDto Datos de la reserva
     * @return Reserva creada
     */
    public ReservationDto createReservation(ReservationDto reservationDto) {
        Client client = clientRepository.findById(reservationDto.getClientId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        TravelPackage travelPackage = packageRepository.findById(reservationDto.getPackageId())
                .orElseThrow(() -> new RuntimeException("Paquete turístico no encontrado"));

        // Validar que la cantidad de pasajeros coincida
        if (reservationDto.getPassengers().size() != reservationDto.getPassengerCount()) {
            throw new RuntimeException("La cantidad de pasajeros no coincide con el número de pasajeros proporcionados");
        }

        // Generar número único de reserva
        String reservationNumber = generateReservationNumber();

        // Calcular precio total
        BigDecimal totalPrice = travelPackage.getBasePrice()
                .multiply(BigDecimal.valueOf(reservationDto.getPassengerCount()));

        Reservation reservation = new Reservation();
        reservation.setReservationNumber(reservationNumber);
        reservation.setClient(client);
        reservation.setTravelPackage(travelPackage);
        reservation.setPassengerCount(reservationDto.getPassengerCount());
        reservation.setStatus(Reservation.ReservationStatus.valueOf(reservationDto.getStatus()));
        reservation.setTotalPrice(totalPrice);

        // Agregar pasajeros
        for (PassengerDto passengerDto : reservationDto.getPassengers()) {
            Passenger passenger = new Passenger();
            passenger.setReservation(reservation);
            passenger.setFirstName(passengerDto.getFirstName());
            passenger.setLastName(passengerDto.getLastName());
            passenger.setDocumentId(passengerDto.getDocumentId());
            passenger.setBirthDate(passengerDto.getBirthDate());
            reservation.getPassengers().add(passenger);
        }

        Reservation saved = reservationRepository.save(reservation);
        return convertToDto(saved);
    }

    /**
     * Obtiene todas las reservas, opcionalmente filtradas por búsqueda
     * @param search Término de búsqueda opcional
     * @return Lista de reservas
     */
    public List<ReservationDto> getAllReservations(String search) {
        List<Reservation> reservations;
        if (search != null && !search.trim().isEmpty()) {
            reservations = reservationRepository.searchReservations(search.trim());
        } else {
            reservations = reservationRepository.findAll();
        }
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una reserva por su ID
     * @param id ID de la reserva
     * @return Reserva encontrada
     */
    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        return convertToDto(reservation);
    }

    /**
     * Actualiza el estado de una reserva
     * @param id ID de la reserva
     * @param status Nuevo estado
     * @return Reserva actualizada
     */
    public ReservationDto updateReservationStatus(Long id, String status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        try {
            Reservation.ReservationStatus newStatus = Reservation.ReservationStatus.valueOf(status);
            reservation.setStatus(newStatus);
            Reservation updated = reservationRepository.save(reservation);
            return convertToDto(updated);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado de reserva inválido: " + status);
        }
    }

    /**
     * Actualiza la cantidad de pasajeros de una reserva
     * @param id ID de la reserva
     * @param passengerCount Nueva cantidad de pasajeros
     * @return Reserva actualizada
     */
    public ReservationDto updatePassengerCount(Long id, Integer passengerCount) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (passengerCount < 1) {
            throw new RuntimeException("La cantidad de pasajeros debe ser al menos 1");
        }

        // Actualizar cantidad de pasajeros
        reservation.setPassengerCount(passengerCount);
        
        // Recalcular precio total
        BigDecimal totalPrice = reservation.getTravelPackage().getBasePrice()
                .multiply(BigDecimal.valueOf(passengerCount));
        reservation.setTotalPrice(totalPrice);

        // Ajustar lista de pasajeros
        int currentPassengerCount = reservation.getPassengers().size();
        if (passengerCount > currentPassengerCount) {
            // Agregar pasajeros vacíos si es necesario
            for (int i = currentPassengerCount; i < passengerCount; i++) {
                Passenger passenger = new Passenger();
                passenger.setReservation(reservation);
                passenger.setFirstName("");
                passenger.setLastName("");
                passenger.setDocumentId("");
                passenger.setBirthDate(null);
                reservation.getPassengers().add(passenger);
            }
        } else if (passengerCount < currentPassengerCount) {
            // Eliminar pasajeros extras - crear nueva lista para evitar problemas de modificación
            List<Passenger> passengersToKeep = new ArrayList<>(reservation.getPassengers().subList(0, passengerCount));
            reservation.getPassengers().clear();
            reservation.getPassengers().addAll(passengersToKeep);
        }

        Reservation updated = reservationRepository.save(reservation);
        return convertToDto(updated);
    }

    /**
     * Elimina una reserva
     * @param id ID de la reserva a eliminar
     */
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada");
        }
        reservationRepository.deleteById(id);
    }

    /**
     * Genera un número único de reserva
     * @return Número de reserva en formato TRV-XXXXXXXX
     */
    private String generateReservationNumber() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "TRV-" + uuid.substring(0, 8);
    }

    /**
     * Convierte una entidad Reservation a DTO
     * @param reservation Entidad Reservation
     * @return ReservationDto
     */
    private ReservationDto convertToDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setReservationNumber(reservation.getReservationNumber());
        dto.setClientId(reservation.getClient().getId());
        dto.setPackageId(reservation.getTravelPackage().getId());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setPassengerCount(reservation.getPassengerCount());
        dto.setStatus(reservation.getStatus().name());
        dto.setTotalPrice(reservation.getTotalPrice());

        // Convertir cliente a DTO
        ClientDto clientDto = convertClientToDto(reservation.getClient());
        dto.setClient(clientDto);

        // Convertir paquete a DTO
        TravelPackageDto packageDto = convertPackageToDto(reservation.getTravelPackage());
        dto.setTravelPackage(packageDto);

        // Convertir pasajeros a DTOs
        List<PassengerDto> passengerDtos = reservation.getPassengers().stream()
                .map(this::convertPassengerToDto)
                .collect(Collectors.toList());
        dto.setPassengers(passengerDtos);

        return dto;
    }

    /**
     * Convierte una entidad Client a DTO
     * @param client Entidad Client
     * @return ClientDto
     */
    private ClientDto convertClientToDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        dto.setFirstNames(client.getFirstNames());
        dto.setLastNames(client.getLastNames());
        dto.setDocumentId(client.getDocumentId());
        dto.setBirthDate(client.getBirthDate());
        dto.setMobilePhone(client.getMobilePhone());
        dto.setEmail(client.getEmail());
        return dto;
    }

    /**
     * Convierte una entidad TravelPackage a DTO
     * @param travelPackage Entidad TravelPackage
     * @return TravelPackageDto
     */
    private TravelPackageDto convertPackageToDto(TravelPackage travelPackage) {
        TravelPackageDto dto = new TravelPackageDto();
        dto.setId(travelPackage.getId());
        dto.setName(travelPackage.getName());
        dto.setDestination(travelPackage.getDestination());
        dto.setDescription(travelPackage.getDescription());
        dto.setAdditionalServices(travelPackage.getAdditionalServices());
        dto.setBasePrice(travelPackage.getBasePrice());
        return dto;
    }

    /**
     * Convierte una entidad Passenger a DTO
     * @param passenger Entidad Passenger
     * @return PassengerDto
     */
    private PassengerDto convertPassengerToDto(Passenger passenger) {
        PassengerDto dto = new PassengerDto();
        dto.setFirstName(passenger.getFirstName());
        dto.setLastName(passenger.getLastName());
        dto.setDocumentId(passenger.getDocumentId());
        dto.setBirthDate(passenger.getBirthDate());
        return dto;
    }
}

