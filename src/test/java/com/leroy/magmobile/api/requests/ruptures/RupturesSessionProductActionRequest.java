package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/v1/ruptures/sessions/{sessionId}/products/{lmCode}")
public class RupturesSessionProductActionRequest extends CommonLegoRequest<RupturesSessionProductActionRequest> {

    public RupturesSessionProductActionRequest setSessionId (int val) {
        return pathParam("sessionId", val);
    }

    public RupturesSessionProductActionRequest setLmCode (String val) {
        return pathParam("lmCode", val);
    }

}
