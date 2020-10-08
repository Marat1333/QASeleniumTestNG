package com.leroy.magportal.api.data.onlineOrders;

import java.util.List;
import lombok.Data;

@Data
public class DeliveryUpdatePayload {

    private String appointmentStart;
    private String appointmentEnd;
    private String date;
    private String fulfillmentTaskId;
    private String latitude;
    private String longitude;
    private String orderId;
    private String referenceStoreId;
    private List<String> lmCodes;
    private List<String> stores;
    private DeliveryCustomerData receiver;
    private ShipToData shipTo;
}
