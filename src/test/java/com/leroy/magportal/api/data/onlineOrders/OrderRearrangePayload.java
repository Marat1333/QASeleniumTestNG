package com.leroy.magportal.api.data.onlineOrders;

import com.leroy.magmobile.api.data.sales.orders.GiveAwayData;
import java.util.List;
import lombok.Data;

@Data
public class OrderRearrangePayload {

    private String fulfillmentTaskId;
    private Integer fulfillmentVersion;
    private String paymentTaskId;
    private Integer paymentVersion;
    private Integer solutionVersion;
    private List<OrderProductDataPayload> products;
    private GiveAwayData giveAway;
}
