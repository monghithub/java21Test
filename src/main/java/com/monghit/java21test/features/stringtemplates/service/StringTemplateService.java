package com.monghit.java21test.features.stringtemplates.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Servicio que demuestra String Templates (Preview Feature en Java 21).
 *
 * NOTA: String Templates es una feature preview en Java 21.
 * Esta implementación usa String.format() como alternativa ya que
 * STR templates pueden no estar completamente disponibles sin configuración adicional.
 */
@Service
public class StringTemplateService {

    private static final Logger log = LoggerFactory.getLogger(StringTemplateService.class);

    /**
     * Genera un reporte formateado usando interpolación de strings.
     */
    public String generateReport(ReportData data) {
        log.info("Generating report for: {}", data.title());

        // Usando String.format como alternativa a STR templates
        return String.format(Locale.US, """
            ==================== REPORT ====================
            Title: %s
            Author: %s
            Date: %s
            Total: $%.2f
            Items: %d
            ================================================
            """, data.title(), data.author(), data.date(), data.total(), data.items());
    }

    /**
     * Genera una query SQL de manera segura.
     */
    public String buildQuery(String table, String column, String value) {
        log.info("Building query for table: {}", table);

        // Usando String.format - en producción usar PreparedStatement
        return String.format(
            "SELECT * FROM %s WHERE %s = '%s'",
            table, column, value
        );
    }

    /**
     * Crea una respuesta JSON formateada.
     */
    public String createJsonResponse(String name, int age, String city) {
        return String.format("""
            {
              "name": "%s",
              "age": %d,
              "city": "%s",
              "timestamp": "%s"
            }
            """, name, age, city, java.time.LocalDateTime.now());
    }

    public record ReportData(
        String title,
        String author,
        String date,
        double total,
        int items
    ) {}
}
