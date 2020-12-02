package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/ruptures/sessions")
public class RupturesCorrectionAccessCheckRequest extends CommonSearchRequestBuilder<RupturesCorrectionAccessCheckRequest> {

    public RupturesCorrectionAccessCheckRequest setStatus(String val) {
        return queryParam("status", val);
    }

}
