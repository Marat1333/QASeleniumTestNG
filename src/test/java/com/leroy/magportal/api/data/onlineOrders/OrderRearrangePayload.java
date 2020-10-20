package com.leroy.magportal.api.data.onlineOrders;

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
    private GiveAway giveAway;

    @Data
    private class GiveAway {

        private String date;
        private String point;
    }
}
