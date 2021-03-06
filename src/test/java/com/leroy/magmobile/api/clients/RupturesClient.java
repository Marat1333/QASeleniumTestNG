package com.leroy.magmobile.api.clients;

import static com.leroy.constants.api.ErrorTextConst.SESSION_NOT_FOUND;
import static com.leroy.constants.api.ErrorTextConst.SESSION_NOT_FOUND_OR_ALREADY_FINISHED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.data.ruptures.ActionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureBulkSessionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionWithActionsData;
import com.leroy.magmobile.api.data.ruptures.ResActionData;
import com.leroy.magmobile.api.data.ruptures.ResActionDataList;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureSessionGroupData;
import com.leroy.magmobile.api.enums.CorrectionAccessLevels;
import com.leroy.magmobile.api.enums.RupturesSessionStatuses;
import com.leroy.magmobile.api.requests.ruptures.RupturesBulkSessionCreateRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesBulkSessionProductAddRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesCorrectionAccessCheckRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionCreateRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionDeleteRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionFinishRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionGroupsRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionProductActionRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionProductAddRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionProductDeleteRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionProductsRequest;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionsRequest;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import ru.leroymerlin.qa.core.clients.base.Response;

public class RupturesClient extends BaseMagMobileClient {

    @Override
    protected void init() {
        gatewayUrl = EnvConstants.RUPTURES_API_HOST;
        jaegerHost = EnvConstants.RUPTURES_JAEGER_HOST;
        jaegerService = EnvConstants.RUPTURES_JAEGER_SERVICE;
    }


    /**
     * ---------- Executable Requests -------------
     **/

    @Step("???????????? ???? ???????????????? ?? ?????????????????? ??????????")
    public Response<JsonNode> checkC3Access(CorrectionAccessLevels accessLevel) {
        RupturesCorrectionAccessCheckRequest req = new RupturesCorrectionAccessCheckRequest();
        req.setLdapHeader(accessLevel.getLdap());
        req.setShopIdHeader(accessLevel.getShopId());
        req.bearerAuthHeader(getUserSessionData().getAccessToken());
        return execute(req, JsonNode.class);
    }

    @Step("?????????????? ?????????????????????? ???????????? ????????????????")
    public Response<JsonNode> createSession(ReqRuptureSessionData postData) {
        RupturesSessionCreateRequest req = new RupturesSessionCreateRequest();
        req.setShopIdHeader(postData.getShopId());
        req.bearerAuthHeader(getUserSessionData().getAccessToken());
        req.jsonBody(postData);
        return execute(req, JsonNode.class);
    }

    @Step("???????????????? ?????????? ?? ?????????????????????? ???????????? ????????????????")
    public Response<JsonNode> addProductToSession(ReqRuptureSessionData putData) {
        RupturesSessionProductAddRequest req = new RupturesSessionProductAddRequest();
        req.setSessionId(putData.getSessionId());
        req.setShopIdHeader(putData.getShopId());
        req.jsonBody(putData);
        req.bearerAuthHeader(getUserSessionData().getAccessToken());
        return execute(req, JsonNode.class);
    }

    @Step("?????????????? ???????????????? ???????????? ????????????????")
    public Response<JsonNode> createBulkSession(ReqRuptureBulkSessionData postData) {
        RupturesBulkSessionCreateRequest req = new RupturesBulkSessionCreateRequest();
        req.setShopIdHeader(getUserSessionData().getUserShopId());
        req.bearerAuthHeader(getUserSessionData().getAccessToken());
        req.jsonBody(postData);
        return execute(req, JsonNode.class);
    }

    @Step("???????????????? ?????????? ?? ???????????????? ???????????? ????????????????")
    public Response<JsonNode> addProductToBulkSession(ReqRuptureBulkSessionData postData, int sessionId) {
        RupturesBulkSessionProductAddRequest req = new RupturesBulkSessionProductAddRequest();
        req.setShopIdHeader(getUserSessionData().getUserShopId());
        req.setSessionId(sessionId);
        req.bearerAuthHeader(getUserSessionData().getAccessToken());
        req.jsonBody(postData);
        return execute(req, JsonNode.class);
    }

    @Step("?????????????? ???? ???????????? ?????????? ?? lmCode={lmCode}")
    public Response<JsonNode> deleteProductInSession(String lmCode, Object sessionId) {
        RupturesSessionProductDeleteRequest req = new RupturesSessionProductDeleteRequest();
        if (lmCode != null)
            req.setLmCode(lmCode);
        if (sessionId != null)
            req.setSessionId(sessionId);
        return execute(req, JsonNode.class);
    }

    @Step("???????????????? ???????????? ?????? ???????????? ?? ???????????? ????????????????")
    public Response<ResActionDataList> actionProduct(ReqRuptureSessionWithActionsData putData) {
        RupturesSessionProductActionRequest req = new RupturesSessionProductActionRequest();
        req.setSessionId(putData.getSessionId());
        req.setLmCode(putData.getLmCode());
        putData.setLmCode(null);
        putData.setSessionId(null);
        req.jsonBody(putData);
        return execute(req, ResActionDataList.class);
    }

    @Step("???????????????? ???????????? ???????????? ?? sessionId={sessionId}")
    public Response<RuptureProductDataList> getProducts(
            Object sessionId, Boolean actionState, Integer action, Integer[] productState, Integer startFrom, Integer pageSize) {
        RupturesSessionProductsRequest req = new RupturesSessionProductsRequest();
        req.setShopIdHeader(getUserSessionData().getUserShopId());
        if (sessionId != null)
            req.setSessionId(sessionId);
        if (actionState != null)
            req.setActionState(actionState);
        if (action != null)
            req.setAction(action);
        if (productState != null)
            req.setProductState(productState);
        if (startFrom != null)
            req.setStartFrom(startFrom);
        if (pageSize != null)
            req.setPageSize(pageSize);
        return execute(req, RuptureProductDataList.class);
    }

    @Step("???????????????? ????????????")
    public Response<RuptureProductDataList> getProducts(Object sessionId) {
        return getProducts(sessionId, null, null, null, null, null);
    }

    @Step("???????????????? ???????????? ?????? action {action}")
    public Response<RuptureProductDataList> getProductsWithAction(Integer sessionId, Boolean actionState, Integer action) {
        return getProducts(sessionId, actionState, action, null, null, null);
    }

    @Step("???????????????? ???????????? ???? ???????????????? ???????????????? {productState}")
    public Response<RuptureProductDataList> getProductsWithProductState(Integer sessionId, Integer... productState) {
        return getProducts(sessionId, null, null, productState, null, null);
    }

    @Step("???????????????? ???????????? c ????????????????????")
    public Response<RuptureProductDataList> getProductsWithPagination(Integer sessionId, Integer startFrom, Integer pageSize) {
        return getProducts(sessionId, null, null, null, startFrom, pageSize);
    }

    // ---------- GET /ruptures/sessions ---------------- //

    private RupturesSessionsRequest getSessionsDefaultRequest() {
        RupturesSessionsRequest req = new RupturesSessionsRequest();
        req.setShopIdHeader(getUserSessionData().getUserShopId());
        req.setDepartmentId(getUserSessionData().getUserDepartmentId());
        return req;
    }

    @Step("???????????????? ???????????? ????????????")
    public Response<ResRuptureSessionDataList> getSessions(RupturesSessionsRequest req) {
        return execute(req, ResRuptureSessionDataList.class);
    }

    @Step("???????????????? ???????????? ????????????")
    public Response<ResRuptureSessionDataList> getSessions() {
        RupturesSessionsRequest req = getSessionsDefaultRequest();
        return getSessions(req);
    }

    @Step("???????????????? ???????????? ????????????")
    public Response<ResRuptureSessionDataList> getSessions(int pageSize) {
        RupturesSessionsRequest req = getSessionsDefaultRequest();
        req.setPageSize(pageSize);
        return getSessions(req);
    }

    @Step("???????????????? ???????????? ????????????")
    public Response<ResRuptureSessionDataList> getSessions(int startFrom, int pageSize) {
        RupturesSessionsRequest req = getSessionsDefaultRequest();
        req.setStartFrom(startFrom);
        req.setPageSize(pageSize);
        return getSessions(req);
    }

    @Step("???????????????? ???????????? ???????????? ???? ???????????????? {status}")
    public Response<ResRuptureSessionDataList> getSessions(RupturesSessionStatuses status, int pageSize) {
        RupturesSessionsRequest req = getSessionsDefaultRequest();
        req.setStatus(status.getName());
        req.setPageSize(pageSize);
        return getSessions(req);
    }

    // ---------------------- GET /ruptures/session/groups -------------- //

    @Step("???????????????? ???????????? ?????????????? ?????? ???????????? ?? sessionId={sessionId}")
    public Response<RuptureSessionGroupData> getGroups(int sessionId) {
        RupturesSessionGroupsRequest req = new RupturesSessionGroupsRequest();
        req.setSessionId(sessionId);
        return execute(req, RuptureSessionGroupData.class);
    }

    @Step("?????????????????? ???????????? ?? sessionId={sessionId}")
    public Response<JsonNode> finishSession(Object sessionId) {
        RupturesSessionFinishRequest req = new RupturesSessionFinishRequest();
        if (sessionId != null)
            req.setSessionId(sessionId);
        return execute(req, JsonNode.class);
    }

    @Step("?????????????? ???????????? ?? sessionId={sessionId}")
    public Response<JsonNode> deleteSession(Object sessionId) {
        RupturesSessionDeleteRequest req = new RupturesSessionDeleteRequest();
        if (sessionId != null)
            req.setSessionId(sessionId);
        return execute(req, JsonNode.class);
    }

    //Verifications
    @Step("??????????????????, ?????? ???????????? ???????? ?????????????? ?? ?????????????? sessionId")
    public Integer assertThatSessionIsCreatedAndGetId(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        Integer sessionId = resp.asJson().get("sessionId").intValue();
        assertThat("session id", sessionId, greaterThan(0));
        return sessionId;
    }

    @Step("??????????????????, ?????? ?????????????????? ???????????? ???????? ?? ???????????? ?? ?????????? ???????????? ??????")
    public void assertThatCreatedSessionPresentsInSessionsList(int sessionId, String type) {
        Response<ResRuptureSessionDataList> getResp = getSessions(50);
        assertThatResponseIsOk(getResp);
        ResRuptureSessionDataList respBody = getResp.asJson();
        List<ResRuptureSessionData> items = respBody.getItems().stream().filter(
                a -> a.getSessionId().equals(sessionId)).collect(Collectors.toList());
        assertThat("Session " + sessionId + " wasn't found", items, hasSize(1));
        assertThat("Session " + sessionId + " isn't " + type, items.get(0).getType(), equalTo(type));
    }

    @Step("??????????????????, ?????? ?????????? ???????????????????????? ?? ????????????")
    public void assertThatProductPresentsInSessionList(int sessionId, String lmCode) {
        Response<RuptureProductDataList> respGetProducts = getProducts(sessionId);
        List<RuptureProductData> items = respGetProducts.asJson().getItems().stream().filter(
                a -> a.getLmCode().equals(lmCode)).collect(Collectors.toList());
        assertThat("Session " + sessionId + " do not have product " + lmCode, items, hasSize(1));
    }

    @Step("??????????????????, ?????? ?? ???????????? ???????????????????????? ???????????? ?????????????????????????? ????????????")
    public void assertThatSessionContainsProducts(int sessionId, String...  expectedLmCodes) {
        int totalCount = expectedLmCodes.length;
        RuptureProductDataList productDataList = getProducts(sessionId).asJson();
        assertThat("Wrong totalCount in session " + sessionId, productDataList.getTotalCount(), equalTo(totalCount));
        List<RuptureProductData> items = new ArrayList<>(productDataList.getItems());
        for (RuptureProductData item : items) {
            String lmCode = item.getLmCode();
            assertThat("Wrong lmCode in session", expectedLmCodes, hasItemInArray(lmCode));
        }
    }

    @Step("??????????????????, ?????? ?????????????? ??????????????????/??????????????")
    public void assertThatIsUpdatedOrDeleted(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("success", resp.asJson().get("success").booleanValue());
    }

    private void assertThatSessionIsActivated(Response<ResActionDataList> resp, List<ActionData> expectedActions, boolean isSuccessful) {
        assertThatResponseIsOk(resp);
        ResActionDataList actualData = resp.asJson();
        assertThat("action count", actualData.getActions(), hasSize(expectedActions.size()));
        for (int i = 0; i < expectedActions.size(); i++) {
            ResActionData actualActionData = actualData.getActions().get(i);
            ActionData expectedActionData = expectedActions.get(i);
            assertThat("action", actualActionData.getAction(), is(expectedActionData.getAction()));
            assertThat("result", actualActionData.getResult(), is(isSuccessful));
        }
    }

    @Step("??????????????????, ?????? ???????????? ?????????????? ????????????????")
    public void assertThatSessionIsActivated(Response<ResActionDataList> resp, List<ActionData> expectedActions) {
        assertThatSessionIsActivated(resp, expectedActions, true);
    }

    @Step("??????????????????, ?????? ???????????? ???? ???????? ????????????????")
    public void assertThatSessionIsNotActivated(Response<ResActionDataList> resp, List<ActionData> expectedActions) {
        assertThatSessionIsActivated(resp, expectedActions, false);
    }

    @Step("??????????????????, ?????? ???????? ???????????? ?????????????????????????? ???????????? expectedData")
    public void assertThatDataMatches(Response<RuptureProductDataList> resp, @NotNull RuptureProductDataList expectedData) {
        assertThatResponseIsOk(resp);
        RuptureProductDataList actualData = resp.asJson();
        List<RuptureProductData> expectedProductItems = new ArrayList<>(expectedData.getItems());
        Collections.reverse(expectedProductItems);
        assertThat("total count", actualData.getTotalCount(), equalTo(expectedData.getTotalCount()));
        assertThat("item count", actualData.getItems(), hasSize(expectedData.getItems().size()));
        for (int i = 0; i < actualData.getItems().size(); i++) {
            RuptureProductData actualProduct = actualData.getItems().get(i);
            RuptureProductData expectedProduct = expectedProductItems.get(i);
            assertThat("#" + (i + 1) + " lmCode", actualProduct.getLmCode(), equalTo(expectedProduct.getLmCode()));
            assertThat("#" + (i + 1) + " barCode", actualProduct.getBarCode(), equalTo(expectedProduct.getBarCode()));
            assertThat("#" + (i + 1) + " title", actualProduct.getTitle(), equalTo(expectedProduct.getTitle()));
            assertThat("#" + (i + 1) + " gamma", actualProduct.getGamma(), equalTo(expectedProduct.getGamma()));
            assertThat("#" + (i + 1) + " top", actualProduct.getTop(), equalTo(expectedProduct.getTop()));
            assertThat("#" + (i + 1) + " price", actualProduct.getPrice(), equalTo(expectedProduct.getPrice()));
            assertThat("#" + (i + 1) + " twentyEighty", actualProduct.getTwentyEighty(),
                    equalTo(expectedProduct.getTwentyEighty()));
            assertThat("#" + (i + 1) + " provider", actualProduct.getProvider(),
                    equalTo(expectedProduct.getProvider()));
            assertThat("#" + (i + 1) + " planningDeliveryTime", actualProduct.getPlanningDeliveryTimeAsLocalDateTime(),
                    equalTo(expectedProduct.getPlanningDeliveryTimeAsLocalDateTime()));
            assertThat("#" + (i + 1) + " shopStock", actualProduct.getShopStock(),
                    equalTo(expectedProduct.getShopStock()));
            assertThat("#" + (i + 1) + " shelfStock", actualProduct.getShelfStock(),
                    equalTo(expectedProduct.getShelfStock()));
            assertThat("#" + (i + 1) + " theoreticalStock", actualProduct.getTheoreticalStock(),
                    equalTo(expectedProduct.getTheoreticalStock()));
            assertThat("#" + (i + 1) + " image", actualProduct.getImage(),
                    equalTo(expectedProduct.getImage()));
            assertThat("#" + (i + 1) + " stockRmCount", actualProduct.getStockRmCount(),
                    equalTo(expectedProduct.getStockRmCount()));
            assertThat("#" + (i + 1) + " lsStock", actualProduct.getLsStock(),
                    equalTo(expectedProduct.getLsStock()));
            assertThat("#" + (i + 1) + " shoppingHallCount", actualProduct.getShoppingHallCount(),
                    equalTo(expectedProduct.getShoppingHallCount()));
            assertThat("#" + (i + 1) + " rmFeedbackCount", actualProduct.getRmFeedbackCount(),
                    equalTo(expectedProduct.getRmFeedbackCount()));
            assertThat("#" + (i + 1) + " shelfCount", actualProduct.getShelfCount(),
                    equalTo(expectedProduct.getShelfCount()));
            assertThat("#" + (i + 1) + " comment", actualProduct.getComment(),
                    equalTo(expectedProduct.getComment()));

            assertThat("#" + (i + 1) + " Action count", actualProduct.getActions(),
                    hasSize(expectedProduct.getActions().size()));
            for (int j = 0; j < actualProduct.getActions().size(); j++) {
                ActionData actualAction = actualProduct.getActions().get(j);
                List<ActionData> expectedActions = expectedProduct.getActions().stream().filter(a -> a.getAction()
                        .equals(actualAction.getAction())).collect(Collectors.toList());
                assertThat("#" + (i + 1) + " Multiple actions found in one product", expectedActions, hasSize(1));
                ActionData expectedAction = expectedActions.get(0);
                assertThat("#" + (i + 1) + " Action #" + j + " action", actualAction.getAction(),
                        equalTo(expectedAction.getAction()));
                assertThat("#" + (i + 1) + " Action #" + j + " state", actualAction.getState(),
                        equalTo(expectedAction.getState()));
                assertThat("#" + (i + 1) + " Action #" + j + " user position", actualAction.getUserPosition(),
                        equalTo(expectedAction.getUserPosition()));
            }
        }
    }

    @Step("??????????????????, ?????? ???????????? ???? ?????????????? ?????? ??????????????????")
    public void assertThatSessionNotFoundOrFinished(Response<JsonNode> resp, Integer sessionId) {
        assertThat("Response code", resp.getStatusCode(), equalTo(400));
        assertThat("Error", resp.asJson().get("error").asText(),
                equalTo(String.format(SESSION_NOT_FOUND_OR_ALREADY_FINISHED, sessionId)));
    }

    @Step("??????????????????, ?????? ???????????? ???? ??????????????")
    public void assertThatSessionNotFound(Response<JsonNode> resp, Integer sessionId) {
        assertThat("Response code", resp.getStatusCode(), equalTo(400));
        assertThat("Error", resp.asJson().get("error").asText(),
                equalTo(String.format(SESSION_NOT_FOUND, sessionId)));
    }

    @Step("?????????????????? ?????????????? ?? ?????????????????? ??????????")
    public void assertStockCorrectionAccess(Response<JsonNode> resp, CorrectionAccessLevels accessLevel) {
        if (accessLevel == CorrectionAccessLevels.UNKNOWN_USER) {
            assertThat("Response code", resp.getStatusCode(), equalTo(404));
            assertThat("Error", resp.asString(), equalTo(accessLevel.getResponse()));
        }
        else {
            assertThatResponseIsOk(resp);
            assertThat("Access", resp.asString(), equalTo(accessLevel.getResponse()));
        }
    }
}
