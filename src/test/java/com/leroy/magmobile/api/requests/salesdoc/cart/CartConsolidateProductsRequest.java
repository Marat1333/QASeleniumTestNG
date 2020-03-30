package com.leroy.magmobile.api.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/cart/consolidateProducts")
public class CartConsolidateProductsRequest  extends BaseCartRequest<CartChangeStatusRequest> {
}
