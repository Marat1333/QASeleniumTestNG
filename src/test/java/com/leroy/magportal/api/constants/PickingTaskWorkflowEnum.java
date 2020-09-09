package com.leroy.magportal.api.constants;

public enum PickingTaskWorkflowEnum {
    START("start-picking"),
    PAUSE("pause-picking"),
    RESUME("unpause-picking"),
    COMPLETE("picked-quantity"),
    LOCATE("storage-location");

    private final String value;

    PickingTaskWorkflowEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
