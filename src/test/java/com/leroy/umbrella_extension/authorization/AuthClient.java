package com.leroy.umbrella_extension.authorization;

import com.leroy.umbrella_extension.authorization.data.TokenData;
import com.leroy.umbrella_extension.authorization.requests.TokenRequest;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Response;

import javax.annotation.PostConstruct;

//@Dependencies(bricks = Application.IS4AUTH)
public class AuthClient extends BaseClient {

    private String gatewayUrl;

    // TODO should be get from properties:
    private String clientId = "check-token-test";
    private String secretKey = "secret";

    public Response<TokenData> getResponseToken(String username, String password) {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setClientIdAndSecret(clientId, secretKey);
        tokenRequest.setUserCredentials(username, password);
        return execute(tokenRequest.build(gatewayUrl), TokenData.class);
    }

    public String getAccessToken(String username, String password) {
        return getResponseToken(username, password).asJson().getAccess_token();
    }

    @PostConstruct
    private void init() {
        gatewayUrl = params.getProperty("mashup.is4Auth.url");
    }
}
