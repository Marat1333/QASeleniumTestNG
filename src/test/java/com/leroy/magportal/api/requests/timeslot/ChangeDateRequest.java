package com.leroy.magportal.api.requests.timeslot;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/giveaway/changeDate")
public class ChangeDateRequest extends RequestBuilder<ChangeDateRequest> {

    public ChangeDateRequest setUserLdap(String val) {
        return header("ldap", val);
    }
}
