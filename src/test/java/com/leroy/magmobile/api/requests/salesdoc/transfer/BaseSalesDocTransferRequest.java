package com.leroy.magmobile.api.requests.salesdoc.transfer;

import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

public class BaseSalesDocTransferRequest<T extends BaseSalesDocTransferRequest<T>> extends RequestBuilder<T> {

    public T setTaskId(String val) {
        return queryParam("taskId", val);
    }

    public T setLdap(String val) {
        return header("ldap", val);
    }

    public T setShopId(String val) {
        return header("shopid", val);
    }

}
