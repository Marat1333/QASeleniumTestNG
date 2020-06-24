package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/ruptures/sessions")
public class RupturesSessionsRequest extends CommonSearchRequestBuilder<RupturesSessionsRequest> {

    public RupturesSessionsRequest setStatus(String val) {
        return queryParam("status", val);
    }

}
