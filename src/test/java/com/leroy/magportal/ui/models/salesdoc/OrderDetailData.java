package com.leroy.magportal.ui.models.salesdoc;

import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import java.util.List;
import lombok.Data;

@Data
public class OrderDetailData {

    // Общая информаия
    private String number;
    private String status;

    // Способ получения
    private Object deliveryType;
    private SimpleCustomerData customer;
    private SimpleCustomerData recipient;
    private String pinCode;
    private String comment;

    // Состав заказа
    List<ProductOrderCardWebData> products;

}
