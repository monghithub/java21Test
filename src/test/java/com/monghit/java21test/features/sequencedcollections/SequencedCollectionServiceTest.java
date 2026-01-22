package com.monghit.java21test.features.sequencedcollections;

import com.monghit.java21test.features.sequencedcollections.service.SequencedCollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SequencedCollectionServiceTest {

    @Autowired
    private SequencedCollectionService service;

    @Test
    void testSequencedList() {
        var result = service.demonstrateSequencedList();

        assertThat(result.first()).isEqualTo("New First");
        assertThat(result.last()).isEqualTo("Last");
        assertThat(result.reversed()).isNotEmpty();
    }

    @Test
    void testSequencedSet() {
        var result = service.demonstrateSequencedSet();

        assertThat(result.first()).isEqualTo(5);
        assertThat(result.last()).isEqualTo(20);
    }

    @Test
    void testSequencedMap() {
        var result = service.demonstrateSequencedMap();

        assertThat(result.firstEntry().getKey()).isEqualTo("a");
        assertThat(result.lastEntry().getKey()).isEqualTo("c");
    }
}
