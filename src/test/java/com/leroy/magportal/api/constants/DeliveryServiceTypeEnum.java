package com.leroy.magportal.api.constants;

public enum DeliveryServiceTypeEnum {
    PICKUP("PICKUP", "Самовывоз", ""),
    DELIVERY_ENTRANCE("MTK2", "ДО ПОДЪЕЗДА", "TO_ENTRANCE"),
    DELIVERY_DOOR("MTK2", "В КОМНАТУ", "TO_DOOR"),
    DELIVERY_PVZ("PVZ", "ПВЗ", ""),
    COURIER("COURIER", "Курьерская доставка", "TO_DOOR");

    private final String service;
    private final String type;
    private final String aemCode;

    DeliveryServiceTypeEnum(String service, String type, String aemCode) {
        this.service = service;
        this.type = type;
        this.aemCode = aemCode;
    }

    public String getService() {
        return service;
    }

    public String getType() {
        return type;
    }

    public String getAemCode() { return aemCode; }
}
