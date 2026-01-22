package com.monghit.java21test.features.recordpatterns.controller;

import com.monghit.java21test.common.dto.ApiResponse;
import com.monghit.java21test.features.recordpatterns.model.Address;
import com.monghit.java21test.features.recordpatterns.model.Department;
import com.monghit.java21test.features.recordpatterns.model.Employee;
import com.monghit.java21test.features.recordpatterns.service.RecordPatternService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para demostrar Record Patterns.
 */
@RestController
@RequestMapping("/record-patterns")
@Tag(name = "Record Patterns", description = "APIs para demostrar Record Patterns con deconstrucción anidada")
public class RecordPatternController {

    private final RecordPatternService recordPatternService;

    public RecordPatternController(RecordPatternService recordPatternService) {
        this.recordPatternService = recordPatternService;
    }

    @PostMapping("/analyze-employee")
    @Operation(summary = "Analizar empleado", description = "Usa record patterns para analizar un empleado")
    public ResponseEntity<ApiResponse<String>> analyzeEmployee(@RequestBody EmployeeRequest request) {
        Employee employee = createEmployee(request);
        String analysis = recordPatternService.analyzeEmployee(employee);

        return ResponseEntity.ok(ApiResponse.success("Record Patterns - Analyze", analysis));
    }

    @PostMapping("/extract-info")
    @Operation(summary = "Extraer información", description = "Usa record patterns anidados para extraer información")
    public ResponseEntity<ApiResponse<String>> extractInfo(@RequestBody EmployeeRequest request) {
        Employee employee = createEmployee(request);
        String info = recordPatternService.extractCityFromEmployee(employee);

        return ResponseEntity.ok(ApiResponse.success("Record Patterns - Extract Info", info));
    }

    @PostMapping("/filter-by-city")
    @Operation(summary = "Filtrar por ciudad", description = "Filtra empleados por ciudad usando record patterns")
    public ResponseEntity<ApiResponse<List<Employee>>> filterByCity(
            @RequestBody FilterRequest request) {
        List<Employee> employees = request.employees().stream()
            .map(this::createEmployee)
            .toList();

        List<Employee> filtered = recordPatternService.filterByCity(employees, request.filterValue());

        return ResponseEntity.ok(ApiResponse.success("Record Patterns - Filter by City", filtered));
    }

    @PostMapping("/generate-report")
    @Operation(summary = "Generar reporte", description = "Genera reporte detallado usando record patterns")
    public ResponseEntity<ApiResponse<RecordPatternService.EmployeeReport>> generateReport(
            @RequestBody EmployeeRequest request) {
        Employee employee = createEmployee(request);
        RecordPatternService.EmployeeReport report = recordPatternService.generateReport(employee);

        return ResponseEntity.ok(ApiResponse.success("Record Patterns - Report", report));
    }

    @PostMapping("/compare")
    @Operation(summary = "Comparar empleados", description = "Compara dos empleados usando record patterns")
    public ResponseEntity<ApiResponse<RecordPatternService.EmployeeComparison>> compareEmployees(
            @RequestBody CompareRequest request) {
        Employee emp1 = createEmployee(request.employee1());
        Employee emp2 = createEmployee(request.employee2());

        RecordPatternService.EmployeeComparison comparison =
            recordPatternService.compareEmployees(emp1, emp2);

        return ResponseEntity.ok(ApiResponse.success("Record Patterns - Compare", comparison));
    }

    private Employee createEmployee(EmployeeRequest request) {
        Address address = new Address(
            request.street(), request.city(), request.zipCode(), request.country());
        Department department = new Department(
            request.departmentName(), request.departmentCode(), request.manager());
        return new Employee(request.name(), request.age(), address, department, request.salary());
    }

    public record EmployeeRequest(
        String name, int age, String street, String city, String zipCode, String country,
        String departmentName, String departmentCode, String manager, double salary
    ) {}

    public record FilterRequest(List<EmployeeRequest> employees, String filterValue) {}

    public record CompareRequest(EmployeeRequest employee1, EmployeeRequest employee2) {}
}
