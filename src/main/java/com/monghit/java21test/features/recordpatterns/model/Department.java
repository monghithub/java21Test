package com.monghit.java21test.features.recordpatterns.model;

/**
 * Record que representa un departamento.
 */
public record Department(
    String name,
    String code,
    String manager
) {
    public Department {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Department code cannot be null or empty");
        }
    }
}
