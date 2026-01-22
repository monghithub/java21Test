package com.monghit.java21test.common.exception;

import com.monghit.java21test.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Manejador global de excepciones para todos los controladores.
 *
 * Captura excepciones no manejadas y las convierte en respuestas HTTP apropiadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja todas las excepciones genéricas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex, WebRequest request) {
        String errorMessage = "Error: " + ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("General", errorMessage, ex.getClass().getSimpleName()));
    }

    /**
     * Maneja excepciones de argumento ilegal.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation", ex.getMessage()));
    }

    /**
     * Maneja excepciones de estado ilegal.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("State", ex.getMessage()));
    }

    /**
     * Maneja excepciones de operación no soportada.
     */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiResponse<String>> handleUnsupportedOperationException(
            UnsupportedOperationException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(ApiResponse.error("Operation", ex.getMessage()));
    }
}
