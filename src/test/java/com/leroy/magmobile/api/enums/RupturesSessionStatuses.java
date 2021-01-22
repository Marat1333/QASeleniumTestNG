package com.leroy.magmobile.api.enums;

public enum  RupturesSessionStatuses {
    ACTIVE("active"),
    FINISHED("finished");

    private final String name;

    RupturesSessionStatuses(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
