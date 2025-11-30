package co.trvly.service;

import co.trvly.dto.UserDto;
import co.trvly.entity.Role;
import co.trvly.entity.User;
import co.trvly.repository.RoleRepository;
import co.trvly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDto advisorDto;
    private Role advisorRole;

    @BeforeEach
    void setUp() {
        advisorDto = new UserDto();
        advisorDto.setUsername("advisor1");
        advisorDto.setPassword("password123");
        advisorDto.setEmail("advisor@trvly.co");
        advisorDto.setFirstName("Advisor");
        advisorDto.setLastName("User");
        Set<String> roles = new HashSet<>();
        roles.add("ADVISOR");
        advisorDto.setRoles(roles);

        advisorRole = new Role();
        advisorRole.setId(1L);
        advisorRole.setName("ADVISOR");
    }

    @Test
    void createNewAdvisorOnlyByAdmin() {
        // Given: El administrador desea crear un nuevo usuario asesor
        when(userRepository.existsByUsername("advisor1")).thenReturn(false);
        when(userRepository.existsByEmail("advisor@trvly.co")).thenReturn(false);
        when(roleRepository.findByName("ADVISOR")).thenReturn(Optional.of(advisorRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // When: Crear usuario asesor
        UserDto result = userService.createUser(advisorDto);

        // Then: El usuario debe crearse con el rol de ASESOR
        assertNotNull(result);
        assertEquals("advisor1", result.getUsername());
        assertEquals("advisor@trvly.co", result.getEmail());
        assertTrue(result.getRoles().contains("ADVISOR"));
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByName("ADVISOR");
    }

    @Test
    void createUserWithExistingUsernameShouldFail() {
        // Given: Un usuario con nombre de usuario existente
        when(userRepository.existsByUsername("advisor1")).thenReturn(true);

        // When & Then: Debería lanzar una excepción
        assertThrows(RuntimeException.class, () -> {
            userService.createUser(advisorDto);
        }, "Should fail when username already exists");

        verify(userRepository, never()).save(any(User.class));
    }
}

