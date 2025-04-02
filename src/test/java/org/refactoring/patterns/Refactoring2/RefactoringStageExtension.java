package org.refactoring.patterns.Refactoring2;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RefactoringStageExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Map<String, Long> startTimes = new HashMap<>();

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        RefactoringStage stage = testMethod.getAnnotation(RefactoringStage.class);
        if (stage != null) {
            String stageName = stage.value();
            System.out.println("=== Refactoring Step: \"" + stageName + "\" Test Start ===");
            startTimes.put(testMethod.getName(), System.nanoTime());
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        RefactoringStage stage = testMethod.getAnnotation(RefactoringStage.class);
        if (stage != null) {
            String stageName = stage.value();
            String testName = testMethod.getName();

            long startTime = startTimes.getOrDefault(testName, 0L);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;

            System.out.println("=== Refactoring Step : \"" + stageName + "\" Test End ===");
            System.out.println("실행 시간: " + duration + "ms");
        }
    }




}