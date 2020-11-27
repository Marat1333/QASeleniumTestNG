package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "DELETE", path = "/v1/ruptures/sessions/{sessionId}")
public class RupturesSessionDeleteRequest extends CommonLegoRequest<RupturesSessionDeleteRequest> {
    public RupturesSessionDeleteRequest setSessionId(Object val) {
        return pathParam("sessionId", val);
    }
}
