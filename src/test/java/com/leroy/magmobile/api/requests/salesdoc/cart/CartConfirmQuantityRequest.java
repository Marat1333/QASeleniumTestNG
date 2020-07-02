package com.leroy.magmobile.api.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/cart/confirmQuantity")
public class CartConfirmQuantityRequest extends BaseCartRequest<CartChangeStatusRequest> {

}
