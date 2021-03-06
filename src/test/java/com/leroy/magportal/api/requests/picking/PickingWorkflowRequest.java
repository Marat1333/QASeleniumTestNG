package com.leroy.magportal.api.requests.picking;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "PUT", path = "/v2/order/workflow")
public class PickingWorkflowRequest extends CommonLegoRequest<PickingWorkflowRequest> {

    public PickingWorkflowRequest setPickingTaskId(String val) {
        return queryParam("pickingTaskId", val);
    }
}
