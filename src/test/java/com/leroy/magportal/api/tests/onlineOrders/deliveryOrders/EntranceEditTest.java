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

public class EntranceEditTest extends BaseMagPortalApiTest {

    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private OrderClient orderClient;

    private String currentOrderId;
    private OnlineOrderTypeData currentOrderType;
    private States currentStatus;
    private Double currentCount;
    private Boolean isDimensional;


    @BeforeClass
    private void setUp() {
        currentCount = 10.0;
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

            orderClient.editOrder(currentOrderId, 0, currentCount);
            orderClient.moveNewOrderToStatus(currentOrderId, currentStatus);
        }

    }

    @AfterMethod
    private void orderAfterMethod(ITestResult result) {
        if (result.getStatus() != TestResult.SUCCESS) {
            currentOrderId = null;
        }
    }

    @Test(description = "C23425639 Entrance: Edit Allowed For Picking Order", priority = 1)
    @AllureId("16043")
    public void testEditAllowedForPicking() {
        currentCount = 9.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425642 Entrance: Edit Picked Paid Order", priority = 2)
    @AllureId("16044")
    public void testEditPickedPaid() {
        currentCount = 7.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425645 Entrance: Edit Dimensional Product Allowed For Picking Order", priority = 3)
    @AllureId("16045")
    public void testEditDimensionalProduct() {
        makeDimensionalOrder();
        currentCount = 6.66;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient
                .assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425646 Entrance: Edit Dimensional Product: Paid Order", priority = 4)
    @AllureId("16046")
    public void testEditDimensionalProductPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        currentCount = 2.22;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient
                .assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425647 Entrance: Cancel order by Edit Dimensional Product: Paid Order", priority = 5)
    @AllureId("16047")
    public void testCancelByEditDimensionalProductPaid() {
        currentCount = 10.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, 0.0);
        orderClient.assertWorkflowResult(response, currentOrderId, States.CANCELLED);
    }

    @Test(description = "C23425648 Entrance: Cancel order by Edit with New Product Added", priority = 6)
    @AllureId("16048")
    public void testCancelByEditWithNewProduct() {
        makeDimensionalOrder();
        orderClient.editOrder(currentOrderId, 1, null);
        Response<?> response = orderClient.editOrder(currentOrderId, 0, 0.0);
        orderClient.assertWorkflowResult(response, currentOrderId, States.CANCELLED);
    }

    private void makeDimensionalOrder() {
        currentCount = 10.0;
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