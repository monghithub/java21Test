package com.monghit.java21test.features.recordpatterns.service;

import com.monghit.java21test.features.recordpatterns.model.Address;
import com.monghit.java21test.features.recordpatterns.model.Department;
import com.monghit.java21test.features.recordpatterns.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que demuestra Record Patterns en Java 21.
 *
 * Record Patterns permiten deconstruir records directamente en pattern matching,
 * incluyendo deconstrucción anidada de records dentro de records.
 */
@Service
public class RecordPatternService {

    private static final Logger log = LoggerFactory.getLogger(RecordPatternService.class);

    /**
     * Analiza un empleado usando record patterns.
     *
     * Demuestra:
     * - Deconstrucción de records
     * - Pattern matching con records
     */
    public String analyzeEmployee(Employee employee) {
        log.info("Analyzing employee: {}", employee.name());

        // Record pattern - deconstruye el employee
        return switch (employee) {
            case Employee(String name, int age, Address addr, Department dept, double salary)
                when salary > 100000 ->
                String.format("Senior employee %s, age %d, earns $%.2f in %s department",
                    name, age, salary, dept.name());

            case Employee(String name, int age, Address addr, Department dept, double salary)
                when salary > 50000 ->
                String.format("Mid-level employee %s, age %d, earns $%.2f in %s department",
                    name, age, salary, dept.name());

            case Employee(String name, int age, Address addr, Department dept, double salary) ->
                String.format("Junior employee %s, age %d, earns $%.2f in %s department",
                    name, age, salary, dept.name());
        };
    }

    /**
     * Extrae información usando record patterns anidados.
     *
     * Demuestra:
     * - Deconstrucción anidada de records
     * - Acceso directo a campos internos
     */
    public String extractCityFromEmployee(Employee employee) {
        log.info("Extracting city from employee: {}", employee.name());

        // Record pattern anidado - deconstruye employee y address
        if (employee instanceof Employee(String name, int age, Address(String street, String city, String zip, String country), Department dept, double salary)) {
            return String.format("%s lives in %s, %s (ZIP: %s)", name, city, country, zip);
        }

        return "Unable to extract city information";
    }

    /**
     * Filtra empleados por ubicación usando record patterns.
     */
    public List<Employee> filterByCity(List<Employee> employees, String targetCity) {
        log.info("Filtering employees by city: {}", targetCity);

        return employees.stream()
            .filter(emp -> {
                // Usar record pattern para extraer city
                if (emp instanceof Employee(var name, var age, Address(var street, String city, var zip, var country), var dept, var salary)) {
                    return city.equalsIgnoreCase(targetCity);
                }
                return false;
            })
            .collect(Collectors.toList());
    }

    /**
     * Filtra empleados por departamento usando record patterns.
     */
    public List<Employee> filterByDepartment(List<Employee> employees, String deptCode) {
        log.info("Filtering employees by department: {}", deptCode);

        return employees.stream()
            .filter(emp -> {
                // Usar record pattern para extraer department code
                if (emp instanceof Employee(var name, var age, var addr, Department(var deptName, String code, var manager), var salary)) {
                    return code.equalsIgnoreCase(deptCode);
                }
                return false;
            })
            .collect(Collectors.toList());
    }

    /**
     * Genera un reporte detallado usando record patterns anidados.
     */
    public EmployeeReport generateReport(Employee employee) {
        log.info("Generating report for employee: {}", employee.name());

        // Deconstrucción completa con record patterns
        if (employee instanceof Employee(
                String name,
                int age,
                Address(String street, String city, String zip, String country),
                Department(String deptName, String deptCode, String manager),
                double salary)) {

            String ageCategory = age < 30 ? "Young professional" :
                                age < 50 ? "Experienced professional" :
                                "Senior professional";

            String salaryBracket = salary < 50000 ? "Entry level" :
                                  salary < 100000 ? "Mid level" :
                                  "Senior level";

            return new EmployeeReport(
                name,
                age,
                ageCategory,
                String.format("%s, %s %s", street, city, zip),
                country,
                deptName,
                deptCode,
                manager,
                salary,
                salaryBracket
            );
        }

        throw new IllegalStateException("Unable to generate report");
    }

    /**
     * Compara dos empleados usando record patterns.
     */
    public EmployeeComparison compareEmployees(Employee emp1, Employee emp2) {
        log.info("Comparing employees: {} and {}", emp1.name(), emp2.name());

        // Deconstruir ambos empleados
        if (emp1 instanceof Employee(String name1, int age1, Address addr1, Department dept1, double salary1) &&
            emp2 instanceof Employee(String name2, int age2, Address addr2, Department dept2, double salary2)) {

            boolean sameDepartment = dept1.code().equals(dept2.code());
            boolean sameCity = addr1.city().equals(addr2.city());
            double salaryDiff = Math.abs(salary1 - salary2);
            int ageDiff = Math.abs(age1 - age2);

            String comparison = sameDepartment && sameCity ?
                String.format("%s and %s work in the same department and city", name1, name2) :
                sameDepartment ?
                String.format("%s and %s work in the same department but different cities", name1, name2) :
                sameCity ?
                String.format("%s and %s live in the same city but work in different departments", name1, name2) :
                String.format("%s and %s work in different departments and cities", name1, name2);

            return new EmployeeComparison(
                name1,
                name2,
                sameDepartment,
                sameCity,
                salaryDiff,
                ageDiff,
                comparison
            );
        }

        throw new IllegalStateException("Unable to compare employees");
    }

    /**
     * Valida la consistencia de datos de un empleado usando record patterns.
     */
    public ValidationResult validateEmployee(Employee employee) {
        log.info("Validating employee: {}", employee.name());

        // Validar usando record patterns
        if (employee instanceof Employee(
                String name,
                int age,
                Address(String street, String city, String zip, String country),
                Department(String deptName, String deptCode, String manager),
                double salary)) {

            boolean validAge = age >= 18 && age <= 100;
            boolean validSalary = salary > 0 && salary < 1000000;
            boolean validZip = zip != null && !zip.isBlank();
            boolean validDeptCode = deptCode != null && deptCode.matches("[A-Z]{2,5}");

            boolean isValid = validAge && validSalary && validZip && validDeptCode;

            String message = isValid ? "Employee data is valid" :
                String.format("Validation issues: age=%s, salary=%s, zip=%s, deptCode=%s",
                    validAge, validSalary, validZip, validDeptCode);

            return new ValidationResult(isValid, message, name);
        }

        return new ValidationResult(false, "Unable to validate employee", "Unknown");
    }

    /**
     * Record para reporte de empleado.
     */
    public record EmployeeReport(
        String name,
        int age,
        String ageCategory,
        String fullAddress,
        String country,
        String departmentName,
        String departmentCode,
        String manager,
        double salary,
        String salaryBracket
    ) {}

    /**
     * Record para comparación de empleados.
     */
    public record EmployeeComparison(
        String employee1,
        String employee2,
        boolean sameDepartment,
        boolean sameCity,
        double salaryDifference,
        int ageDifference,
        String comparison
    ) {}

    /**
     * Record para resultado de validación.
     */
    public record ValidationResult(
        boolean valid,
        String message,
        String employeeName
    ) {}
}
