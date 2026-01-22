package com.monghit.java21test.features.unnamedpatterns;

import com.monghit.java21test.features.unnamedpatterns.service.UnnamedPatternService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UnnamedPatternServiceTest {

    @Autowired
    private UnnamedPatternService service;

    @Test
    void testUnnamedInException() {
        String result = service.demonstrateUnnamedInException();

        assertThat(result).contains("ArithmeticException");
    }

    @Test
    void testUnnamedInLoop() {
        int count = service.demonstrateUnnamedInLoop();

        assertThat(count).isEqualTo(10);
    }

    @Test
    void testUnnamedInSwitch() {
        String result = service.demonstrateUnnamedInSwitch("test");

        assertThat(result).contains("string");
    }

    @Test
    void testProcessTuple() {
        var tuple = new UnnamedPatternService.DataTuple("Test", "ignored", 150);
        var result = service.processTuple(tuple);

        assertThat(result.category()).isEqualTo("High value");
    }
}
