package com.leroy.magmobile.api.requests;

import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

public class CommonSearchRequestBuilder<J extends CommonSearchRequestBuilder<J>> extends RequestBuilder<J> {

    // Header
    public J setLdap(String val) {
        return header("ldap", val);
    }

    // Query params

    public J setPageSize(Integer val) {
        return queryParam("pageSize", val);
    }

    public J setStartFrom(Integer val) {
        return queryParam("startFrom", val);
    }

    public J setShopId(String val) {
        return queryParam("shopId", val);
    }

    public J setDepartmentId(Object val) {
        return queryParam("departmentId", val);
    }
}
