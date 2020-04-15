package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.*;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

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

    @Test(description = "C3233579 POST rupture session product")
    public void testCreateRuptureSessionProduct() {
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
        rupturePostData.setShopId(Integer.parseInt(sessionData.getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(sessionData.getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(sessionData.getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createProduct(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
        ruptureProductDataList = new RuptureProductDataList();
        ruptureProductDataList.addItem(productData);
    }

    @Test(description = "C3233582 PUT ruptures product - Add new product")
    public void testUpdateRuptureSessionProduct() {
        ActionData action1 = new ActionData();
        action1.setAction(0);
        action1.setState(false);
        action1.setUserPosition(0);

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(Collections.singletonList(action1));

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setSessionId(sessionId);
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(sessionData.getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(sessionData.getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(sessionData.getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.updateProduct(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataList.addItem(productData);
    }

    @Test(description = "C23195088 PUT rupture actions with different states")
    public void testActionRuptureSessionProduct() {
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

    @Test(description = "C3233583 GET ruptures products")
    public void testSearchForRuptureSessionProducts() {
        Response<RuptureProductDataList> resp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(resp, ruptureProductDataList);
    }

    @Test(description = "C3285462 GET ruptures groups for new session with groups")
    public void testRuptureSessionGrouping() {
        Response<RuptureSessionGroupData> resp = rupturesClient.getGroups(sessionId);
        isResponseOk(resp);
        List<RuptureSessionGroupData> groups = resp.asJsonList(RuptureSessionGroupData.class);
        assertThat("groups count", groups, hasSize(3));
        RuptureSessionGroupData gr1 = groups.get(0);
        assertThat("gr1 - ", gr1.getAction(), is(0));
        assertThat("gr1 - ", gr1.getActiveCount(), is(1));
        assertThat("gr1 - ", gr1.getFinishedCount(), is(1));

        RuptureSessionGroupData gr2 = groups.get(1);
        assertThat("gr2 - ", gr2.getAction(), is(1));
        assertThat("gr2 - ", gr2.getActiveCount(), is(0));
        assertThat("gr2 - ", gr2.getFinishedCount(), is(1));

        RuptureSessionGroupData gr3 = groups.get(2);
        assertThat("gr3 - ", gr3.getAction(), is(2));
        assertThat("gr3 - ", gr3.getActiveCount(), is(1));
        assertThat("gr3 - ", gr3.getFinishedCount(), is(0));
    }

    @Test(description = "C3233585 PUT ruptures session finish")
    public void testFinishRuptureSession() {
        Response<JsonNode> resp = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
    }

    @Test(description = "C3298403 DELETE ruptures product from finished session")
    public void testDeleteRuptureSessionProducts() {
        step("Delete product");
        String deleteLmCode = ruptureProductDataList.getItems().get(0).getLmCode();
        Response<JsonNode> resp = rupturesClient.deleteProduct(deleteLmCode, sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataList.removeItem(0);

        step("Send get Request and check data");
        Response<RuptureProductDataList> getResp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(getResp, ruptureProductDataList);
    }

    @Test(description = "C3233587 DELETE finished ruptures session")
    public void testDeleteFinishedRuptureSession() {
        step("Delete session");
        Response<JsonNode> resp = rupturesClient.deleteSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        if (ruptureProductDataList.getItems().size() > 0)
            ruptureProductDataList.removeItem(0);

        step("Send get Request and check data");
        Response<RuptureProductDataList> getResp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(getResp, ruptureProductDataList);
    }

}
