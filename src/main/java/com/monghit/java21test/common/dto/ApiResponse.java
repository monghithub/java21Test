package com.monghit.java21test.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Record genérico para respuestas de API.
 *
 * Este record demuestra el uso de Records en Java (introducidos en Java 16, finales en Java 16).
 * Los records proporcionan una forma concisa de crear clases inmutables de datos.
 *
 * @param <T> Tipo de datos de la respuesta
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String featureName,
        String javaVersion,
        LocalDateTime timestamp
) {

    /**
     * Crea una respuesta exitosa.
     */
    public static <T> ApiResponse<T> success(String featureName, T data) {
        return new ApiResponse<>(
                true,
                "Success",
                data,
                featureName,
                "Java 21",
                LocalDateTime.now()
        );
    }

    /**
     * Crea una respuesta exitosa con mensaje personalizado.
     */
    public static <T> ApiResponse<T> success(String featureName, String message, T data) {
        return new ApiResponse<>(
                true,
                message,
                data,
                featureName,
                "Java 21",
                LocalDateTime.now()
        );
    }

    /**
     * Crea una respuesta de error.
     */
    public static <T> ApiResponse<T> error(String featureName, String message) {
        return new ApiResponse<>(
                false,
                message,
                null,
                featureName,
                "Java 21",
                LocalDateTime.now()
        );
    }

    /**
     * Crea una respuesta de error con datos adicionales.
     */
    public static <T> ApiResponse<T> error(String featureName, String message, T data) {
        return new ApiResponse<>(
                false,
                message,
                data,
                featureName,
                "Java 21",
                LocalDateTime.now()
        );
    }
}
