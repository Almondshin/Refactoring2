package org.refactoring.patterns.Refactoring2.maketest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refactoring.patterns.Refactoring2.RefactoringStage;
import org.refactoring.patterns.Refactoring2.RefactoringStageExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(RefactoringStageExtension.class)
class ProvinceTest {
    Province asia;

    @BeforeEach
    void setUp() {
        asia = new Province(SampleProvinceData.get());
    }

    @Test
    @RefactoringStage("Before")
    void shortfall() {
        assertEquals(5, asia.getShortfall());
    }

    @Test
    @RefactoringStage("Before")
    void profit() {
        assertEquals(230, asia.getProfit());
    }

    @Test
    @RefactoringStage("Refactored1")
    void changeProduction() {
        asia.getProducers().get(0).setProduction(20);
        assertEquals(-6, asia.getShortfall());
        assertEquals(292, asia.getProfit());
    }

    @Test
    @RefactoringStage("Refactored1")
    void zeroDemand() {
        asia.setDemand(0);
        assertEquals(-25, asia.getShortfall());
        assertEquals(0, asia.getProfit());
    }

    @Test
    @RefactoringStage("Refactored2")
    void negativeDemand() {
        asia.setDemand(-1);
        assertEquals(-26, asia.getShortfall());
        assertEquals(0, asia.getProfit());
    }

    @Test
    @RefactoringStage("Refactored2")
    void emptyStringDemand() {
        asia.setDemandString("");
        assertTrue(Double.isNaN(asia.getShortfall()));
        assertTrue(Double.isNaN(asia.getProfit()));
    }
}
