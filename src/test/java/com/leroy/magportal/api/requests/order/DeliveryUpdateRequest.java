package com.leroy.magportal.api.requests.order;

import com.leroy.magmobile.api.requests.order.BaseOrderRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/order/delivery")
public class DeliveryUpdateRequest extends BaseOrderRequest<DeliveryUpdateRequest> {

    public DeliveryUpdateRequest setTaskId(String val) {
        return queryParam("taskId", val);
    }
}
