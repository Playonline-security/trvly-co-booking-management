package co.trvly.service;

import co.trvly.entity.User;
import co.trvly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de detalles de usuario para Spring Security
 * Carga los detalles del usuario desde la base de datos y los convierte en UserDetails
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Carga un usuario por su nombre de usuario
     * @param username Nombre de usuario
     * @return Detalles del usuario para Spring Security
     * @throws UsernameNotFoundException Si el usuario no se encuentra
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getEnabled())
                .build();
    }

    /**
     * Obtiene las autoridades (permisos) del usuario basándose en sus roles
     * @param user Usuario
     * @return Colección de autoridades del usuario
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getName())))
                .collect(Collectors.toList());
    }
}

