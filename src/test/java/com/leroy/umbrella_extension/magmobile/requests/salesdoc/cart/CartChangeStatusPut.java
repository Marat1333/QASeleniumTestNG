package com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/cart/changeStatus")
public class CartChangeStatusPut extends BaseCartRequest<CartChangeStatusPut> {

    public CartChangeStatusPut setCartId(String val) {
        return queryParam("cartId", val);
    }

}
