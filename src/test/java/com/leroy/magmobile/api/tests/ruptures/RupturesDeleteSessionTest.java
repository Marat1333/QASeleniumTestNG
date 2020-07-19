package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.ErrorTextConst;
import com.leroy.constants.api.StatusCodes;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.CommonErrorResponseData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class RupturesDeleteSessionTest extends BaseRuptureTest {

    @Override
    protected boolean isDeleteSessionAfterEveryMethod() {
        return false;
    }

    private void setUp(boolean finishSession) {
        RupturesClient rupturesClient = rupturesClient();

        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);

        if (finishSession) {
            Response<JsonNode> respFinish = rupturesClient.finishSession(sessionId);
            rupturesClient.assertThatIsUpdatedOrDeleted(respFinish);
        }
    }

    @Test(description = "C3285343 DELETE active rupture session")
    public void testDeleteActiveRuptureSession() {
        setUp(false);

        step("Удаляем сессию");
        RupturesClient rupturesClient = rupturesClient();
        Response<JsonNode> resp = rupturesClient.deleteSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);

        step("Ищем созданные сессии и проверяем, что 'нашей' там нет");
        Response<ResRuptureSessionDataList> getResp = rupturesClient.getSessions(50);
        isResponseOk(getResp);
        ResRuptureSessionDataList respBody = getResp.asJson();
        for (ResRuptureSessionData sessionData : respBody.getItems()) {
            assertThat("Session wasn't deleted", sessionData.getSessionId(), not(equalTo(sessionId)));
        }
    }

    @Test(description = "C3285341 DELETE a previously deleted rupture session")
    public void testDeletePreviouslyDeletedRuptureSession() {
        RupturesClient rupturesClient = rupturesClient();
        if (sessionId == null) {
            setUp(false);
            step("Удаляем сессию");
            Response<JsonNode> resp = rupturesClient.deleteSession(sessionId);
            rupturesClient.assertThatIsUpdatedOrDeleted(resp);
        }

        step("Пробуем удалить сессию повторно");
        Response<JsonNode> resp = rupturesClient.deleteSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
    }

    @Test(description = "C3233587 DELETE finished ruptures session")
    public void testDeleteFinishedRuptureSession() {
        setUp(true);

        step("Удаляем сессию");
        RupturesClient rupturesClient = rupturesClient();
        Response<JsonNode> resp = rupturesClient.deleteSession(sessionId);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);

        step("Ищем созданные сессии и проверяем, что 'нашей' там нет");
        Response<ResRuptureSessionDataList> getResp = rupturesClient.getSessions(50);
        isResponseOk(getResp);
        ResRuptureSessionDataList respBody = getResp.asJson();
        for (ResRuptureSessionData sessionData : respBody.getItems()) {
            assertThat("Session wasn't deleted", sessionData.getSessionId(), not(equalTo(sessionId)));
        }
    }

    @Test(description = "C3285342 DELETE a not existing session")
    public void testDeleteNotExistingSession() {
        RupturesClient rupturesClient = rupturesClient();

        step("Пробуем удалить несуществующую сессию");
        Response<JsonNode> resp = rupturesClient.deleteSession(Integer.MAX_VALUE);
        rupturesClient.assertThatIsUpdatedOrDeleted(resp);
    }

    @Test(description = "C23409769 DELETE rupture session mashup validation")
    public void testDeleteSessionMashupValidation() {
        RupturesClient rupturesClient = rupturesClient();

        Response<JsonNode> resp = rupturesClient.deleteSession(null);
        assertThat("Response code", resp.getStatusCode(), equalTo(StatusCodes.ST_400_BAD_REQ));
        CommonErrorResponseData errorResp = resp.asJson(CommonErrorResponseData.class);
        assertThat("error text", errorResp.getError(),
                equalTo(ErrorTextConst.WRONG_QUERY_DATA));
        assertThat("validation sessionId", errorResp.getValidation().getSessionId(),
                equalTo(ErrorTextConst.REQUIRED));

        sessionId = null;
    }

}
