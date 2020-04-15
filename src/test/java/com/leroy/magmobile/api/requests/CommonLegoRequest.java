package com.leroy.magmobile.api.requests;

import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

public class CommonLegoRequest<J extends CommonLegoRequest<J>> extends RequestBuilder<J> {

    // Header
    public J setLdap(String val) {
        return header("ldap", val);
    }

    public J setAppVersion(String val) {
        return header("appversion", val);
    }

    // Query params

    public J setShopId(String val) {
        return queryParam("shopId", val);
    }

    public J setDepartmentId(Object val) {
        return queryParam("departmentId", val);
    }
}
