package com.leroy.magportal.api.tests.picking;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

import com.google.inject.Inject;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.data.picking.OrdersPickingTasksDataList;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import io.qameta.allure.AllureId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class GetPickingTasksTest extends BaseMagPortalApiTest {

    @Inject
    private PickingTaskClient pickingTaskClient;
    @Inject
    private PAOHelper paoHelper;

    private String currentOrderId;
    private String currentTaskId;

    @BeforeClass
    private void setUp() {
        OrderData orderData = paoHelper.createConfirmedPickupOrder(paoHelper.makeCartProducts(3), true);
        currentOrderId = orderData.getOrderId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().get().getTaskId();
    }

    @Test(description = "C23438532 Get Picking Task")
    @AllureId("16176")
    public void testGetPickingTask() {
        Response<PickingTaskData> response = pickingTaskClient.getPickingTask(currentTaskId);
        assertGetPickingTaskResult(response);
    }

    @Test(description = "C23438533 Get Orders' Picking Tasks")
    @AllureId("16177")
    public void testGetOrdersPickingTask() {
        Response<OrdersPickingTasksDataList> response = pickingTaskClient
                .getPickingTasks(currentOrderId);
        assertGetPickingTasksResult(response);
    }

    //Verification
    @Step("Get Picking Tasks results verification")
    public void assertGetPickingTasksResult(Response<OrdersPickingTasksDataList> response) {
        assertThat("Request to Get Picking Tasks has Failed.", response, successful());
        OrdersPickingTasksDataList tasksData = response.asJson();
        softAssert().isEquals(tasksData.getOrderId(), currentOrderId,
                "OrderId is NOT provided or Invalid");
        assertThat("There are NO Picking Tasks available", tasksData.getPickingTasks().size(),
                greaterThanOrEqualTo(1));
        for (PickingTaskData pickingTaskData : tasksData.getPickingTasks()) {
            softAssertShortPickingTaskData(pickingTaskData);
        }
        softAssert().verifyAll();
    }

    @Step("Get Picking Task results verification")
    public void assertGetPickingTaskResult(Response<PickingTaskData> response) {
        assertThat("Request to Get Picking Tasks has Failed.", response, successful());
        PickingTaskData pickingTaskData = response.asJson();
        softAssertShortPickingTaskData(pickingTaskData);
        softAssert().isEquals(pickingTaskData.getOrderId(), currentOrderId,
                "OrderId is NOT provided or Invalid");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getChannel()),
                "Task Channel is NOT provided");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getPaymentStatus()),
                "Task Payment Status is NOT provided");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getPaymentType()),
                "Task Payment Type is NOT provided");
        softAssert().verifyAll();
    }

    private void softAssertShortPickingTaskData(PickingTaskData pickingTaskData) {
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getTaskId()),
                "TaskId is NOT provided");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getTaskStatus()),
                "Task Status is NOT provided");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getPickingZone()),
                "Task Picking Zone is NOT provided");
        softAssert().isTrue(pickingTaskData.getProductData().size() > 0,
                "There are NO Products available");
    }
}