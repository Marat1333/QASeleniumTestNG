package com.leroy.magportal.api.requests.picking;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/pickingTasks/get")
public class PickingTaskGetRequest extends
        CommonSearchRequestBuilder<PickingTaskGetRequest> {

    public PickingTaskGetRequest setTaskId(String val) {
        return queryParam("taskId", val);
    }
}