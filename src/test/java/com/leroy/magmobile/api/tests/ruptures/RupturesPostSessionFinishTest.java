package com.leroy.magmobile.api.tests.ruptures;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.ErrorTextConst;
import com.leroy.constants.api.StatusCodes;
import com.leroy.magmobile.api.data.CommonErrorResponseData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import io.qameta.allure.Step;
import java.util.List;
import java.util.stream.Collectors;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class RupturesPostSessionFinishTest extends BaseRuptureTest {

    // Test constants
    private static final String ACTIVE_STATUS = "active";
    private static final String FINISHED_STATUS = "finished";

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
    public void testFinishRuptureSession() {
        setUp();
        step("Завершаем сессию");
        Response<JsonNode> resp = rupturesClient.finishSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);

        step("Ищем созданные сессии и проверяем, что нужная нам в статсе 'finished'");
        Response<ResRuptureSessionDataList> getResp = rupturesClient.getSessions(FINISHED_STATUS, 50);
        isResponseOk(getResp);
        ResRuptureSessionDataList respBody = getResp.asJson();
        List<ResRuptureSessionData> items = respBody.getItems().stream().filter(
                a -> a.getSessionId().equals(sessionId)).collect(Collectors.toList());
        assertThat("Session " + sessionId + " wasn't found", items, hasSize(1));
        assertThat("Session " + sessionId + " should be finished", items.get(0).getStatus(), equalTo(FINISHED_STATUS));
    }

    @Test(description = "C3285352 PUT ruptures session finish for finished session")
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
    public void testPutRupturesSessionFinishForNotExistingSession() {
        int notExistingSession = Integer.MAX_VALUE;

        step("Пытаемся завершить несуществующую сессию");
        Response<JsonNode> resp = rupturesClient.finishSession(notExistingSession);
        rupturesClient.assertThatSessionNotFound(resp, notExistingSession);
    }

    @Test(description = "C23409768 PUT ruptures session finish mashup validation")
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
