package com.monghit.java21test.features.virtualthreads.controller;

import com.monghit.java21test.common.dto.ApiResponse;
import com.monghit.java21test.features.virtualthreads.model.TaskResult;
import com.monghit.java21test.features.virtualthreads.service.VirtualThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para demostrar Virtual Threads (Project Loom).
 *
 * Los Virtual Threads son una característica clave de Java 21 que permite
 * crear aplicaciones altamente concurrentes con un modelo de programación simple.
 */
@RestController
@RequestMapping("/virtual-threads")
@Tag(name = "Virtual Threads", description = "APIs para demostrar Virtual Threads (Project Loom)")
public class VirtualThreadController {

    private final VirtualThreadService virtualThreadService;

    public VirtualThreadController(VirtualThreadService virtualThreadService) {
        this.virtualThreadService = virtualThreadService;
    }

    /**
     * Ejecuta múltiples tareas concurrentemente usando Virtual Threads.
     */
    @PostMapping("/execute-tasks")
    @Operation(summary = "Ejecutar tareas concurrentes",
               description = "Ejecuta múltiples tareas en paralelo usando Virtual Threads")
    public ResponseEntity<ApiResponse<List<TaskResult>>> executeTasks(
            @Parameter(description = "Número de tareas a ejecutar")
            @RequestParam(defaultValue = "10") int numberOfTasks) {

        if (numberOfTasks <= 0 || numberOfTasks > 10000) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Virtual Threads",
                            "Number of tasks must be between 1 and 10000"));
        }

        List<TaskResult> results = virtualThreadService.executeConcurrentTasks(numberOfTasks);
        return ResponseEntity.ok(
                ApiResponse.success("Virtual Threads",
                        String.format("Executed %d tasks successfully", numberOfTasks),
                        results)
        );
    }

    /**
     * Compara rendimiento entre Virtual Threads y Platform Threads.
     */
    @GetMapping("/benchmark")
    @Operation(summary = "Benchmark de rendimiento",
               description = "Compara el rendimiento entre Virtual Threads y Platform Threads")
    public ResponseEntity<ApiResponse<VirtualThreadService.BenchmarkResult>> benchmark(
            @Parameter(description = "Número de tareas para el benchmark")
            @RequestParam(defaultValue = "100") int numberOfTasks) {

        if (numberOfTasks <= 0 || numberOfTasks > 5000) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Virtual Threads",
                            "Number of tasks must be between 1 and 5000"));
        }

        VirtualThreadService.BenchmarkResult result = virtualThreadService.benchmark(numberOfTasks);
        return ResponseEntity.ok(
                ApiResponse.success("Virtual Threads Benchmark", result.summary(), result)
        );
    }

    /**
     * Simula una operación de I/O bloqueante.
     */
    @PostMapping("/simulate-blocking-io")
    @Operation(summary = "Simular I/O bloqueante",
               description = "Simula una operación de I/O bloqueante usando Virtual Threads")
    public ResponseEntity<ApiResponse<TaskResult>> simulateBlockingIO(
            @Parameter(description = "Tiempo de delay en milisegundos")
            @RequestParam(defaultValue = "1000") long delayMs) {

        if (delayMs < 0 || delayMs > 30000) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Virtual Threads",
                            "Delay must be between 0 and 30000ms"));
        }

        TaskResult result = virtualThreadService.simulateBlockingIO(delayMs);
        return ResponseEntity.ok(
                ApiResponse.success("Virtual Threads - Blocking I/O", result)
        );
    }

    /**
     * Obtiene información sobre el thread actual.
     */
    @GetMapping("/current-thread-info")
    @Operation(summary = "Información del thread actual",
               description = "Obtiene información sobre el thread que maneja la request")
    public ResponseEntity<ApiResponse<ThreadInfo>> getCurrentThreadInfo() {
        Thread currentThread = Thread.currentThread();
        ThreadInfo info = new ThreadInfo(
                currentThread.getName(),
                currentThread.isVirtual(),
                currentThread.getThreadGroup() != null ? currentThread.getThreadGroup().getName() : "N/A",
                currentThread.getPriority()
        );

        return ResponseEntity.ok(
                ApiResponse.success("Virtual Threads - Thread Info", info)
        );
    }

    /**
     * Record con información del thread.
     */
    public record ThreadInfo(
            String name,
            boolean isVirtual,
            String threadGroup,
            int priority
    ) {}
}
