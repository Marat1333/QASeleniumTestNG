package com.leroy.magmobile.api.enums;

public enum  RupturesSessionStatuses {
    ACTIVE_STATUS("active"),
    FINISHED_STATUS("finished");

    private String name;

    RupturesSessionStatuses(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
