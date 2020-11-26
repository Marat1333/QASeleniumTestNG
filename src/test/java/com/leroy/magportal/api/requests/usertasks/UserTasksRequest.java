package com.leroy.magportal.api.requests.usertasks;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/user-tasks")
public class UserTasksRequest extends CommonLegoRequest<UserTasksRequest> {

    public UserTasksRequest setProjectId(String value) {
        return queryParam("projectId", value);
    }

    public UserTasksRequest setOrderId(String value) {
        return queryParam("orderId", value);
    }

}
