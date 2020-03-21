package com.leroy.magmobile.api.requests.order;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/order/checkQuantity")
public class OrderCheckQuantityRequest extends BaseOrderRequest<OrderGet> {

}
