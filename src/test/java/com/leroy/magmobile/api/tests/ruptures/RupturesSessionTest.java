package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.*;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;

public class RupturesSessionTest extends BaseProjectApiTest {

    private RupturesClient rupturesClient() {
        return apiClientProvider.getRupturesClient();
    }

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    private Integer sessionId;
    private RuptureProductDataList ruptureProductDataList;

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

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
        ruptureProductDataList = new RuptureProductDataList();
        ruptureProductDataList.addItem(productData);
    }

    @Test(description = "C23195088 PUT rupture actions with different states", enabled = false) // TODO Removed from TestRail
    public void testActionRuptureSessionProduct() {
        RupturesClient rupturesClient = rupturesClient();
        RuptureProductData ruptureProductData = ruptureProductDataList.getItems().get(0);
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

    @Test(description = "C3233585 PUT ruptures session finish")
    public void testFinishRuptureSession() {
        RupturesClient rupturesClient = rupturesClient();
        Response<JsonNode> resp = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
    }

    @Test(description = "C3285352 PUT ruptures session finish for finished session")
    public void testFinishFinishedRuptureSession() {
        RupturesClient rupturesClient = rupturesClient();
        Response<JsonNode> resp = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatActionIsNotAllowed(resp, sessionId);
    }

    @Test(description = "C3285353 PUT ruptures session finish for deleted session")
    public void testFinishDeletedRuptureSession() {
        RupturesClient rupturesClient = rupturesClient();
        Response<JsonNode> resp = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatActionIsNotAllowed(resp, sessionId);
    }

}
