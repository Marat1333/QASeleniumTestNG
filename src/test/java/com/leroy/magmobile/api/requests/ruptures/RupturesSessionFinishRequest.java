package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/v1/ruptures/sessions/{sessionId}/finish")
public class RupturesSessionFinishRequest extends CommonLegoRequest<RupturesSessionFinishRequest> {
    public RupturesSessionFinishRequest setSessionId(Object val) {
        return pathParam("sessionId", val);
    }
}
