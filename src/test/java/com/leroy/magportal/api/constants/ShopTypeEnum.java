package com.leroy.magportal.api.constants;

public enum ShopTypeEnum {
    CIZ("62"),
    KK("62"),
    TK("139"),
    CDS("141"),
    PVZ("139"),
    AK("141");//Адресное хранение

    private final String value;

    ShopTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
