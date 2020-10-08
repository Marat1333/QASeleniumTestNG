package com.leroy.magportal.api.tests.offlineOrders;

import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.LmCodeTypeEnum;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import java.util.List;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;
import ru.leroymerlin.qa.core.clients.base.Response;

public class EditTest extends BaseMagPortalApiTest {

    @Inject
    private PAOHelper paoHelper;
    @Inject
    private OrderClient orderClient;

    private String currentOrderId;
    private List<CartProductOrderData> cartProducts;
    private States currentStatus;
    private Integer currentProductsCount;
    private Double currentCount;


    @BeforeClass
    private void setUp() {
        currentProductsCount = 3;
        currentCount = 10.0;
        currentStatus = States.ALLOWED_FOR_PICKING;
        cartProducts = paoHelper.makeCartProducts(currentProductsCount);

        OrderData orderData = paoHelper.createConfirmedOrder(cartProducts, true);
        currentOrderId = orderData.getOrderId();
    }

    @BeforeMethod
    private void prepareTest() {
        if (currentOrderId == null) {
            OrderData orderData = paoHelper.createConfirmedOrder(cartProducts, true);
            currentOrderId = orderData.getOrderId();

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

    @Test(description = "C23425605 Offline: Edit Allowed For Picking Order", priority = 1)
    public void testEditAllowedForPicking() {
        currentCount = 9.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425606 Offline: Add Product to Allowed For Picking Order", priority = 2)
    public void testAddProductAllowedForPicking() {
        currentProductsCount = 5;
        Response<?> response = orderClient.rearrange(currentOrderId, 2, null);
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425607 Offline: Edit And Add Product to Allowed For Picking Order", priority = 3)
    public void testEditAndAddProductAllowedForPicking() {
        currentProductsCount = 7;
        currentCount = 8.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 2, currentCount);
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425608 Offline: Edit Picked Paid Order", priority = 5)
    public void testEditPickedPaid() {
        currentCount = 7.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425609 Offline: Add Product to Paid Order (Negative)", priority = 4)
    public void testAddProductPickedPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        Response<?> response = orderClient.rearrange(currentOrderId, 2, null);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOnlineOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425610 Offline: Edit Add Product to Paid Order (Negative)", priority = 6)
    public void testEditAndAddProductPickedPaid() {
        Response<?> response = orderClient.rearrange(currentOrderId, 2, 1.0);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOnlineOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425611 Offline: Edit Dimensional Product Allowed For Picking Order", priority = 7)
    public void testEditDimensionalProduct() throws Exception {
        makeDimensionalOrder();
        currentCount = 6.66;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425612 Offline: Edit Dimensional Product: Paid Order", priority = 8)
    public void testEditDimensionalProductPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        currentCount = 2.22;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425613 Offline: Cancel order by Edit Dimensional Product: Paid Order", priority = 9)
    public void testCancelByEditDimensionalProductPaid() {
        currentCount = 10.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, 0.0);
        orderClient.assertWorkflowResult(response, currentOrderId, States.CANCELLED);
    }

    @Test(description = "C23425614 Offline: Cancel order by Edit with New Product Added", priority = 10)
    public void testCancelByEditWithNewProduct() throws Exception {
        makeDimensionalOrder();
        orderClient.editOrder(currentOrderId, 1, null);
        Response<?> response = orderClient.editOrder(currentOrderId, 0, 0.0);
        orderClient.assertWorkflowResult(response, currentOrderId, States.CANCELLED);
    }

    private void makeDimensionalOrder() throws Exception {
        currentCount = 10.0;
        currentProductsCount = 1;

        OrderData orderData = paoHelper.createConfirmedOrder(
                paoHelper.makeCartProductByLmCode(LmCodeTypeEnum.DIMENSIONAL.getValue()), true);
        currentOrderId = orderData.getOrderId();
    }
}