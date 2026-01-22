package com.monghit.java21test.features.unnamedpatterns.controller;

import com.monghit.java21test.common.dto.ApiResponse;
import com.monghit.java21test.features.unnamedpatterns.service.UnnamedPatternService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unnamed")
@Tag(name = "Unnamed Patterns", description = "APIs para demostrar Unnamed Patterns and Variables")
public class UnnamedPatternController {

    private final UnnamedPatternService service;

    public UnnamedPatternController(UnnamedPatternService service) {
        this.service = service;
    }

    @GetMapping("/exception")
    @Operation(summary = "Demostrar unnamed en excepciones")
    public ResponseEntity<ApiResponse<String>> demonstrateException() {
        return ResponseEntity.ok(
            ApiResponse.success("Unnamed Patterns - Exception", service.demonstrateUnnamedInException())
        );
    }

    @GetMapping("/loop")
    @Operation(summary = "Demostrar unnamed en loops")
    public ResponseEntity<ApiResponse<Integer>> demonstrateLoop() {
        return ResponseEntity.ok(
            ApiResponse.success("Unnamed Patterns - Loop", service.demonstrateUnnamedInLoop())
        );
    }

    @PostMapping("/switch")
    @Operation(summary = "Demostrar unnamed en switch")
    public ResponseEntity<ApiResponse<String>> demonstrateSwitch(@RequestBody ObjectRequest request) {
        Object obj = convertToObject(request);
        return ResponseEntity.ok(
            ApiResponse.success("Unnamed Patterns - Switch", service.demonstrateUnnamedInSwitch(obj))
        );
    }

    @PostMapping("/process-tuple")
    @Operation(summary = "Procesar tupla con unnamed patterns")
    public ResponseEntity<ApiResponse<UnnamedPatternService.ProcessResult>> processTuple(
            @RequestBody UnnamedPatternService.DataTuple tuple) {
        return ResponseEntity.ok(
            ApiResponse.success("Unnamed Patterns - Tuple", service.processTuple(tuple))
        );
    }

    private Object convertToObject(ObjectRequest request) {
        return switch (request.type().toLowerCase()) {
            case "string" -> request.stringValue();
            case "integer" -> request.intValue();
            default -> request.stringValue();
        };
    }

    public record ObjectRequest(String type, String stringValue, Integer intValue) {}
}
