package com.leroy.magportal.api.data.onlineOrders;

import java.util.List;
import lombok.Data;

@Data
public class OrderDeliveryRecalculatePayload {

    private List<OrderProductDataPayload> products;
}
