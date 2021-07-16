package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.TmsLink;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ExpressDeliveryEditTest extends BaseMagPortalApiTest {

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
        currentOrderType = OnlineOrderTypeConst.DELIVERY_EXPRESS;
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

    @Test(description = "C23425879 Express: Delivery and Lift Update", priority = 1)
    @TmsLink("1868")
    public void testEditDeliveryAndLift() {
        deliveryTotalPrice = 99.99;
        liftPrice = 55.55;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425880 Express: Delivery Update", priority = 2)
    @TmsLink("1869")
    public void testEditDelivery() {
        deliveryTotalPrice = 88.88;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425881 Express: Paid: Lift Delivery Update to zero", priority = 3)
    @TmsLink("1870")
    public void testEditDeliveryLiftToZeroPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        deliveryTotalPrice -= liftPrice;
        liftPrice = 0.0;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425882 Express: Paid: Delivery Update to zero", priority = 4)
    @TmsLink("1871")
    public void testEditDeliveryToZeroPaid() {
        deliveryTotalPrice = 0.0;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425883 Express: Delivery Update for Dimensional products", priority = 5)
    @TmsLink("1872")
    public void testEditDeliveryDimensional() {
        currentStatus = States.ALLOWED_FOR_PICKING;
        makeDimensionalOrder();
        deliveryTotalPrice = 99.99;
        liftPrice = 55.55;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425884 Express: Delivery Update to zero for Dimensional products", priority = 6)
    @TmsLink("1873")
    public void testEditDeliveryToZeroDimensional() {
        deliveryTotalPrice = 0.0;
        liftPrice = 0.0;
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