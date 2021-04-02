package com.leroy.magportal.api.constants;

public enum ShopTypeEnum {
    CIZ(62),
    KK(62),
    TK(139),
    CDS(141),
    PVZ(139),
    AK(141);//Адресное хранение

    private final Integer value;

    ShopTypeEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
