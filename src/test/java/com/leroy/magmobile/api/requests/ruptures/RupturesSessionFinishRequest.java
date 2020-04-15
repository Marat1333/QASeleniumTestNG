package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/ruptures/session/finish")
public class RupturesSessionFinishRequest extends CommonLegoRequest<RupturesSessionFinishRequest> {
    public RupturesSessionFinishRequest setSessionId(int val) {
        return queryParam("sessionId", val);
    }
}
