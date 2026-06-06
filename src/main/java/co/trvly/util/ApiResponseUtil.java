package co.trvly.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad para respuestas JSON de error — RF-09 (refactorización)
 */
public final class ApiResponseUtil {

    private ApiResponseUtil() {
    }

    public static ResponseEntity<Map<String, String>> messageResponse(String message, HttpStatus status) {
        Map<String, String> body = new HashMap<>();
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
