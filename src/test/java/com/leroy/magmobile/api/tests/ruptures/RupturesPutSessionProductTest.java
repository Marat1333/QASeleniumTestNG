package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.ruptures.ActionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductDataList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.qameta.allure.AllureId;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class RupturesPutSessionProductTest extends BaseRuptureTest {

    @Override
    protected boolean isDeleteSessionAfterEveryMethod() {
        return false;
    }

    @BeforeClass
    private void createSession() {
        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        ActionData actionData1 = ActionData.returnRandomData();
        actionData1.setAction(0);
        ActionData actionData2 = ActionData.returnRandomData();
        actionData2.setAction(1);
        productData.setActions(new ArrayList<>(Arrays.asList(actionData1, actionData2)));

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
        ruptureProductDataListBody = new RuptureProductDataList();
        ruptureProductDataListBody.addItem(productData);
    }

    private ReqRuptureSessionData getTypicalReqRuptureSessionData(RuptureProductData productData) {
        return getTypicalReqRuptureSessionData(this.sessionId, productData);
    }

    private ReqRuptureSessionData getTypicalReqRuptureSessionData(int sessionId, RuptureProductData productData) {
        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setSessionId(sessionId);
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));
        return rupturePostData;
    }

    @Test(description = "C3233582 PUT ruptures product - Add new product")
    @AllureId("13180")
    public void testUpdateRuptureSessionProduct() {
        ActionData action1 = new ActionData();
        action1.setAction(2);
        action1.setState(new Random().nextBoolean());
        action1.setUserPosition(0);

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(Collections.singletonList(action1));

        ReqRuptureSessionData rupturePostData = getTypicalReqRuptureSessionData(productData);

        step("?????????????????? ?????????? ??????????????");
        Response<JsonNode> resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.addItem(productData);

        step("???????????????????? GET ???????????? ?? ??????????????????, ?????? ???????????? ?????????????????????????? ????????????????????");
        Response<RuptureProductDataList> getResp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(getResp, ruptureProductDataListBody);
    }

    @Test(description = "C3285581 PUT ruptures product to finished session")
    @AllureId("13181")
    public void testPutRupturesProductToFinishedSession() {
        step("?????????????????? ????????????");
        Response<JsonNode> respFinishSession = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(respFinishSession);

        step("?????????????? ???????????????? ?????????? ?? ?????????????????????? ????????????");
        ActionData action1 = ActionData.returnRandomData();

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(Collections.singletonList(action1));

        ReqRuptureSessionData rupturePostData = getTypicalReqRuptureSessionData(productData);

        Response<JsonNode> resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatSessionNotFoundOrFinished(resp, sessionId);

        step("???????????????????? GET ???????????? ?? ??????????????????, ?????? ???????????? ?????????????????????????? ???? ????????????????????");
        Response<RuptureProductDataList> getResp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(getResp, ruptureProductDataListBody);
    }

    @Test(description = "C3285582 PUT ruptures product to deleted session")
    @AllureId("13182")
    public void testPutRupturesProductToDeletedSession() {
        step("???????????????? ???????????????? ?????????? ?? ???????????????????????????? ????????????");
        ActionData action1 = ActionData.returnRandomData();

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(Collections.singletonList(action1));

        int deletedSessionId = Integer.MAX_VALUE;
        ReqRuptureSessionData rupturePostData = getTypicalReqRuptureSessionData(deletedSessionId, productData);

        Response<JsonNode> resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatSessionNotFoundOrFinished(resp, deletedSessionId);
    }

    @Test(description = "C23409188 PUT ruptures product - Change existing product")
    @AllureId("13183")
    public void testPutRupturesProductChangeExistingProduct() {
        RuptureProductData productData = ruptureProductDataListBody.getItems().get(0);
        // ???????????? shelfCount
        productData.setShelfCount(new Random().nextInt(4));
        // ???????????? ??????????????????????
        productData.setComment(RandomStringUtils.randomAlphanumeric(6));
        List<ActionData> actions = productData.getActions();
        // ???????????? ???????????? ?? ???????????? ???? Action
        actions.get(1).setState(!actions.get(1).getState());
        // ?????????????? ???????? Action
        actions.remove(0);
        // ?????????????????? ?????????? action
        ActionData newAction = ActionData.returnRandomData();
        newAction.setAction(2);
        actions.add(newAction);
        step("???????????????? ?????????? ?? ????????????");
        ReqRuptureSessionData rupturePostData = getTypicalReqRuptureSessionData(productData);

        Response<JsonNode> resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);

        step("???????????????????? GET ???????????? ?? ??????????????????, ?????? ?????????? ?????? ??????????????");
        Response<RuptureProductDataList> getResp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(getResp, ruptureProductDataListBody);
    }

}
