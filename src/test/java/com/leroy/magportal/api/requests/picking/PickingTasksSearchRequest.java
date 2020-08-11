package com.leroy.magportal.api.requests.picking;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/pickingTasks/search")
public class PickingTasksSearchRequest extends CommonSearchRequestBuilder<PickingTasksSearchRequest> {

    @Override
    public PickingTasksSearchRequest setLdapHeader(String val) {
        return queryParam("ldap", val);
    }

    public PickingTasksSearchRequest setOrderId(String val) {
        return queryParam("orderId", val);
    }
}