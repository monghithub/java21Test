package com.monghit.java21test.features.virtualthreads.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Record que representa el resultado de una tarea ejecutada.
 *
 * Demuestra el uso de Records para modelar datos inmutables.
 */
public record TaskResult(
        int taskId,
        String threadName,
        boolean isVirtual,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Duration executionTime,
        String result,
        boolean success
) {

    /**
     * Crea un resultado exitoso de tarea.
     */
    public static TaskResult success(int taskId, String threadName, boolean isVirtual,
                                      LocalDateTime startTime, LocalDateTime endTime, String result) {
        Duration executionTime = Duration.between(startTime, endTime);
        return new TaskResult(taskId, threadName, isVirtual, startTime, endTime, executionTime, result, true);
    }

    /**
     * Crea un resultado fallido de tarea.
     */
    public static TaskResult failure(int taskId, String threadName, boolean isVirtual,
                                      LocalDateTime startTime, LocalDateTime endTime, String error) {
        Duration executionTime = Duration.between(startTime, endTime);
        return new TaskResult(taskId, threadName, isVirtual, startTime, endTime, executionTime, error, false);
    }

    /**
     * Obtiene el tiempo de ejecución en milisegundos.
     */
    public long executionTimeMillis() {
        return executionTime.toMillis();
    }
}
