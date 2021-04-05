package com.leroy.magportal.api.requests.usertasks;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/user-tasks")
public class UserTasksPutRequest extends CommonLegoRequest<UserTasksPutRequest> {

    public UserTasksPutRequest setTaskId(String value) {
        return queryParam("taskId", value);
    }

}
