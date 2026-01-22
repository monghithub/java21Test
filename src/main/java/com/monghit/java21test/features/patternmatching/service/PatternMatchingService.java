package com.monghit.java21test.features.patternmatching.service;

import com.monghit.java21test.features.patternmatching.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Servicio que demuestra Pattern Matching for Switch en Java 21.
 *
 * Pattern Matching permite escribir código más conciso y seguro cuando se trabaja
 * con jerarquías de tipos, especialmente con sealed interfaces/classes.
 */
@Service
public class PatternMatchingService {

    private static final Logger log = LoggerFactory.getLogger(PatternMatchingService.class);

    /**
     * Calcula el área de una forma usando pattern matching en switch.
     *
     * Demuestra:
     * - Pattern matching con sealed interfaces
     * - Switch expressions
     * - Type patterns
     */
    public double calculateArea(Shape shape) {
        log.info("Calculating area for shape: {}", shape.getClass().getSimpleName());

        // Pattern matching for switch - Java 21
        return switch (shape) {
            case Circle c -> {
                log.debug("Circle with radius: {}", c.radius());
                yield c.area();
            }
            case Rectangle r -> {
                log.debug("Rectangle with width: {} and height: {}", r.width(), r.height());
                yield r.area();
            }
            case Triangle t -> {
                log.debug("Triangle with sides: {}, {}, {}", t.side1(), t.side2(), t.side3());
                yield t.area();
            }
        };
    }

    /**
     * Describe una forma usando pattern matching con guards (when clauses).
     *
     * Demuestra:
     * - Pattern matching con guards
     * - Deconstrucción de records en patterns
     * - Switch expressions con múltiples condiciones
     */
    public String describeShape(Shape shape) {
        log.info("Describing shape: {}", shape.getClass().getSimpleName());

        return switch (shape) {
            case Circle c when c.radius() < 5 ->
                String.format(Locale.US, "Small circle with radius %.2f", c.radius());

            case Circle c when c.radius() >= 5 && c.radius() < 10 ->
                String.format(Locale.US, "Medium circle with radius %.2f", c.radius());

            case Circle c ->
                String.format(Locale.US, "Large circle with radius %.2f", c.radius());

            case Rectangle r when r.isSquare() ->
                String.format(Locale.US, "Square with side %.2f", r.width());

            case Rectangle r when r.width() > r.height() ->
                String.format(Locale.US, "Horizontal rectangle (%.2f x %.2f)", r.width(), r.height());

            case Rectangle r ->
                String.format(Locale.US, "Vertical rectangle (%.2f x %.2f)", r.width(), r.height());

            case Triangle t when t.isEquilateral() ->
                String.format(Locale.US, "Equilateral triangle with side %.2f", t.side1());

            case Triangle t when t.isIsosceles() ->
                String.format(Locale.US, "Isosceles triangle with sides %.2f, %.2f, %.2f",
                    t.side1(), t.side2(), t.side3());

            case Triangle t ->
                String.format(Locale.US, "Scalene triangle with sides %.2f, %.2f, %.2f",
                    t.side1(), t.side2(), t.side3());
        };
    }

    /**
     * Valida una forma usando pattern matching.
     *
     * Demuestra:
     * - Pattern matching con validaciones
     * - Guards complejos
     */
    public ValidationResult validateShape(Shape shape) {
        log.info("Validating shape: {}", shape.getClass().getSimpleName());

        boolean isValid = switch (shape) {
            case Circle c -> c.radius() > 0 && c.radius() < 1000;
            case Rectangle r -> r.width() > 0 && r.height() > 0 &&
                               r.width() < 1000 && r.height() < 1000;
            case Triangle t -> t.side1() > 0 && t.side2() > 0 && t.side3() > 0 &&
                              t.side1() < 1000 && t.side2() < 1000 && t.side3() < 1000;
        };

        String message = isValid ? "Shape is valid" : "Shape dimensions are out of range";
        return new ValidationResult(isValid, message, shape.type());
    }

    /**
     * Categoriza una forma por su tamaño usando pattern matching.
     */
    public String categorizeBySize(Shape shape) {
        double area = shape.area();

        return switch (shape) {
            case Circle c when area < 50 -> "Small circle";
            case Circle c when area < 200 -> "Medium circle";
            case Circle c -> "Large circle";

            case Rectangle r when area < 100 -> "Small rectangle";
            case Rectangle r when area < 500 -> "Medium rectangle";
            case Rectangle r -> "Large rectangle";

            case Triangle t when area < 50 -> "Small triangle";
            case Triangle t when area < 200 -> "Medium triangle";
            case Triangle t -> "Large triangle";
        };
    }

    /**
     * Demuestra pattern matching con tipos primitivos y objetos.
     */
    public String describeObject(Object obj) {
        return switch (obj) {
            case null -> "null object";
            case String s when s.isEmpty() -> "empty string";
            case String s -> String.format("String with length %d: %s", s.length(), s);
            case Integer i when i < 0 -> String.format("negative integer: %d", i);
            case Integer i when i == 0 -> "zero";
            case Integer i -> String.format("positive integer: %d", i);
            case Double d -> String.format(Locale.US, "double: %.2f", d);
            case Shape s -> String.format(Locale.US, "Shape: %s with area %.2f", s.type(), s.area());
            default -> String.format("unknown type: %s", obj.getClass().getSimpleName());
        };
    }

    /**
     * Compara dos formas usando pattern matching.
     */
    public ComparisonResult compareShapes(Shape shape1, Shape shape2) {
        log.info("Comparing {} with {}", shape1.type(), shape2.type());

        double area1 = shape1.area();
        double area2 = shape2.area();
        double difference = Math.abs(area1 - area2);
        double percentageDiff = (difference / Math.min(area1, area2)) * 100;

        String comparison = switch (shape1) {
            case Circle c1 when shape2 instanceof Circle c2 ->
                String.format(Locale.US, "Both circles: radius %.2f vs %.2f", c1.radius(), c2.radius());

            case Rectangle r1 when shape2 instanceof Rectangle r2 ->
                String.format(Locale.US, "Both rectangles: %.2fx%.2f vs %.2fx%.2f",
                    r1.width(), r1.height(), r2.width(), r2.height());

            case Triangle t1 when shape2 instanceof Triangle t2 ->
                String.format(Locale.US, "Both triangles: area %.2f vs %.2f", t1.area(), t2.area());

            default ->
                String.format("Different shapes: %s vs %s", shape1.type(), shape2.type());
        };

        String winner = area1 > area2 ? shape1.type() : shape2.type();

        return new ComparisonResult(
            shape1.type(),
            shape2.type(),
            area1,
            area2,
            difference,
            percentageDiff,
            comparison,
            winner
        );
    }

    /**
     * Record para resultado de validación.
     */
    public record ValidationResult(
        boolean valid,
        String message,
        String shapeType
    ) {}

    /**
     * Record para resultado de comparación.
     */
    public record ComparisonResult(
        String shape1Type,
        String shape2Type,
        double shape1Area,
        double shape2Area,
        double areaDifference,
        double percentageDifference,
        String comparison,
        String largerShape
    ) {}
}
