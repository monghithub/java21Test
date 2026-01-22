package com.monghit.java21test.features.unnamedpatterns.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio que demuestra Unnamed Patterns and Variables en Java 21.
 *
 * Los unnamed patterns (_) permiten ignorar valores que no se necesitan,
 * haciendo el código más limpio y expresivo.
 */
@Service
public class UnnamedPatternService {

    private static final Logger log = LoggerFactory.getLogger(UnnamedPatternService.class);

    /**
     * Demuestra el uso de unnamed variables en excepciones.
     */
    public String demonstrateUnnamedInException() {
        log.info("Demonstrating unnamed variables in exception handling");

        try {
            int result = 10 / 0;
            return "Success";
        } catch (ArithmeticException _) {
            // Usamos _ porque no necesitamos la excepción
            return "Caught ArithmeticException (exception details not needed)";
        }
    }

    /**
     * Demuestra el uso de unnamed variables en loops.
     */
    public int demonstrateUnnamedInLoop() {
        log.info("Demonstrating unnamed variables in loops");

        int count = 0;
        // Usar _ cuando no necesitamos el valor del elemento
        for (var _ : java.util.List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) {
            count++;
        }
        return count;
    }

    /**
     * Demuestra el uso de unnamed patterns en switch.
     */
    public String demonstrateUnnamedInSwitch(Object obj) {
        return switch (obj) {
            case String _ -> "It's a string (value not needed)";
            case Integer _ -> "It's an integer (value not needed)";
            default -> "It's something else";  // default
        };
    }

    /**
     * Procesa una tupla ignorando algunos valores.
     */
    public ProcessResult processTuple(DataTuple tuple) {
        log.info("Processing tuple with unnamed patterns");

        // Usar unnamed pattern para valores que no necesitamos
        return switch (tuple) {
            case DataTuple(String name, _, int value) when value > 100 ->
                new ProcessResult(name, value, "High value");
            case DataTuple(String name, _, int value) ->
                new ProcessResult(name, value, "Normal value");
        };
    }

    public record DataTuple(String name, String ignoredField, int value) {}

    public record ProcessResult(String name, int value, String category) {}
}
