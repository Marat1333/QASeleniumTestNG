package com.leroy.magmobile.api.requests.notification;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "POST", path = "/sms/notification")
public class SmsNotificationCreateRequest extends RequestBuilder<SmsNotificationCreateRequest> {

    public SmsNotificationCreateRequest setLdap(String val) {
        return header("ldap", val);
    }

}
