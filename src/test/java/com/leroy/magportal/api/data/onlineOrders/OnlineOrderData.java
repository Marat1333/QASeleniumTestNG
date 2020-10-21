package com.leroy.magportal.api.data.onlineOrders;

import com.leroy.magmobile.api.data.sales.orders.OrderData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OnlineOrderData extends OrderData {

    private DeliveryData deliveryData;
}
