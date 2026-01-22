package com.monghit.java21test.features.recordpatterns;

import com.monghit.java21test.features.recordpatterns.model.Address;
import com.monghit.java21test.features.recordpatterns.model.Department;
import com.monghit.java21test.features.recordpatterns.model.Employee;
import com.monghit.java21test.features.recordpatterns.service.RecordPatternService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RecordPatternServiceTest {

    @Autowired
    private RecordPatternService recordPatternService;

    private Employee createTestEmployee(String name, int age, String city, double salary) {
        Address address = new Address("123 Main St", city, "12345", "USA");
        Department dept = new Department("Engineering", "ENG", "John Manager");
        return new Employee(name, age, address, dept, salary);
    }

    @Test
    void testAnalyzeEmployee_seniorLevel() {
        Employee employee = createTestEmployee("Alice", 35, "New York", 120000);

        String analysis = recordPatternService.analyzeEmployee(employee);

        assertThat(analysis).contains("Senior employee");
        assertThat(analysis).contains("Alice");
    }

    @Test
    void testExtractCityFromEmployee() {
        Employee employee = createTestEmployee("Bob", 28, "San Francisco", 75000);

        String info = recordPatternService.extractCityFromEmployee(employee);

        assertThat(info).contains("Bob");
        assertThat(info).contains("San Francisco");
    }

    @Test
    void testFilterByCity() {
        List<Employee> employees = List.of(
            createTestEmployee("Alice", 30, "New York", 80000),
            createTestEmployee("Bob", 25, "San Francisco", 70000),
            createTestEmployee("Charlie", 35, "New York", 90000)
        );

        List<Employee> filtered = recordPatternService.filterByCity(employees, "New York");

        assertThat(filtered).hasSize(2);
        assertThat(filtered).extracting(Employee::name).containsExactlyInAnyOrder("Alice", "Charlie");
    }

    @Test
    void testGenerateReport() {
        Employee employee = createTestEmployee("Alice", 35, "New York", 120000);

        RecordPatternService.EmployeeReport report = recordPatternService.generateReport(employee);

        assertThat(report.name()).isEqualTo("Alice");
        assertThat(report.salaryBracket()).isEqualTo("Senior level");
    }
}
