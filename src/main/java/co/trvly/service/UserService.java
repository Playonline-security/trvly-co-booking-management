package co.trvly.service;

import co.trvly.dto.UserDto;
import co.trvly.entity.Role;
import co.trvly.entity.User;
import co.trvly.repository.RoleRepository;
import co.trvly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar usuarios
 */
@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Crea un nuevo usuario
     * @param userDto Datos del usuario
     * @return Usuario creado
     */
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con este nombre de usuario");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este correo electrónico");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEnabled(userDto.getEnabled() != null ? userDto.getEnabled() : true);

        // Asignar roles
        Set<Role> roles = new HashSet<>();
        if (userDto.getRoles() != null) {
            for (String roleName : userDto.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
                roles.add(role);
            }
        }
        user.setRoles(roles);

        User saved = userRepository.save(user);
        return convertToDto(saved);
    }

    /**
     * Obtiene todos los usuarios
     * @return Lista de usuarios
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDto(user);
    }

    /**
     * Actualiza un usuario existente
     * @param id ID del usuario
     * @param userDto Datos actualizados del usuario
     * @return Usuario actualizado
     */
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.getUsername().equals(userDto.getUsername()) &&
            userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con este nombre de usuario");
        }

        if (!user.getEmail().equals(userDto.getEmail()) &&
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este correo electrónico");
        }

        user.setUsername(userDto.getUsername());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        if (userDto.getEnabled() != null) {
            user.setEnabled(userDto.getEnabled());
        }

        // Actualizar roles
        if (userDto.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : userDto.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User updated = userRepository.save(user);
        return convertToDto(updated);
    }

    /**
     * Elimina un usuario
     * @param id ID del usuario a eliminar
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    /**
     * Convierte una entidad User a DTO
     * @param user Entidad User
     * @return UserDto
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.getEnabled());

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        dto.setRoles(roleNames);

        return dto;
    }
}

