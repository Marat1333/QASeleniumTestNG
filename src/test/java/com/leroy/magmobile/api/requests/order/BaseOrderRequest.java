package com.leroy.magmobile.api.requests.order;

import com.leroy.magmobile.api.requests.CommonLegoRequest;

public class BaseOrderRequest<J extends BaseOrderRequest<J>> extends CommonLegoRequest<J> {

    public J setOrderId(String val) {
        return queryParam("orderId", val);
    }

    public J setShopId(String val) {
        return header("shopid", val);
    }

    public J setUserLdap(String val) {
        return header("ldap", val);
    }

}