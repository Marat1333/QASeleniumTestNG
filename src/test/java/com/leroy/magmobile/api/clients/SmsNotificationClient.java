package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.catalog.SmsNotificationData;
import com.leroy.magmobile.api.requests.notification.SmsNotificationCreateRequest;
import com.leroy.magmobile.api.requests.notification.SmsNotificationGetRequest;
import ru.leroymerlin.qa.core.clients.base.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SmsNotificationClient extends MagMobileClient {

    /**
     * ---------- Requests -------------
     **/


    public Response<SmsNotificationData> createNotification(SmsNotificationData data) {
        SmsNotificationCreateRequest req = new SmsNotificationCreateRequest();
        req.setLdap(sessionData.getUserLdap());
        req.jsonBody(data);
        return execute(req, SmsNotificationData.class);
    }

    public Response<SmsNotificationData> getNotification(String lmCode) {
        SmsNotificationGetRequest req = new SmsNotificationGetRequest();
        req.setShopId(sessionData.getUserShopId());
        req.setLmCode(lmCode);
        return execute(req, SmsNotificationData.class);
    }

    // ---------- Verifications -------------------- //

    public void assertThatIsCreated(Response<SmsNotificationData> resp, SmsNotificationData expectedData) {
        assertThatResponseIsOk(resp);
        assertThat("Response with sms notification data",
                resp.asJson(), is(expectedData));
    }

    public void assertThatGetResponseMatches(Response<SmsNotificationData> resp,
                                             SmsNotificationData expectedData) {
        assertThatResponseIsOk(resp);
        // TODO
    }

}
