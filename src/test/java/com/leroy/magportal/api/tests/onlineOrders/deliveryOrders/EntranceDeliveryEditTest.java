package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.AllureId;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;
import ru.leroymerlin.qa.core.clients.base.Response;

public class EntranceDeliveryEditTest extends BaseMagPortalApiTest {

    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private OrderClient orderClient;

    private String currentOrderId;
    private OnlineOrderTypeData currentOrderType;
    private States currentStatus;
    private Double deliveryTotalPrice;
    private Double liftPrice;

    private boolean isDimensional;


    @BeforeClass
    private void setUp() {
        currentStatus = States.ALLOWED_FOR_PICKING;
        currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_ENTRANCE;
        makeNewOrder();
    }

    @BeforeMethod
    private void prepareTest() {
        if (currentOrderId == null) {
            if (isDimensional) {
                makeDimensionalOrder();
            } else {
                makeNewOrder();
            }

            orderClient.moveNewOrderToStatus(currentOrderId, currentStatus);
        }

    }

    @AfterMethod
    private void orderAfterMethod(ITestResult result) {
        if (result.getStatus() != TestResult.SUCCESS) {
            currentOrderId = null;
        }
    }

    @Test(description = "C23425873 Entrance: Delivery and Lift Update", priority = 1)
    @AllureId("1858")
    public void testEditDeliveryAndLift() {
        deliveryTotalPrice = 99.99;
        liftPrice = 0.0;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425874 Entrance: Delivery Update", priority = 2)
    @AllureId("1859")
    public void testEditDelivery() {
        deliveryTotalPrice = 88.88;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425876 Entrance: Paid: Delivery Update to zero", priority = 4)
    @AllureId("1860")
    public void testEditDeliveryToZeroPaid() {
        deliveryTotalPrice = 0.0;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425877 Entrance: Delivery Update for Dimensional products", priority = 5)
    @AllureId("1861")
    public void testEditDeliveryDimensional() {
        currentStatus = States.ALLOWED_FOR_PICKING;
        makeDimensionalOrder();
        deliveryTotalPrice = 99.99;
        liftPrice = 0.0;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425878 Entrance: Delivery Update to zero for Dimensional products", priority = 6)
    @AllureId("1862")
    public void testEditDeliveryToZeroDimensional() {
        deliveryTotalPrice = 0.0;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    private void makeDimensionalOrder() {
        isDimensional = true;

        currentOrderId = onlineOrderHelper.createDimensionalOnlineOrder(currentOrderType)
                .getSolutionId();
    }

    private void makeNewOrder() {
        isDimensional = false;

        currentOrderId = onlineOrderHelper.createOnlineOrderCardPayment(currentOrderType)
                .getSolutionId();
    }
}