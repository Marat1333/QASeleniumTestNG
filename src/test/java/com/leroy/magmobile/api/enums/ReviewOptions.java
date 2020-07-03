package com.leroy.magmobile.api.enums;

public enum ReviewOptions {
    DEFAULT(""),
    TIME_USAGE_LESS_MONTH("Меньше месяца"),
    TIME_USAGE_LESS_HALF_YEAR("От месяца до полугода"),
    TIME_USAGE_LESS_YEAR("От полугода до года"),
    TIME_USAGE_MORE_YEAR("Больше года");

    private String name;

    ReviewOptions(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
