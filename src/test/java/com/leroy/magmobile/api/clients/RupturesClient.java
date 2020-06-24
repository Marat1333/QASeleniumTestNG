package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.ruptures.*;
import com.leroy.magmobile.api.requests.ruptures.*;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RupturesClient extends MagMobileClient {

    private String appVersion = "1.6.4-autotest";

    /**
     * ---------- Executable Requests -------------
     **/

    @Step("Create Rupture session product")
    public Response<JsonNode> createProduct(ReqRuptureSessionData postData) {
        RupturesSessionProductPostRequest req = new RupturesSessionProductPostRequest();
        req.setAppVersion(appVersion);
        req.bearerAuthHeader(userSessionData.getAccessToken());
        req.jsonBody(postData);
        return execute(req, JsonNode.class);
    }

    @Step("Update rupture session product")
    public Response<JsonNode> updateProduct(ReqRuptureSessionData putData) {
        RupturesSessionProductRequest req = new RupturesSessionProductRequest();
        req.setAppVersion(appVersion);
        req.jsonBody(putData);
        req.bearerAuthHeader(userSessionData.getAccessToken());
        return execute(req, JsonNode.class);
    }

    @Step("Delete rupture session product for lmCode={lmCode}")
    public Response<JsonNode> deleteProduct(String lmCode, int sessionId) {
        RupturesSessionProductDeleteRequest req = new RupturesSessionProductDeleteRequest();
        req.setLmCode(lmCode);
        req.setSessionId(sessionId);
        req.setAppVersion(appVersion);
        return execute(req, JsonNode.class);
    }

    @Step("Change actions for rupture session products")
    public Response<ResActionDataList> actionProduct(ReqRuptureSessionWithActionsData putData) {
        RupturesSessionProductActionRequest req = new RupturesSessionProductActionRequest();
        req.setAppVersion(appVersion);
        req.jsonBody(putData);
        return execute(req, ResActionDataList.class);
    }

    @Step("Get products for sessionId={sessionId}")
    public Response<RuptureProductDataList> getProducts(Integer sessionId) {
        RupturesSessionProductsRequest req = new RupturesSessionProductsRequest();
        req.setAppVersion(appVersion);
        req.setSessionId(sessionId);
        return execute(req, RuptureProductDataList.class);
    }

    // ---------- GET /ruptures/sessions ---------------- //

    private RupturesSessionsRequest getSessionsDefaultRequest() {
        RupturesSessionsRequest req = new RupturesSessionsRequest();
        req.setShopId(userSessionData.getUserShopId());
        req.setDepartmentId(userSessionData.getUserDepartmentId());
        req.setAppVersion(appVersion);
        return req;
    }

    @Step("Get rupture sessions")
    public Response<ResRuptureSessionDataList> getSessions(RupturesSessionsRequest req) {
        return execute(req, ResRuptureSessionDataList.class);
    }

    public Response<ResRuptureSessionDataList> getSessions() {
        RupturesSessionsRequest req = getSessionsDefaultRequest();
        return getSessions(req);
    }

    public Response<ResRuptureSessionDataList> getSessions(int pageSize) {
        RupturesSessionsRequest req = getSessionsDefaultRequest();
        req.setPageSize(pageSize);
        return getSessions(req);
    }

    public Response<ResRuptureSessionDataList> getSessions(int startFrom, int pageSize) {
        RupturesSessionsRequest req = getSessionsDefaultRequest();
        req.setStartFrom(startFrom);
        req.setPageSize(pageSize);
        return getSessions(req);
    }

    public Response<ResRuptureSessionDataList> getSessions(String status, int pageSize) {
        RupturesSessionsRequest req = getSessionsDefaultRequest();
        req.setStatus(status);
        req.setPageSize(pageSize);
        return getSessions(req);
    }

    // ---------------------- GET /ruptures/session/groups -------------- //

    @Step("Get groups for sessionId={sessionId}")
    public Response<RuptureSessionGroupData> getGroups(int sessionId) {
        RupturesSessionGroupsRequest req = new RupturesSessionGroupsRequest();
        req.setAppVersion(appVersion);
        req.setSessionId(sessionId);
        return execute(req, RuptureSessionGroupData.class);
    }

    @Step("Finish session with id = {sessionId}")
    public Response<JsonNode> finishSession(int sessionId) {
        RupturesSessionFinishRequest req = new RupturesSessionFinishRequest();
        req.setAppVersion(appVersion);
        req.setSessionId(sessionId);
        return execute(req, JsonNode.class);
    }

    @Step("Delete session with id = {sessionId}")
    public Response<JsonNode> deleteSession(int sessionId) {
        RupturesSessionDeleteRequest req = new RupturesSessionDeleteRequest();
        req.setSessionId(sessionId);
        req.setAppVersion(appVersion);
        return execute(req, JsonNode.class);
    }

    //Verifications

    @Step("Check that session is created")
    public Integer assertThatSessionIsCreatedAndGetId(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        Integer sessionId = resp.asJson().get("sessionId").intValue();
        assertThat("session id", sessionId, greaterThan(0));
        return sessionId;
    }

    @Step("Check that updated/deleted is successful")
    public void assertThatIsUpdatedOrDeleted(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("success", resp.asJson().get("success").booleanValue());
    }

    @Step("Check that session is activated")
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

    @Step("Check that Response body matches expectedData")
    public void assertThatDataMatches(Response<RuptureProductDataList> resp, RuptureProductDataList expectedData) {
        assertThatResponseIsOk(resp);
        RuptureProductDataList actualData = resp.asJson();
        List<RuptureProductData> expectedProductItems = expectedData.getItems();
        Collections.reverse(expectedProductItems);
        assertThat("total count", actualData.getTotalCount(), equalTo(expectedData.getTotalCount()));
        assertThat("rupture session product items", actualData.getItems(), equalTo(expectedData.getItems()));
    }

    @Step("Check that action is unavailable")
    public void assertThatActionIsNotAllowed(Response<JsonNode> resp, Integer sessionId) {
        assertThat("Response code", resp.getStatusCode(), equalTo(400));
        assertThat("Error", resp.asJson().get("error").asText(),
                equalTo(String.format("Session with SessionId = %d not found or already finished", sessionId)));
    }

}
