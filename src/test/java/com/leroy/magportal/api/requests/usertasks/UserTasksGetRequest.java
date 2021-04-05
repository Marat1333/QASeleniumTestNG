package com.leroy.magportal.api.requests.usertasks;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/user-tasks")
public class UserTasksGetRequest extends CommonLegoRequest<UserTasksGetRequest> {

    public UserTasksGetRequest setProjectId(String value) {
        return queryParam("projectId", value);
    }

    public UserTasksGetRequest setOrderId(String value) {
        return queryParam("orderId", value);
    }

}
