package co.trvly.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Generated Hash: " + hash);

        // Verificar el hash existente
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJwqO";
        boolean matches = encoder.matches(password, existingHash);
        System.out.println("\nExisting hash matches 'admin123': " + matches);

        // Generar un nuevo hash para usar
        String newHash = encoder.encode(password);
        System.out.println("\nNew hash to use: " + newHash);
    }
}

