package com.leroy.umbrella_extension.magmobile.requests.salesdoc.cart;

import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

public class BaseCartRequest<J extends BaseCartRequest<J>> extends RequestBuilder<J> {

    public J setShopId(String val) {
        return header("shopid", val);
    }

}
