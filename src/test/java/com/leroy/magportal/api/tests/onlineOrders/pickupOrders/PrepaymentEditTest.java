package com.leroy.magportal.api.tests.onlineOrders.pickupOrders;

import static org.hamcrest.MatcherAssert.assertThat;

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

public class PrepaymentEditTest extends BaseMagPortalApiTest {

    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private OrderClient orderClient;

    private String currentOrderId;
    private OnlineOrderTypeData currentOrderType;
    private States currentStatus;
    private Integer currentProductsCount;
    private Double currentCount;

    private Boolean isDimensional;


    @BeforeClass
    private void setUp() {
        currentProductsCount = 3;
        currentCount = 10.0;
        currentStatus = States.ALLOWED_FOR_PICKING;
        currentOrderType = OnlineOrderTypeConst.PICKUP_PREPAYMENT;
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

    @Test(description = "C23425616 PrePayment: Edit Allowed For Picking Order", priority = 1)
    @AllureId("16111")
    public void testEditAllowedForPicking() {
        currentCount = 9.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425617 PrePayment: Add Product to Allowed For Picking Order (Negative)", priority = 2)
    @AllureId("16112")
    public void testAddProductAllowedForPicking() {
        Response<?> response = orderClient.rearrange(currentOrderId, 2, null);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOnlineOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425618 PrePayment: Edit And Add Product to Allowed For Picking Order (Negative)", priority = 3)
    @AllureId("16113")
    public void testEditAndAddProductAllowedForPicking() {
        Response<?> response = orderClient.rearrange(currentOrderId, 2, 1.0);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOnlineOrder(currentOrderId);//just make it successful
        orderClient
                .assertRearrangeResult(response, currentOrderId, currentCount,
                        currentProductsCount);
    }

    @Test(description = "C23425619 PrePayment: Edit Picked Paid Order", priority = 4)
    @AllureId("16114")
    public void testAddProductPickedPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        Response<?> response = orderClient.rearrange(currentOrderId, 2, null);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOnlineOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425620 PrePayment: Add Product to Paid Order (Negative)", priority = 5)
    @AllureId("16115")
    public void testEditPickedPaid() {
        currentCount = 7.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425621 PrePayment: Edit Add Product to Paid Order (Negative)", priority = 6)
    @AllureId("16116")
    public void testEditAndAddProductPickedPaid() {
        Response<?> response = orderClient.rearrange(currentOrderId, 2, 1.0);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOnlineOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425622 PrePayment: Edit Dimensional Product Allowed For Picking Order", priority = 7)
    @AllureId("16117")
    public void testEditDimensionalProduct() {
        makeDimensionalOrder();
        currentCount = 6.66;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425623 PrePayment: Edit Dimensional Product: Paid Order", priority = 8)
    @AllureId("16118")
    public void testEditDimensionalProductPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        currentCount = 2.22;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425624 PrePayment: Cancel order by Edit Dimensional Product: Paid Order", priority = 9)
    @AllureId("16119")
    public void testCancelByEditDimensionalProductPaid() {
        currentCount = 10.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, 0.0);
        orderClient.assertWorkflowResult(response, currentOrderId, States.CANCELLED);
    }

    @Test(description = "C23425625 PrePayment: Cancel order by Edit with New Product Added", priority = 10)
    @AllureId("16120")
    public void testCancelByEditWithNewProduct() {
        makeDimensionalOrder();
        orderClient.editOrder(currentOrderId, 1, null);
        Response<?> response = orderClient.editOrder(currentOrderId, 0, 0.0);
        orderClient.assertWorkflowResult(response, currentOrderId, States.CANCELLED);
    }

    private void makeDimensionalOrder() {
        currentCount = 10.0;
        currentProductsCount = 1;
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