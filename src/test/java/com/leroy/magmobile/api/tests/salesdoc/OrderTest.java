package com.leroy.magmobile.api.tests.salesdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.constants.StatusCodes;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.OrderClient;
import com.leroy.magmobile.api.clients.SalesDocSearchClient;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.GiveAwayData;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.PostOrderData;
import com.leroy.magmobile.api.data.sales.orders.PostOrderProductData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDateTime;
import java.util.Collections;

public class OrderTest extends BaseProjectApiTest {

    @Inject
    private CartClient cartClient;

    @Inject
    private OrderClient orderClient;

    @Inject
    private SalesDocSearchClient salesDocSearchClient;

    private CatalogSearchClient searchClient;

    private OrderData orderData;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeClass
    private void setUp() {
        searchClient = getCatalogSearchClient();
        cartClient.setSessionData(sessionData);
        orderClient.setSessionData(sessionData);
        salesDocSearchClient.setSessionData(sessionData);
    }

    @Test(description = "Create Order")
    public void testCreateOrder() {
        // Prepare request data
        CartEstimateProductOrderData productOrderData = new CartEstimateProductOrderData(
                searchClient.getProducts(1).get(0));
        productOrderData.setQuantity(1.0);

        // Create
        step("Create Cart");
        Response<CartData> response = cartClient.sendRequestCreate(productOrderData);
        CartData cartData = cartClient.assertThatIsCreatedAndGetData(response, true);

        step("Create Order");
        PostOrderData postOrderData = new PostOrderData();
        postOrderData.setCartId(cartData.getCartId());
        postOrderData.setDateOfGiveAway(LocalDateTime.now().plusDays(5));
        postOrderData.setDocumentVersion(1);

        CartEstimateProductOrderData cardProduct = cartData.getProducts().get(0);

        PostOrderProductData postProductData = new PostOrderProductData();
        postProductData.setLineId(cardProduct.getLineId());
        postProductData.setLmCode(cardProduct.getLmCode());
        postProductData.setQuantity(cardProduct.getQuantity());
        postProductData.setPrice(cardProduct.getPrice());

        postOrderData.getProducts().add(postProductData);

        Response<OrderData> orderResp = orderClient.createOrder(postOrderData);
        orderData = orderClient.assertThatIsCreatedAndGetData(orderResp);

        orderClient.assertThatResponseContainsAddedProducts(orderResp,
                Collections.singletonList(postProductData));

        orderData.increasePaymentVersion(); // TODO это правильно или баг?
    }

    @Test(description = "Get Order")
    public void testGetOrder() {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        Response<OrderData> getResp = orderClient.getOrder(orderData.getOrderId());
        if (getResp.getStatusCode() == StatusCodes.ST_500_ERROR) {
            Log.error(getResp.toString());
            getResp = orderClient.getOrder(orderData.getOrderId());
        }
        orderClient.assertThatGetResponseMatches(getResp, orderData);
    }

    @Test(description = "Set PinCode")
    public void testSetPinCode() {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        String validPinCode = salesDocSearchClient.getValidPinCode();
        Response<JsonNode> response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        orderClient.assertThatPinCodeIsSet(response);
        orderData.setPinCode(validPinCode);
        orderData.increasePaymentVersion();
        orderData.increaseFulfillmentVersion();
    }

    @Test(description = "Confirm Order")
    public void testConfirmOrder() {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        OrderData confirmOrderData = new OrderData();
        confirmOrderData.setPriority(SalesDocumentsConst.Priorities.HIGH.getApiVal());
        confirmOrderData.setShopId(sessionData.getUserShopId());
        confirmOrderData.setSolutionVersion(orderData.getSolutionVersion());
        confirmOrderData.setPaymentVersion(orderData.getPaymentVersion());
        confirmOrderData.setFulfillmentVersion(orderData.getFulfillmentVersion());
        confirmOrderData.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        confirmOrderData.setPaymentTaskId(orderData.getPaymentTaskId());
        confirmOrderData.setProducts(orderData.getProducts());

        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDate(LocalDateTime.now().plusDays(1));
        giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        giveAwayData.setShopId(Integer.valueOf(sessionData.getUserShopId()));
        confirmOrderData.setGiveAway(giveAwayData);

        Response<OrderData> getResp = orderClient.confirmOrder(orderData.getOrderId(), confirmOrderData);
        orderClient.assertThatIsConfirmed(getResp, orderData);
        orderData.increaseFulfillmentVersion();
    }

    @Test(description = "Cancel Order")
    public void testCancelOrder() throws Exception {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        step("Wait until order is confirmed");
        orderClient.waitUntilOrderIsConfirmed(orderData.getOrderId());
        step("Cancel Order");
        Response<JsonNode> resp = orderClient.cancelOrder(orderData.getOrderId());
        orderClient.assertThatIsCancelled(resp);
        orderData.setStatus(SalesDocumentsConst.States.CANCELLED.getApiVal());
        orderData.setSalesDocStatus(SalesDocumentsConst.States.CANCELLED.getApiVal());
        orderData.increaseFulfillmentVersion();
        step("Check that Order is cancelled after GET request");
        Response<OrderData> getResp = orderClient.getOrder(orderData.getOrderId());
        orderClient.assertThatGetResponseMatches(getResp, orderData);
    }

}
