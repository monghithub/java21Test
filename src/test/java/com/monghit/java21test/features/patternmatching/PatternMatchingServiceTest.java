package com.monghit.java21test.features.patternmatching;

import com.monghit.java21test.features.patternmatching.model.*;
import com.monghit.java21test.features.patternmatching.service.PatternMatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para PatternMatchingService.
 *
 * Verifica que el pattern matching funcione correctamente.
 */
@SpringBootTest
class PatternMatchingServiceTest {

    @Autowired
    private PatternMatchingService patternMatchingService;

    @Test
    void testCalculateArea_withCircle() {
        // Given
        Circle circle = new Circle(5.0);

        // When
        double area = patternMatchingService.calculateArea(circle);

        // Then
        assertThat(area).isCloseTo(Math.PI * 25, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void testCalculateArea_withRectangle() {
        // Given
        Rectangle rectangle = new Rectangle(4.0, 6.0);

        // When
        double area = patternMatchingService.calculateArea(rectangle);

        // Then
        assertThat(area).isEqualTo(24.0);
    }

    @Test
    void testCalculateArea_withTriangle() {
        // Given
        Triangle triangle = new Triangle(3.0, 4.0, 5.0);

        // When
        double area = patternMatchingService.calculateArea(triangle);

        // Then
        assertThat(area).isCloseTo(6.0, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void testDescribeShape_withSmallCircle() {
        // Given
        Circle circle = new Circle(3.0);

        // When
        String description = patternMatchingService.describeShape(circle);

        // Then
        assertThat(description).contains("Small circle");
        assertThat(description).contains("3.00");
    }

    @Test
    void testDescribeShape_withSquare() {
        // Given
        Rectangle square = new Rectangle(5.0, 5.0);

        // When
        String description = patternMatchingService.describeShape(square);

        // Then
        assertThat(description).contains("Square");
        assertThat(description).contains("5.00");
    }

    @Test
    void testDescribeShape_withEquilateralTriangle() {
        // Given
        Triangle triangle = new Triangle(5.0, 5.0, 5.0);

        // When
        String description = patternMatchingService.describeShape(triangle);

        // Then
        assertThat(description).contains("Equilateral");
    }

    @Test
    void testValidateShape_withValidCircle() {
        // Given
        Circle circle = new Circle(10.0);

        // When
        PatternMatchingService.ValidationResult result = patternMatchingService.validateShape(circle);

        // Then
        assertThat(result.valid()).isTrue();
        assertThat(result.shapeType()).isEqualTo("Circle");
    }

    @Test
    void testCategorizeBySize_withSmallCircle() {
        // Given
        Circle circle = new Circle(3.0);

        // When
        String category = patternMatchingService.categorizeBySize(circle);

        // Then
        assertThat(category).isEqualTo("Small circle");
    }

    @Test
    void testCategorizeBySize_withLargeRectangle() {
        // Given
        Rectangle rectangle = new Rectangle(30.0, 40.0);

        // When
        String category = patternMatchingService.categorizeBySize(rectangle);

        // Then
        assertThat(category).isEqualTo("Large rectangle");
    }

    @Test
    void testDescribeObject_withString() {
        // Given
        String text = "Hello Java 21";

        // When
        String description = patternMatchingService.describeObject(text);

        // Then
        assertThat(description).contains("String with length");
        assertThat(description).contains("Hello Java 21");
    }

    @Test
    void testDescribeObject_withInteger() {
        // Given
        Integer number = 42;

        // When
        String description = patternMatchingService.describeObject(number);

        // Then
        assertThat(description).contains("positive integer");
        assertThat(description).contains("42");
    }

    @Test
    void testDescribeObject_withShape() {
        // Given
        Circle circle = new Circle(5.0);

        // When
        String description = patternMatchingService.describeObject(circle);

        // Then
        assertThat(description).contains("Shape: Circle");
        assertThat(description).contains("area");
    }

    @Test
    void testCompareShapes_withTwoCircles() {
        // Given
        Circle circle1 = new Circle(5.0);
        Circle circle2 = new Circle(10.0);

        // When
        PatternMatchingService.ComparisonResult result =
            patternMatchingService.compareShapes(circle1, circle2);

        // Then
        assertThat(result.shape1Type()).isEqualTo("Circle");
        assertThat(result.shape2Type()).isEqualTo("Circle");
        assertThat(result.largerShape()).isEqualTo("Circle");
        assertThat(result.shape2Area()).isGreaterThan(result.shape1Area());
    }
}
