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


<details>
<summary><h3>ch6. 기본적인 리팩토링 기법</h3></summary>

> 리팩터링의 핵심은 **작은 스텝**으로 진행하며, 테스트로 기능 변화가 없음을 확인하는 것이다.

## 주요 리팩토링 기법

###  변수 캡슐화
- **설명**: 변수에 직접 접근하는 대신 getter/setter를 통해 캡슐화하여 내부 상태를 보호하고, 접근 제어를 강화한다. getter가 **복제본을 반환**하면 불변성을 보장해 스레드 안전성과 예측 가능성을 높인다.
- **예제**:
  ```java
  public class Order {
      private List<String> items = new ArrayList<>();

      // 복제본 반환으로 불변성 보장
      public List<String> getItems() {
          return new ArrayList<>(items); // 방어적 복사
          // 또는 return Collections.unmodifiableList(items); // 읽기 전용 래퍼
      }

      public void addItem(String item) {
          items.add(item);
      }
  }
  ```
  ```java
  // DTO로 불변 객체 설계
  public record OrderDTO(String id, List<String> items) {
      public OrderDTO {
          items = List.copyOf(items); // 불변 컬렉션
      }
  }
  ```
- **실무 관점**:
  - **장점**: 복제본을 반환하면 객체 내부 상태가 외부에서 바뀌는 걸 막을 수 있어서, 멀티스레드 환경이나 캐시처럼 공유되는 데이터에서는 안정성 측면에서 유리함. 도메인 설계에서도 값 자체가 바뀌면 안 되는 상황(예: 금융, 인증 토큰 등)에 잘 맞음.
  - **단점**: 무조건 복제하면 성능 부담 생김. 특히 대용량 컬렉션을 매번 복사하게 되면 GC 압박도 커지고, 불필요한 오브젝트 생성을 유발해서 오히려 병목이 생기기도 함. 그래서 대부분의 REST API 서버처럼 요청-응답 단위 트랜잭션이 짧고, 공유 상태가 거의 없는 구조에서는 굳이 복제본 안 쓰는 게 보통임.
  - **적용 기준**: 컬렉션(List, Map)을 반환할 땐 외부에서 수정될 가능성을 차단하려고 Collections.unmodifiableList()나 복사본 반환하는 경우가 많음. 반면에 도메인 객체는 애초에 불변으로 설계하는 게 일반적이라 record나 Lombok의 @Value 같은 걸로 처리함.
    ```java
    @Service
    public class UserService {
        public UserDTO getUser(String id) {
            User user = userRepository.findById(id).orElseThrow();
            return new UserDTO(user.getId(), user.getName()); // 복사본 반환
        }
    }
    ```
  - **팁**: 복제할지 말지는 팀 컨벤션으로 정해두는 게 좋음. 예: "컬렉션은 무조건 불변 래퍼로 감싸기"처럼 룰을 정해두면 혼선 줄일 수 있음. 그리고 진짜 민감한 경우엔 JMeter 같은 걸로 성능 체크해서 결정하는 게 안정적.

### 매개변수 객체 만들기
- **설명**: 관련 있는 여러 개의 파라미터(예: startDate, endDate, customerId)를 하나의 객체로 묶어서 가독성 높이고, 검증 로직도 같이 담아서 재사용성과 안정성 챙기는 방식. 특히 파라미터가 3개 이상 넘어가면 객체로 만드는것을 추천
- **예제**:
  ```java
  // 리팩터링 전
  public BigDecimal calculateInvoice(LocalDate startDate, LocalDate endDate, String customerId) {
      // 계산 로직
      return BigDecimal.ZERO;
  }

  // 리팩터링 후: 매개변수 객체 사용
  public record InvoiceParameters(LocalDate startDate, LocalDate endDate, String customerId) {
      public InvoiceParameters {
          Objects.requireNonNull(startDate, "startDate must not be null");
          Objects.requireNonNull(endDate, "endDate must not be null");
          Objects.requireNonNull(customerId, "customerId must not be null");
      }
  }

  public BigDecimal calculateInvoice(InvoiceParameters params) {
      // 계산 로직
      return BigDecimal.ZERO;
  }
  ```
  ```java
  public record CreateOrderRequest(String customerId, LocalDate orderDate, BigDecimal amount) {
      public CreateOrderRequest {
          Objects.requireNonNull(customerId, "customerId must not be null");
          Objects.requireNonNull(orderDate, "orderDate must not be null");
          Objects.requireNonNull(amount, "amount must not be null");
      }
  }

  @RestController
  public class OrderController {
      @PostMapping("/orders")
      public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
          // 주문 처리
          return ResponseEntity.ok().build();
      }
  }
  ```
- **실무 관점**:
  - **장점**: 타입 안정성과 도메인 의미 명확화(예: `String` 대신 `UserName`). Spring REST API에서 DTO로 파라미터 간소화.
  - **한계**: 단순 메서드에서는 오버엔지니어링. 객체 생성 비용은 고성능 요구사항(예: Spring Batch)에서 부담.
  - **적용 기준**: 파라미터 3개 이상이거나 여러 메서드에서 반복되면 객체로 묶음. 공통 검증 로직은 `ValidationUtils`로 분리.
    ```java
    public class ValidationUtils {
        public static boolean isValidName(String value) {
            return value != null && value.matches("[a-zA-Z]+");
        }
    }
    ```
  - **실무 사례**: DDD에서 Value Object(예: `Name`, `OrderId`)로 도메인 모델 강화. 유저/그룹 이름 검증 예시:
    ```java
    public record Name(String value) {
        public Name {
            if (value == null || !value.matches("[a-zA-Z]+")) {
                throw new IllegalArgumentException("Invalid name");
            }
        }
    }

    @Service
    public class ValidationService {
        public boolean validateName(Name name) {
            return true; // 검증은 생성자에서 처리
        }
    }
    ```
  - **팁**: IntelliJ의 "Extract Parameter Object"로 리팩토링 시도, 테스트로 안전성 검증. 팀 내 DTO 사용 기준을 따름.

### 단계 쪼개기 (Split Phase)
- **설명**: 복잡한 로직을 명확한 단계로 분리해 가독성과 유지보수성을 높인다. 중간 데이터 구조를 사용해 단계 간 데이터 전달 명확화.
- **예제**:
  ```java
  public class Compiler {
      public String compile(String source) {
          List<String> tokens = tokenize(source); // 1단계: 토큰화
          SyntaxTree tree = parse(tokens);       // 2단계: 구문 분석
          return generate(tree);                 // 3단계: 코드 생성
      }

      private List<String> tokenize(String source) { /* 토큰화 로직 */ return List.of(); }
      private SyntaxTree parse(List<String> tokens) { /* 파싱 로직 */ return new SyntaxTree(); }
      private String generate(SyntaxTree tree) { /* 코드 생성 로직 */ return ""; }
  }

  record SyntaxTree() {}
  ```
- **실무 관점**:
  - **장점**: 단계별 디버깅 용이, 복잡한 로직(예: 데이터 파이프라인)에서 유용.
  - **한계**: 중간 데이터 구조의 복잡성 증가. 단순 로직에서는 불필요한 추상화.
  - **적용 기준**: 로직이 여러 변환 단계를 거치거나, 테스트/디버깅이 어려운 경우 적용.
  - **실무 사례**: Spring Batch의 ETL(Extract-Transform-Load) 프로세스에서 단계 분리.
    ```java
    @Component
    public class DataProcessor {
        public List<ProcessedData> process(List<RawData> rawData) {
            List<ExtractedData> extracted = extract(rawData); // 1단계
            return transform(extracted);                      // 2단계
        }

        private List<ExtractedData> extract(List<RawData> rawData) { /* 추출 */ return List.of(); }
        private List<ProcessedData> transform(List<ExtractedData> data) { /* 변환 */ return List.of(); }
    }
    ```
  - **팁**: 중간 데이터 구조는 `record`로 간결히 정의, 테스트로 각 단계 검증.

- **테스트 기반 안정성**: JUnit 테스트로 리팩터링 전/후 동일 동작 확인, 작은 스텝 진행.
- **IDE 활용**: IntelliJ 단축키(Alt+Shift+R)로 작업.
- **이름 짓기**: 추출된 함수/변수에 적절한 이름 부여로 가독성과 의도 전달.
- **변수 캡슐화**: 복제본 반환(Immutable) vs. 원본 참조, 성능과 불변성 트레이드오프.
- **매개변수 객체 만들기**: 타입 안정성 vs. 불필요한 데이터 전달, 도메인 특화 vs. 범용성 판단.
- **단계 쪼개기**: 중간 데이터 구조의 복잡성 문제, 컴파일러 같은 복잡 로직에서 유용.
- **리팩터링 기준**: 메서드 라인 수 같은 기계적 기준보다 코드 변화 후 판단, 테스트로 롤백 가능.

## 실습 예제 개요
- **목표**: 책의 JavaScript 예제를 Java로 변환, JUnit 테스트로 검증.
- **대상**: 송장 계산, 데이터 검증 등 간단한 비즈니스 로직에 리팩터링 기법 적용.
- **구성**:
  - 함수 추출: 복잡한 계산 로직을 메서드로 분리.
  - 변수 캡슐화: 내부 상태 보호, getter로 복제본 반환.
  - 매개변수 객체: 연관 파라미터를 DTO/Value Object로 묶음.
  - 단계 쪼개기: 데이터 처리 파이프라인을 단계별로 분리.
- **예제 코드**:
  ```java
  // 송장 계산 리팩터링
  public class InvoiceService {
      public BigDecimal calculate(InvoiceParameters params) {
          // 단계 쪼개기 적용
          AmountData amountData = computeAmount(params);
          return adjustAmount(amountData);
      }

      private AmountData computeAmount(InvoiceParameters params) { /* 계산 */ return new AmountData(); }
      private BigDecimal adjustAmount(AmountData data) { /* 조정 */ return BigDecimal.ZERO; }
  }

  record InvoiceParameters(LocalDate startDate, LocalDate endDate, String customerId) {}
  record AmountData() {}
  ```
  
- **테스트 필수**: JUnit으로 리팩터링 전/후 동일 동작 보장.
- **작은 스텝**: 큰 변경 대신 작은 단위로 리팩터링 후 테스트.
- **도메인 중심**: 매개변수 객체와 캡슐화는 도메인 의미 강화.
- **성능 고려**: 복제본 반환, 객체 생성은 JMeter/Gatling으로 검증.
- **팀 컨벤션**: 리팩토링 기준(DTO 사용, 복제본 반환)을 팀 내 합의.
- **IDE 활용**: IntelliJ의 Extract Method, Extract Parameter Object로 효율성 극대화.


</details>

<details>
<summary><h3>ch7. 캡슐화</h3></summary>

### 시나리오

> 객체 내부의 데이터와 구현 세부 사항을 외부로부터 숨기고, 정의된 인터페이스를 통해 상호작용하는 캡슐화 기법을 다룬다.

- **목표**: 데이터와 로직을 캡슐화하여 의존성을 줄이고, 변경의 영향을 최소화하며, 코드의 일관성과 무결성 보장.
- **주요 기법**:
  - 레코드 캡슐화: 단순 데이터 구조를 객체로 감싸 접근 제어.
  - 컬렉션 캡슐화: 컬렉션 직접 노출 대신 메서드로 관리.
  - 기본형을 객체로: 매직 스트링/넘버를 값 객체로 변환.
  - 위임 숨기기: 내부 객체 참조를 메서드로 감춤.
  - 클래스 추출/인라인: 로직을 적절히 분리하거나 통합.

### 리팩터링 과정

7장은 캡슐화를 통해 객체의 내부 상태를 보호하고, 외부 인터페이스를 명확히 하는 과정을 다룬다. 책의 JavaScript 예제를 Java로 변환하며, JUnit 테스트로 리팩터링 전/후 동일 동작을 검증했다.

#### 1. 레코드 캡슐화

- **목표**: JSON과 유사한 데이터 구조(레코드)를 객체로 감싸 접근 제어.
- **예제**:

    ```java
    // 리팩터링 전: 맵으로 데이터 노출
    public class Customer {
        private Map<String, String> data = new HashMap<>();
        public Map<String, String> getData() { return data; }
    }
    
    // 리팩터링 후: 레코드 캡슐화
    public class Customer {
        private final String name;
        private final String id;
    
        public Customer(String name, String id) {
            this.name = name;
            this.id = id;
        }
    
        public String getName() { return name; }
        public String getId() { return id; }
    }
    ```

- **효과**: 데이터 접근을 getter로 제한, 내부 구조 변경 시 외부 영향 최소화.
- **테스트**:

    ```java
    @Test
    void testCustomerEncapsulation() {
        Customer customer = new Customer("BigCo", "123");
        assertEquals("BigCo", customer.getName());
        assertEquals("123", customer.getId());
    }
    ```


#### 2. 컬렉션 캡슐화

- **목표**: 컬렉션 직접 노출 대신 메서드로 추가/삭제 관리, 불변성 보장.
- **예제**:

    ```java
    // 리팩터링 전: 컬렉션 직접 노출
    public class Order {
        private List<String> items = new ArrayList<>();
        public List<String> getItems() { return items; }
    }
    
    // 리팩터링 후: 컬렉션 캡슐화
    public class Order {
        private final List<String> items = new ArrayList<>();
    
        public List<String> getItems() {
            return Collections.unmodifiableList(items); 
        }
    
        public void addItem(String item) {
            items.add(item);
        }
    
        public void removeItem(String item) {
            items.remove(item);
        }
    }
    ```

- **효과**: 외부에서 컬렉션 수정 불가, 데이터 무결성 보장.
- **테스트**:

    ```java
    @Test
    void testCollectionEncapsulation() {
        Order order = new Order();
        order.addItem("item1");
        assertEquals(List.of("item1"), order.getItems());
        assertThrows(UnsupportedOperationException.class, () -> order.getItems().add("item2"));
    }
    ```

- **실무 팁**: `Collections.unmodifiableList()` 사용으로 간단히 불변성 보장. 대용량 데이터는 복사본 반환 대신 프록시 고려.

#### 3. 기본형을 객체로 바꾸기

- **목표**: 매직 스트링/넘버를 값 객체로 변환해 가독성과 타입 안정성 강화.
- **예제**:

    ```java
    // 리팩터링 전: 매직 스트링 사용
    public class Play {
        private String type; // "tragedy", "comedy"
        public String getType() { return type; }
    }
    
    // 리팩터링 후: Enum 사용
    public enum PlayType {
        TRAGEDY, COMEDY
    }
    
    public class Play {
        private final PlayType type;
    
        public Play(PlayType type) {
            this.type = type;
        }
    
        public PlayType getType() { return type; }
    }
    ```

- **효과**: 컴파일 타임 오류 감지, 코드 가독성 향상.
- **테스트**:

    ```java
    @Test
    void testPlayType() {
        Play play = new Play(PlayType.TRAGEDY);
        assertEquals(PlayType.TRAGEDY, play.getType());
    }
    ```


#### 4. 내부 객체 참조 감추기

- **목표**: 내부 객체 참조를 노출하지 않고 메서드로 감춰 의존성 관리.
- **예제**:

    ```java
    // 리팩터링 전: 내부 객체 노출
    public class Person {
        private Department department;
        public Department getDepartment() { return department; }
    }
    
    // 리팩터링 후: 내부 객체 참조 감추기
    public class Person {
        private final Department department;
    
        public Person(Department department) {
            this.department = department;
        }
    
        public String getManager() {
            return department.getManager();
        }
    }
    
    public class Department {
        private final String manager;
        public Department(String manager) { this.manager = manager; }
        public String getManager() { return manager; }
    }
    ```

- **효과**: 내부 구조 변경 시 외부 영향 최소화, 의존성 감소.
- **테스트**:

    ```java
    @Test
    void testDelegationHiding() {
        Department dept = new Department("John");
        Person person = new Person(dept);
        assertEquals("John", person.getManager());
    }
    ```

- **실무 팁**: 과도한 위임은 중계자 역할 증가로 복잡성 유발. 중계자 제거와 균형 필요.

### 실무 관점

- **캡슐화의 장점**:
  - 객체 간 의존성 감소, 변경 영향 최소화.
  - 컬렉션 캡슐화로 데이터 무결성 보장.
  - 값 객체로 코드 가독성과 안정성 강화.
- **실무 한계**:
  - 무상태 서비스(예: Spring REST API)에서는 캡슐화 적용 제한.
  - 복제본 반환은 성능 문제 유발 가능, JMeter로 검증 권장.
  - 자바스크립트 예제는 자바 환경에서 직관적이지 않을 수 있음.
- **적용 기준**:
  - 컬렉션 반환 시 `Collections.unmodifiableList()` 기본 적용.
  - DTO/값 객체로 도메인 의미 강화, 3개 이상 파라미터는 객체로 묶음.
  - 위임 숨기기는 내부 구조 변경 빈도 높은 경우 유리.
- **팀 컨벤션**:
  - 불변성 처리(복제본 vs. 프록시) 기준 명확화.
  - 리팩터링 후 테스트로 롤백 가능성 확보.
- **IDE 활용**: IntelliJ의 "Encapsulate Fields", "Extract Class"로 작업 효율화.

### 테스트 기반 안정성

- JUnit으로 리팩터링 전/후 동일 동작 검증.
- 경계 조건(빈 컬렉션, 잘못된 입력) 테스트로 안정성 강화.
- `@RefactoringStage` 애너테이션으로 단계별 결과 비교.

### 실행 결과 예시

```bash
> Task :test
=== Refactoring Step: "RecordEncapsulation" Test Start ===
Customer name: BigCo, ID: 123
실행 시간: 10ms
=== Refactoring Step: "CollectionEncapsulation" Test Start ===
Order items: [item1]
UnsupportedOperationException: Cannot modify immutable list
실행 시간: 8ms
=== Refactoring Step: "PrimitiveToObject" Test Start ===
Play type: TRAGEDY
실행 시간: 5ms
=== Refactoring Step: "HideDelegation" Test Start ===
Manager: John
실행 시간: 6ms
BUILD SUCCESSFUL in 1s
```


</details>