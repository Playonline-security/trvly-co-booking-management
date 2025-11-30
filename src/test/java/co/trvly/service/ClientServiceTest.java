package co.trvly.service;

import co.trvly.dto.ClientDto;
import co.trvly.entity.Client;
import co.trvly.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private ClientDto clientDto;
    private Client existingClient;

    @BeforeEach
    void setUp() {
        clientDto = new ClientDto();
        clientDto.setFirstNames("John");
        clientDto.setLastNames("Doe");
        clientDto.setDocumentId("12345678");
        clientDto.setBirthDate(LocalDate.of(1990, 1, 1));
        clientDto.setMobilePhone("1234567890");
        clientDto.setEmail("john.doe@example.com");

        existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setFirstNames("John");
        existingClient.setLastNames("Doe");
        existingClient.setDocumentId("12345678");
        existingClient.setBirthDate(LocalDate.of(1990, 1, 1));
        existingClient.setMobilePhone("1234567890");
        existingClient.setEmail("john.doe@example.com");
    }

    @Test
    void createExistingClientShouldFail() {
        // Given: Un cliente con un ID de documento existente
        when(clientRepository.existsByDocumentId("12345678")).thenReturn(true);

        // When & Then: Debería lanzar una excepción
        assertThrows(RuntimeException.class, () -> {
            clientService.createClient(clientDto);
        }, "Should fail when document ID already exists");

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void createClientWithExistingEmailShouldFail() {
        // Given: Un cliente con un correo electrónico existente
        when(clientRepository.existsByDocumentId("12345678")).thenReturn(false);
        when(clientRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When & Then: Debería lanzar una excepción
        assertThrows(RuntimeException.class, () -> {
            clientService.createClient(clientDto);
        }, "Should fail when email already exists");

        verify(clientRepository, never()).save(any(Client.class));
    }
}

