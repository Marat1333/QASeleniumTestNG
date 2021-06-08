package com.leroy.umbrella_extension.authorization;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.EnvConstants;
import com.leroy.core.configuration.Log;
import com.leroy.umbrella_extension.authorization.data.TokenData;
import com.leroy.umbrella_extension.authorization.requests.AccountLoginGetRequest;
import com.leroy.umbrella_extension.authorization.requests.AccountLoginPostRequest;
import com.leroy.umbrella_extension.authorization.requests.AuthorizeCallbackGetRequest;
import com.leroy.umbrella_extension.authorization.requests.TokenRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Response;

//@Dependencies(bricks = Application.IS4AUTH)
public class AuthClient extends BaseClient {

    private final String gatewayUrl = EnvConstants.IS4_AUTH_HOST;

    public String authClientId;
    private final String clientId = EnvConstants.AUTH_CLIENT_ID;
    private final String secretKey = EnvConstants.AUTH_SECRET_KEY;;

    public Response<TokenData> getResponseToken(String username, String password, String clientId, String secretKey, String gatewayUrl) {
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setClientIdAndSecret(clientId, secretKey);
        tokenRequest.setUserCredentials(username, password);
        return execute(tokenRequest.build(gatewayUrl), TokenData.class);
    }

    public Response<TokenData> getResponseToken(String username, String password) {
        return getResponseToken(username, password, clientId, secretKey, gatewayUrl);
    }

    public String getAccessToken(String username, String password, String clientId, String secretKey, String gatewayUrl) {
        Response<TokenData> resp = getResponseToken(username, password, clientId, secretKey, gatewayUrl);
        if (!resp.isSuccessful()) {
            int tryCount = 5;
            for (int i = 0; i < tryCount; i++) {
                resp = getResponseToken(username, password);
                if (resp.isSuccessful())
                    break;
                try {
                    Thread.sleep(new Random().nextInt(2000) + 1000);
                } catch (Exception err) {
                    Log.error(err.getMessage());
                }
            }
        }
        Assert.assertTrue(resp.isSuccessful(),
                "API: Impossible to get Access Token. Response: " + resp.toString());
        return resp.asJson().getAccess_token();
    }

    public String getAccessToken(String username, String password) {
        return getAccessToken(username, password, clientId, secretKey, gatewayUrl);
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
        callBackReq.queryParam("client_secret", secretKey);
        callBackReq.queryParam("client_id", authClientId);
        callBackReq.queryParam("scope", "openid profile grants email offline_access");
        callBackReq.queryParam("redirect_uri", "magasin://authcode_branch");
        callBackReq.queryParam("response_type", "code");
        Response<JsonNode> resp = execute(callBackReq.build(gatewayUrl), JsonNode.class);
        return StringUtils.substringBetween(resp.toString(), "code=", "&");
    }

    @PostConstruct
    protected void init() {
        authClientId = EnvConstants.MM_AUTH_CLIENT_ID;
    }
}
