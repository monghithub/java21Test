package com.monghit.java21test.features.stringtemplates;

import com.monghit.java21test.features.stringtemplates.service.StringTemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StringTemplateServiceTest {

    @Autowired
    private StringTemplateService service;

    @Test
    void testGenerateReport() {
        var data = new StringTemplateService.ReportData("Monthly Report", "John Doe", "2024-01-15", 1500.50, 10);

        String report = service.generateReport(data);

        assertThat(report).contains("Monthly Report");
        assertThat(report).contains("John Doe");
        assertThat(report).contains("1500.50");
    }

    @Test
    void testBuildQuery() {
        String query = service.buildQuery("users", "name", "Alice");

        assertThat(query).contains("SELECT");
        assertThat(query).contains("users");
        assertThat(query).contains("Alice");
    }

    @Test
    void testCreateJsonResponse() {
        String json = service.createJsonResponse("Bob", 30, "New York");

        assertThat(json).contains("Bob");
        assertThat(json).contains("30");
        assertThat(json).contains("New York");
    }
}
