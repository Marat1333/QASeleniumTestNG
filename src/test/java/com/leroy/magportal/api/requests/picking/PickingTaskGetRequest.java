package com.leroy.magportal.api.requests.picking;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v2/pickingTasks/{taskId}")
public class PickingTaskGetRequest extends CommonLegoRequest<PickingTaskGetRequest> {

    public PickingTaskGetRequest setTaskId(String val) {
        return pathParam("taskId", val);
    }
}