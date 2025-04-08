package org.refactoring.patterns.Refactoring2.maketest;

import java.util.Map;

public class Producer {
    private final Province province;
    private final String name;
    private int cost;
    private int production;

    public Producer(Province province, Map<String, Object> data) {
        this.province = province;
        this.name = (String) data.get("name");
        this.cost = (int) data.get("cost");
        this.production = (int) data.getOrDefault("production", 0);
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getProduction() {
        return production;
    }

    public void setProduction(int newProduction) {
        province.setTotalProduction(
            province.getTotalProduction() - this.production + newProduction
        );
        this.production = newProduction;
    }
}
