package com.leroy.umbrella_extension.magmobile.enums;

public enum CatalogSearchFields {

    LM_CODE("lmCode"),
    NAME("name"),
    AVAILABLE_STOCK("availableStock");

    private String name;

    CatalogSearchFields(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
