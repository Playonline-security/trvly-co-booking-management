package co.trvly.config;

import co.trvly.entity.Permission;
import co.trvly.entity.Role;
import co.trvly.entity.User;
import co.trvly.repository.PermissionRepository;
import co.trvly.repository.RoleRepository;
import co.trvly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Inicializador de datos que se ejecuta al iniciar la aplicación
 * Crea roles, permisos y el usuario administrador por defecto
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Método que se ejecuta al iniciar la aplicación
     * @param args Argumentos de línea de comandos
     */
    @Override
    public void run(String... args) throws Exception {
        initializeRolesAndPermissions();
        initializeAdminUser();
    }

    /**
     * Inicializa los roles y permisos del sistema
     */
    private void initializeRolesAndPermissions() {
        // Crear permisos
        String[] permissionNames = {
            "user_management",
            "package_management",
            "client_management_r",
            "client_management_w",
            "client_management_d",
            "reservation_management_r",
            "reservation_management_w",
            "reservation_management_d"
        };

        for (String permName : permissionNames) {
            if (permissionRepository.findByName(permName).isEmpty()) {
                Permission permission = new Permission();
                permission.setName(permName);
                permission.setDescription("Permiso: " + permName);
                permissionRepository.save(permission);
            }
        }

        // Crear roles
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    role.setDescription("Administrador con acceso total al sistema");
                    return roleRepository.save(role);
                });

        Role supervisorRole = roleRepository.findByName("SUPERVISOR")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("SUPERVISOR");
                    role.setDescription("Supervisor con permisos de gestión y eliminación");
                    return roleRepository.save(role);
                });

        Role advisorRole = roleRepository.findByName("ADVISOR")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADVISOR");
                    role.setDescription("Asesor con permisos de lectura y escritura");
                    return roleRepository.save(role);
                });

        // Asignar todos los permisos al rol ADMIN
        Set<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
        adminRole.setPermissions(allPermissions);
        roleRepository.save(adminRole);

        // Asignar permisos al rol SUPERVISOR
        Set<Permission> supervisorPermissions = new HashSet<>();
        supervisorPermissions.add(permissionRepository.findByName("client_management_r").orElse(null));
        supervisorPermissions.add(permissionRepository.findByName("client_management_w").orElse(null));
        supervisorPermissions.add(permissionRepository.findByName("client_management_d").orElse(null));
        supervisorPermissions.add(permissionRepository.findByName("reservation_management_r").orElse(null));
        supervisorPermissions.add(permissionRepository.findByName("reservation_management_w").orElse(null));
        supervisorPermissions.add(permissionRepository.findByName("reservation_management_d").orElse(null));
        supervisorPermissions.remove(null);
        supervisorRole.setPermissions(supervisorPermissions);
        roleRepository.save(supervisorRole);

        // Asignar permisos al rol ADVISOR
        Set<Permission> advisorPermissions = new HashSet<>();
        advisorPermissions.add(permissionRepository.findByName("client_management_r").orElse(null));
        advisorPermissions.add(permissionRepository.findByName("client_management_w").orElse(null));
        advisorPermissions.add(permissionRepository.findByName("reservation_management_r").orElse(null));
        advisorPermissions.add(permissionRepository.findByName("reservation_management_w").orElse(null));
        advisorPermissions.remove(null);
        advisorRole.setPermissions(advisorPermissions);
        roleRepository.save(advisorRole);
    }

    /**
     * Inicializa el usuario administrador por defecto
     * Si no existe, lo crea. Si existe, verifica y actualiza su contraseña y roles
     */
    private void initializeAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@trvly.co");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEnabled(true);

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
            System.out.println("Usuario administrador creado exitosamente");
        } else {
            // Actualizar usuario administrador para asegurar contraseña y roles correctos
            User admin = userRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Usuario administrador no encontrado"));
            
            boolean updated = false;
            
            // Siempre actualizar la contraseña para asegurar que sea correcta
            String newPasswordHash = passwordEncoder.encode("admin123");
            if (!passwordEncoder.matches("admin123", admin.getPassword())) {
                admin.setPassword(newPasswordHash);
                updated = true;
                System.out.println("Contraseña del usuario administrador actualizada");
            }
            
            // Asegurar que el administrador esté habilitado
            if (!admin.getEnabled()) {
                admin.setEnabled(true);
                updated = true;
            }
            
            // Asegurar que el administrador tenga el rol ADMIN
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
            if (admin.getRoles().isEmpty() || 
                admin.getRoles().stream().noneMatch(r -> r.getName().equals("ADMIN"))) {
                admin.getRoles().add(adminRole);
                updated = true;
                System.out.println("Rol ADMIN asignado al usuario administrador");
            }
            
            if (updated) {
                userRepository.save(admin);
            }
        }
    }
}

