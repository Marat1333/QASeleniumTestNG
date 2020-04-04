package com.leroy.magmobile.api.requests.oauth;


import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

import java.util.HashMap;
import java.util.Map;

@Method(value = "POST", path = "/oauthlmru/refresh")
public class Is4AuthRefreshResponse extends RequestBuilder<Is4AuthRefreshResponse> {

    public Is4AuthRefreshResponse setRefreshToken(String token) {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", token);
        return formBody(body);
    }

}