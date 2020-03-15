package com.leroy.umbrella_extension.magmobile.requests.order;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/v2/order/workflow")
public class OrderWorkflowPut extends BaseOrderRequest<OrderWorkflowPut> {

    public OrderWorkflowPut setOrderId(String val) {
        return queryParam("orderId", val);
    }

}
