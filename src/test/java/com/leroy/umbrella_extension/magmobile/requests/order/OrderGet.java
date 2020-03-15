package com.leroy.umbrella_extension.magmobile.requests.order;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/order")
public class OrderGet extends BaseOrderRequest<OrderGet> {

    public OrderGet setOrderId(String val) {
        return queryParam("orderId", val);
    }

}
