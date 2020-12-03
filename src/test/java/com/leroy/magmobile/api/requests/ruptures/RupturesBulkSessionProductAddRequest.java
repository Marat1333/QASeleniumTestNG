package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/v1/ruptures/sessions/{sessionId}/products")
public class RupturesBulkSessionProductAddRequest extends CommonLegoRequest<RupturesBulkSessionProductAddRequest> {

    public RupturesBulkSessionProductAddRequest setSessionId (int val) {
        return pathParam("sessionId", val);
    }

}
