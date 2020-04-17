package com.leroy.magmobile.api.data.notification;

import com.leroy.magmobile.api.data.sales.BaseProductOrderData;
import lombok.Data;

@Data
public class SmsNotificationData {
    private Integer shopId;
    private NotificationCustomerData customer;
    private BaseProductOrderData product;
}
