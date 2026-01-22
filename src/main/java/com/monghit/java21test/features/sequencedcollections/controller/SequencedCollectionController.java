package com.monghit.java21test.features.sequencedcollections.controller;

import com.monghit.java21test.common.dto.ApiResponse;
import com.monghit.java21test.features.sequencedcollections.service.SequencedCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sequenced")
@Tag(name = "Sequenced Collections", description = "APIs para demostrar Sequenced Collections")
public class SequencedCollectionController {

    private final SequencedCollectionService service;

    public SequencedCollectionController(SequencedCollectionService service) {
        this.service = service;
    }

    @GetMapping("/list")
    @Operation(summary = "Demostrar SequencedCollection con List")
    public ResponseEntity<ApiResponse<SequencedCollectionService.SequencedListDemo>> demonstrateList() {
        return ResponseEntity.ok(
            ApiResponse.success("Sequenced Collections - List", service.demonstrateSequencedList())
        );
    }

    @GetMapping("/set")
    @Operation(summary = "Demostrar SequencedSet")
    public ResponseEntity<ApiResponse<SequencedCollectionService.SequencedSetDemo>> demonstrateSet() {
        return ResponseEntity.ok(
            ApiResponse.success("Sequenced Collections - Set", service.demonstrateSequencedSet())
        );
    }

    @GetMapping("/map")
    @Operation(summary = "Demostrar SequencedMap")
    public ResponseEntity<ApiResponse<SequencedCollectionService.SequencedMapDemo>> demonstrateMap() {
        return ResponseEntity.ok(
            ApiResponse.success("Sequenced Collections - Map", service.demonstrateSequencedMap())
        );
    }
}
