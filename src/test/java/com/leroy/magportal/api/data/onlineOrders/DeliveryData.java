package com.leroy.magportal.api.data.onlineOrders;

import lombok.Data;

@Data
public class DeliveryData {

    private String id;
    private Double deliveryServicePrice;
    private Double liftupServicePrice;
    private Double totalServicePrice;
    private Double totalAmount;
    private String liftType;
    private Boolean manualProcessing;
    private String payerType;
    private String planDate;
    private String referenceStoreId;
    private String shipFromShopId;
    private String status;
    private String tariff;
    private DeliveryCustomerData receiver;
    private ShipToData shipTo;
    private DeliveryCustomerData customer;
}
