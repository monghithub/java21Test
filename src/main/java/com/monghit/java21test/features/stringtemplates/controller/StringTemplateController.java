package com.monghit.java21test.features.stringtemplates.controller;

import com.monghit.java21test.common.dto.ApiResponse;
import com.monghit.java21test.features.stringtemplates.service.StringTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/string-templates")
@Tag(name = "String Templates", description = "APIs para demostrar String Templates (Preview)")
public class StringTemplateController {

    private final StringTemplateService service;

    public StringTemplateController(StringTemplateService service) {
        this.service = service;
    }

    @PostMapping("/format-report")
    @Operation(summary = "Generar reporte formateado")
    public ResponseEntity<ApiResponse<String>> formatReport(
            @RequestBody StringTemplateService.ReportData data) {
        return ResponseEntity.ok(
            ApiResponse.success("String Templates - Report", service.generateReport(data))
        );
    }

    @PostMapping("/sql-query")
    @Operation(summary = "Generar query SQL")
    public ResponseEntity<ApiResponse<String>> buildQuery(@RequestBody QueryRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success("String Templates - SQL",
                service.buildQuery(request.table(), request.column(), request.value()))
        );
    }

    @PostMapping("/json-builder")
    @Operation(summary = "Construir JSON")
    public ResponseEntity<ApiResponse<String>> buildJson(@RequestBody JsonRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success("String Templates - JSON",
                service.createJsonResponse(request.name(), request.age(), request.city()))
        );
    }

    public record QueryRequest(String table, String column, String value) {}
    public record JsonRequest(String name, int age, String city) {}
}
