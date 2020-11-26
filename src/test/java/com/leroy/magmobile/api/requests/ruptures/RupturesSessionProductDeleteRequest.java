package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "DELETE", path = "/v1/ruptures/sessions/{sessionId}/products/{lmCode}")
public class RupturesSessionProductDeleteRequest extends CommonLegoRequest<RupturesSessionProductDeleteRequest> {

    public RupturesSessionProductDeleteRequest setSessionId(Object val) {
        return pathParam("sessionId", val);
    }

    public RupturesSessionProductDeleteRequest setLmCode(String val) {
        return pathParam("lmCode", val);
    }
}
