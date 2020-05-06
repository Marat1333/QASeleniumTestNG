package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.notification.SmsNotificationData;
import com.leroy.magmobile.api.requests.notification.SmsNotificationCreateRequest;
import com.leroy.magmobile.api.requests.notification.SmsNotificationGetRequest;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SmsNotificationClient extends MagMobileClient {

    /**
     * ---------- Requests -------------
     **/

    @Step("Create Notification")
    public Response<SmsNotificationData> createNotification(SmsNotificationData data) {
        SmsNotificationCreateRequest req = new SmsNotificationCreateRequest();
        req.setLdap(sessionData.getUserLdap());
        req.jsonBody(data);
        return execute(req, SmsNotificationData.class);
    }

    @Step("Get notification with lmCode={lmCode}")
    public Response<SmsNotificationData> getNotification(String lmCode) {
        SmsNotificationGetRequest req = new SmsNotificationGetRequest();
        req.setShopId(sessionData.getUserShopId());
        req.setLmCode(lmCode);
        return execute(req, SmsNotificationData.class);
    }

    // ---------- Verifications -------------------- //

    @Step("Check that notification is created")
    public void assertThatIsCreated(Response<SmsNotificationData> resp, SmsNotificationData expectedData) {
        assertThatResponseIsOk(resp);
        assertThat("Response with sms notification data",
                resp.asJson(), is(expectedData));
    }

    @Step("Check that GET response is OK")
    public void assertThatGetResponseMatches(Response<SmsNotificationData> resp,
                                             SmsNotificationData expectedData) {
        assertThatResponseIsOk(resp);
        // to do
    }

}
