package com.leroy.magmobile.api.requests.ruptures;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/c3/permissions")
public class RupturesCorrectionAccessCheckRequest extends CommonSearchRequestBuilder<RupturesCorrectionAccessCheckRequest> {
}
