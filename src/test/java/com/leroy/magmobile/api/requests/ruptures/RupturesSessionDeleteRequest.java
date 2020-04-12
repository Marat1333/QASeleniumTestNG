package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "DELETE", path = "/ruptures/session")
public class RupturesSessionDeleteRequest extends CommonLegoRequest<RupturesSessionDeleteRequest> {
    public RupturesSessionDeleteRequest setSessionId(int val) {
        return queryParam("sessionId", val);
    }
}
