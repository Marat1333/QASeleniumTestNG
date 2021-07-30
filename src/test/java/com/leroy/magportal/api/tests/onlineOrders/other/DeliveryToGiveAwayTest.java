package com.leroy.magportal.api.tests.onlineOrders.other;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.onlineOrders.OrderProductsToGivenAwayData;
import com.leroy.magportal.api.data.picking.PickingTaskData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.AllureId;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.customerorders.enums.PaymentStatus;

public class DeliveryToGiveAwayTest extends BaseMagPortalApiTest {

    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private PaymentHelper paymentHelper;
    @Inject
    private OrderClient orderClient;
    @Inject
    private PickingTaskClient pickingTaskClient;

    private String currentOrderId;
    private String currentTaskId;


    @BeforeClass
    private void setUp() {
        OnlineOrderTypeData currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_DOOR;
        currentOrderId = onlineOrderHelper.createOnlineOrderCardPayment(currentOrderType)
                .getSolutionId();

        currentTaskId = pickingTaskClient.searchForPickingTasks(currentOrderId).asJson().getItems()
                .stream().findFirst().orElse(new PickingTaskData()).getTaskId();
    }

    @Test(description = "C23748038 Delivery: GiveAway for Start Picking")
    @AllureId("16137")
    public void testStartPickingGiveAway() {
        Response<?> resp = pickingTaskClient.startPicking(currentTaskId);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.PICKING_IN_PROGRESS);
    }

    @SneakyThrows
    @Test(description = "C23748039 Delivery: GiveAway for Picked", dependsOnMethods = {"testStartPickingGiveAway"})
    @AllureId("16138")
    public void testPickedGiveAway() {
        Response<?> resp = pickingTaskClient.completePicking(currentTaskId, true);
        isResponseOk(resp);
        paymentHelper.makePaid(currentOrderId);
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.PICKED, PaymentStatus.PAID);
        Thread.sleep(15000);//Added for FF delay
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.PICKED);
    }

    @Test(description = "C23748040 Delivery: GiveAway for Partially Shipped", dependsOnMethods = {
            "testPickedGiveAway"})
    @AllureId("16139")
    public void testPartiallyShippedGiveAway() {
        Response<?> resp = orderClient.giveAway(currentOrderId, false);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.PARTIALLY_SHIPPED);
    }

    @Test(description = "C23748041 Delivery: GiveAway for Shipped", dependsOnMethods = {
            "testPickedGiveAway"})
    @AllureId("16140")
    public void testShippedGiveAway() {
        Response<?> resp = orderClient.giveAway(currentOrderId, true);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.SHIPPED);
    }

    @Test(description = "C23748042 Delivery: GiveAway for Delivered", dependsOnMethods = {
            "testShippedGiveAway"})
    @AllureId("16141")
    public void testDeliverGiveAway() {
        orderClient.waitUntilOrderGetStatus(currentOrderId,
                States.ON_DELIVERY, null);
        Response<?> resp = orderClient.deliver(currentOrderId, true);
        isResponseOk(resp);
        Response<OrderProductsToGivenAwayData> response = orderClient
                .productsToGivenAway(currentOrderId);
        orderClient.assertProductsToGivenAwayResult(response, States.DELIVERED);
    }
}