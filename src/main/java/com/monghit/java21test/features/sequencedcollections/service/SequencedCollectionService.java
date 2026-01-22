package com.monghit.java21test.features.sequencedcollections.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio que demuestra Sequenced Collections en Java 21.
 *
 * Sequenced Collections introducen nuevos métodos para trabajar con colecciones ordenadas:
 * - SequencedCollection: addFirst(), addLast(), getFirst(), getLast(), reversed()
 * - SequencedSet
 * - SequencedMap
 */
@Service
public class SequencedCollectionService {

    private static final Logger log = LoggerFactory.getLogger(SequencedCollectionService.class);

    public SequencedListDemo demonstrateSequencedList() {
        log.info("Demonstrating SequencedCollection with List");

        List<String> list = new ArrayList<>();
        list.addFirst("First");
        list.addLast("Last");
        list.addFirst("New First");

        String first = list.getFirst();
        String last = list.getLast();
        List<String> reversed = list.reversed();

        return new SequencedListDemo(list, first, last, reversed);
    }

    public SequencedSetDemo demonstrateSequencedSet() {
        log.info("Demonstrating SequencedSet");

        LinkedHashSet<Integer> set = new LinkedHashSet<>();
        set.addFirst(10);
        set.addLast(20);
        set.addFirst(5);

        Integer first = set.getFirst();
        Integer last = set.getLast();
        SequencedSet<Integer> reversed = set.reversed();

        return new SequencedSetDemo(new ArrayList<>(set), first, last, new ArrayList<>(reversed));
    }

    public SequencedMapDemo demonstrateSequencedMap() {
        log.info("Demonstrating SequencedMap");

        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        Map.Entry<String, Integer> firstEntry = map.firstEntry();
        Map.Entry<String, Integer> lastEntry = map.lastEntry();
        SequencedMap<String, Integer> reversed = map.reversed();

        return new SequencedMapDemo(map, firstEntry, lastEntry, reversed);
    }

    public record SequencedListDemo(
        List<String> list,
        String first,
        String last,
        List<String> reversed
    ) {}

    public record SequencedSetDemo(
        List<Integer> set,
        Integer first,
        Integer last,
        List<Integer> reversed
    ) {}

    public record SequencedMapDemo(
        Map<String, Integer> map,
        Map.Entry<String, Integer> firstEntry,
        Map.Entry<String, Integer> lastEntry,
        Map<String, Integer> reversed
    ) {}
}
