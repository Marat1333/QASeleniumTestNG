package com.leroy.magportal.api.requests.order;

import com.leroy.magmobile.api.requests.order.BaseOrderRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/order/checkQuantity")
public class CheckQuantityRequest extends BaseOrderRequest<CheckQuantityRequest> {

}
