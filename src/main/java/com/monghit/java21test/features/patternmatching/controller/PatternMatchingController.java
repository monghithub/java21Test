package com.monghit.java21test.features.patternmatching.controller;

import com.monghit.java21test.common.dto.ApiResponse;
import com.monghit.java21test.features.patternmatching.model.*;
import com.monghit.java21test.features.patternmatching.service.PatternMatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para demostrar Pattern Matching for Switch.
 *
 * Pattern Matching es una característica de Java 21 que permite escribir
 * código más expresivo y seguro al trabajar con tipos.
 */
@RestController
@RequestMapping("/pattern-matching")
@Tag(name = "Pattern Matching", description = "APIs para demostrar Pattern Matching for Switch")
public class PatternMatchingController {

    private final PatternMatchingService patternMatchingService;

    public PatternMatchingController(PatternMatchingService patternMatchingService) {
        this.patternMatchingService = patternMatchingService;
    }

    /**
     * Calcula el área de una forma usando pattern matching.
     */
    @PostMapping("/calculate-area")
    @Operation(summary = "Calcular área de una forma",
               description = "Usa pattern matching para calcular el área según el tipo de forma")
    public ResponseEntity<ApiResponse<AreaResult>> calculateArea(@RequestBody ShapeRequest request) {
        Shape shape = createShape(request);
        double area = patternMatchingService.calculateArea(shape);

        AreaResult result = new AreaResult(
            shape.type(),
            area,
            shape.perimeter(),
            patternMatchingService.describeShape(shape)
        );

        return ResponseEntity.ok(
            ApiResponse.success("Pattern Matching - Calculate Area", result)
        );
    }

    /**
     * Describe una forma usando pattern matching con guards.
     */
    @PostMapping("/describe-shape")
    @Operation(summary = "Describir forma",
               description = "Usa pattern matching con guards para describir características de una forma")
    public ResponseEntity<ApiResponse<String>> describeShape(@RequestBody ShapeRequest request) {
        Shape shape = createShape(request);
        String description = patternMatchingService.describeShape(shape);

        return ResponseEntity.ok(
            ApiResponse.success("Pattern Matching - Describe Shape", description)
        );
    }

    /**
     * Valida una forma usando pattern matching.
     */
    @PostMapping("/validate-shape")
    @Operation(summary = "Validar forma",
               description = "Usa pattern matching para validar dimensiones de una forma")
    public ResponseEntity<ApiResponse<PatternMatchingService.ValidationResult>> validateShape(
            @RequestBody ShapeRequest request) {
        Shape shape = createShape(request);
        PatternMatchingService.ValidationResult result = patternMatchingService.validateShape(shape);

        return ResponseEntity.ok(
            ApiResponse.success("Pattern Matching - Validate Shape", result)
        );
    }

    /**
     * Categoriza una forma por tamaño.
     */
    @PostMapping("/categorize")
    @Operation(summary = "Categorizar forma por tamaño",
               description = "Usa pattern matching para categorizar una forma según su área")
    public ResponseEntity<ApiResponse<CategoryResult>> categorizeShape(@RequestBody ShapeRequest request) {
        Shape shape = createShape(request);
        String category = patternMatchingService.categorizeBySize(shape);
        double area = shape.area();

        CategoryResult result = new CategoryResult(shape.type(), area, category);

        return ResponseEntity.ok(
            ApiResponse.success("Pattern Matching - Categorize", result)
        );
    }

    /**
     * Compara dos formas usando pattern matching.
     */
    @PostMapping("/compare")
    @Operation(summary = "Comparar dos formas",
               description = "Usa pattern matching para comparar dos formas")
    public ResponseEntity<ApiResponse<PatternMatchingService.ComparisonResult>> compareShapes(
            @RequestBody CompareRequest request) {
        Shape shape1 = createShape(request.shape1());
        Shape shape2 = createShape(request.shape2());

        PatternMatchingService.ComparisonResult result =
            patternMatchingService.compareShapes(shape1, shape2);

        return ResponseEntity.ok(
            ApiResponse.success("Pattern Matching - Compare Shapes", result)
        );
    }

    /**
     * Demuestra pattern matching con diferentes tipos de objetos.
     */
    @PostMapping("/describe-object")
    @Operation(summary = "Describir objeto genérico",
               description = "Usa pattern matching para describir diferentes tipos de objetos")
    public ResponseEntity<ApiResponse<String>> describeObject(@RequestBody ObjectRequest request) {
        Object obj = convertToObject(request);
        String description = patternMatchingService.describeObject(obj);

        return ResponseEntity.ok(
            ApiResponse.success("Pattern Matching - Describe Object", description)
        );
    }

    /**
     * Crea una forma a partir del request.
     */
    private Shape createShape(ShapeRequest request) {
        return switch (request.type().toLowerCase()) {
            case "circle" -> new Circle(request.radius());
            case "rectangle" -> new Rectangle(request.width(), request.height());
            case "triangle" -> new Triangle(request.side1(), request.side2(), request.side3());
            default -> throw new IllegalArgumentException("Unknown shape type: " + request.type());
        };
    }

    /**
     * Convierte el request a un objeto.
     */
    private Object convertToObject(ObjectRequest request) {
        return switch (request.type().toLowerCase()) {
            case "string" -> request.stringValue();
            case "integer" -> request.intValue();
            case "double" -> request.doubleValue();
            case "shape" -> createShape(request.shapeValue());
            default -> throw new IllegalArgumentException("Unknown object type: " + request.type());
        };
    }

    // DTOs
    public record ShapeRequest(
        String type,
        Double radius,
        Double width,
        Double height,
        Double side1,
        Double side2,
        Double side3
    ) {}

    public record CompareRequest(
        ShapeRequest shape1,
        ShapeRequest shape2
    ) {}

    public record ObjectRequest(
        String type,
        String stringValue,
        Integer intValue,
        Double doubleValue,
        ShapeRequest shapeValue
    ) {}

    public record AreaResult(
        String shapeType,
        double area,
        double perimeter,
        String description
    ) {}

    public record CategoryResult(
        String shapeType,
        double area,
        String category
    ) {}
}
