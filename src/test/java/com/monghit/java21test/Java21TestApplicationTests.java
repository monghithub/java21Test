package com.monghit.java21test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de contexto de la aplicación Spring Boot.
 *
 * Verifica que el contexto de la aplicación carga correctamente
 * con todas las features de Java 21 configuradas.
 */
@SpringBootTest
class Java21TestApplicationTests {

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring Boot carga correctamente
        // con todas las configuraciones de Java 21
    }

    @Test
    void verifyJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        System.out.println("Running on Java version: " + javaVersion);

        // Verifica que estamos corriendo en Java 21
        assert javaVersion.startsWith("21") : "Expected Java 21, but got: " + javaVersion;
    }
}
