package com.leroy.magportal.api.requests.picking;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/order/pickingTasks")
public class OrdersPickingTasksGetRequest extends CommonLegoRequest<OrdersPickingTasksGetRequest> {

    public OrdersPickingTasksGetRequest setOrderId(String val) {
        return queryParam("orderId", val);
    }
}