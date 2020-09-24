package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.LmCodeTypeEnum;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import java.util.List;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.TestResult;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

public class DoorEditTest extends BaseMagPortalApiTest {

    @Inject
    private BitrixHelper bitrixHelper;
    @Inject
    private OrderClient orderClient;

    private String currentOrderId;
    private OnlineOrderTypeData currentOrderType;
    private States currentStatus;
    private Integer currentProductsCount;
    private Double currentCount;


    @BeforeClass
    private void setUp() {
        currentProductsCount = 3;
        currentCount = 10.0;
        currentStatus = States.ALLOWED_FOR_PICKING;
        currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_DOOR;

        List<BitrixSolutionResponse> bitrixSolutionResponses = bitrixHelper
                .createOnlineOrders(1, currentOrderType, currentProductsCount);

        currentOrderId = bitrixSolutionResponses.stream().findAny().get().getSolutionId();
        bitrixSolutionResponses.remove(bitrixSolutionResponses.stream()
                .filter(x -> x.getSolutionId().equals(currentOrderId)).findFirst().get());
    }

    @BeforeMethod
    private void prepareTest() {
        if (currentOrderId == null) {
            List<BitrixSolutionResponse> bitrixSolutionResponses = bitrixHelper
                    .createOnlineOrders(1, currentOrderType, currentProductsCount);
            currentOrderId = bitrixSolutionResponses.stream().findAny().get().getSolutionId();

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

    @Test(description = "C23425629 Door: Edit Allowed For Picking Order", priority = 1)
    public void testEditAllowedForPicking() {
        currentCount = 9.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425630 Door: Add Product to Allowed For Picking Order (Negative)", priority = 2)
    public void testAddProductAllowedForPicking() {
        Response<?> response = orderClient.rearrange(currentOrderId, 2, null);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425631 Door: Edit And Add Product to Allowed For Picking Order (Negative)", priority = 3)
    public void testEditAndAddProductAllowedForPicking() {
        Response<?> response = orderClient.rearrange(currentOrderId, 2, 1.0);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425632 Door: Edit Picked Paid Order", priority = 5)
    public void testEditPickedPaid() {
        currentCount = 7.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425633 Door: Add Product to Paid Order (Negative)", priority = 4)
    public void testAddProductPickedPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        Response<?> response = orderClient.rearrange(currentOrderId, 2, null);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425634 Door: Edit Add Product to Paid Order (Negative)", priority = 6)
    public void testEditAndAddProductPickedPaid() {
        Response<?> response = orderClient.rearrange(currentOrderId, 2, 1.0);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C23425635 Door: Edit Dimensional Product Allowed For Picking Order", priority = 7)
    public void testEditDimensionalProduct() {
        makeDimensionalOrder();
        currentCount = 6.66;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425636 Door: Edit Dimensional Product: Paid Order", priority = 8)
    public void testEditDimensionalProductPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        currentCount = 2.22;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C23425637 Door: Cancel order by Edit Dimensional Product: Paid Order", priority = 9)
    public void testCancelByEditDimensionalProductPaid() {
        currentCount = 10.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, 0.0);
        orderClient.assertWorkflowResult(response, currentOrderId, States.CANCELLED);
    }

    @Test(description = "C23425638 Door: Cancel order by Edit with New Product Added", priority = 10)
    public void testCancelByEditWithNewProduct() {
        makeDimensionalOrder();
        orderClient.editOrder(currentOrderId, 1, null);
        Response<?> response = orderClient.editOrder(currentOrderId, 0, 0.0);
        orderClient.assertWorkflowResult(response, currentOrderId, States.CANCELLED);
    }

    private void makeDimensionalOrder() {
        currentCount = 10.0;
        currentProductsCount = 1;

        BitrixSolutionResponse bitrixSolutionResponses = bitrixHelper
                .createOnlineOrder(currentOrderType, LmCodeTypeEnum.DIMENSIONAL.getValue());
        currentOrderId = bitrixSolutionResponses.getSolutionId();
    }
}