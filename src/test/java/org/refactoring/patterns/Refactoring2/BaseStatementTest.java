package org.refactoring.patterns.Refactoring2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ExtendWith(RefactoringStageExtension.class)
public class BaseStatementTest {

    private Play hamlet;
    private Play asYouLikeIt;
    private Play othello;

    private Performance perf1;
    private Performance perf2;
    private Performance perf3;

    private List<Performance> performances;

    private Invoice invoice;

    private Map<String, Play> plays;

    @BeforeEach
    void setUp() {
        // Play 객체 생성
        hamlet = new Play("Hamlet", "tragedy");
        asYouLikeIt = new Play("As You Like It", "comedy");
        othello = new Play("Othello", "tragedy");

        // Performance 객체 생성
        perf1 = new Performance("hamlet", 55);
        perf2 = new Performance("as-like", 35);
        perf3 = new Performance("othello", 40);

        // Performance 리스트 생성
        performances = List.of(perf1, perf2, perf3);

        // Invoice 객체 생성
        invoice = new Invoice("BigCo", performances);

        plays = new HashMap<>();
        plays.put("hamlet", hamlet);
        plays.put("as-like", asYouLikeIt);
        plays.put("othello", othello);
    }

    /**
     * 리팩토링 전 초기 버전: 모든 로직이 하나의 메서드에 포함되어 있음
     */
    private String statement(Invoice invoice) {
        long totalAmount = 0;
        int volumeCredits = 0;
        StringBuilder result = new StringBuilder();
        result.append(String.format("청구 내역 (고객명: %s)\n", invoice.getCustomer()));

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        format.setMinimumFractionDigits(2);

        for (Performance perf : invoice.getPerformances()) {
            Play play = plays.get(perf.getPlayID());
            long thisAmount = 0;

            switch (play.getType()) {
                case "tragedy":
                    thisAmount = 40000;
                    if (perf.getAudience() > 30) {
                        thisAmount += 1000 * (perf.getAudience() - 30);
                    }
                    break;
                case "comedy":
                    thisAmount = 30000;
                    if (perf.getAudience() > 20) {
                        thisAmount += 10000 + 500 * (perf.getAudience() - 20);
                    }
                    thisAmount += 300 * perf.getAudience();
                    break;
                default:
                    throw new IllegalArgumentException("알 수 없는 장르: " + play.getType());
            }

            // 볼륨 크레딧 계산
            volumeCredits += Math.max(perf.getAudience() - 30, 0);
            if ("comedy".equals(play.getType())) {
                volumeCredits += Math.floorDiv(perf.getAudience(), 5);
            }

            result.append(String.format("%s: %s (%d석)\n",
                    play.getName(),
                    format.format(thisAmount / 100.0),
                    perf.getAudience()));
            totalAmount += thisAmount;
        }

        result.append(String.format("총액: %s\n", format.format(totalAmount / 100.0)));
        result.append(String.format("적립 포인트: %d점\n", volumeCredits));

        return result.toString();
    }

    /**
     * 1차 리팩토링: JavaScript refactoring1.js 기반
     * - 로직을 여러 메서드로 분리 (amountFor, volumeCreditsFor, totalAmount, totalVolumeCredits)
     * - 가독성 향상과 모듈화 진행
     */
    private String statementRefactored1(Invoice invoice) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("청구 내역 (고객명: %s)\n", invoice.getCustomer()));

        for (Performance perf : invoice.getPerformances()) {
            result.append(String.format("%s: %s (%d석)\n",
                    playFor(perf).getName(),
                    usd(amountFor(perf)),
                    perf.getAudience()));
        }

        result.append(String.format("총액: %s\n", usd(totalAmount())));
        result.append(String.format("적립 포인트: %d점\n", totalVolumeCredits()));

        return result.toString();
    }

    private String usd(long aNumber) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        format.setMinimumFractionDigits(2);
        return format.format(aNumber / 100.0);
    }

    private Play playFor(Performance aPerformance) {
        return plays.get(aPerformance.getPlayID());
    }

    private long amountFor(Performance aPerformance) {
        long result = 0;

        switch (playFor(aPerformance).getType()) {
            case "tragedy":
                result = 40000;
                if (aPerformance.getAudience() > 30) {
                    result += 1000 * (aPerformance.getAudience() - 30);
                }
                break;
            case "comedy":
                result = 30000;
                if (aPerformance.getAudience() > 20) {
                    result += 10000 + 500 * (aPerformance.getAudience() - 20);
                }
                result += 300 * aPerformance.getAudience();
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 장르: " + playFor(aPerformance).getType());
        }
        return result;
    }

    private int volumeCreditsFor(Performance aPerformance) {
        int result = 0;
        result += Math.max(aPerformance.getAudience() - 30, 0);
        if ("comedy".equals(playFor(aPerformance).getType())) {
            result += Math.floorDiv(aPerformance.getAudience(), 5);
        }
        return result;
    }

    private int totalVolumeCredits() {
        int result = 0;
        for (Performance perf : invoice.getPerformances()) {
            result += volumeCreditsFor(perf);
        }
        return result;
    }

    private long totalAmount() {
        long result = 0;
        for (Performance perf : invoice.getPerformances()) {
            result += amountFor(perf);
        }
        return result;
    }

    @Test
    @RefactoringStage("Before")
    void 리팩토링_전_동작() {
        String result = statement(invoice);
        System.out.println("\n리팩토링 전 결과:\n" + result);
    }

    @Test
    @RefactoringStage("Refactored1")
    void 리팩토링_1차_동작() {
        String result = statementRefactored1(invoice);
        System.out.println("\n리팩토링 전 결과:\n" + result);
    }


    // Play 클래스 - 연극 정보
    static class Play {
        private String name;
        private String type;

        public Play() {}
        public Play(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {return name;}
        public String getType() {return type;}
    }

    // Performance 클래스 - 공연 정보
    static class Performance {
        private String playID;
        private int audience;

        public Performance() {}
        public Performance(String playID, int audience) {
            this.playID = playID;
            this.audience = audience;
        }

        public String getPlayID() {return playID;}
        public int getAudience() {return audience;}
    }

    // Invoice 클래스 - 전체 고객과 공연 정보
    static class Invoice {
        private String customer;
        private List<Performance> performances;

        public Invoice() {}
        public Invoice(String customer, List<Performance> performances) {
            this.customer = customer;
            this.performances = performances;
        }

        public String getCustomer() {return customer;}
        public List<Performance> getPerformances() {return performances;}
    }


}
