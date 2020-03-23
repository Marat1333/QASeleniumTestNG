package com.leroy.magmobile.api.requests.notification;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/sms/notifications")
public class SmsNotificationGetRequest extends RequestBuilder<SmsNotificationGetRequest> {

    public SmsNotificationGetRequest setShopId(String val) {
        return queryParam("shopId", val);
    }

    public SmsNotificationGetRequest setLmCode(String val) {
        return queryParam("lmCode", val);
    }

}
