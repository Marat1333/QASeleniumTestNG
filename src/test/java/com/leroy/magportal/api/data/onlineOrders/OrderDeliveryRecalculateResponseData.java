package com.leroy.magportal.api.data.onlineOrders;

import lombok.Data;

@Data
public class OrderDeliveryRecalculateResponseData {

    private Double deliveryLiftPrice;
    private Double deliveryTotalPrice;
    private Double orderGoodsPrice;
}
