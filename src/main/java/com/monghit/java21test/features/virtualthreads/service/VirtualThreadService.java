package com.monghit.java21test.features.virtualthreads.service;

import com.monghit.java21test.features.virtualthreads.model.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Servicio que demuestra el uso de Virtual Threads (Project Loom).
 *
 * Los Virtual Threads permiten crear millones de threads con overhead mínimo,
 * mejorando significativamente el rendimiento en operaciones I/O bound.
 */
@Service
public class VirtualThreadService {

    private static final Logger log = LoggerFactory.getLogger(VirtualThreadService.class);

    /**
     * Ejecuta múltiples tareas concurrentemente usando Virtual Threads.
     *
     * @param numberOfTasks Número de tareas a ejecutar
     * @return Lista de resultados de las tareas
     */
    public List<TaskResult> executeConcurrentTasks(int numberOfTasks) {
        log.info("Executing {} tasks with Virtual Threads", numberOfTasks);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<TaskResult>> tasks = new ArrayList<>();

            for (int i = 0; i < numberOfTasks; i++) {
                final int taskId = i;
                tasks.add(() -> executeTask(taskId));
            }

            List<Future<TaskResult>> futures = executor.invokeAll(tasks);
            List<TaskResult> results = new ArrayList<>();

            for (Future<TaskResult> future : futures) {
                results.add(future.get());
            }

            log.info("Completed {} tasks", results.size());
            return results;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error executing tasks", e);
            throw new RuntimeException("Error executing concurrent tasks", e);
        }
    }

    /**
     * Ejecuta una tarea individual.
     */
    private TaskResult executeTask(int taskId) {
        LocalDateTime startTime = LocalDateTime.now();
        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();
        boolean isVirtual = currentThread.isVirtual();

        try {
            // Simular trabajo (I/O bound)
            Thread.sleep(100);

            LocalDateTime endTime = LocalDateTime.now();
            String result = String.format("Task %d completed on %s", taskId, threadName);

            return TaskResult.success(taskId, threadName, isVirtual, startTime, endTime, result);

        } catch (InterruptedException e) {
            LocalDateTime endTime = LocalDateTime.now();
            return TaskResult.failure(taskId, threadName, isVirtual, startTime, endTime, e.getMessage());
        }
    }

    /**
     * Compara rendimiento entre Virtual Threads y Platform Threads.
     *
     * @param numberOfTasks Número de tareas a ejecutar
     * @return Estadísticas de rendimiento
     */
    public BenchmarkResult benchmark(int numberOfTasks) {
        log.info("Benchmarking Virtual Threads vs Platform Threads with {} tasks", numberOfTasks);

        // Benchmark con Virtual Threads
        long virtualStartTime = System.currentTimeMillis();
        List<TaskResult> virtualResults = executeConcurrentTasks(numberOfTasks);
        long virtualEndTime = System.currentTimeMillis();
        long virtualDuration = virtualEndTime - virtualStartTime;

        // Benchmark con Platform Threads (pool limitado)
        long platformStartTime = System.currentTimeMillis();
        List<TaskResult> platformResults = executeWithPlatformThreads(numberOfTasks);
        long platformEndTime = System.currentTimeMillis();
        long platformDuration = platformEndTime - platformStartTime;

        return new BenchmarkResult(
                numberOfTasks,
                virtualDuration,
                platformDuration,
                virtualResults.size(),
                platformResults.size(),
                calculateSpeedup(platformDuration, virtualDuration)
        );
    }

    /**
     * Ejecuta tareas con Platform Threads tradicionales.
     */
    private List<TaskResult> executeWithPlatformThreads(int numberOfTasks) {
        // Usar un pool limitado para simular Platform Threads
        int poolSize = Math.min(numberOfTasks, 200);

        try (ExecutorService executor = Executors.newFixedThreadPool(poolSize)) {
            List<Callable<TaskResult>> tasks = new ArrayList<>();

            for (int i = 0; i < numberOfTasks; i++) {
                final int taskId = i;
                tasks.add(() -> executeTask(taskId));
            }

            List<Future<TaskResult>> futures = executor.invokeAll(tasks);
            List<TaskResult> results = new ArrayList<>();

            for (Future<TaskResult> future : futures) {
                results.add(future.get());
            }

            return results;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error executing platform thread tasks", e);
        }
    }

    /**
     * Simula operación de I/O bloqueante.
     */
    public TaskResult simulateBlockingIO(long delayMs) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<TaskResult> future = executor.submit(() -> {
                int taskId = ThreadLocalRandom.current().nextInt(1000);
                LocalDateTime startTime = LocalDateTime.now();
                Thread currentThread = Thread.currentThread();

                try {
                    log.info("Simulating blocking I/O for {}ms on thread {}", delayMs, currentThread.getName());
                    Thread.sleep(delayMs);

                    LocalDateTime endTime = LocalDateTime.now();
                    return TaskResult.success(
                            taskId,
                            currentThread.getName(),
                            currentThread.isVirtual(),
                            startTime,
                            endTime,
                            String.format("Blocking I/O completed after %dms", delayMs)
                    );

                } catch (InterruptedException e) {
                    LocalDateTime endTime = LocalDateTime.now();
                    return TaskResult.failure(taskId, currentThread.getName(), currentThread.isVirtual(),
                            startTime, endTime, e.getMessage());
                }
            });

            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error executing blocking I/O simulation", e);
        }
    }

    /**
     * Calcula la mejora de rendimiento (speedup).
     */
    private double calculateSpeedup(long platformTime, long virtualTime) {
        if (virtualTime == 0) return 0;
        return (double) platformTime / virtualTime;
    }

    /**
     * Record que representa los resultados del benchmark.
     */
    public record BenchmarkResult(
            int numberOfTasks,
            long virtualThreadsTimeMs,
            long platformThreadsTimeMs,
            int virtualThreadsCompleted,
            int platformThreadsCompleted,
            double speedup
    ) {
        public String summary() {
            return String.format(
                    "Benchmark: %d tasks | Virtual: %dms | Platform: %dms | Speedup: %.2fx",
                    numberOfTasks, virtualThreadsTimeMs, platformThreadsTimeMs, speedup
            );
        }
    }
}
