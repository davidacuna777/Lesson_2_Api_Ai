package cr.una.leccion2.app.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        String code = ex.getMessage() != null ? ex.getMessage() : "BAD_REQUEST";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errorCode", code));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("errorCode", "INTERNAL_ERROR", "message", ex.getMessage() != null ? ex.getMessage() : ""));
    }
}