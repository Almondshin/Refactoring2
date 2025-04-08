package org.refactoring.patterns.Refactoring2.maketest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refactoring.patterns.Refactoring2.RefactoringStage;
import org.refactoring.patterns.Refactoring2.RefactoringStageExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RefactoringStageExtension.class)
class StringProducersTest {
    @Test
    @RefactoringStage("Refactored3")
    void stringInsteadOfProducers() {
        Map<String, Object> data = Map.of(
                "name", "String producers",
                "producers", null,
                "demand", 30,
                "price", 20
        );

        Province prov = new Province(data);
        assertEquals(0, prov.getShortfall());
    }
}