package com.monghit.java21test.features.patternmatching.model;

/**
 * Record que representa un círculo.
 *
 * Implementa la sealed interface Shape y combina records con pattern matching.
 */
public record Circle(double radius) implements Shape {

    public Circle {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    public String type() {
        return "Circle";
    }
}
