package com.leroy.magmobile.api.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.*;
import com.leroy.magportal.api.helpers.BaseHelper;
import io.qameta.allure.Step;
import org.testng.Assert;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThan;

public class RuptureHelper extends BaseHelper {

    @Inject
    RupturesClient rupturesClient;

    @Step("Создать сессию с продуктами")
    public int createSession(List<RuptureProductData> productDataList) {

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productDataList.get(0));
        rupturePostData.setShopId(Integer.parseInt(userSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(userSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(userSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        int sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

        if (productDataList.size() > 1) {
            rupturePostData.setSessionId(sessionId);
            for (int i = 1; i < productDataList.size(); i++) {
                rupturePostData.setProduct(productDataList.get(i));
                Response<JsonNode> respUpdate = rupturesClient.addProductToSession(rupturePostData);
                rupturesClient.assertThatIsUpdatedOrDeleted(respUpdate);
            }
        }
        return sessionId;
    }

    @Step("Создать несколько сессий")
    public List<Integer> createFewSessions(int sessionsCount) {
        List<Integer> sessionIdList = new ArrayList<>();
        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();
        productData.setActions(null);

        for (int i = 0; i < sessionsCount; i++) {
            sessionIdList.add(this.createSession(Collections.singletonList(productData)));
        }
        return sessionIdList;
    }

    @Step("Завершить сессию по id {sessionId}")
    public void finishSession(int sessionId) {
        finishFewSessions(Collections.singletonList(sessionId));
    }

    @Step("Завершить несколько сессий")
    public void finishFewSessions(List<Integer> sessionsIdList) {
        for (Integer each : sessionsIdList) {
            Response<JsonNode> resp = rupturesClient.finishSession(each);
            rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        }
    }

    @Step("Удалить все сессии в отделе")
    public void deleteAllSessionInCurrentDepartment() {
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(100);
        assertThat("getSession", resp, successful());
        List<ResRuptureSessionData> sessionsDataList = resp.asJson().getItems();
        for (ResRuptureSessionData each : sessionsDataList) {
            Response<JsonNode> respDelete = rupturesClient.deleteSession(each.getSessionId());
            rupturesClient.assertThatIsUpdatedOrDeleted(respDelete);
        }
    }

    @Step("Удалить несколько сессий")
    public void deleteSessions(int... sessions) {
        for (int each : sessions) {
            Response<JsonNode> respDelete = rupturesClient.deleteSession(each);
            rupturesClient.assertThatIsUpdatedOrDeleted(respDelete);
        }
    }

    @Step("Получить список продуктов сессии {sessionId}")
    public RuptureProductDataList getProducts(int sessionId) {
        Response<RuptureProductDataList> resp = rupturesClient.getProductsWithPagination(sessionId, 1, 100);
        assertThat(resp, successful());
        return resp.asJson();
    }

    @Step("Получить активные сессии")
    public ResRuptureSessionDataList getActiveSessions() {
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions("active", 10);
        assertThat(resp, successful());
        return resp.asJson();
    }

    @Step("Получить стандартные Активные сессии в которых есть продукты")
    public int getActiveSessionIdWithProducts() {
        List<ResRuptureSessionData> activeSessionsData = getActiveSessions().getItems();
        assertThat("There is no active sessions", activeSessionsData, hasSize(greaterThan(0)));
        for (ResRuptureSessionData each : activeSessionsData) {
            if (each.getTotalProductCount() > 0) {
                return each.getSessionId();
            }
        }
        Assert.fail("No one active session with products found");
        return 0;
    }

    @Step("Убедиться, что сессия завершена")
    public void checkSessionIsFinished(int sessionId) {
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions("finished", 100);
        assertThat(resp, successful());
        List<ResRuptureSessionData> finishedSessions = resp.asJson().getItems();
        for (ResRuptureSessionData session:finishedSessions) {
            if (session.getSessionId() == sessionId)
                return;
        }
        Assert.fail("Session " + sessionId + " is not finished");
    }

}
