package com.leroy.magmobile.api.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "DELETE", path = "/cart/items")
public class CartItemsRequest extends BaseCartRequest<CartItemsRequest> {

    public CartItemsRequest setDocumentVersion(Object val) {
        return queryParam("documentVersion", val);
    }

    public CartItemsRequest setLineId(String val) {
        return queryParam("lineIds", val);
    }
}
