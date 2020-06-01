package com.leroy.constants;

public enum Units {
    ITEM("EA");

    Units(String name){
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }
}
