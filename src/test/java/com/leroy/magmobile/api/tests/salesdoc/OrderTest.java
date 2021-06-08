package com.leroy.magmobile.api.tests.salesdoc;

import static com.leroy.core.matchers.Matchers.successful;
import static com.leroy.magportal.ui.constants.TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.customer_accounts.data.PhoneData;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.api.StatusCodes;
import com.leroy.constants.customer.CustomerConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.OrderClient;
import com.leroy.magmobile.api.clients.PaoClient;
import com.leroy.magmobile.api.clients.SalesDocSearchClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.GiveAwayData;
import com.leroy.magmobile.api.data.sales.orders.OrderCustomerData;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderProductData;
import com.leroy.magmobile.api.data.sales.orders.ResOrderCheckQuantityData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class OrderTest extends BaseProjectApiTest {

    @Inject
    private PAOHelper paoHelper;
    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private SalesDocSearchClient salesDocSearchClient;
    @Inject
    private CartClient cartClient;
    @Inject
    private OrderClient orderClient;
    @Inject
    private PaoClient paoClient;

    private OrderData orderData;
    private List<ProductData> productDataList;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeClass
    private void setUp() {
        productDataList = searchProductHelper.getProducts(2);
    }

    private void sendGetOrderRequestAndCheckData() {
        sendGetOrderRequestAndCheckData(false);
    }

    private void sendGetOrderRequestAndCheckData(boolean afterCancelOrder) {
        Response<OrderData> getResp = orderClient.getOrder(orderData.getOrderId());
        if (getResp.getStatusCode() == StatusCodes.ST_500_ERROR) {
            Log.error(getResp.toString());
            getResp = orderClient.getOrder(orderData.getOrderId());
        }
        BaseMashupClient.ResponseType responseType =
                afterCancelOrder ? BaseMashupClient.ResponseType.DELETE : BaseMashupClient.ResponseType.GET;
        orderClient.assertThatResponseMatches(
                getResp, orderData, responseType);
    }

    @Test(description = "C23195019 POST Order", priority = 1)
    public void testCreateOrder() {
        // Prepare request data
        CartProductOrderData productOrderData = new CartProductOrderData(
                productDataList.get(0));
        productOrderData.setQuantity(10.0);

        // Create
        step("Create Cart");
        Response<CartData> response = cartClient.createCartRequest(productOrderData);
        CartData cartData = cartClient.assertThatIsCreatedAndGetData(response, true);

        step("Create Order");
        ReqOrderData reqOrderData = new ReqOrderData();
        reqOrderData.setCartId(cartData.getCartId());
        reqOrderData.setDateOfGiveAway(LocalDateTime.now().plusDays(5));
        reqOrderData.setDocumentVersion(1);

        CartEstimateProductOrderData cardProduct = cartData.getProducts().get(0);

        ReqOrderProductData postProductData = new ReqOrderProductData();
        postProductData.setLineId(cardProduct.getLineId());
        postProductData.setLmCode(cardProduct.getLmCode());
        postProductData.setQuantity(cardProduct.getQuantity());
        postProductData.setPrice(cardProduct.getPrice());

        reqOrderData.getProducts().add(postProductData);

        Response<OrderData> orderResp = paoClient.createOrder(reqOrderData);
        orderData = paoClient.assertThatIsCreatedAndGetData(orderResp);

        paoClient.assertThatResponseContainsAddedProducts(orderResp,
                Collections.singletonList(postProductData));

        orderData.increasePaymentVersion(); // TODO это правильно или баг?
    }

    @Test(description = "C23195023 GET Order", priority = 2)
    public void testGetOrder() {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        Response<OrderData> getResp = orderClient.getOrder(orderData.getOrderId());
        orderClient.assertThatResponseMatches(getResp, orderData);
        orderData = getResp.asJson();//TODO Create Order response issue: no delivery field
    }

    @Test(description = "C23195024 PUT SetPinCode", priority = 3)
    public void testSetPinCode() {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        String validPinCode = paoHelper.getValidPinCode(orderData.getDelivery());
        Response<JsonNode> response = paoClient.setPinCode(orderData.getOrderId(), validPinCode);
        if (response.getStatusCode() == StatusCodes.ST_400_BAD_REQ) {
            validPinCode = paoHelper.getValidPinCode(orderData.getDelivery());
            response = paoClient.setPinCode(orderData.getOrderId(), validPinCode);
        }
        paoClient.assertThatPinCodeIsSet(response);
        orderData.setPinCode(validPinCode);
        orderData.increasePaymentVersion();
//        orderData.increaseSolutionVersion();
    }

    @Test(description = "C23195027 PUT Confirm Order", priority = 4)
    public void testConfirmOrder() {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        step("Search for a customer");
        SimpleCustomerData customerData = SIMPLE_CUSTOMER_DATA_1;
        OrderCustomerData orderCustomerData = new OrderCustomerData();
        orderCustomerData.setFirstName(customerData.getName());
        orderCustomerData.setRoles(Collections.singletonList(CustomerConst.Role.RECEIVER.name()));
        orderCustomerData.setType(CustomerConst.Type.PERSON.name());
        orderCustomerData.setPhone(new PhoneData(customerData.getPhoneNumber()));

        step("Confirm order");
        OrderData confirmOrderData = new OrderData();
        confirmOrderData.setPriority(SalesDocumentsConst.Priorities.HIGH.getApiVal());
        confirmOrderData.setShopId(getUserSessionData().getUserShopId());
        confirmOrderData.setSolutionVersion(orderData.getSolutionVersion());
        confirmOrderData.setPaymentVersion(orderData.getPaymentVersion());
        confirmOrderData.setFulfillmentVersion(orderData.getFulfillmentVersion());
        confirmOrderData.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        confirmOrderData.setPaymentTaskId(orderData.getPaymentTaskId());
        confirmOrderData.setProducts(orderData.getProducts());
        confirmOrderData.setCustomers(Collections.singletonList(orderCustomerData));
        confirmOrderData.setPinCode(orderData.getPinCode());

        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDateAsLocalDateTime(LocalDateTime.now().plusDays(1));
        giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        giveAwayData.setShopId(Integer.valueOf(getUserSessionData().getUserShopId()));
        confirmOrderData.setGiveAway(giveAwayData);
        Response<OrderData> resp = paoClient.confirmOrder(orderData.getOrderId(), confirmOrderData);
        paoClient.assertThatIsConfirmed(resp, orderData);
        OrderData confirmResponseData = resp.asJson();
        orderData.setCustomers(confirmResponseData.getCustomers());
    }

    @Test(description = "C23195028 PUT Rearrange Order - Add new product in confirmed order", priority = 5)
    public void testRearrangeOrder() throws Exception {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        ProductData productData = productDataList.get(1);
        OrderProductData orderProductData = new OrderProductData(productData);
        orderProductData.setQuantity(1.0);

        step("Wait until order is confirmed");
        OrderData respData = orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderData.getOrderId(),
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal());
        orderData.setStatus(respData.getStatus());
        orderData.setFulfillmentVersion(respData.getFulfillmentVersion());

        step("Rearrange Order");
        Response<JsonNode> resp = paoClient.rearrange(orderData, orderProductData);
        orderClient.assertThatRearranged(resp);
        orderData.getProducts().add(orderProductData);

        step("Check that Order is rearranged after GET request");
        Response<OrderData> getResp = orderClient.getOrder(orderData.getOrderId());
        orderClient.assertThatResponseMatches(getResp, orderData,
                BaseMashupClient.ResponseType.GET, false);
        orderData.setProducts(getResp.asJson().getProducts());
    }

    @Test(description = "C23195026 POST Check Quantity Order - happy path", priority = 6)
    public void testCheckQuantity() {
        OrderProductData orderProductData = orderData.getProducts().get(0);

        ReqOrderProductData putProductData = new ReqOrderProductData();
        putProductData.setLmCode(orderProductData.getLmCode());
        putProductData.setQuantity(orderProductData.getQuantity() + 1);

        ReqOrderData reqOrderData = new ReqOrderData();
        reqOrderData.setDateOfGiveAway(orderData.getGiveAway().getDateAsLocalDateTime());
        reqOrderData.setProducts(Collections.singletonList(putProductData));

        Response<ResOrderCheckQuantityData> resp = orderClient.checkQuantity(reqOrderData);
        orderClient.assertThatCheckQuantityIsOk(resp, reqOrderData.getProducts());
    }

    @Test(description = "C23195030 PUT OrderWorkflow - cancel confirmed order", priority = 7)
    public void testCancelOrder() {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        step("Cancel Order");
        Response<JsonNode> resp = orderClient.cancelOrder(orderData.getOrderId());
        orderClient.assertThatIsCancelled(resp);
        orderData.setStatus(SalesDocumentsConst.States.CANCELLED.getApiVal());
        orderData.setSalesDocStatus(SalesDocumentsConst.States.CANCELLED.getApiVal());
        orderData.increaseFulfillmentVersion();
        step("Check that Order is cancelled after GET request");
        sendGetOrderRequestAndCheckData(true);
    }

    @Test(description = "C23195040 PUT Order - Remove Product line from Draft Order", priority = 8)
    public void testUpdateDraftOrderRemoveProductLine() {
        // Prepare request data
        CartProductOrderData productOrderData1 = new CartProductOrderData(
                productDataList.get(0));
        productOrderData1.setQuantity(1.0);
        CartProductOrderData productOrderData2 = new CartProductOrderData(
                productDataList.get(1));
        productOrderData2.setQuantity(1.0);

        // Create
        step("Create Cart");
        Response<CartData> response = cartClient.createCartRequest(Arrays.asList(productOrderData1, productOrderData2));
        CartData cartData = cartClient.assertThatIsCreatedAndGetData(response, true);

        step("Create Order");
        ReqOrderData reqOrderData = new ReqOrderData();
        reqOrderData.setCartId(cartData.getCartId());
        reqOrderData.setDateOfGiveAway(LocalDateTime.now().plusDays(1));
        reqOrderData.setDocumentVersion(1);

        for (CartEstimateProductOrderData cardProduct : cartData.getProducts()) {
            ReqOrderProductData postProductData = new ReqOrderProductData();
            postProductData.setLineId(cardProduct.getLineId());
            postProductData.setLmCode(cardProduct.getLmCode());
            postProductData.setQuantity(cardProduct.getQuantity());
            postProductData.setPrice(cardProduct.getPrice());
            reqOrderData.getProducts().add(postProductData);
        }

        Response<OrderData> orderResp = paoClient.createOrder(reqOrderData);
        orderData = paoClient.assertThatIsCreatedAndGetData(orderResp);

        step("Remove one product item from Draft Order");
        orderData.getProducts().remove(1);
        orderData.setPriority(SalesDocumentsConst.Priorities.HIGH.getApiVal());
        Response<OrderData> respPut = paoClient.updateDraftOrder(orderData);
        orderClient.assertThatResponseMatches(respPut, orderData, BaseMashupClient.ResponseType.PUT);
        orderData.setPriority(null);

        step("Search for the created order document");
        Response<SalesDocumentListResponse> respSearch = salesDocSearchClient
                .searchForDocumentsByDocId(orderData.getFullDocId());
        assertThat(respSearch, successful());
        assertThat("Count of documents found", respSearch.asJson().getSalesDocuments(), hasSize(1));

        step("Send GET request and check that one product is removed");
        sendGetOrderRequestAndCheckData();
    }

    @Test(description = "C23195043 PUT Order - Change status to Deleted from Draft", priority = 9)
    public void testDeleteDraftOrder() {
        if (orderData == null)
            throw new IllegalArgumentException("order data hasn't been created");
        step("Change status to Deleted for the order");
        orderClient.assertThatResponseChangeStatusIsOk(
                paoClient.deleteDraftOrder(orderData.getOrderId()));
    }

}
