package com.leroy.magportal.api.tests.onlineOrders.deliveryOrders;

import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.onlineOrders.OrderDeliveryRecalculateResponseData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.AllureId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ExpressDeliveryRecalculateTest extends BaseMagPortalApiTest {

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
        currentOrderType = OnlineOrderTypeConst.DELIVERY_EXPRESS;
        currentOrderId = onlineOrderHelper.createOnlineOrderCardPayment(currentOrderType)
                .getSolutionId();
    }

    @Test(description = "C23425655 Express: Delivery ReCalc: One product", priority = 1)
    @AllureId("1863")
    public void testDeliveryRecalcOneProduct() {
        Response<OrderDeliveryRecalculateResponseData> response = orderClient
                .deliveryRecalculate(currentOrderId, 1, 5.0);
        orderClient.assertDeliveryRecalculateResult(response, currentOrderId);
    }

    @Test(description = "C23425656 Express: Delivery ReCalc: All products", priority = 2)
    @AllureId("1864")
    public void testDeliveryRecalcAllProducts() {
        Response<OrderDeliveryRecalculateResponseData> response = orderClient
                .deliveryRecalculate(currentOrderId, currentProductsCount, 5.0);
        orderClient.assertDeliveryRecalculateResult(response, currentOrderId);
    }

    @Test(description = "C23425657 Express: Delivery ReCalc: Dimensional product", priority = 3)
    @AllureId("1865")
    public void testDeliveryRecalcDimensionalProduct() {
        makeDimensionalOrder();
        Response<OrderDeliveryRecalculateResponseData> response = orderClient
                .deliveryRecalculate(currentOrderId, 1, 5.55);
        orderClient.assertDeliveryRecalculateResult(response, currentOrderId);
    }

    @Test(description = "C23425658 Express: Delivery ReCalc: One product exceed count", priority = 4)
    @AllureId("1866")
    public void testDeliveryRecalcOneProductExceedCount() {
        Response<OrderDeliveryRecalculateResponseData> response = orderClient
                .deliveryRecalculate(currentOrderId, 1, 50.0);
        orderClient.assertDeliveryRecalculateResult(response, currentOrderId);
    }

    @Test(description = "C23425661 Express: Delivery ReCalc: No products (Negative)", priority = 5)
    @AllureId("1867")
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