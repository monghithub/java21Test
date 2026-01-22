package com.monghit.java21test.features.scopedvalues.controller;

import com.monghit.java21test.common.dto.ApiResponse;
import com.monghit.java21test.features.scopedvalues.service.ScopedValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scoped-values")
@Tag(name = "Scoped Values", description = "APIs para demostrar Scoped Values (Preview)")
public class ScopedValueController {

    private final ScopedValueService service;

    public ScopedValueController(ScopedValueService service) {
        this.service = service;
    }

    @PostMapping("/process-request")
    @Operation(summary = "Procesar request con contexto")
    public ResponseEntity<ApiResponse<String>> processRequest(
            @RequestBody ScopedValueService.RequestContext context) {
        return ResponseEntity.ok(
            ApiResponse.success("Scoped Values - Process", service.processWithContext(context))
        );
    }

    @PostMapping("/nested-scopes")
    @Operation(summary = "Demostrar scopes anidados")
    public ResponseEntity<ApiResponse<String>> nestedScopes(@RequestBody NestedRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success("Scoped Values - Nested",
                service.nestedScopes(request.outerContext(), request.innerContext()))
        );
    }

    public record NestedRequest(
        ScopedValueService.RequestContext outerContext,
        ScopedValueService.RequestContext innerContext
    ) {}
}
