package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.ruptures.*;
import com.leroy.magmobile.api.requests.ruptures.*;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Collections;
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

    public Response<ResRuptureSessionDataList> getSessions() {
        RupturesSessionsRequest req = new RupturesSessionsRequest();
        req.setAppVersion(appVersion);
        req.setShopId(sessionData.getUserShopId());
        req.setDepartmentId(sessionData.getUserDepartmentId());
        return execute(req, ResRuptureSessionDataList.class);
    }

    public Response<RuptureSessionGroupData> getGroups(int sessionId) {
        RupturesSessionGroupsRequest req = new RupturesSessionGroupsRequest();
        req.setAppVersion(appVersion);
        req.setSessionId(sessionId);
        return execute(req, RuptureSessionGroupData.class);
    }

    public Response<JsonNode> finishSession(int sessionId) {
        RupturesSessionFinishRequest req = new RupturesSessionFinishRequest();
        req.setAppVersion(appVersion);
        req.setSessionId(sessionId);
        return execute(req, JsonNode.class);
    }

    public Response<JsonNode> deleteSession(int sessionId) {
        RupturesSessionDeleteRequest req = new RupturesSessionDeleteRequest();
        req.setSessionId(sessionId);
        req.setAppVersion(appVersion);
        return execute(req, JsonNode.class);
    }

    //Verifications

    public Integer assertThatSessionIsCreatedAndGetId(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        Integer sessionId = resp.asJson().get("sessionId").intValue();
        assertThat("session id", sessionId, greaterThan(0));
        return sessionId;
    }

    public void assertThatIsUpdatedOrDeleted(Response<JsonNode> resp) {
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
            assertThat("result", actualActionData.getResult(), is(true));
        }
    }

    public void assertThatDataMatches(Response<RuptureProductDataList> resp, RuptureProductDataList expectedData) {
        assertThatResponseIsOk(resp);
        RuptureProductDataList actualData = resp.asJson();
        List<RuptureProductData> expectedProductItems = expectedData.getItems();
        Collections.reverse(expectedProductItems);
        assertThat("total count", actualData.getTotalCount(), equalTo(expectedData.getTotalCount()));
        assertThat("rupture session product items", actualData.getItems(), equalTo(expectedData.getItems()));
    }

}
