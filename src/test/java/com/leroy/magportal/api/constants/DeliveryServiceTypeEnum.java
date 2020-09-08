package com.leroy.magportal.api.constants;

public enum DeliveryServiceTypeEnum {
    PICKUP("PICKUP", "Самовывоз"),
    DELIVERY_ENTRANCE("MTK2", "ДО ПОДЪЕЗДА"),
    DELIVERY_DOOR("MTK2", "В КОМНАТУ"),
    DELIVERY_PVZ("PVZ", "ПВЗ"),
    COURIER("COURIER", "Курьерская доставка");

    private final String service;
    private final String type;

    DeliveryServiceTypeEnum(String service, String type) {
        this.service = service;
        this.type = type;
    }

    public String getService() {
        return service;
    }

    public String getType() {
        return type;
    }
}
