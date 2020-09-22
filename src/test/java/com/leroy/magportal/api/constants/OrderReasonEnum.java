package com.leroy.magportal.api.constants;

public enum OrderReasonEnum {
    CLIENT("По решению клиента"),
    ABSENT("Недоступность товара"),
    BROKEN("Бракованный товар"),
    EXAMPLE("Товар на образце"),
    ;

    private final String value;

    OrderReasonEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
