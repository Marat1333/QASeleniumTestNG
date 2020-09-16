package com.leroy.magportal.api.constants;

public enum PickingReasonEnum {
    ABSENT("Недоступность товара"),
    BROKEN("Бракованный товар"),
    EXAMPLE("Товар на образце"),
    ;

    private final String value;

    PickingReasonEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
