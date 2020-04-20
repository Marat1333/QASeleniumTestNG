package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.sales.picking.PickingTaskDataList;
import com.leroy.magmobile.api.data.sales.picking.PickingTaskFilter;
import com.leroy.magmobile.api.requests.salesdoc.picking.PickingTasksSearchRequest;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PickingTaskClient extends MagMobileClient {

    /**
     * ---------- Executable Requests -------------
     **/

    @Step("Search for tasks")
    public Response<PickingTaskDataList> searchForTasks(PickingTaskFilter filter, int page, int pageSize) {
        PickingTasksSearchRequest req = new PickingTasksSearchRequest();
        req.setPageSize(pageSize)
                .setStartFrom(page);
        return execute(req, PickingTaskDataList.class);
    }

    public Response<PickingTaskDataList> searchForTasks(PickingTaskFilter filter) {
        return searchForTasks(filter, 1, 10);
    }

    /**
     * ------------  Verifications -----------------
     **/
}
