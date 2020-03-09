package com.leroy.umbrella_extension.magmobile.requests.order;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "PUT", path = "/v2/order/workflow")
public class LegoOrderWorkflowPut extends RequestBuilder<LegoOrderWorkflowPut> {

    public LegoOrderWorkflowPut setUserLdap(String val) {
        return header("ldap", val);
    }

    public LegoOrderWorkflowPut setOrderId(String val) {
        return queryParam("orderId", val);
    }

}
