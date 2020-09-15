package com.leroy.magportal.api.requests.order;

import com.leroy.magmobile.api.requests.order.BaseOrderRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/v2/order/workflow")
public class OrderWorkflowRequest extends BaseOrderRequest<OrderWorkflowRequest> {

}
