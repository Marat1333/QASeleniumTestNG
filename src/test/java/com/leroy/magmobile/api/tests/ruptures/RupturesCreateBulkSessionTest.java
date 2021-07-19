package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureBulkSessionData;
import io.qameta.allure.TmsLink;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class RupturesCreateBulkSessionTest extends BaseRuptureTest{

    @Test(description = "C23718164 Create bulk session")
    @TmsLink("3363")
    public void testCreateBulkSession() {
        String lmCode = RandomStringUtils.randomNumeric(8);

        ReqRuptureBulkSessionData postData = new ReqRuptureBulkSessionData();
        postData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));
        postData.setLmCode(lmCode);

        step("Создаем сессию");
        Response<JsonNode> resp = rupturesClient.createBulkSession(postData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

        step("GET /ruptures/sessions - Проверяем что созданная сессия есть в списке");
        rupturesClient.assertThatCreatedSessionPresentsInSessionsList(sessionId, "Bulk");

        step("GET /ruptures/session/{sessionId}/products - Проверяем, что товар есть в списке");
        rupturesClient.assertThatProductPresentsInSessionList(sessionId, lmCode);
    }

}
