package com.monghit.java21test.features.patternmatching.model;

/**
 * Record que representa un triángulo.
 *
 * Implementa la sealed interface Shape y combina records con pattern matching.
 */
public record Triangle(double side1, double side2, double side3) implements Shape {

    public Triangle {
        if (side1 <= 0 || side2 <= 0 || side3 <= 0) {
            throw new IllegalArgumentException("All sides must be positive");
        }
        if (!isValidTriangle(side1, side2, side3)) {
            throw new IllegalArgumentException("Invalid triangle: sum of any two sides must be greater than the third");
        }
    }

    private static boolean isValidTriangle(double a, double b, double c) {
        return (a + b > c) && (a + c > b) && (b + c > a);
    }

    @Override
    public double area() {
        // Fórmula de Herón
        double s = (side1 + side2 + side3) / 2;
        return Math.sqrt(s * (s - side1) * (s - side2) * (s - side3));
    }

    @Override
    public double perimeter() {
        return side1 + side2 + side3;
    }

    @Override
    public String type() {
        return "Triangle";
    }

    public boolean isEquilateral() {
        return side1 == side2 && side2 == side3;
    }

    public boolean isIsosceles() {
        return side1 == side2 || side2 == side3 || side1 == side3;
    }
}
