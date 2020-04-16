package com.leroy.magmobile.api.requests.salesdoc.picking;

import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/pickingTasks/search")
public class PickingTasksSearchRequest extends CommonSearchRequestBuilder<PickingTasksSearchRequest> {

    @Override
    public PickingTasksSearchRequest setStartFrom(Integer val) {
        return queryParam("page", val);
    }

    public PickingTasksSearchRequest setTaskStatus(String val) {
        return queryParam("taskStatus", val);
    }

}
