package com.monghit.java21test.features.recordpatterns.model;

/**
 * Record que representa un empleado.
 *
 * Demuestra records anidados para usar con Record Patterns.
 */
public record Employee(
    String name,
    int age,
    Address address,
    Department department,
    double salary
) {
    public Employee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (age < 18 || age > 100) {
            throw new IllegalArgumentException("Age must be between 18 and 100");
        }
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }
}
