package com.monghit.java21test.features.virtualthreads;

import com.monghit.java21test.features.virtualthreads.model.TaskResult;
import com.monghit.java21test.features.virtualthreads.service.VirtualThreadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para VirtualThreadService.
 *
 * Verifica que las funcionalidades de Virtual Threads funcionen correctamente.
 */
@SpringBootTest
class VirtualThreadServiceTest {

    @Autowired
    private VirtualThreadService virtualThreadService;

    @Test
    void testExecuteConcurrentTasks_shouldCompleteAllTasks() {
        // Given
        int numberOfTasks = 50;

        // When
        List<TaskResult> results = virtualThreadService.executeConcurrentTasks(numberOfTasks);

        // Then
        assertThat(results).isNotNull();
        assertThat(results).hasSize(numberOfTasks);
        assertThat(results).allMatch(TaskResult::success);
    }

    @Test
    void testExecuteConcurrentTasks_shouldUseVirtualThreads() {
        // Given
        int numberOfTasks = 10;

        // When
        List<TaskResult> results = virtualThreadService.executeConcurrentTasks(numberOfTasks);

        // Then
        assertThat(results).isNotNull();
        assertThat(results).allMatch(TaskResult::isVirtual,
                "All tasks should be executed on Virtual Threads");
    }

    @Test
    void testSimulateBlockingIO_shouldCompleteSuccessfully() {
        // Given
        long delayMs = 100;

        // When
        TaskResult result = virtualThreadService.simulateBlockingIO(delayMs);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.success()).isTrue();
        assertThat(result.isVirtual()).isTrue();
        assertThat(result.executionTimeMillis()).isGreaterThanOrEqualTo(delayMs);
    }

    @Test
    void testBenchmark_shouldComparePerformance() {
        // Given
        int numberOfTasks = 100;

        // When
        VirtualThreadService.BenchmarkResult result = virtualThreadService.benchmark(numberOfTasks);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.numberOfTasks()).isEqualTo(numberOfTasks);
        assertThat(result.virtualThreadsCompleted()).isEqualTo(numberOfTasks);
        assertThat(result.platformThreadsCompleted()).isEqualTo(numberOfTasks);
        assertThat(result.virtualThreadsTimeMs()).isGreaterThan(0);
        assertThat(result.platformThreadsTimeMs()).isGreaterThan(0);
        assertThat(result.speedup()).isGreaterThan(0);
    }

    @Test
    void testExecuteConcurrentTasks_withLargeNumberOfTasks() {
        // Given
        int numberOfTasks = 1000;

        // When
        List<TaskResult> results = virtualThreadService.executeConcurrentTasks(numberOfTasks);

        // Then
        assertThat(results).isNotNull();
        assertThat(results).hasSize(numberOfTasks);
        long successfulTasks = results.stream().filter(TaskResult::success).count();
        assertThat(successfulTasks).isEqualTo(numberOfTasks);
    }

    @Test
    void testTaskResult_shouldHaveExecutionTime() {
        // Given
        int numberOfTasks = 5;

        // When
        List<TaskResult> results = virtualThreadService.executeConcurrentTasks(numberOfTasks);

        // Then
        assertThat(results).allMatch(result -> result.executionTimeMillis() > 0);
        assertThat(results).allMatch(result -> result.executionTime() != null);
    }
}
