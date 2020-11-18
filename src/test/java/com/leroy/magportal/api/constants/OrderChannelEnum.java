package com.leroy.magportal.api.constants;

public enum OrderChannelEnum {
    ONLINE("ONLINE"),
    OFFLINE("OFFLINE"),
    ;

    private final String value;

    OrderChannelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
