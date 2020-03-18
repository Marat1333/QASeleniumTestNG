package com.leroy.magmobile.api.requests.order;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/order/confirm")
public class OrderConfirmRequest extends BaseOrderRequest<OrderGet> {

}
