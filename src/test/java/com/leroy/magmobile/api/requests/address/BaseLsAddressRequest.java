package com.leroy.magmobile.api.requests.address;

import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

public class BaseLsAddressRequest<J extends BaseLsAddressRequest<J>> extends RequestBuilder<J> {

    // headers

    public J setLdap(String val) {
        return header("ldap", val);
    }

    // Query

    public J setShopId(String val) {
        return queryParam("shopId", val);
    }

    public J setDepartmentId(String val) {
        return queryParam("departmentId", val);
    }
}
