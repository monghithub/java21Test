package com.monghit.java21test.features.structuredconcurrency;

import com.monghit.java21test.features.structuredconcurrency.service.StructuredConcurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StructuredConcurrencyServiceTest {

    @Autowired
    private StructuredConcurrencyService service;

    @Test
    void testFetchParallelData() {
        var result = service.fetchParallelData();

        assertThat(result.status()).isEqualTo("Success");
        assertThat(result.source1Data()).contains("source 1");
        assertThat(result.source2Data()).contains("source 2");
        assertThat(result.source3Data()).contains("source 3");
    }
}
