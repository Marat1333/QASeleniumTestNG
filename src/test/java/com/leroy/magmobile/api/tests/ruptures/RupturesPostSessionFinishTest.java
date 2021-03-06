package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.ErrorTextConst;
import com.leroy.constants.api.StatusCodes;
import com.leroy.magmobile.api.data.CommonErrorResponseData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import io.qameta.allure.Step;
import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;
import java.util.stream.Collectors;

import static com.leroy.magmobile.api.enums.RupturesSessionStatuses.FINISHED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class RupturesPostSessionFinishTest extends BaseRuptureTest {

    @Override
    protected boolean isDeleteSessionAfterEveryMethod() {
        return false;
    }

    @Step("Pre-condition: Создаем сессию")
    private void setUp() {
        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

    }

    @Test(description = "C3233585 PUT ruptures session finish")
    @AllureId("13210")
    public void testFinishRuptureSession() {
        setUp();
        step("Завершаем сессию");
        Response<JsonNode> resp = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);

        step("Ищем созданные сессии и проверяем, что нужная нам в статсе 'finished'");
        Response<ResRuptureSessionDataList> getResp = rupturesClient.getSessions(FINISHED, 50);
        isResponseOk(getResp);
        ResRuptureSessionDataList respBody = getResp.asJson();
        List<ResRuptureSessionData> items = respBody.getItems().stream().filter(
                a -> a.getSessionId().equals(sessionId)).collect(Collectors.toList());
        assertThat("Session " + sessionId + " wasn't found", items, hasSize(1));
        assertThat("Session " + sessionId + " should be finished", items.get(0).getStatus(),
                equalTo(FINISHED.getName()));
    }

    @Test(description = "C3285352 PUT ruptures session finish for finished session")
    @AllureId("13211")
    public void testFinishFinishedRuptureSession() {
        if (sessionId == null) {
            setUp();
            step("Завершаем сессию");
            Response<JsonNode> resp = rupturesClient.finishSession(sessionId);
            rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        }

        step("Завершаем сессию повторно");
        Response<JsonNode> resp = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
    }

    @Test(description = "C3285353 PUT ruptures session finish for deleted session")
    @AllureId("13212")
    public void testFinishDeletedRuptureSession() {
        if (sessionId == null) {
            setUp();
        }
        step("Удаляем сессию");
        Response<JsonNode> respDelete = rupturesClient.deleteSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(respDelete);

        step("Пытаемся завершить удаленную сессию");
        Response<JsonNode> resp = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatSessionNotFound(resp, sessionId);
    }

    @Test(description = "C3285354 PUT ruptures session finish for a not existing session")
    @AllureId("13213")
    public void testPutRupturesSessionFinishForNotExistingSession() {
        int notExistingSession = Integer.MAX_VALUE;

        step("Пытаемся завершить несуществующую сессию");
        Response<JsonNode> resp = rupturesClient.finishSession(notExistingSession);
        rupturesClient.assertThatSessionNotFound(resp, notExistingSession);
    }

    @Test(description = "C23409768 PUT ruptures session finish mashup validation")
    @AllureId("13214")
    public void testPutRupturesSessionFinishMashupValidation() {
        Response<JsonNode> resp = rupturesClient.finishSession("");
        assertThat("Response code", resp.getStatusCode(), equalTo(StatusCodes.ST_400_BAD_REQ));
        CommonErrorResponseData errorResp = resp.asJson(CommonErrorResponseData.class);
        assertThat("error text", errorResp.getError(),
                equalTo(ErrorTextConst.WRONG_PATH));
        assertThat("validation sessionId", errorResp.getValidation().getSessionId(),
                equalTo(ErrorTextConst.REQUIRED));
    }

}
