package com.leroy.magportal.api.tests.onlineOrders.other;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst.States;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.DeliveryServiceTypeEnum;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.data.onlineOrders.OnlineOrderData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

import io.qameta.allure.AllureId;
import lombok.SneakyThrows;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PostRefundsTest extends BaseMagPortalApiTest {

    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private OrderClient orderClient;

    private List<String> lmCodes;


    private String currentOrderId;
    private OnlineOrderTypeData currentOrderType;


    @BeforeClass
    private void setUp() {
        currentOrderType = OnlineOrderTypeConst.PICKUP_PREPAYMENT;
        prepareNewOrder(false);
    }

    @Test(description = "C23720602 Pickup: Refund One Product One Item Pickup", priority = 1)
    @AllureId("16127")
    public void testRefundOneProductOneItemPickup() {
        setLmCodes(true);
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 1.0, null);
        assertRefundResult(response, 1);
    }

    @Test(description = "C23720603 Pickup: Refund One Product All Items", priority = 2)
    @AllureId("16128")
    public void testRefundOneProductAllItemsPickup() {
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 9.0, null);
        assertRefundResult(response, 2);
    }

    @Test(description = "C23720604 Pickup: Refund All Products All Items", priority = 3)
    @AllureId("16129")
    public void testRefundAllProductsAllItemsPickup() {
        prepareNewOrder(false);
        setLmCodes(false);
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 10.0, null);
        assertRefundResult(response, 1);
    }

    @Test(description = "C23720605 Pickup: Refund Dimensional Products", priority = 4)
    @AllureId("16130")
    public void testRefundDimensionalProductsPickup() {
        prepareNewOrder(true);
        setLmCodes(false);
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 5.55, null);
        assertRefundResult(response, 1);
    }

    @Test(description = "C23720606 Delivery: Refund Part of Delivery price", priority = 5)
    @AllureId("16131")
    public void testRefundPartDelivery() {
        currentOrderType = OnlineOrderTypeConst.DELIVERY_TO_ENTRANCE;
        prepareNewOrder(false);
        setLmCodes(true);
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 1.0, 9.99);
        assertRefundResult(response, 1);
    }

    @Test(description = "C23720607 Delivery: Refund One Product One Item and Part Delivery", priority = 6)
    @AllureId("16132")
    public void testRefundOneProductOneItemPartDelivery() {
        setLmCodes(true);
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 1.0, 1.0);
        assertRefundResult(response, 2);
    }

    @Test(description = "C23720608 Delivery: Refund One Product All Items and All Delivery", priority = 7)
    @AllureId("16133")
    public void testRefundOneProductAllItemsAllDelivery() {
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 9.0, 0.0);
        assertRefundResult(response, 3);
    }

    @Test(description = "C23720609 Delivery: Refund All Products All Items", priority = 8)
    @AllureId("16134")
    public void testRefundAllProductsAllItemsDelivery() {
        prepareNewOrder(false);
        setLmCodes(false);
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 10.0, null);
        assertRefundResult(response, 1);
    }

    @Test(description = "C23720610 Delivery: Dimensional Product", priority = 9)
    @AllureId("16135")
    public void testRefundDimensionalProductsDelivery() {
        prepareNewOrder(true);
        setLmCodes(false);
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 5.55, null);
        assertRefundResult(response, 1);
    }

    @Test(description = "C23720611 PostPayment: Try to refund (Negative)", priority = 10)
    @AllureId("16136")
    public void testRefundPostPayment() {
        currentOrderType = OnlineOrderTypeConst.PICKUP_POSTPAYMENT;
        prepareNewOrder(false);
        setLmCodes(true);
        Response<?> response = orderClient.postRefund(currentOrderId, lmCodes, 1.0, null);
        assertThat("It's possible to refund PostPayment.", !response.isSuccessful());
    }

    private void setLmCodes(boolean onlyFirst) {
        OnlineOrderData orderData = orderClient.getOnlineOrder(currentOrderId).asJson();
        lmCodes = new ArrayList<>();
        for (OrderProductData product : orderData.getProducts()) {
            lmCodes.add(product.getLmCode());
            if (onlyFirst) {
                break;
            }
        }
    }

    private void prepareNewOrder(boolean isDimensional) {
        if (isDimensional) {
            currentOrderId = onlineOrderHelper.createDimensionalOnlineOrder(currentOrderType)
                    .getSolutionId();
        } else {
            currentOrderId = onlineOrderHelper.createOnlineOrderCardPayment(currentOrderType)
                    .getSolutionId();
        }

        if (currentOrderType.getDeliveryType().equals(DeliveryServiceTypeEnum.PICKUP)) {
            orderClient.moveNewOrderToStatus(currentOrderId, States.GIVEN_AWAY);
        } else {
            orderClient.moveNewOrderToStatus(currentOrderId, States.DELIVERED);
        }
    }

    //Verification
    @SneakyThrows
    @Step("Check that Refund response is OK.")
    public void assertRefundResult(Response<?> response, int refunds) {
        assertThat("Post Refunds request has Failed.", response, successful());
        Thread.sleep(15000);
        OnlineOrderData orderData = orderClient.getOnlineOrder(currentOrderId).asJson();
        assertThat("Invalid count of refunds", orderData.getRefunds().size() == refunds);
    }
}