package com.leroy.magmobile.api.clients;

import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.data.notification.SmsNotificationData;
import com.leroy.magmobile.api.requests.notification.SmsNotificationCreateRequest;
import com.leroy.magmobile.api.requests.notification.SmsNotificationGetRequest;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SmsNotificationClient extends BaseMagMobileClient {

    /**
     * ---------- Requests -------------
     **/

    @Step("Create Notification")
    public Response<JsonNode> createNotification(SmsNotificationData data) {
        SmsNotificationCreateRequest req = new SmsNotificationCreateRequest();
        req.setLdap(getUserSessionData().getUserLdap());
        req.jsonBody(data);
        return execute(req, JsonNode.class);
    }

    @Step("Get notification with lmCode={lmCode}")
    public Response<SmsNotificationData> getNotification(String lmCode) {
        SmsNotificationGetRequest req = new SmsNotificationGetRequest();
        req.setShopId(getUserSessionData().getUserShopId());
        req.setLmCode(lmCode);
        return execute(req, SmsNotificationData.class);
    }

    // ---------- Verifications -------------------- //

    @Step("Check that notification is created")
    public void assertThatIsCreated(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        assertThat("success", resp.asJson().get("success").booleanValue());
    }

    @Step("Check that GET response is OK")
    public void assertThatGetResponseMatches(Response<SmsNotificationData> resp,
                                             SmsNotificationData expectedData) {
        assertThatResponseIsOk(resp);
        // to do
    }

}
