package com.monghit.java21test.features.patternmatching.model;

/**
 * Record que representa un rectángulo.
 *
 * Implementa la sealed interface Shape y combina records con pattern matching.
 */
public record Rectangle(double width, double height) implements Shape {

    public Rectangle {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }

    @Override
    public String type() {
        return "Rectangle";
    }

    public boolean isSquare() {
        return width == height;
    }
}
