package org.refactoring.patterns.Refactoring2.maketest;

import java.util.*;

public class SampleProvinceData {
    public static Map<String, Object> get() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Asia");
        data.put("demand", 30);
        data.put("price", 20);

        List<Map<String, Object>> producers = new ArrayList<>();
        producers.add(Map.of("name", "Byzantium", "cost", 10, "production", 9));
        producers.add(Map.of("name", "Attalia", "cost", 12, "production", 10));
        producers.add(Map.of("name", "Sinope", "cost", 10, "production", 6));

        data.put("producers", producers);
        return data;
    }
}
