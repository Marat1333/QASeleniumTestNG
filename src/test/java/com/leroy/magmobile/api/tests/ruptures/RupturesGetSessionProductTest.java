package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.api.ErrorTextConst;
import com.leroy.constants.api.StatusCodes;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.CommonErrorResponseData;
import com.leroy.magmobile.api.data.ruptures.ActionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductDataList;
import io.qameta.allure.AllureId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RupturesGetSessionProductTest extends BaseRuptureTest {

    @Inject
    private RupturesClient rupturesClient;

    @Override
    protected boolean isDeleteSessionAfterEveryMethod() {
        return false;
    }

    @BeforeClass
    public void setUp() {
        // Generate test data
        ActionData actionTrue0 = ActionData.returnRandomData();
        actionTrue0.setAction(0);
        actionTrue0.setState(true);

        ActionData actionFalse0 = ActionData.returnRandomData();
        actionFalse0.setAction(0);
        actionFalse0.setState(false);

        ActionData actionTrue1 = ActionData.returnRandomData();
        actionTrue1.setAction(1);
        actionTrue1.setState(true);

        ActionData actionFalse1 = ActionData.returnRandomData();
        actionFalse1.setAction(1);
        actionFalse1.setState(false);

        ActionData actionTrue7 = ActionData.returnRandomData();
        actionTrue7.setAction(7);
        actionTrue7.setState(true);

        ruptureProductDataListBody = new RuptureProductDataList();
        List<RuptureProductData> ruptureItems = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            RuptureProductData productData = new RuptureProductData();
            productData.generateRandomData();
            if (i < 4)
                productData.setActions(Arrays.asList(actionTrue0, actionTrue1));
            else if (i < 11) {
                if (i == 8)
                    productData.setActions(Arrays.asList(actionFalse0, actionFalse1, actionTrue7));
                else if (i < 8)
                    productData.setActions(Arrays.asList(actionTrue0, actionFalse1));
                else
                    productData.setActions(Arrays.asList(actionFalse0, actionFalse1));
            } else
                productData.setActions(new ArrayList<>());
            ruptureItems.add(productData);
        }
        ruptureProductDataListBody.setItems(ruptureItems);
        ruptureProductDataListBody.setTotalCount(ruptureItems.size());

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(ruptureItems.get(0));
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

        rupturePostData.setSessionId(sessionId);
        for (int i = 1; i < ruptureItems.size(); i++) {
            rupturePostData.setProduct(ruptureItems.get(i));
            resp = rupturesClient.addProductToSession(rupturePostData);
            rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        }

    }

    @Test(description = "C3233583 GET ruptures products")
    @AllureId("13184")
    public void testSearchForRuptureSessionProducts() {
        Response<RuptureProductDataList> resp = rupturesClient.getProducts(sessionId);
        rupturesClient.assertThatDataMatches(resp, ruptureProductDataListBody);
    }

    @Test(description = "C3298405 GET ruptures session products productState=0")
    @AllureId("13185")
    public void testSearchForRuptureSessionProductsWithProductState0() {
        int productState = 0;
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithProductState(sessionId, productState);
        List<RuptureProductData> expectedItems = new ArrayList<>();
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(3);
        expectedResponse.setItems(expectedItems);
        for (RuptureProductData productData : ruptureProductDataListBody.getItems()) {
            if (productData.getActions() == null || productData.getActions().size() == 0 ||
                    productData.getActions().stream().filter(
                            p -> p.getState().equals(false)).count() == productData.getActions().size())
                expectedItems.add(productData);
        }
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C3298406 GET ruptures session products productState=1")
    @AllureId("13186")
    public void testSearchForRuptureSessionProductsWithProductStateTrue() {
        int productState = 1;
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithProductState(sessionId, productState);
        List<RuptureProductData> expectedItems = new ArrayList<>();
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(5);
        expectedResponse.setItems(expectedItems);
        for (RuptureProductData productData : ruptureProductDataListBody.getItems()) {
            boolean falseActionPresent = false;
            boolean trueActionPresent = false;
            for (ActionData actionData : productData.getActions()) {
                if (!actionData.getState())
                    falseActionPresent = true;
                if (actionData.getState())
                    trueActionPresent = true;
                if (falseActionPresent && trueActionPresent)
                    break;
            }
            if (falseActionPresent && trueActionPresent)
                expectedItems.add(productData);
        }
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C3298407 GET ruptures session products productState=2")
    @AllureId("13187")
    public void testSearchForRuptureSessionProductsWithProductState2() {
        int productState = 2;
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithProductState(sessionId, productState);
        List<RuptureProductData> expectedItems = new ArrayList<>();
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(4);
        expectedResponse.setItems(expectedItems);
        for (RuptureProductData productData : ruptureProductDataListBody.getItems()) {
            if (productData.getActions() != null && productData.getActions().size() > 0 &&
                    productData.getActions().stream().filter(
                            p -> p.getState().equals(true)).count() == productData.getActions().size())
                expectedItems.add(productData);
        }
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C3298408 GET ruptures session products productState=0,1")
    @AllureId("13188")
    public void testSearchForRuptureSessionProductsWithProductState0and1() {
        Integer[] productStates = {0, 1};
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithProductState(sessionId, productStates);
        List<RuptureProductData> expectedItems = new ArrayList<>();
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(8);
        expectedResponse.setItems(expectedItems);
        for (RuptureProductData productData : ruptureProductDataListBody.getItems()) {
            if (productData.getActions() == null || productData.getActions().size() == 0 ||
                    productData.getActions().stream().anyMatch(p -> p.getState().equals(false)))
                expectedItems.add(productData);
        }
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C3298409 GET ruptures session products productState=1,2")
    @AllureId("13189")
    public void testSearchForRuptureSessionProductsWithProductState1and2() {
        Integer[] productStates = {1, 2};
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithProductState(sessionId, productStates);
        List<RuptureProductData> expectedItems = new ArrayList<>();
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(9);
        expectedResponse.setItems(expectedItems);
        for (RuptureProductData productData : ruptureProductDataListBody.getItems()) {
            if (productData.getActions().stream().anyMatch(p -> p.getState().equals(true)))
                expectedItems.add(productData);
        }
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C23409753 GET ruptures products pagination 1-st page")
    @AllureId("13190")
    public void testGetRupturesProductsPaginationFirstPage() {
        int pageSize = 4;
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithPagination(sessionId, null, pageSize);
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(12);
        List<RuptureProductData> expectedItems = new ArrayList<>(ruptureProductDataListBody.getItems());
        expectedResponse.setItems(expectedItems.subList(expectedItems.size() - pageSize, expectedItems.size()));
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C23409754 GET ruptures products pagination 2-nd page")
    @AllureId("13191")
    public void testGetRupturesProductsPaginationSecondPage() {
        int pageSize = 4;
        int startFrom = 5;
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithPagination(sessionId, startFrom, pageSize);
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(12);
        List<RuptureProductData> expectedItems = new ArrayList<>(ruptureProductDataListBody.getItems());
        expectedResponse.setItems(expectedItems.subList(4, 8));
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C23409755 GET ruptures products pagination default is absent")
    @AllureId("13192")
    public void testGetRupturesProductsDefaultPaginationIsAbsent() {
        Response<RuptureProductDataList> resp = rupturesClient.getProducts(sessionId);
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(12);
        expectedResponse.setItems(new ArrayList<>(ruptureProductDataListBody.getItems()));
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C23409756 GET ruptures products mashup validation")
    @AllureId("13193")
    public void testGetRupturesProductsMashupValidation() {
        Response<?> resp = rupturesClient.getProducts("");
        assertThat("Response Code", resp.getStatusCode(), equalTo(StatusCodes.ST_400_BAD_REQ));
        CommonErrorResponseData errorResp = resp.asJson(CommonErrorResponseData.class);
        assertThat("error text", errorResp.getError(),
                equalTo(ErrorTextConst.WRONG_PATH));
        assertThat("validation sessionId", errorResp.getValidation().getSessionId(),
                equalTo(ErrorTextConst.REQUIRED));
    }

    @Test(description = "C23409757 GET ruptures products action + action state (only action state do not work)")
    @AllureId("13194")
    public void testGetRupturesProductsActionPlusOnlyActionState() {
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithAction(sessionId, true, null);
        rupturesClient.assertThatDataMatches(resp, ruptureProductDataListBody);
    }

    @Test(description = "C23409758 GET ruptures products action + action (only action do not work)")
    @AllureId("13195")
    public void testGetRupturesProductsActionPlusOnlyAction() {
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithAction(sessionId, null, 7);
        rupturesClient.assertThatDataMatches(resp, ruptureProductDataListBody);
    }

    @Test(description = "C23409759 GET ruptures products action + action state (action + true)")
    @AllureId("13196")
    public void testGetRupturesProductsActionPlusActionStateTrue() {
        int action = 0;
        boolean actionState = true;
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithAction(sessionId, actionState, action);
        List<RuptureProductData> expectedItems = new ArrayList<>();
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(8);
        expectedResponse.setItems(expectedItems);
        for (RuptureProductData productData : ruptureProductDataListBody.getItems()) {
            if (productData.getActions().stream().anyMatch(
                    p -> p.getAction().equals(action) && p.getState().equals(actionState)))
                expectedItems.add(productData);
        }
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C23409760 GET ruptures products action + action state (action + false)")
    @AllureId("13197")
    public void testGetRupturesProductsActionPlusActionStateFalse() {
        int action = 1;
        boolean actionState = false;
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithAction(sessionId, actionState, action);
        List<RuptureProductData> expectedItems = new ArrayList<>();
        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(7);
        expectedResponse.setItems(expectedItems);
        for (RuptureProductData productData : ruptureProductDataListBody.getItems()) {
            if (productData.getActions().stream().anyMatch(
                    p -> p.getAction().equals(action) && p.getState().equals(actionState)))
                expectedItems.add(productData);
        }
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }

    @Test(description = "C23409761 GET ruptures products all filters")
    @AllureId("13198")
    public void testGetRupturesProductsAllFilters() {
        int action = 0;
        boolean actionState = true; // TODO check?
        Integer[] productState = {2};
        int pageSize = 1;
        Response<RuptureProductDataList> resp = rupturesClient.getProducts(
                sessionId, actionState, action, productState, null, pageSize);

        RuptureProductDataList expectedResponse = new RuptureProductDataList();
        expectedResponse.setTotalCount(4);
        expectedResponse.setItems(Collections.singletonList(
                ruptureProductDataListBody.getItems().get(3)));
        rupturesClient.assertThatDataMatches(resp, expectedResponse);
    }


}
