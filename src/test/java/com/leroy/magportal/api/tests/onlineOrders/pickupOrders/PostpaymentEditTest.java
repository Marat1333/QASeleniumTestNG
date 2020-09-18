package com.leroy.magportal.api.tests.onlineOrders.pickupOrders;

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

public class PostpaymentEditTest extends BaseMagPortalApiTest {

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
        currentOrderType = OnlineOrderTypeConst.PICKUP_POSTPAYMENT;

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
    private void cartAfterMethod(ITestResult result) {
        if (result.getStatus() != TestResult.SUCCESS) {
            currentOrderId = null;
        }
    }

    @Test(description = "C0", priority = 1)
    public void testEditAllowedForPicking() {
        currentCount = 9.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C1", priority = 2)
    public void testAddProductAllowedForPicking() {
        currentProductsCount = 5;
        Response<?> response = orderClient.rearrange(currentOrderId, 2, null);
        orderClient
                .assertRearrangeResult(response, currentOrderId, currentCount,
                        currentProductsCount);
    }

    @Test(description = "C2", priority = 3)
    public void testEditAndAddProductAllowedForPicking() {
        currentProductsCount = 7;
        currentCount = 8.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 2, currentCount);
        orderClient
                .assertRearrangeResult(response, currentOrderId, currentCount,
                        currentProductsCount);
    }

    @Test(description = "C3", priority = 4)
    public void testAddProductPickedPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        Response<?> response = orderClient.rearrange(currentOrderId, 2, null);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOrder(currentOrderId);//just make it successful
        orderClient.assertRearrangeResult(response, currentOrderId, currentCount,
                currentProductsCount);
    }

    @Test(description = "C4", priority = 5)
    public void testEditPickedPaid() {
        currentCount = 7.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C5", priority = 6)
    public void testEditAndAddProductPickedPaid() {
        Response<?> response = orderClient.rearrange(currentOrderId, 2, 1.0);
        assertThat("It's possible to ADD product into payed Order", !response.isSuccessful());
        response = orderClient.getOrder(currentOrderId);//just make it successful
        orderClient
                .assertRearrangeResult(response, currentOrderId, currentCount,
                        currentProductsCount);
    }

    @Test(description = "C6", priority = 7)
    public void testEditDimensionalProduct() {
        makeDimensionalOrder();
        currentCount = 6.66;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C7", priority = 8)
    public void testEditDimensionalProductPaid() {
        currentStatus = States.PICKED;
        orderClient.moveNewOrderToStatus(currentOrderId, States.PICKED);
        currentCount = 2.22;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, currentCount);
        orderClient.assertEditResult(response, currentOrderId, currentCount);
    }

    @Test(description = "C8", priority = 9)
    public void testCancelByEditDimensionalProductPaid() {
        currentCount = 10.0;
        Response<?> response = orderClient.editOrder(currentOrderId, 0, 0.0);
        orderClient.assertWorkflowResult(response, currentOrderId, States.CANCELLED);
    }

    @Test(description = "C9", priority = 10)
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