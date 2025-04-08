# 리팩터링 2판: Java로 따라가는 실습


## 리팩터링
리팩터링에 대한 원리와 원칙을 읽는 것보다, 예시를 통해 직접 따라가며 배우는 것이 더 와닿았다. 원칙은 너무 일반화되기 쉬워 실제로 어떻게 적용해야 할지 감을 잡기 어렵지만, 구체적인 예시가 있으면 "아, 이렇게 하는 거구나" 하고 명확해진다.

이 프로젝트는 리팩터링 2판에서 저자가 선보이는 과정을 Java로 옮겨와 실습하며 리팩터링의 흐름을 따라가본 결과물이다. <br>
"어떻게, 무엇을, 왜"라는 질문에 얽매이기보다는, 일단 실습을 통해 손으로 익히는 데 초점을 맞췄다. 책의 JavaScript 코드를 Java로 변환하면서, 각 단계별로 테스트를 추가해 동작을 확인했다.

책에서 다루는 예제는 간단한 프로그램이라 모든 리팩터링 기법을 전부 적용할 필요는 없지만, 이 과정을 통해 리팩터링의 핵심 아이디어를 적용해볼 수 있었다.

<details> 
<summary><h3>ch1. 리팩터링 첫번째 예시</h3></summary>
## 시나리오
> 다양한 연극을 외주로 받아 공연하는 **극단**을 배경으로 한다.

- 각 공연은 장르(`tragedy`, `comedy`)와 관객 수에 따라 **비용**을 책정한다.
- 고객에게는 **포인트**를 지급하여 다음 공연에서 **할인** 혜택을 제공한다.

### 1. 공연 요청 처리:
  - 고객이 공연을 요청하면, 연극의 장르와 관객 규모를 기준으로 비용을 계산한다.
  - 현재 극단은 두 가지 장르(비극, 희극)만 공연한다.

### 2. 비용 책정 로직:
  - 비극: 기본료 $400 + 관객 30명 초과 시 인원당 $10 추가.
  - 희극: 기본료 $300 + 관객 20명 초과 시 $100 + 초과 인원당 $5 + 전체 관객 수당 $3 추가.

### 3. 포인트 제도:
  - 공연료와 별개로 포인트를 지급한다.
  - 기본: 관객 30명 초과 시 초과 인원만큼 포인트 적립.

### 4. 희극 추가: 관객 5명당 1포인트 추가.
  - 포인트는 다음 공연 의뢰 시 할인에 사용할 수 있다.

### 5. 청구서 출력:
  - 고객명, 공연별 비용, 총액, 적립 포인트를 포함한 청구서를 생성한다.

## 리팩터링 과정
이 프로젝트는 책의 JavaScript 코드를 Java로 변환하며, 단계별로 리팩터링을 적용한 결과를 보여준다.
각 단계는 JUnit 테스트로 검증되며, `@RefactoringStage` 애너테이션을 활용해 리팩터링 단계를 구분했다.
모든 테스트에 동일한 결과를 검증할 수 있도록 `assertStatementContains(result)`를 구성했다.

### 1. 리팩터링 전: 단일 메서드의 복잡한 로직
- 파일: [`BaseStatementTest.statement()`](https://github.com/Almondshin/Refactoring2/blob/886ae00d2bc3019c3424cc718d6643b1fa47fa4f/src/test/java/org/refactoring/patterns/Refactoring2/BaseStatementTest.java#L59)
- 특징: 모든 계산(비용, 포인트)과 출력 형식이 하나의 메서드에 얽혀 있다.
- 문제점: 로직이 뒤엉켜 수정이 어렵고, 가독성이 떨어진다.

### 2. 1차 리팩터링: 메서드 분리
- 파일: [`BaseStatementTest.statementRefactored1()`](https://github.com/Almondshin/Refactoring2/blob/886ae00d2bc3019c3424cc718d6643b1fa47fa4f/src/test/java/org/refactoring/patterns/Refactoring2/BaseStatementTest.java#L114)
- 변경점:
    - amountFor: 공연별 비용 계산 분리.
    - volumeCreditsFor: 포인트 계산 분리.
    - totalAmount, totalVolumeCredits: 합계 계산 분리.
- 효과: 로직이 모듈화되어 가독성이 좋아지고, 개별 기능 수정이 쉬워졌다.

### 3. 2차 리팩터링: 메서드 분리
- 파일: [`BaseStatementTest.statementRefactored2()`](https://github.com/Almondshin/Refactoring2/blob/886ae00d2bc3019c3424cc718d6643b1fa47fa4f/src/test/java/org/refactoring/patterns/Refactoring2/BaseStatementTest.java#L194)
- 변경점:
  - createStatementData: 데이터를 준비하는 계산 단계 분리.
  - renderPlainText: 출력 형식을 담당.
  - EnrichedPerformance: 공연별 데이터를 구조화.
- 효과: 계산 로직과 출력 형식이 독립적으로 구성, 다른 포맷으로 확장이 쉬워졌다.

### 4. 3차 리팩터링: 다형성 활용
- 파일: [`BaseStatementTest.statementRefactored3()`](https://github.com/Almondshin/Refactoring2/blob/886ae00d2bc3019c3424cc718d6643b1fa47fa4f/src/test/java/org/refactoring/patterns/Refactoring2/BaseStatementTest.java#L283)
- 변경점:
  - PerformanceCalculator 추상 클래스를 도입.
  - TragedyCalculator, ComedyCalculator 서브클래스로 장르별 계산 분리.
- 효과: 새로운 장르 추가 시 서브클래스만 만들면 되므로 확장성이 높아졌다.

## 프로젝트 구조
- [`BaseStatementTest.java`](src/test/java/org/refactoring/patterns/Refactoring2/BaseStatementTest.java): 리팩터링 단계별 메서드와 테스트 포함.
- [`RefactoringStage.java`](src/test/java/org/refactoring/patterns/Refactoring2/RefactoringStage.java): 테스트 단계 구분을 위한 애너테이션.
- [`RefactoringStageExtension.java`](src/test/java/org/refactoring/patterns/Refactoring2/RefactoringStageExtension.java): 테스트 실행 시간 측정 및 단계별 로그 출력.
- [`JavaScript 원본`](src/test/javascript/org/refactoring/patterns/refactoring): refactoring1.js, refactoring2.js, refactoring3.js 등으로 각 단계별 참고.

## 프로젝트 구성

| 파일 | 설명 |
|------|------|
| [`BaseStatementTest.java`](src/test/java/org/refactoring/patterns/Refactoring2/BaseStatementTest.java) | 모든 리팩터링 단계 구현 및 테스트 포함 |
| [`RefactoringStage.java`](src/test/java/org/refactoring/patterns/Refactoring2/RefactoringStage.java) | 리팩터링 단계 구분용 애노테이션 |
| [`RefactoringStageExtension.java`](src/test/java/org/refactoring/patterns/Refactoring2/RefactoringStageExtension.java) | 테스트 실행 시간 측정 및 단계 로그 출력 |
| [`base.js`](src/test/javascript/org/refactoring/patterns/base.js) | 리팩토링 전 JS 버전|
| [`refactoring1.js`](src/test/javascript/org/refactoring/patterns/refactoring/refactoring1.js) | 1단계 JS 버전 |
| [`refactoring2.js`](src/test/javascript/org/refactoring/patterns/refactoring/refactoring2.js) | 2단계 JS 버전 |
| [`refactoring3.js`](src/test/javascript/org/refactoring/patterns/refactoring/refactoring3.js) | 3단계 JS 버전 |
| [`test.js`](src/test/javascript/org/refactoring/patterns/test.js) | JS 전체 테스트 |


```js
> Task :compileJava UP-TO-DATE
> Task :processResources UP-TO-DATE
> Task :classes UP-TO-DATE
> Task :compileTestJava UP-TO-DATE
> Task :processTestResources NO-SOURCE
> Task :testClasses UP-TO-DATE
=== Refactoring Step: "Before" Test Start ===

리팩토링 전 결과:
청구 내역 (고객명: BigCo)
Hamlet: $650.00 (55석)
As You Like It: $580.00 (35석)
Othello: $500.00 (40석)
총액: $1,730.00
적립 포인트: 47점

실행 시간: 15ms
=== Refactoring Step : "Before" Test End ===
=== Refactoring Step: "Refactored1" Test Start ===

1차 리팩토링 결과:
청구 내역 (고객명: BigCo)
Hamlet: $650.00 (55석)
As You Like It: $580.00 (35석)
Othello: $500.00 (40석)
총액: $1,730.00
적립 포인트: 47점

실행 시간: 1ms
=== Refactoring Step : "Refactored1" Test End ===
=== Refactoring Step: "Refactored2" Test Start ===

2차 리팩토링 결과:
청구 내역 (고객명: BigCo)
Hamlet: $650.00 (55석)
As You Like It: $580.00 (35석)
Othello: $500.00 (40석)
총액: $1,730.00
적립 포인트: 47점

실행 시간: 5ms
=== Refactoring Step : "Refactored2" Test End ===
=== Refactoring Step: "CalculatorSubclass" Test Start ===

3차 리팩토링 결과:
청구 내역 (고객명: BigCo)
Hamlet: $650.00 (55석)
As You Like It: $580.00 (35석)
Othello: $500.00 (40석)
총액: $1,730.00
적립 포인트: 47점

실행 시간: 1ms
=== Refactoring Step : "CalculatorSubclass" Test End ===
> Task :test
BUILD SUCCESSFUL in 2s
4 actionable tasks: 1 executed, 3 up-to-date
오후 8:27:14: Execution finished ':test --tests "org.refactoring.patterns.Refactoring2.BaseStatementTest"'.
```

</details>

<details> 
<summary><h3>ch4. 테스트 구축하기</h3></summary>

리팩터링을 제대로 하려면, 테스트 스위트가 뒷바침 되어야 한다.
좋은 테스트를 작성하는 일은 개발 효율을 높여준다.

> 테스트는 단순히 동작 확인을 넘어서,
> **"디자인 방향을 잡고, 디버깅 시간을 줄이며, 회귀 버그를 방지"** 하는 데 도움을 준다.


자가 테스트 코드의 가치 <br>
- 현재 상황을 빠르게 파악할 수 있게 도와준다.
- 설계에 대한 고민을 구체화할 수 있다.
- 대부분의 시간을 디버깅이 아닌 검증에 집중할 수 있다.

> 📌 모든 테스트는 완전히 자동화되어야 하며, 결과까지 자동으로 검사되게 구성해야 한다.

## 회귀 버그 방지

**회귀 버그**란 이전까지 잘 작동하던 기능이 변경 후 문제가 생기는 현상이다.  
리팩터링은 코드 내부 구조를 변경하는 작업이므로, 이를 방지하기 위해 테스트가 반드시 필요하다.

---

## 테스트 프레임워크의 등장

- 켄트 벡과 에릭 감마는 스몰토크 단위 테스트 프레임워크를 Java로 포팅했고,  
  그 결과물이 바로 **JUnit**이다.

- JUnit은 자바 생태계에서 TDD와 단위 테스트의 핵심 도구로 자리잡았다.


---

## 테스트 주도 개발 (TDD)

> 테스트 → 코드 → 리팩터링 순서로 짧은 주기를 반복하며 개발한다.

1. **통과하지 못할 테스트 작성**

2. **해당 테스트를 통과시키는 최소한의 코드 작성**

3. **중복 제거, 네이밍 개선 등 리팩터링 진행**


이 주기를 반복함으로써, 코드 품질과 안정성을 동시에 확보할 수 있다.

---

## 실습 예제 개요

이번 장에서는 비즈니스 로직 코드로 다음 두 클래스를 사용한다:

|클래스|설명|
|---|---|
|`Producer`|생산자 한 명을 표현|
|`Province`|지역 전체를 표현, 여러 생산자를 포함|

- `Province` 객체는 JSON 형식의 데이터로부터 생성된다.

- 테스트를 통해 수익, 수요, 잉여 생산량 등 다양한 로직을 검증할 수 있다.


---

## 테스트 작성 원칙

- **"실패해야 할 상황에서는 반드시 실패하게 만들어야 한다."**

- 문제가 생길 가능성이 높은 **경계 조건**은 집중적으로 테스트해야 한다.

- **버그 리포트가 발생하면**, 해당 버그를 드러내는 테스트를 가장 먼저 작성해야 한다.

- 전체 테스트는 **최소 하루 1회 이상** 실행하는 습관을 들이자.


---

## 테스트의 목적

> 테스트는 코드가 "정상 동작한다"는 것을 보장하려는 게 아니다.  
> 오히려 **"문제가 생길 수 있는 지점"을 빠르게 파악하기 위한 도구**다.

- 즉, **위험요소 중심으로 테스트를 구성**해야 한다.

- 테스트 커버리지가 높다고 해서 무조건 좋은 것은 아니다.

  - 커버리지보다 **의도된 실패 케이스를 잡는 힘**이 중요하다.



</details>