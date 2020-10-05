package com.leroy.magportal.api.data.onlineOrders;

import com.leroy.magmobile.api.data.sales.orders.OrderData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeliveryCustomerData extends OrderData {

    private String fullName;
    private String phone;
    private String email;
    private String serviceCard;
}
