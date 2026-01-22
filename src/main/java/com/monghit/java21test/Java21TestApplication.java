package com.monghit.java21test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para testear todas las funcionalidades de Java 21.
 *
 * Features incluidas:
 * - Virtual Threads (Project Loom)
 * - Pattern Matching for Switch
 * - Record Patterns
 * - String Templates (Preview)
 * - Sequenced Collections
 * - Unnamed Patterns and Variables
 * - Structured Concurrency (Preview)
 * - Scoped Values (Preview)
 */
@SpringBootApplication
public class Java21TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(Java21TestApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("Java 21 Features Testing Application Started!");
        System.out.println("Swagger UI: http://localhost:8080/api/swagger-ui.html");
        System.out.println("API Docs: http://localhost:8080/api/api-docs");
        System.out.println("Health: http://localhost:8080/api/actuator/health");
        System.out.println("===========================================\n");
    }
}
