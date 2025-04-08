package org.refactoring.patterns.Refactoring2.maketest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refactoring.patterns.Refactoring2.RefactoringStage;
import org.refactoring.patterns.Refactoring2.RefactoringStageExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(RefactoringStageExtension.class)
class NoProducersTest {
    Province noProducers;

    @BeforeEach
    void setUp() {
        Map<String, Object> data = Map.of(
                "name", "No producers",
                "producers", List.of(),
                "demand", 30,
                "price", 20
        );
        noProducers = new Province(data);
    }

    @Test
    @RefactoringStage("Refactored3")
    void shortfall() {
        assertEquals(30, noProducers.getShortfall());
    }

    @Test
    @RefactoringStage("Refactored3")
    void profit() {
        assertEquals(0, noProducers.getProfit());
    }
}
