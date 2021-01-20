package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureBulkSessionData;
import io.qameta.allure.Issue;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class RupturesAddProductToBulkSessionTest extends BaseRuptureTest{

    static final String firstLmCode = RandomStringUtils.randomNumeric(8);
    static final String secondLmCode = RandomStringUtils.randomNumeric(8);


    @BeforeMethod(description = "Create bulk session with random product")
    public void createBulkSession() {
        step("Создаем массовую сессию");
        ReqRuptureBulkSessionData postData = new ReqRuptureBulkSessionData();
        postData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));
        postData.setLmCode(firstLmCode);

        Response<JsonNode> resp = rupturesClient.createBulkSession(postData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
    }

    @Test(description = "C23718165 Add product to session")
    public void testAddProductToSession() {
        ReqRuptureBulkSessionData postData = new ReqRuptureBulkSessionData();
        postData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));
        postData.setLmCode(secondLmCode);

        step("Добавляем товар в массовую сессию");
        Response<JsonNode> resp = rupturesClient.addProductToBulkSession(postData, sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);

        step("GET /ruptures/session/{sessionId}/products - Проверяем, что в сессии только те товары, которые добавили");
        rupturesClient.assertThatSessionContainsProducts(sessionId, firstLmCode, secondLmCode);
    }

    @Issue("RUP-377")
    @Test(description = "C23718166 Add product to finished session")
    public void testAddProductToFinishedSession() {
        ReqRuptureBulkSessionData postData = new ReqRuptureBulkSessionData();
        postData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));
        postData.setLmCode(secondLmCode);

        step("Завершаем сессию");
        Response<JsonNode> finishSessionResp = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(finishSessionResp);

        step("Добавляем товар в завершенную массовую сессию");
        Response<JsonNode> resp = rupturesClient.addProductToBulkSession(postData, sessionId);
        rupturesClient.assertThatSessionNotFoundOrFinished(resp, sessionId);

        step("Отправляем GET запрос и проверяем, что данные действительно не изменились");
        rupturesClient.assertThatSessionContainsProducts(sessionId, firstLmCode);
    }

    @Issue("RUP-377")
    @Test(description = "C23718167 Add product to not existed session")
    public void testAddProductToNotExistedSession() {
        ReqRuptureBulkSessionData postData = new ReqRuptureBulkSessionData();
        postData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));
        postData.setLmCode(secondLmCode);

        int notExistedSession = Integer.MAX_VALUE;

        step("Добавляем товар в несуществующую массовую сессию");
        Response<JsonNode> resp = rupturesClient.addProductToBulkSession(postData, notExistedSession);
        rupturesClient.assertThatSessionNotFoundOrFinished(resp, sessionId);
    }

    @Test(description = "C23718168 Add duplicate product")
    public void testAddDuplicateProductToSession() {
        ReqRuptureBulkSessionData postData = new ReqRuptureBulkSessionData();
        postData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));
        postData.setLmCode(firstLmCode);

        step("Добавляем дубль товара в массовую сессию");
        Response<JsonNode> resp = rupturesClient.addProductToBulkSession(postData, sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);

        step("GET /ruptures/session/{sessionId}/products - Проверяем, что в сессии числится только 1 товар");
        rupturesClient.assertThatSessionContainsProducts(sessionId, firstLmCode);
    }
}
