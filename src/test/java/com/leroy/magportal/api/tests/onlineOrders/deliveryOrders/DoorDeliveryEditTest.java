package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;
import ru.leroymerlin.qa.core.clients.base.Response;

public class DoorDeliveryEditTest extends BaseMagPortalApiTest {

    @Inject
    private BitrixHelper bitrixHelper;
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
        currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_DOOR;
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

    @Test(description = "C23425867 Door: Delivery and Lift Update", priority = 1)
    public void testEditDeliveryAndLift() {
        deliveryTotalPrice = 99.99;
        liftPrice = 55.55;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425868 Door: Delivery Update", priority = 2)
    public void testEditDelivery() {
        deliveryTotalPrice = 88.88;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425869 Door: Paid: Lift Delivery Update to zero", priority = 3)
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

    @Test(description = "C23425870 Door: Paid: Delivery Update to zero", priority = 4)
    public void testEditDeliveryToZeroPaid() {
        deliveryTotalPrice = 0.0;
        Response<?> response = orderClient
                .editDeliveryOrder(currentOrderId, deliveryTotalPrice, liftPrice);
        orderClient.assertDeliveryUpdateResult(response, currentOrderId, deliveryTotalPrice,
                liftPrice);
    }

    @Test(description = "C23425871 Door: Delivery Update for Dimensional products", priority = 5)
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

    @Test(description = "C23425872 Door: Delivery Update to zero for Dimensional products", priority = 6)
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

        currentOrderId = bitrixHelper.createDimensionalOnlineOrder(currentOrderType)
                .getSolutionId();
    }

    private void makeNewOrder() {
        isDimensional = false;

        currentOrderId = bitrixHelper.createOnlineOrderCardPayment(currentOrderType).getSolutionId();
    }
}