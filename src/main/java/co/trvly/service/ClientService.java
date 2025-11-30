package co.trvly.service;

import co.trvly.dto.ClientDto;
import co.trvly.entity.Client;
import co.trvly.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar clientes
 */
@Service
@Transactional
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    /**
     * Crea un nuevo cliente
     * @param clientDto Datos del cliente
     * @return Cliente creado
     */
    public ClientDto createClient(ClientDto clientDto) {
        if (clientRepository.existsByDocumentId(clientDto.getDocumentId())) {
            throw new RuntimeException("Ya existe un cliente con este número de documento");
        }
        if (clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con este correo electrónico");
        }

        Client client = new Client();
        client.setFirstNames(clientDto.getFirstNames());
        client.setLastNames(clientDto.getLastNames());
        client.setDocumentId(clientDto.getDocumentId());
        client.setBirthDate(clientDto.getBirthDate());
        client.setMobilePhone(clientDto.getMobilePhone());
        client.setEmail(clientDto.getEmail());

        Client saved = clientRepository.save(client);
        return convertToDto(saved);
    }

    public List<ClientDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un cliente por su ID
     * @param id ID del cliente
     * @return Cliente encontrado
     */
    public ClientDto getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return convertToDto(client);
    }

    /**
     * Busca clientes por término de búsqueda
     * @param search Término de búsqueda
     * @return Lista de clientes encontrados
     */
    public List<ClientDto> searchClients(String search) {
        return clientRepository.searchClients(search).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un cliente existente
     * @param id ID del cliente
     * @param clientDto Datos actualizados del cliente
     * @return Cliente actualizado
     */
    public ClientDto updateClient(Long id, ClientDto clientDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Verificar duplicado de documento (excluyendo el cliente actual)
        if (!client.getDocumentId().equals(clientDto.getDocumentId()) &&
            clientRepository.existsByDocumentId(clientDto.getDocumentId())) {
            throw new RuntimeException("Ya existe un cliente con este número de documento");
        }

        // Verificar duplicado de email (excluyendo el cliente actual)
        if (!client.getEmail().equals(clientDto.getEmail()) &&
            clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con este correo electrónico");
        }

        client.setFirstNames(clientDto.getFirstNames());
        client.setLastNames(clientDto.getLastNames());
        client.setDocumentId(clientDto.getDocumentId());
        client.setBirthDate(clientDto.getBirthDate());
        client.setMobilePhone(clientDto.getMobilePhone());
        client.setEmail(clientDto.getEmail());

        Client updated = clientRepository.save(client);
        return convertToDto(updated);
    }

    /**
     * Elimina un cliente
     * @param id ID del cliente a eliminar
     */
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado");
        }
        clientRepository.deleteById(id);
    }

    /**
     * Convierte una entidad Client a DTO
     * @param client Entidad Client
     * @return ClientDto
     */
    private ClientDto convertToDto(Client client) {
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
}

