package com.leroy.magportal.api.constants;

public enum LmCodeTypeEnum {
    KK("11795347"),
    DIMENSIONAL("18642950"),
    PVZ("11795347");

    private final String value;

    LmCodeTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
