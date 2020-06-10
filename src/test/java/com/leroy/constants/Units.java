package com.leroy.constants;

public enum Units {
    EA("шт.");

    Units(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }
}
