package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.ruptures.ActionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionWithActionsData;
import com.leroy.magmobile.api.data.ruptures.ResActionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductDataList;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class RuptureSessionProductActionTest extends BaseRuptureTest {

    private RuptureProductData getCurrentRuptureProduct() {
        return ruptureProductDataListBody.getItems().get(0);
    }

    @Step("Pre-condition: Создаем сессию с товаром и Action'ами")
    private void setUp(boolean actionState0, boolean actionState1) {

        // Generate test data
        ActionData action0 = ActionData.returnRandomData();
        action0.setAction(0);
        action0.setState(actionState0);

        ActionData action1 = ActionData.returnRandomData();
        action1.setAction(1);
        action1.setState(actionState1);

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(new ArrayList<>(Arrays.asList(action0, action1)));

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        // Create session
        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

        ruptureProductDataListBody = new RuptureProductDataList();
        ruptureProductDataListBody.setTotalCount(1);
        ruptureProductDataListBody.setItems(Collections.singletonList(productData));
    }

    @Test(description = "C3233584 PUT rupture action true")
    @AllureId("13199")
    public void testPutRuptureActionTrue() {
        setUp(false, false);
        RuptureProductData ruptureProductData = ruptureProductDataListBody.getItems().get(0);
        ruptureProductData.getActions().get(0).setState(true);

        ReqRuptureSessionWithActionsData ruptureData = new ReqRuptureSessionWithActionsData();
        ruptureData.setSessionId(sessionId);
        ruptureData.setLmCode(ruptureProductData.getLmCode());
        ruptureData.setActions(ruptureProductData.getActions());

        step("Изменяем Action у товара на true");
        Response<ResActionDataList> resp = rupturesClient.actionProduct(ruptureData);
        rupturesClient.assertThatSessionIsActivated(resp, ruptureData.getActions());

        step("Отправляем GET запрос и проверяем, что изменения применились");
        Response<RuptureProductDataList> respGet = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(respGet, ruptureProductDataListBody);
    }

    @Test(description = "C3285466 PUT rupture action false")
    @AllureId("13200")
    public void testPutRuptureActionFalse() {
        setUp(true, false);
        RuptureProductData ruptureProductData = getCurrentRuptureProduct();
        ruptureProductData.getActions().get(0).setState(false);

        ReqRuptureSessionWithActionsData ruptureData = new ReqRuptureSessionWithActionsData();
        ruptureData.setSessionId(sessionId);
        ruptureData.setLmCode(ruptureProductData.getLmCode());
        ruptureData.setActions(ruptureProductData.getActions());

        step("Изменяем Action у товара на true");
        Response<ResActionDataList> resp = rupturesClient.actionProduct(ruptureData);
        rupturesClient.assertThatSessionIsActivated(resp, ruptureData.getActions());

        step("Отправляем GET запрос и проверяем, что изменения применились");
        Response<RuptureProductDataList> respGet = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(respGet, ruptureProductDataListBody);
    }

    @Test(description = "C3285467 PUT ruptures action for not existed product")
    @AllureId("13201")
    public void testPutRuptureActionForNotExistedProduct() {
        setUp(false, false);

        ReqRuptureSessionWithActionsData ruptureData = new ReqRuptureSessionWithActionsData();
        ruptureData.setSessionId(sessionId);
        ruptureData.setLmCode("11111111"); // not existed product
        ruptureData.setActions(getCurrentRuptureProduct().getActions());

        step("Изменяем Action у товара на true");
        Response<ResActionDataList> resp = rupturesClient.actionProduct(ruptureData);
        rupturesClient.assertThatSessionIsNotActivated(resp, ruptureData.getActions());

        step("Отправляем GET запрос и проверяем, что изменения применились");
        Response<RuptureProductDataList> respGet = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(respGet, ruptureProductDataListBody);
    }

    @Test(description = "C23409762 PUT rupture action add not included actions")
    @AllureId("13202")
    public void testPutRuptureActionAddNotIncludeActions() {
        setUp(false, true);

        ActionData actionData2 = ActionData.returnRandomData();
        actionData2.setAction(2);
        ActionData actionData3 = ActionData.returnRandomData();
        actionData3.setAction(3);
        ActionData actionData4 = ActionData.returnRandomData();
        actionData4.setAction(4);
        ActionData actionData5 = ActionData.returnRandomData();
        actionData5.setAction(5);
        ActionData actionData6 = ActionData.returnRandomData();
        actionData6.setAction(6);
        ActionData actionData7 = ActionData.returnRandomData();
        actionData7.setAction(7);

        RuptureProductData ruptureProductData = getCurrentRuptureProduct();

        ReqRuptureSessionWithActionsData ruptureData = new ReqRuptureSessionWithActionsData();
        ruptureData.setSessionId(sessionId);
        ruptureData.setLmCode(ruptureProductData.getLmCode());
        ruptureProductData.getActions().addAll(new ArrayList<>(Arrays.asList(actionData2, actionData3, actionData4,
                actionData5, actionData6, actionData7)));
        ruptureData.setActions(ruptureProductData.getActions());

        step("Добавляем Action'ы товару");
        Response<ResActionDataList> resp = rupturesClient.actionProduct(ruptureData);
        rupturesClient.assertThatSessionIsActivated(resp, ruptureData.getActions());

        step("Отправляем GET запрос и проверяем, что изменения применились");
        Response<RuptureProductDataList> respGet = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(respGet, ruptureProductDataListBody);
    }

    @Test(description = "C23409763 PUT rupture action remove one action")
    @AllureId("13203")
    public void testPutRuptureActionRemoveActions() {
        setUp(true, false);

        ReqRuptureSessionWithActionsData ruptureData = new ReqRuptureSessionWithActionsData();
        ruptureData.setSessionId(sessionId);
        ruptureData.setLmCode(getCurrentRuptureProduct().getLmCode());
        ruptureData.setActions(getCurrentRuptureProduct().getActions().subList(0, 1));

        step("Удаляем один Action в товаре");
        Response<ResActionDataList> resp = rupturesClient.actionProduct(ruptureData);
        rupturesClient.assertThatSessionIsActivated(resp, ruptureData.getActions());
        ruptureProductDataListBody.getItems().get(0).getActions().remove(1);


        step("Отправляем GET запрос и проверяем, что изменения применились");
        Response<RuptureProductDataList> respGet = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(respGet, ruptureProductDataListBody);
    }

    @Test(description = "C23409765 PUT rupture action for finished session")
    @AllureId("13204")
    public void testPutRuptureActionForFinishedSession() {
        setUp(true, false);

        step("Завершаем сессию");
        Response<JsonNode> respFinishSession = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(respFinishSession);

        step("Добавляем Action'ы в завершенную сессию");
        ActionData actionData2 = ActionData.returnRandomData();
        actionData2.setAction(2);
        ActionData actionData3 = ActionData.returnRandomData();
        actionData3.setAction(3);

        RuptureProductData ruptureProductData = getCurrentRuptureProduct();

        ReqRuptureSessionWithActionsData ruptureData = new ReqRuptureSessionWithActionsData();
        ruptureData.setSessionId(sessionId);
        ruptureData.setLmCode(ruptureProductData.getLmCode());
        ruptureProductData.getActions().addAll(new ArrayList<>(Arrays.asList(actionData2, actionData3)));
        ruptureData.setActions(ruptureProductData.getActions());

        Response<ResActionDataList> resp = rupturesClient.actionProduct(ruptureData);
        rupturesClient.assertThatSessionIsActivated(resp, ruptureData.getActions());

        step("Отправляем GET запрос и проверяем, что изменения применились");
        Response<RuptureProductDataList> respGet = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(respGet, ruptureProductDataListBody);
    }


}
