package com.monghit.java21test.features.scopedvalues;

import com.monghit.java21test.features.scopedvalues.service.ScopedValueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ScopedValueServiceTest {

    @Autowired
    private ScopedValueService service;

    @Test
    void testProcessWithContext() {
        var context = new ScopedValueService.RequestContext("REQ-123", "user-456", "2024-01-15T10:00:00");

        String result = service.processWithContext(context);

        assertThat(result).contains("REQ-123");
        assertThat(result).contains("user-456");
    }

    @Test
    void testNestedScopes() {
        var outer = new ScopedValueService.RequestContext("OUTER-1", "user-1", "2024-01-15T10:00:00");
        var inner = new ScopedValueService.RequestContext("INNER-2", "user-2", "2024-01-15T10:01:00");

        String result = service.nestedScopes(outer, inner);

        assertThat(result).contains("OUTER-1");
        assertThat(result).contains("INNER-2");
    }
}
