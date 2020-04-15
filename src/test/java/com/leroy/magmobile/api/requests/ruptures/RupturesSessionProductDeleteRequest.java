package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "DELETE", path = "/ruptures/session/product")
public class RupturesSessionProductDeleteRequest extends CommonLegoRequest<RupturesSessionProductDeleteRequest> {
    public RupturesSessionProductDeleteRequest setSessionId(int val) {
        return queryParam("sessionId", val);
    }

    public RupturesSessionProductDeleteRequest setLmCode(String val) {
        return queryParam("lmCode", val);
    }
}
