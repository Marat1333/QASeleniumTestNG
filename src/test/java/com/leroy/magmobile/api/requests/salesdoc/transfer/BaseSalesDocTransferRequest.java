package com.leroy.magmobile.api.requests.salesdoc.transfer;

import com.leroy.magmobile.api.requests.CommonLegoRequest;

public class BaseSalesDocTransferRequest<T extends BaseSalesDocTransferRequest<T>> extends CommonLegoRequest<T> {

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
