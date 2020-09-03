package com.leroy.magportal.api.clients;

import com.leroy.core.api.BaseMashupClient;
import com.leroy.magportal.api.data.picking.PickingTaskDataList;
import com.leroy.magportal.api.requests.picking.PickingTasksSearchRequest;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PickingTaskClient extends BaseMashupClient {

    @Step("Search for picking tasks")
    public Response<PickingTaskDataList> searchForPickingTasks(String orderId) {
        PickingTasksSearchRequest req = new PickingTasksSearchRequest();
        req.setOrderId(orderId);
        req.setPageNumber(1);
        req.setPageSize(5);
        return execute(req, PickingTaskDataList.class);
    }

}
