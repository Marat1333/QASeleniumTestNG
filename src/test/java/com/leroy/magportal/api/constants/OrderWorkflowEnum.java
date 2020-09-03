package com.leroy.magportal.api.constants;

public enum OrderWorkflowEnum {
    GIVEAWAY("confirm-giveaway"),
    DELIVER("confirm-delivery"),
    CANCEL("cancel-order"),
    EDIT("edit-quantity"),
//    UPDATE("picked-quantity")//TODO: add update delivery data and cost recalculation
    ;

    private final String value;

    OrderWorkflowEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
