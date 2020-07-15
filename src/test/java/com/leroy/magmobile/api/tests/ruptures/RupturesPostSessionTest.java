package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.*;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class RupturesPostSessionTest extends BaseRuptureTest {

    @Test(description = "C3233579 POST rupture session product")
    public void testCreateRuptureSessionProduct() {
        RupturesClient rupturesClient = rupturesClient();
        ActionData action1 = new ActionData();
        action1.setAction(0);
        action1.setState(false);
        action1.setUserPosition(0);
        ActionData action2 = new ActionData();
        action2.setAction(1);
        action2.setState(false);
        action2.setUserPosition(0);
        ActionData action3 = new ActionData();
        action3.setAction(2);
        action3.setState(true);
        action3.setUserPosition(0);

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(Arrays.asList(action1, action2, action3));

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        step("Создаем сессию");
        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
        ruptureProductDataListBody = new RuptureProductDataList();
        ruptureProductDataListBody.setTotalCount(1);
        ruptureProductDataListBody.setItems(Collections.singletonList(productData));

        step("GET /ruptures/sessions - Проверяем, что сессия была создана");
        Response<ResRuptureSessionDataList> getResp = rupturesClient.getSessions(50);
        isResponseOk(getResp);
        ResRuptureSessionDataList respBody = getResp.asJson();
        List<ResRuptureSessionData> items = respBody.getItems().stream().filter(
                a -> a.getSessionId().equals(sessionId)).collect(Collectors.toList());
        assertThat("Session " + sessionId + " wasn't found", items, hasSize(1));

        step("GET /ruptures/session/products - Проверяем, что товар был добавлен в сессию");
        Response<RuptureProductDataList> respGetProducts = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(respGetProducts, ruptureProductDataListBody);
    }

    @Test(description = "C23195088 PUT rupture actions with different states", enabled = false)
    // TODO Removed from TestRail
    public void testActionRuptureSessionProduct() {
        RupturesClient rupturesClient = rupturesClient();
        RuptureProductData ruptureProductData = ruptureProductDataListBody.getItems().get(0);
        for (ActionData actionData : ruptureProductData.getActions()) {
            actionData.setState(!actionData.getState());
        }

        ReqRuptureSessionWithActionsData ruptureData = new ReqRuptureSessionWithActionsData();
        ruptureData.setSessionId(sessionId);
        ruptureData.setLmCode(ruptureProductData.getLmCode());
        ruptureData.setActions(ruptureProductData.getActions());

        Response<ResActionDataList> resp = rupturesClient.actionProduct(ruptureData);
        rupturesClient.assertThatSessionIsActivated(resp, ruptureData.getActions());
    }

}
