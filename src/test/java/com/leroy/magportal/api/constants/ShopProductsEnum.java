package com.leroy.magportal.api.constants;

public enum ShopProductsEnum {
    MM(Constants.MM),
    EQ(Constants.EQ),
    CP(Constants.CP),
    CO(Constants.CO);

    private final String value;

    ShopProductsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class Constants {
        public static final String MM = "MM";
        public static final String EQ = "EQ";
        public static final String CP = "CP";
        public static final String CO = "CO";
    }
}
