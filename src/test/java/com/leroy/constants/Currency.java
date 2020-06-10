package com.leroy.constants;

public enum Currency {
    RUB("₽");

    Currency(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }
}
