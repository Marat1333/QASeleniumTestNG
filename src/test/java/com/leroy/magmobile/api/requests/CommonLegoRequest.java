package com.leroy.magmobile.api.requests;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

public class CommonLegoRequest<J extends CommonLegoRequest<J>> extends RequestBuilder<J> {

    public Map<String, String> getQueryParams() {
        HashMap<String, String> queryParams = new HashMap<>();
        String query = build("").getUri().getQuery();
        if (query != null) {
            String[] queryParamArr = build("").getUri().getQuery().split("&");
            for (String one : queryParamArr) {
                String[] param = one.split("=");
                queryParams.put(param[0], param[1]);
            }
        }
        return queryParams;
    }

    public String getPath() {
        return this.getClass().getAnnotation(Method.class).path();
    }

    public String getMethod() {
        return this.getClass().getAnnotation(Method.class).value();
    }

    // Header
    public J setLdapHeader(String val) {
        return header("ldap", val);
    }

    public J setAppVersion(String val) {
        return header("appversion", val);
    }

    public J setShopIdHeader(Object val) {
        if (val == null)
            return (J) this;
        return header("shopid", val.toString());
    }

    // Query params

    public J setShopId(Object val) {
        if (val == null)
            return (J) this;
        return queryParam("shopId", val);
    }

    public J setDepartmentId(Object val) {
        return queryParam("departmentId", val);
    }
}
