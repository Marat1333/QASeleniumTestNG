package com.leroy.magportal.api.data.onlineOrders;

import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import java.util.List;
import lombok.Data;

@Data
public class OrderProductsToGivenAwayData {

    private String fulfillmentStatus;
    private String fulfillmentTaskId;
    private Integer fulfillmentVersion;
    private List<FulfilmentGroups> groups;
    private Boolean refundAvailable;

    @Data
    public static class FulfilmentGroups {

        private List<OrderProductData> products;
        private String groupName;
    }
}
