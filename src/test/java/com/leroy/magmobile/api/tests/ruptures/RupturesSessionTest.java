package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.*;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;

public class RupturesSessionTest extends BaseProjectApiTest {

    private RupturesClient rupturesClient;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    private Integer sessionId;
    private RuptureProductDataList ruptureProductDataList;

    @BeforeClass
    public void setUp() {
        rupturesClient = apiClientProvider.getRupturesClient();
    }

    @Test(description = "Create Rupture session product")
    public void testCreateRuptureSessionProduct() {
        ActionData action1 = new ActionData();
        action1.generateRandomData();
        action1.setAction(0);
        action1.setState(false);
        ActionData action2 = new ActionData();
        action2.generateRandomData();
        action2.setAction(1);
        action2.setState(false);

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(Arrays.asList(action1, action2));

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(sessionData.getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(sessionData.getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(sessionData.getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createProduct(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
        ruptureProductDataList = new RuptureProductDataList();
        ruptureProductDataList.addItem(productData);
    }

    @Test(description = "Update Rupture session product")
    public void testUpdateRuptureSessionProduct() {
        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setSessionId(sessionId);
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(sessionData.getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(sessionData.getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(sessionData.getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.updateProduct(rupturePostData);
        rupturesClient.assertThatProductIsUpdatedOrDeleted(resp);
        ruptureProductDataList.addItem(productData);
    }

    @Test(description = "Action Rupture session product")
    public void testActionRuptureSessionProduct() {
        ActionData action1 = new ActionData();
        action1.generateRandomData();
        ActionData action2 = new ActionData();
        action2.generateRandomData();

        ReqRuptureSessionWithActionsData ruptureData = new ReqRuptureSessionWithActionsData();
        ruptureData.setSessionId(sessionId);
        ruptureData.setLmCode(RandomStringUtils.randomNumeric(8));
        ruptureData.setActions(Arrays.asList(action1, action2));

        Response<ResActionDataList> resp = rupturesClient.actionProduct(ruptureData);
        rupturesClient.assertThatSessionIsActivated(resp, ruptureData.getActions());
    }

    @Test(description = "Search for Rupture session products")
    public void testSearchForRuptureSessionProducts() {
        Response<RuptureProductDataList> resp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(resp, ruptureProductDataList);
    }

    @Test(description = "Delete Rupture session products")
    public void testDeleteRuptureSessionProducts() {
        String deleteLmCode = ruptureProductDataList.getItems().get(0).getLmCode();
        Response<JsonNode> resp = rupturesClient.deleteProduct(deleteLmCode, sessionId);
        rupturesClient.assertThatProductIsUpdatedOrDeleted(resp);

        // TODO search again and check?
    }

}
