package co.trvly.service;

import co.trvly.dto.LoginRequest;
import co.trvly.dto.LoginResponse;
import co.trvly.entity.User;
import co.trvly.repository.UserRepository;
import co.trvly.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Servicio para gestionar la autenticación de usuarios
 */
@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    /**
     * Autentica un usuario y genera un token JWT
     * @param loginRequest Datos de inicio de sesión (usuario y contraseña)
     * @return Respuesta con el token JWT y datos del usuario
     */
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // Autenticar las credenciales del usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Credenciales inválidas", e);
        } catch (Exception e) {
            throw new RuntimeException("Error en la autenticación: " + e.getMessage(), e);
        }

        // Obtener detalles del usuario y generar token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        // Obtener el usuario completo de la base de datos
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el usuario esté habilitado
        if (!user.getEnabled()) {
            throw new RuntimeException("Usuario deshabilitado");
        }

        // Construir respuesta con token y datos del usuario
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));

        return response;
    }
}

