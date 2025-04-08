package org.refactoring.patterns.Refactoring2.maketest;

import java.util.*;
import java.util.stream.Collectors;

public class Province {
    private final String name;
    private final List<Producer> producers = new ArrayList<>();
    private int totalProduction = 0;
    private int demand;
    private int price;

    public Province(Map<String, Object> doc) {
        this.name = (String) doc.get("name");
        this.demand = (int) doc.get("demand");
        this.price = (int) doc.get("price");

        List<Map<String, Object>> producersData = (List<Map<String, Object>>) doc.get("producers");
        if (producersData != null) {
            for (Map<String, Object> d : producersData) {
                this.addProducer(new Producer(this, d));
            }
        }
    }

    public void addProducer(Producer producer) {
        producers.add(producer);
        totalProduction += producer.getProduction();
    }

    public String getName() {
        return name;
    }

    public List<Producer> getProducers() {
        return new ArrayList<>(producers);
    }

    public int getTotalProduction() {
        return totalProduction;
    }

    public void setTotalProduction(int totalProduction) {
        this.totalProduction = totalProduction;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public void setDemandString(String demandStr) {
        try {
            this.demand = Integer.parseInt(demandStr);
        } catch (NumberFormatException e) {
            this.demand = (int) Double.NaN;
        }
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getShortfall() {
        return demand - totalProduction;
    }

    public int getProfit() {
        return getDemandValue() - getDemandCost();
    }

    public int getDemandValue() {
        return getSatisfiedDemand() * price;
    }

    public int getSatisfiedDemand() {
        return Math.min(demand, totalProduction);
    }

    public int getDemandCost() {
        int remainingDemand = demand;
        int result = 0;
        List<Producer> sorted = producers.stream()
            .sorted(Comparator.comparingInt(Producer::getCost))
            .collect(Collectors.toList());

        for (Producer p : sorted) {
            int contribution = Math.min(remainingDemand, p.getProduction());
            remainingDemand -= contribution;
            result += contribution * p.getCost();
        }
        return result;
    }
}
