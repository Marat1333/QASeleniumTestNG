package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.*;
import io.qameta.allure.Step;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RuptureSessionProductActionTest extends BaseRuptureTest {

    private RuptureProductData getCurrentRuptureProduct() {
        return ruptureProductDataListBody.getItems().get(0);
    }

    @Step("Pre-condition: Создаем сессию с товаром и Action'ами")
    private void setUp(boolean actionState0, boolean actionState1) {
        RupturesClient rupturesClient = rupturesClient();

        // Generate test data
        ActionData actionFalse0 = ActionData.returnRandomData();
        actionFalse0.setAction(0);
        actionFalse0.setState(actionState0);

        ActionData actionFalse1 = ActionData.returnRandomData();
        actionFalse1.setAction(1);
        actionFalse1.setState(actionState1);

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(new ArrayList<>(Arrays.asList(actionFalse0, actionFalse1)));

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
    public void testPutRuptureActionTrue() {
        setUp(false, false);

        RupturesClient rupturesClient = rupturesClient();
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
    public void testPutRuptureActionFalse() {
        setUp(true, false);

        RupturesClient rupturesClient = rupturesClient();
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
    public void testPutRuptureActionForNotExistedProduct() {
        setUp(false, false);

        RupturesClient rupturesClient = rupturesClient();

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
    public void testPutRuptureActionAddNotIncludeActions() {
        setUp(false, true);

        RupturesClient rupturesClient = rupturesClient();

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

    @Test(description = "C23409763 PUT rupture action true remove actions", enabled = false) // TODO check!
    public void testPutRuptureActionRemoveActions() {
        setUp(true, false);

        RupturesClient rupturesClient = rupturesClient();

        ReqRuptureSessionWithActionsData ruptureData = new ReqRuptureSessionWithActionsData();
        ruptureData.setSessionId(sessionId);
        ruptureData.setLmCode(getCurrentRuptureProduct().getLmCode());
        ruptureData.setActions(new ArrayList<>());

        step("Удаляем Action'ы в товаре");
        Response<ResActionDataList> resp = rupturesClient.actionProduct(ruptureData);
        rupturesClient.assertThatSessionIsActivated(resp, ruptureData.getActions());

        //step("Отправляем GET запрос и проверяем, что изменения применились");
        //Response<RuptureProductDataList> respGet = rupturesClient.getProducts(sessionId);
        //rupturesClient.assertThatDataMatches(respGet, ruptureProductDataListBody);
    }

    @Test(description = "C23409765 PUT rupture action for finished session")
    public void testPutRuptureActionForFinishedSession() {
        setUp(true, false);

        RupturesClient rupturesClient = rupturesClient();

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
