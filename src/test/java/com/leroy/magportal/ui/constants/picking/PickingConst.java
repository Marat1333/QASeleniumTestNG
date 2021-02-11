package com.leroy.magportal.ui.constants.picking;

public class PickingConst {

    public enum AssemblyType {
        SHOPPING_ROOM, STOCK, SS
    }

    public enum PickingStatus {
        READY_TO_PICKING,
        PICKING,
        PICKING_PAUSE,
        PARTIALLY_PICKED,
        PICKED;
    }

    public enum OrderType {
        ONLINE,
        OFFLINE;
    }

    public enum ClientType {
        ENTITY,
        CLIENT,
        PROFI;
    }

    public enum SaleScheme {
        LT,
        LTD,
        SLT,
        SLTD,
        SLTX;
    }

    public enum DeliveryType {
        PICKUP,
        DELIVERY_TK,
        DELIVERY_PVZ,
        DELIVERY_KK;
    }

    public enum Tag {
        DEBT,
        RISK_OF_NOT_COLLECT,
        RISK_NOT_TO_SHIP,
        PICKING_TODAY,
        FOR_FUTURE_DATES;
    }

}
