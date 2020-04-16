package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/ruptures/session/groups")
public class RupturesSessionGroupsRequest extends CommonLegoRequest<RupturesSessionGroupsRequest> {

    public RupturesSessionGroupsRequest setSessionId(int val) {
        return queryParam("sessionId", val);
    }

}
