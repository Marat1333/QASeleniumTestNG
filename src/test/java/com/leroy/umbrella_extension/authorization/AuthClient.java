package com.leroy.umbrella_extension.authorization;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.EnvConstants;
import com.leroy.umbrella_extension.authorization.data.TokenData;
import com.leroy.umbrella_extension.authorization.requests.AccountLoginGetRequest;
import com.leroy.umbrella_extension.authorization.requests.AccountLoginPostRequest;
import com.leroy.umbrella_extension.authorization.requests.AuthorizeCallbackGetRequest;
import com.leroy.umbrella_extension.authorization.requests.TokenRequest;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Response;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

//@Dependencies(bricks = Application.IS4AUTH)
public class AuthClient extends BaseClient {

    private String gatewayUrl;

    private String clientId = EnvConstants.AUTH_CLIENT_ID;
    private String secretKey = EnvConstants.AUTH_SECRET_KEY;

    public Response<TokenData> getResponseToken(String username, String password) {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setClientIdAndSecret(clientId, secretKey);
        tokenRequest.setUserCredentials(username, password);
        return execute(tokenRequest.build(gatewayUrl), TokenData.class);
    }

    public String getAccessToken(String username, String password) {
        Response<TokenData> resp = getResponseToken(username, password);
        if (!resp.isSuccessful()) {
            int tryCount = 2;
            for (int i = 0; i < tryCount; i++) {
                resp = getResponseToken(username, password);
                if (resp.isSuccessful())
                    break;
            }
        }
        Assert.assertTrue(resp.isSuccessful(),
                "API: Impossible to get Access Token. Response: " + resp.toString());
        return resp.asJson().getAccess_token();
    }

    public String authAndGetCode(String username, String password) {
        Response<JsonNode> getResp = execute(new AccountLoginGetRequest().build(gatewayUrl), JsonNode.class);
        String verificationToken = StringUtils.substringBetween(getResp.toString(),
                "<input name=\"__RequestVerificationToken\" type=\"hidden\" value=\"",
                "\" />");
        AccountLoginPostRequest req = new AccountLoginPostRequest();
        Map<String, String> map = new HashMap<>();
        map.put("Username", username);
        map.put("Password", password);
        map.put("button", "login");
        map.put("__RequestVerificationToken", verificationToken);
        req.formBody(map);
        Response<JsonNode> postResp = execute(req.build(gatewayUrl), JsonNode.class);

        AuthorizeCallbackGetRequest callBackReq = new AuthorizeCallbackGetRequest();
        callBackReq.queryParam("client_secret", "secret");
        callBackReq.queryParam("client_id", "mag-mobile-test"); // TODO Надо вынести в EnvConst
        callBackReq.queryParam("scope", "openid profile grants email offline_access");
        callBackReq.queryParam("redirect_uri", "magasin://authcode_branch");
        callBackReq.queryParam("response_type", "code");
        Response<JsonNode> resp = execute(callBackReq.build(gatewayUrl), JsonNode.class);
        return StringUtils.substringBetween(resp.toString(), "code=", "&");
    }

    @PostConstruct
    private void init() {
        gatewayUrl = EnvConstants.IS4_AUTH_HOST;
    }
}
