package com.monghit.java21test.features.structuredconcurrency.controller;

import com.monghit.java21test.common.dto.ApiResponse;
import com.monghit.java21test.features.structuredconcurrency.service.StructuredConcurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/structured-concurrency")
@Tag(name = "Structured Concurrency", description = "APIs para demostrar Structured Concurrency (Preview)")
public class StructuredConcurrencyController {

    private final StructuredConcurrencyService service;

    public StructuredConcurrencyController(StructuredConcurrencyService service) {
        this.service = service;
    }

    @GetMapping("/fetch-parallel")
    @Operation(summary = "Obtener datos en paralelo")
    public ResponseEntity<ApiResponse<StructuredConcurrencyService.AggregatedResult>> fetchParallel() {
        return ResponseEntity.ok(
            ApiResponse.success("Structured Concurrency - Parallel Fetch", service.fetchParallelData())
        );
    }
}
