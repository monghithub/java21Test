package com.monghit.java21test.features.structuredconcurrency.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * Servicio que demuestra Structured Concurrency (Preview Feature en Java 21).
 *
 * NOTA: Structured Concurrency es una feature preview/incubator en Java 21.
 * Esta implementación usa ExecutorService como alternativa.
 */
@Service
public class StructuredConcurrencyService {

    private static final Logger log = LoggerFactory.getLogger(StructuredConcurrencyService.class);

    /**
     * Obtiene datos de múltiples fuentes en paralelo.
     */
    public AggregatedResult fetchParallelData() {
        log.info("Fetching data from multiple sources in parallel");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> future1 = executor.submit(() -> fetchFromSource1());
            Future<String> future2 = executor.submit(() -> fetchFromSource2());
            Future<String> future3 = executor.submit(() -> fetchFromSource3());

            String data1 = future1.get();
            String data2 = future2.get();
            String data3 = future3.get();

            return new AggregatedResult(data1, data2, data3, "Success");

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error fetching parallel data", e);
            return new AggregatedResult("Error", "Error", "Error", e.getMessage());
        }
    }

    private String fetchFromSource1() throws InterruptedException {
        Thread.sleep(100);
        return "Data from source 1";
    }

    private String fetchFromSource2() throws InterruptedException {
        Thread.sleep(150);
        return "Data from source 2";
    }

    private String fetchFromSource3() throws InterruptedException {
        Thread.sleep(120);
        return "Data from source 3";
    }

    public record AggregatedResult(
        String source1Data,
        String source2Data,
        String source3Data,
        String status
    ) {}
}
