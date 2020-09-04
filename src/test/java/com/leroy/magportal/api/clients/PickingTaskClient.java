package com.leroy.magportal.api.clients;

import com.leroy.core.api.BaseMashupClient;
import com.leroy.magportal.api.constants.PickingReasonEnum;
import com.leroy.magportal.api.constants.PickingTaskWorkflowEnum;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.data.picking.PickingTaskData.ProductData;
import com.leroy.magportal.api.data.picking.PickingTaskDataList;
import com.leroy.magportal.api.data.picking.PickingTaskWorkflowPayload;
import com.leroy.magportal.api.data.picking.PickingTaskWorkflowPayload.ProductDataPayload;
import com.leroy.magportal.api.data.picking.PickingTaskWorkflowPayload.StoragePayload;
import com.leroy.magportal.api.data.picking.PickingTaskWorkflowPayload.WorkflowPayload;
import com.leroy.magportal.api.requests.picking.PickingTaskGetRequest;
import com.leroy.magportal.api.requests.picking.PickingTasksSearchRequest;
import com.leroy.magportal.api.requests.picking.PickingWorkflowRequest;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
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

    @Step("Get picking task")
    public Response<PickingTaskData> getPickingTask(String taskId) {
        PickingTaskGetRequest req = new PickingTaskGetRequest();
        req.setTaskId(taskId);
        return execute(req, PickingTaskData.class);
    }

    @Step("Start Picking of task = {taskId}")
    public Response<PickingTaskData> startPicking(String taskId) {
        return makeAction(taskId, PickingTaskWorkflowEnum.START.getValue(),
                new PickingTaskWorkflowPayload());
    }

    @Step("Pause Picking of task = {taskId}")
    public Response<PickingTaskData> pausePicking(String taskId) {
        return makeAction(taskId, PickingTaskWorkflowEnum.PAUSE.getValue(),
                new PickingTaskWorkflowPayload());
    }

    @Step("Resume Picking of task = {taskId}")
    public Response<PickingTaskData> resumePicking(String taskId) {
        return makeAction(taskId, PickingTaskWorkflowEnum.RESUME.getValue(),
                new PickingTaskWorkflowPayload());
    }

    @Step("Complete Picking of task = {taskId}")
    public Response<PickingTaskData> completePicking(String taskId,
            PickingTaskWorkflowPayload workflowPayload) {
        return makeAction(taskId, PickingTaskWorkflowEnum.COMPLETE.getValue(), workflowPayload);
    }

    @Step("Complete Picking of task = {taskId}")
    public Response<PickingTaskData> completePicking(String taskId, Boolean isFull) {
        return makeAction(taskId, PickingTaskWorkflowEnum.COMPLETE.getValue(),
                makeWorkflowPayload(taskId, isFull));
    }

    @Step("Complete Picking of task = {taskId}")
    public Response<PickingTaskData> locatePicking(String taskId,
            Integer locationsCount) {
        return makeAction(taskId, PickingTaskWorkflowEnum.LOCATE.getValue(),
                makeStoragePayload(locationsCount));
    }

    private Response<PickingTaskData> makeAction(String taskId, String action,
            PickingTaskWorkflowPayload payload) {
        payload.setAction(action);
        return execute(new PickingWorkflowRequest()
                .setPickingTaskId(taskId)
                .setUserLdap(userSessionData.getUserLdap())
                .jsonBody(payload), PickingTaskData.class);
    }

    private PickingTaskWorkflowPayload makeWorkflowPayload(String taskId, Boolean isFull) {
        double count = 1;
        String reason = PickingReasonEnum.BROKEN.getValue();

        PickingTaskWorkflowPayload payload = new PickingTaskWorkflowPayload();
        WorkflowPayload workflowPayload = new WorkflowPayload();
        List<ProductDataPayload> products = new ArrayList<>();
        PickingTaskData pickingTaskData = this.getPickingTask(taskId).asJson();
        for (ProductData productData : pickingTaskData.getProductData()) {
            if (isFull) {
                count = productData.getConfirmedQuantity();
                reason = "";
            }
            ProductDataPayload productDataPayload = new ProductDataPayload();
            productDataPayload.setLineId(productData.getLineId());
            productDataPayload.setLmCode(productData.getLmCode());
            productDataPayload.setQuantity(count);
            productDataPayload.setReason(reason);
            products.add(productDataPayload);
        }
        workflowPayload.setProducts(products);
        payload.setWorkflowPayload(workflowPayload);
        return payload;
    }

    private PickingTaskWorkflowPayload makeStoragePayload(Integer count) {
        PickingTaskWorkflowPayload payload = new PickingTaskWorkflowPayload();
        StoragePayload storagePayload = new StoragePayload();
        List<String> locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            locations.add("V000" + i + ":Выдача Товара");
        }
        storagePayload.setLocations(locations);
        payload.setStoragePayload(storagePayload);
        return payload;
    }

}
