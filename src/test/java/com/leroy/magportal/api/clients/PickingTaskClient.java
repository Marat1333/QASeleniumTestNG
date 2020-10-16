package com.leroy.magportal.api.clients;

import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magportal.api.constants.PickingReasonEnum;
import com.leroy.magportal.api.constants.PickingTaskWorkflowEnum;
import com.leroy.magportal.api.data.onlineOrders.OrderProductDataPayload;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.data.picking.PickingTaskData.ProductData;
import com.leroy.magportal.api.data.picking.PickingTaskDataList;
import com.leroy.magportal.api.data.picking.PickingTaskStoragePayload;
import com.leroy.magportal.api.data.picking.PickingTaskStoragePayload.StoragePayload;
import com.leroy.magportal.api.data.picking.PickingTaskWorkflowPayload;
import com.leroy.magportal.api.data.picking.PickingTaskWorkflowPayload.WorkflowPayload;
import com.leroy.magportal.api.data.picking.StorageLocationData;
import com.leroy.magportal.api.data.picking.StorageLocationData.ZoneLocation;
import com.leroy.magportal.api.requests.picking.PickingLocationGetRequest;
import com.leroy.magportal.api.requests.picking.PickingTaskGetRequest;
import com.leroy.magportal.api.requests.picking.PickingTasksSearchRequest;
import com.leroy.magportal.api.requests.picking.PickingWorkflowRequest;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PickingTaskClient extends BaseMashupClient {

    private String secondUrl;

    @Override
    protected void init() {
        gatewayUrl = EnvConstants.MAIN_API_HOST;
        secondUrl = EnvConstants.PICK_API_HOST;
    }

    @Step("Search for picking tasks")
    public Response<PickingTaskDataList> searchForPickingTasks(String orderId) {
        PickingTasksSearchRequest req = new PickingTasksSearchRequest();
        req.setOrderId(orderId);
        req.setPageNumber(1);
        req.setPageSize(5);
        return execute(req, PickingTaskDataList.class, secondUrl);
    }

    @Step("Get picking task")
    public Response<PickingTaskData> getPickingTask(String taskId) {
        PickingTaskGetRequest req = new PickingTaskGetRequest();
        req.setTaskId(taskId);
        return execute(req, PickingTaskData.class, secondUrl);
    }

    @Step("Start Picking of task for OrderId= {OrderId}")
    public Response<PickingTaskData> startPicking(String orderId, Integer taskNumber) {
        String taskId = this.searchForPickingTasks(orderId).asJson().getItems().get(taskNumber)
                .getTaskId();
        return makeAction(taskId, PickingTaskWorkflowEnum.START.getValue(),
                new PickingTaskWorkflowPayload());
    }

    @Step("Start Pickings of all available tasks for Order = {orderId}")
    public void startAllPickings(String orderId) {
        Response<?> resp;
        List<PickingTaskData> tasks = this.searchForPickingTasks(orderId).asJson().getItems()
                .stream().filter(x -> x.getTaskStatus().equalsIgnoreCase(States.ALLOWED_FOR_PICKING
                        .getApiVal())).collect(Collectors.toList());
        for (PickingTaskData task : tasks) {
            resp = startPicking(task.getTaskId());
            assertThatResponseIsOk(resp);
        }
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

    @Step("Complete Picking of all available tasks for {orderId}")
    public void completeAllPickings(String orderId, Boolean isFull) {
        Response<?> resp;
        List<PickingTaskData> tasks = this.searchForPickingTasks(orderId).asJson().getItems()
                .stream().filter(x -> x.getTaskStatus().equalsIgnoreCase(States.PICKING_IN_PROGRESS
                        .getApiVal())).collect(Collectors.toList());
        for (PickingTaskData task : tasks) {
            resp = completePicking(task.getTaskId(), isFull);
            assertThatResponseIsOk(resp);
        }
    }

    @Step("Complete Picking of task = {taskId}")
    public Response<PickingTaskData> completePicking(String taskId, Boolean isFull) {
        return makeAction(taskId, PickingTaskWorkflowEnum.COMPLETE.getValue(),
                makeWorkflowPayload(taskId, isFull));
    }

    @Step("Get Pickings locations' for shop ={shopId}")
    public Response<StorageLocationData> getStorageLocation(String shopId) {
        return execute(new PickingLocationGetRequest().setShopId(shopId),
                StorageLocationData.class, secondUrl);
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
                .setLdapHeader(getUserSessionData().getUserLdap())
                .jsonBody(payload), PickingTaskData.class);
    }

    private PickingTaskWorkflowPayload makeWorkflowPayload(String taskId, Boolean isFull) {
        double count = 1;
        String reason = PickingReasonEnum.BROKEN.getValue();

        PickingTaskWorkflowPayload payload = new PickingTaskWorkflowPayload();
        WorkflowPayload workflowPayload = new WorkflowPayload();
        List<OrderProductDataPayload> products = new ArrayList<>();
        PickingTaskData pickingTaskData = this.getPickingTask(taskId).asJson();
        for (ProductData productData : pickingTaskData.getProductData()) {
            if (isFull) {
                count = productData.getConfirmedQuantity();
                reason = "";
            }
            OrderProductDataPayload productDataPayload = new OrderProductDataPayload();
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

    private PickingTaskStoragePayload makeStoragePayload(Integer count) {
        PickingTaskStoragePayload payload = new PickingTaskStoragePayload();
        StoragePayload storagePayload = new StoragePayload();
        try {
            Response<StorageLocationData> resp = this
                    .getStorageLocation(getUserSessionData().getUserShopId());
            List<ZoneLocation> zones = resp.asJson().getZones();
            List<String> locations = zones.stream().filter(y -> (y.getZoneCells().size() > count))
                    .findFirst().get().getZoneCells();
            storagePayload.setLocations(locations.subList(1, count));
        } catch (Exception ignoreThis) {
            storagePayload.setLocations(this.makeFakeLocations(count));
        }

        payload.setStoragePayload(storagePayload);
        return payload;
    }

    private List<String> makeFakeLocations(Integer count) {
        List<String> locations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            locations.add("V000" + i + ":Выдача Товара");
        }
        return locations;
    }

}
