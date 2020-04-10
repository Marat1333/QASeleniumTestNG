package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.ruptures.*;
import com.leroy.magmobile.api.requests.ruptures.*;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RupturesClient extends MagMobileClient {

    private String appVersion = "1.6.4";

    /**
     * ---------- Executable Requests -------------
     **/

    public Response<JsonNode> createProduct(ReqRuptureSessionData postData) {
        RupturesSessionProductPostRequest req = new RupturesSessionProductPostRequest();
        req.setAppVersion(appVersion);
        req.bearerAuthHeader(sessionData.getAccessToken());
        req.jsonBody(postData);
        return execute(req, JsonNode.class);
    }

    public Response<JsonNode> updateProduct(ReqRuptureSessionData putData) {
        RupturesSessionProductRequest req = new RupturesSessionProductRequest();
        req.setAppVersion(appVersion);
        req.jsonBody(putData);
        req.bearerAuthHeader(sessionData.getAccessToken());
        return execute(req, JsonNode.class);
    }

    public Response<JsonNode> deleteProduct(String lmCode, int sessionId) {
        RupturesSessionProductDeleteRequest req = new RupturesSessionProductDeleteRequest();
        req.setLmCode(lmCode);
        req.setSessionId(sessionId);
        req.setAppVersion(appVersion);
        return execute(req, JsonNode.class);
    }

    public Response<ResActionDataList> actionProduct(ReqRuptureSessionWithActionsData putData) {
        RupturesSessionProductActionRequest req = new RupturesSessionProductActionRequest();
        req.setAppVersion(appVersion);
        req.jsonBody(putData);
        return execute(req, ResActionDataList.class);
    }

    public Response<RuptureProductDataList> getProducts(Integer sessionId) {
        RupturesSessionProductsRequest req = new RupturesSessionProductsRequest();
        req.setAppVersion(appVersion);
        req.setSessionId(sessionId);
        return execute(req, RuptureProductDataList.class);
    }

    public Response<Object> getSessions() {
        RupturesSessionsRequest req = new RupturesSessionsRequest();
        return execute(req, Object.class);
    }

    public Response<Object> getGroups() {
        RupturesSessionGroupsRequest req = new RupturesSessionGroupsRequest();
        return execute(req, Object.class);
    }

    public Response<Object> finishSession() {
        RupturesSessionFinishRequest req = new RupturesSessionFinishRequest();
        return execute(req, Object.class);
    }

    public Response<Object> deleteSession() {
        RupturesSessionDeleteRequest req = new RupturesSessionDeleteRequest();
        return execute(req, Object.class);
    }

    //Verifications

    public Integer assertThatSessionIsCreatedAndGetId(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        Integer sessionId = resp.asJson().get("sessionId").intValue();
        assertThat("session id", sessionId, greaterThan(0));
        return sessionId;
    }

    public void assertThatProductIsUpdatedOrDeleted(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("success", resp.asJson().get("success").booleanValue());
    }

    public void assertThatSessionIsActivated(Response<ResActionDataList> resp, List<ActionData> expectedActions) {
        assertThatResponseIsOk(resp);
        ResActionDataList actualData = resp.asJson();
        assertThat("action count", actualData.getActions(), hasSize(expectedActions.size()));
        for (int i = 0; i < expectedActions.size(); i++) {
            ResActionData actualActionData = actualData.getActions().get(i);
            ActionData expectedActionData = expectedActions.get(i);
            assertThat("action", actualActionData.getAction(), is(expectedActionData.getAction()));
            assertThat("result", actualActionData.getResult(), is(false)); // ????
        }
    }

    public void assertThatDataMatches(Response<RuptureProductDataList> resp, RuptureProductDataList expectedData) {
        assertThatResponseIsOk(resp);
        assertThat("rupture session product items", resp.asJson(), equalTo(expectedData));
    }

}
