package com.leroy.magportal.api.requests.order;

import com.leroy.magmobile.api.requests.order.BaseOrderRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/order/recalculateDelivery")
public class OrderDeliveryRecalculateRequest extends BaseOrderRequest<OrderDeliveryRecalculateRequest> {

}
