package com.leroy.magportal.api.constants;

import ru.leroymerlin.qa.core.clients.tunnel.data.enums.DeliveryTo;

public enum DeliveryServiceTypeEnum {
    PICKUP("PICKUP", "Самовывоз", null),
    DELIVERY_ENTRANCE("MTK2", "ДО ПОДЪЕЗДА", DeliveryTo.TO_ENTRANCE),
    DELIVERY_TO_LIFT("MTK2", "ДО ПОДЪЕЗДА", DeliveryTo.TO_LIFT),
    DELIVERY_NO_LANDING("MTK2", "ДО ПОДЪЕЗДА", DeliveryTo.WITHOUT_LANDING),
    DELIVERY_DOOR("MTK2", "В КОМНАТУ", DeliveryTo.TO_DOOR),
    DELIVERY_PVZ("PVZ", "ПВЗ", null),
    COURIER("COURIER", "Курьерская доставка", DeliveryTo.TO_DOOR);

    private final String service;
    private final String type;
    private final DeliveryTo aemCode;

    DeliveryServiceTypeEnum(String service, String type, DeliveryTo aemCode) {
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

    public DeliveryTo getAemCode() { return aemCode; }
}
