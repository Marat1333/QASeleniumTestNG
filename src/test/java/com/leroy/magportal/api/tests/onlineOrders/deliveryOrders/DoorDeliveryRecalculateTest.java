package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.onlineOrders.OrderDeliveryRecalculateResponseData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class DoorDeliveryRecalculateTest extends BaseMagPortalApiTest {

    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private OrderClient orderClient;

    private String currentOrderId;
    private Integer currentProductsCount;
    private OnlineOrderTypeData currentOrderType;


    @BeforeClass
    private void setUp() {
        currentProductsCount = 3;
        currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_DOOR;

        currentOrderId = onlineOrderHelper.createOnlineOrderCardPayment(currentOrderType)
                .getSolutionId();
    }

    @Test(description = "C23425649 Door: Delivery ReCalc: One product", priority = 1)
    public void testDeliveryRecalcOneProduct() {
        Response<OrderDeliveryRecalculateResponseData> response = orderClient
                .deliveryRecalculate(currentOrderId, 1, 5.0);
        orderClient.assertDeliveryRecalculateResult(response, currentOrderId);
    }

    @Test(description = "C23425650 Door: Delivery ReCalc: All products", priority = 2)
    public void testDeliveryRecalcAllProducts() {
        Response<OrderDeliveryRecalculateResponseData> response = orderClient
                .deliveryRecalculate(currentOrderId, currentProductsCount, 5.0);
        orderClient.assertDeliveryRecalculateResult(response, currentOrderId);
    }

    @Test(description = "C23425651 Door: Delivery ReCalc: Dimensional product", priority = 3)
    public void testDeliveryRecalcDimensionalProduct() {
        makeDimensionalOrder();
        Response<OrderDeliveryRecalculateResponseData> response = orderClient
                .deliveryRecalculate(currentOrderId, 1, 5.55);
        orderClient.assertDeliveryRecalculateResult(response, currentOrderId);
    }

    @Test(description = "C23425659 Door: Delivery ReCalc: One product exceed count", priority = 4)
    public void testDeliveryRecalcOneProductExceedCount() {
        Response<OrderDeliveryRecalculateResponseData> response = orderClient
                .deliveryRecalculate(currentOrderId, 1, 50.0);
        orderClient.assertDeliveryRecalculateResult(response, currentOrderId);
    }

    @Test(description = "C23425663 Door: Delivery ReCalc: No products (Negative)", priority = 5)
    public void testDeliveryRecalcNoProducts() {
        Response<OrderDeliveryRecalculateResponseData> response = orderClient
                .deliveryRecalculate(currentOrderId, 0, 5.0);
        assertThat("Request to Recalculate Delivery Cost successful for empty products.",
                !response.isSuccessful());
    }

    private void makeDimensionalOrder() {
        currentProductsCount = 1;

        currentOrderId = onlineOrderHelper.createDimensionalOnlineOrder(currentOrderType)
                .getSolutionId();
    }
}