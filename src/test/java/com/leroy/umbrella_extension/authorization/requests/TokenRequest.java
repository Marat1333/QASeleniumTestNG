package com.leroy.umbrella_extension.authorization.requests;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

import java.util.HashMap;

@Method(value = "POST", path = "/connect/token")
public class TokenRequest extends RequestBuilder<TokenRequest> {

    public TokenRequest setUserCredentials(String username, String password) {
        HashMap<String, String> values = new HashMap<>();
        values.put("grant_type", "password");
        values.put("username", username);
        values.put("password", password);
        return header("Content-Type", "application/x-www-form-urlencoded")
                .formBody(values);
    }

    public TokenRequest setClientIdAndSecret(String clientId, String secretKey) {
        return basicAuthHeader(clientId, secretKey);
    }
}
