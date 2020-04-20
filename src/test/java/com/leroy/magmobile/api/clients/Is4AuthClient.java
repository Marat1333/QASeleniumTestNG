package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.oauth.Is4TokenData;
import com.leroy.magmobile.api.requests.oauth.Is4AuthCodeResponse;
import com.leroy.magmobile.api.requests.oauth.Is4AuthRefreshResponse;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.HashMap;
import java.util.Map;

public class Is4AuthClient extends MagMobileClient {

    @Step("(Is4AuthCode) Try to log in with code={code}")
    public Response<Is4TokenData> sendPostCodeRequest(String code) {
        Is4AuthCodeResponse req = new Is4AuthCodeResponse();
        Map<String, String> body = new HashMap<>();
        body.put("code", code);
        body.put("fromUrl", "magasin://authcode_branch");
        req.formBody(body);
        return execute(req, Is4TokenData.class);
    }

    @Step("(Is4AuthRefresh) Try to refresh token with token={token}")
    public Response<Is4TokenData> sendPostRefreshRequest(String token) {
        Is4AuthRefreshResponse req = new Is4AuthRefreshResponse();
        req.setRefreshToken(token);
        return execute(req, Is4TokenData.class);
    }

}
