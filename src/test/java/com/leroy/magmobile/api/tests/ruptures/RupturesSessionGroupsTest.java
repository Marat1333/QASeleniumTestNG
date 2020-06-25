package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.*;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import io.qameta.allure.Step;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RupturesSessionGroupsTest extends BaseProjectApiTest {

    private RupturesClient rupturesClient() {
        return apiClientProvider.getRupturesClient();
    }

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    private ThreadLocal<Integer> sessionId = new ThreadLocal<>();
    private ThreadLocal<RuptureProductDataList> ruptureProductDataList = new ThreadLocal<>();

    @AfterMethod
    private void deleteSessionAfter() {
        if (sessionId.get() != null) {
            RupturesClient rupturesClient = rupturesClient();
            Response<JsonNode> r = rupturesClient.deleteSession(sessionId.get());
            rupturesClient.assertThatIsUpdatedOrDeleted(r);
        }
    }

    // Pre-conditions

    @Step("Pre-conditions: Создаем сессию с товарами, со всеми добавленными экшенами и случайными выполненными экшенами + Товар без экшена")
    private void createSessionWithAllActions() {
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
        ActionData action4 = new ActionData();
        action4.setAction(3);
        action4.setState(true);
        action4.setUserPosition(0);

        RuptureProductData productData1 = new RuptureProductData();
        productData1.generateRandomData();
        productData1.setActions(Collections.singletonList(action1));

        RuptureProductData productData2 = new RuptureProductData();
        productData2.generateRandomData();
        productData2.setActions(Collections.singletonList(action2));

        RuptureProductData productData3 = new RuptureProductData();
        productData3.generateRandomData();
        productData3.setActions(Collections.singletonList(action3));

        RuptureProductData productData4 = new RuptureProductData();
        productData4.generateRandomData();
        productData4.setActions(Collections.singletonList(action4));

        RuptureProductData productData5 = new RuptureProductData();
        productData5.generateRandomData();
        productData5.setActions(null);

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData1);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createProduct(rupturePostData);
        sessionId.set(rupturesClient.assertThatSessionIsCreatedAndGetId(resp));

        RuptureProductDataList dataList = new RuptureProductDataList();
        dataList.addItem(productData1);

        ruptureProductDataList.set(dataList);

        // Добавляем оставшиеся товары

        // Товар 2
        rupturePostData.setSessionId(sessionId.get());
        rupturePostData.setProduct(productData2);

        resp = rupturesClient.updateProduct(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        dataList.addItem(productData2);

        // Товар 3
        rupturePostData.setProduct(productData3);

        resp = rupturesClient.updateProduct(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        dataList.addItem(productData3);

        // Товар 4
        rupturePostData.setProduct(productData4);

        resp = rupturesClient.updateProduct(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        dataList.addItem(productData4);

        // Товар 5
        rupturePostData.setProduct(productData5);

        resp = rupturesClient.updateProduct(rupturePostData);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        dataList.addItem(productData5);
    }

    @Test(description = "C3285462 GET ruptures groups for new session with groups")
    public void testRuptureSessionGrouping() {
        createSessionWithAllActions();

        HashMap<Integer, RuptureSessionGroupData> expectedGroups = new HashMap<>();
        for (RuptureProductData item : ruptureProductDataList.get().getItems()) {
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
                    groupData.increaseActiveCount();
                else
                    groupData.increaseFinishedCount();
            }
        }

        assertThat("Недостаточное кол-во Actions было создано в pre-condition", expectedGroups.size(),
                greaterThanOrEqualTo(4));

        step("Основная часть теста");
        RupturesClient rupturesClient = rupturesClient();
        Response<RuptureSessionGroupData> resp = rupturesClient.getGroups(sessionId.get());
        isResponseOk(resp);
        List<RuptureSessionGroupData> groups = resp.asJsonList(RuptureSessionGroupData.class);
        assertThat("groups count", groups, hasSize(expectedGroups.size()));
        /*RuptureSessionGroupData gr1 = groups.get(0);
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
        assertThat("gr3 - ", gr3.getFinishedCount(), is(0));*/
    }

    @Test(description = "C3285464 GET ruptures groups for non existing session")
    public void testGetRupturesGroupsForNonExistingSession() {
        RupturesClient rupturesClient = rupturesClient();
        Response<RuptureSessionGroupData> resp = rupturesClient.getGroups(Integer.MAX_VALUE);
        isResponseOk(resp);
        assertThat("Response body", resp.asString(), equalTo("[]"));
    }
}
