package com.leroy.magmobile.api.tests.ruptures;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.ruptures.ActionData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.data.ruptures.RuptureProductDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureSessionGroupData;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class RupturesSessionGroupsTest extends BaseRuptureTest {

    // Pre-conditions

    @Step("Pre-conditions: Создаем сессию с товарами с одним экшеном + Товар без экшена")
    private void createSessionWithOneActionAndSeveralProducts() {
        Random random = new Random();
        ActionData action1 = new ActionData();
        action1.setAction(0);
        action1.setUserPosition(0);
        action1.setState(random.nextBoolean());

        RuptureProductData productData1 = new RuptureProductData();
        productData1.generateRandomData();
        productData1.setActions(Collections.singletonList(action1));

        RuptureProductData productData2 = new RuptureProductData();
        productData2.generateRandomData();
        productData2.setActions(Collections.singletonList(action1));

        RuptureProductData productData3 = new RuptureProductData();
        productData3.generateRandomData();
        productData3.setActions(Collections.singletonList(action1));

        RuptureProductData productData4 = new RuptureProductData();
        productData4.generateRandomData();
        productData4.setActions(Collections.singletonList(action1));

        RuptureProductData productData5 = new RuptureProductData();
        productData5.generateRandomData();
        productData5.setActions(null);

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData1);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

        ruptureProductDataListBody = new RuptureProductDataList();
        ruptureProductDataListBody.addItem(productData1);

        // Добавляем оставшиеся товары

        // Товар 2
        rupturePostData.setSessionId(sessionId);
        rupturePostData.setProduct(productData2);

        resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.addItem(productData2);

        // Товар 3
        rupturePostData.setProduct(productData3);

        resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.addItem(productData3);

        // Товар 4
        rupturePostData.setProduct(productData4);

        resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.addItem(productData4);

        // Товар 5
        rupturePostData.setProduct(productData5);

        resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.addItem(productData5);
    }

    @Step("Pre-conditions: Создаем сессию с товарами, со всеми добавленными экшенами и случайными выполненными экшенами + Товар без экшена")
    private void createSessionWithAllActions() {
        Random random = new Random();
        ActionData action1 = new ActionData();
        action1.setAction(0);
        action1.setUserPosition(0);
        action1.setState(random.nextBoolean());
        ActionData action2 = new ActionData();
        action2.setAction(1);
        action2.setUserPosition(0);
        action2.setState(random.nextBoolean());
        ActionData action3 = new ActionData();
        action3.setAction(2);
        action3.setUserPosition(0);
        action3.setState(random.nextBoolean());
        ActionData action4 = new ActionData();
        action4.setAction(3);
        action4.setUserPosition(0);
        action4.setState(random.nextBoolean());

        RuptureProductData productData1 = new RuptureProductData();
        productData1.generateRandomData();
        productData1.setActions(Arrays.asList(action1, action2, action3, action4));

        RuptureProductData productData2 = new RuptureProductData();
        productData2.generateRandomData();
        productData2.setActions(Arrays.asList(action1, action2, action3, action4));

        RuptureProductData productData3 = new RuptureProductData();
        productData3.generateRandomData();
        productData3.setActions(Arrays.asList(action1, action2, action3, action4));

        RuptureProductData productData4 = new RuptureProductData();
        productData4.generateRandomData();
        productData4.setActions(Arrays.asList(action1, action2, action3, action4));

        RuptureProductData productData5 = new RuptureProductData();
        productData5.generateRandomData();
        productData5.setActions(null);

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData1);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

        ruptureProductDataListBody = new RuptureProductDataList();
        ruptureProductDataListBody.addItem(productData1);

        // Добавляем оставшиеся товары

        // Товар 2
        rupturePostData.setSessionId(sessionId);
        rupturePostData.setProduct(productData2);

        resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.addItem(productData2);

        // Товар 3
        rupturePostData.setProduct(productData3);

        resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.addItem(productData3);

        // Товар 4
        rupturePostData.setProduct(productData4);

        resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.addItem(productData4);

        // Товар 5
        rupturePostData.setProduct(productData5);

        resp = rupturesClient.addProductToSession(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        ruptureProductDataListBody.addItem(productData5);
    }

    @Step("Pre-conditions: Создаем сессию с товаром без Action")
    private void createSessionWithProductWithoutActions() {
        RuptureProductData productData1 = new RuptureProductData();
        productData1.generateRandomData();
        productData1.setActions(null);

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData1);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

        ruptureProductDataListBody = new RuptureProductDataList();
        ruptureProductDataListBody.addItem(productData1);
    }

    @Test(description = "C3285462 GET ruptures groups for new session with groups")
    @AllureId("13176")
    public void testGetRupturesGroupsWithGroups() {
        createSessionWithAllActions();

        HashMap<Integer, RuptureSessionGroupData> expectedGroups = new HashMap<>();
        for (RuptureProductData item : ruptureProductDataListBody.getItems()) {
            if (item.getActions() == null)
                continue;
            for (ActionData actionData : item.getActions()) {
                if (expectedGroups.get(actionData.getAction()) == null) {
                    RuptureSessionGroupData ruptureSessionGroupData = new RuptureSessionGroupData();
                    ruptureSessionGroupData.setActiveCount(0);
                    ruptureSessionGroupData.setFinishedCount(0);
                    ruptureSessionGroupData.setAction(actionData.getAction());
                    expectedGroups.put(actionData.getAction(), ruptureSessionGroupData);
                }
                RuptureSessionGroupData groupData = expectedGroups.get(actionData.getAction());
                if (actionData.getState())
                    groupData.increaseFinishedCount();
                else
                    groupData.increaseActiveCount();
            }
        }

        assertThat("Недостаточное кол-во Actions было создано в pre-condition", expectedGroups.size(),
                greaterThanOrEqualTo(4));

        step("Основная часть теста");
        Response<RuptureSessionGroupData> resp = rupturesClient.getGroups(sessionId);
        isResponseOk(resp);
        List<RuptureSessionGroupData> groups = resp.asJsonList(RuptureSessionGroupData.class);
        assertThat("groups count", groups, hasSize(expectedGroups.size()));
        for (int i = 0; i < groups.size(); i++) {
            int action = groups.get(i).getAction();
            assertThat("Action", action, equalTo(i));
            assertThat("Action " + action + "; ActiveCount", groups.get(i).getActiveCount(),
                    equalTo(expectedGroups.get(action).getActiveCount()));
            assertThat("Action " + action + "; FinishedCount", groups.get(i).getFinishedCount(),
                    equalTo(expectedGroups.get(action).getFinishedCount()));
        }
    }

    @Test(description = "C23409187 GET ruptures groups for existing session with 1 group")
    @AllureId("13178")
    public void testGetRupturesGroupsWith1Group() {
        createSessionWithOneActionAndSeveralProducts();

        HashMap<Integer, RuptureSessionGroupData> expectedGroups = new HashMap<>();
        for (RuptureProductData item : ruptureProductDataListBody.getItems()) {
            if (item.getActions() == null)
                continue;
            for (ActionData actionData : item.getActions()) {
                if (expectedGroups.get(actionData.getAction()) == null) {
                    RuptureSessionGroupData ruptureSessionGroupData = new RuptureSessionGroupData();
                    ruptureSessionGroupData.setActiveCount(0);
                    ruptureSessionGroupData.setFinishedCount(0);
                    ruptureSessionGroupData.setAction(actionData.getAction());
                    expectedGroups.put(actionData.getAction(), ruptureSessionGroupData);
                }
                RuptureSessionGroupData groupData = expectedGroups.get(actionData.getAction());
                if (actionData.getState())
                    groupData.increaseFinishedCount();
                else
                    groupData.increaseActiveCount();
            }
        }

        assertThat("Недостаточное кол-во Actions было создано в pre-condition", expectedGroups.size(),
                equalTo(1));

        step("Основная часть теста");
        Response<RuptureSessionGroupData> resp = rupturesClient.getGroups(sessionId);
        isResponseOk(resp);
        List<RuptureSessionGroupData> groups = resp.asJsonList(RuptureSessionGroupData.class);
        assertThat("groups count", groups, hasSize(expectedGroups.size()));
        for (int i = 0; i < groups.size(); i++) {
            int action = groups.get(i).getAction();
            assertThat("Action", action, equalTo(i));
            assertThat("Action " + action + "; ActiveCount", groups.get(i).getActiveCount(),
                    equalTo(expectedGroups.get(action).getActiveCount()));
            assertThat("Action " + action + "; FinishedCount", groups.get(i).getFinishedCount(),
                    equalTo(expectedGroups.get(action).getFinishedCount()));
        }
    }

    @Test(description = "C3233581 GET rupture groups for new session without groups")
    @AllureId("13175")
    public void testGetRuptureGroupsWithoutGroups() {
        createSessionWithProductWithoutActions();

        step("Основная часть теста");
        Response<RuptureSessionGroupData> resp = rupturesClient.getGroups(sessionId);
        isResponseOk(resp);
        assertThat("Response body", resp.asString(), equalTo("[]"));
    }

    @Test(description = "C3285464 GET ruptures groups for non existing session")
    @AllureId("13177")
    public void testGetRupturesGroupsForNonExistingSession() {
        Response<RuptureSessionGroupData> resp = rupturesClient.getGroups(Integer.MAX_VALUE);
        isResponseOk(resp);
        assertThat("Response body", resp.asString(), equalTo("[]"));
    }
}
