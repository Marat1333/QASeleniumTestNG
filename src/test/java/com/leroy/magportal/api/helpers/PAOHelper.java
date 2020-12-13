package com.leroy.magportal.api.helpers;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.customer_accounts.clients.CustomerClient;
import com.leroy.common_mashups.customer_accounts.data.CustomerData;
import com.leroy.common_mashups.customer_accounts.data.CustomerListData;
import com.leroy.common_mashups.customer_accounts.data.CustomerSearchFilters;
import com.leroy.common_mashups.customer_accounts.data.PhoneData;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.api.StatusCodes;
import com.leroy.constants.customer.CustomerConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.constants.sales.SalesDocumentsConst.GiveAwayPoints;
import com.leroy.core.ContextProvider;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.EstimateClient;
import com.leroy.magmobile.api.clients.SalesDocSearchClient;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateCustomerData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.GiveAwayData;
import com.leroy.magmobile.api.data.sales.orders.OrderCustomerData;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderProductData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.utils.ParserUtil;
import com.leroy.utils.RandomUtil;
import io.qameta.allure.Step;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PAOHelper extends BaseHelper {

    @Inject
    SearchProductHelper searchProductHelper;
    @Inject
    CustomerClient customerClient;
    @Inject
    CartClient cartClient;
    @Inject
    EstimateClient estimateClient;
    @Inject
    OrderClient orderClient;
    @Inject
    SalesDocSearchClient salesDocSearchClient;

    @Step("Создает Список CartProductOrderData для СОЗДАНИЯ коозины")
    public List<CartProductOrderData> makeCartProducts(int necessaryCount) {
        List<CartProductOrderData> cartsProducts = new ArrayList<>();
        for (ProductItemData productData : searchProductHelper.getProducts(necessaryCount)) {
            CartProductOrderData productCard = new CartProductOrderData(productData);
            cartsProducts.add(convertItemToCartProduct(productCard.getLmCode()));
        }
        return cartsProducts;
    }

    @Step("Создает CartProductOrderData для СОЗДАНИЯ корзины из ЛМкода")
    public CartProductOrderData makeCartProductByLmCode(String lmCode) {
        return convertItemToCartProduct(lmCode);
    }

    private CartProductOrderData convertItemToCartProduct(String lmCode) {
        CartProductOrderData productCard = new CartProductOrderData();
        productCard.setQuantity(10.0);
        productCard.setLmCode(lmCode);
        return productCard;
    }

    @Step("API: Ищем подходящие товары для создания корзины с несколькими заказами")
    public List<CartProductOrderData> findProductsForSeveralOrdersInCart() {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setAvs(false);
        filtersData.setTopEM(false);
        filtersData.setHasAvailableStock(true);
        List<ProductItemData> productItemDataList = searchProductHelper.getProducts(2, filtersData);
        CartProductOrderData productWithNegativeBalance = new CartProductOrderData(
                productItemDataList.get(0));
        productWithNegativeBalance
                .setQuantity(productItemDataList.get(0).getAvailableStock() + 10.0);
        CartProductOrderData productWithPositiveBalance = new CartProductOrderData(
                productItemDataList.get(1));
        productWithPositiveBalance.setQuantity(1.0);

        return Arrays.asList(productWithNegativeBalance, productWithPositiveBalance);
    }

    // Поиск Клиентов

    @Step("API: Ищем клиента")
    public CustomerData searchForCustomer(SimpleCustomerData simpleCustomerData) {
        if (simpleCustomerData.getPhoneNumber() != null) {
            CustomerSearchFilters customerSearchFilters = new CustomerSearchFilters();
            customerSearchFilters.setCustomerType(CustomerSearchFilters.CustomerType.NATURAL);
            customerSearchFilters
                    .setDiscriminantType(CustomerSearchFilters.DiscriminantType.PHONENUMBER);
            customerSearchFilters.setDiscriminantValue(simpleCustomerData.getPhoneNumber());
            Response<CustomerListData> resp = customerClient
                    .searchForCustomers(customerSearchFilters);
            assertThat(resp, successful());
            CustomerListData data = resp.asJson();
            assertThat("Не был найден клиент: " + simpleCustomerData.toString(),
                    data.getItems(), hasSize(greaterThan(0)));
            return data.getItems().get(0);
        }
        return null;
    }

    // Создание Корзин, Смет и Заказов

    @Step("API: Создаем корзину")
    public CartData createCart(List<CartProductOrderData> products) {
        Response<CartData> cartDataResponse = cartClient.sendRequestCreate(products);
        assertThat(cartDataResponse, successful());
        return cartDataResponse.asJson();
    }

    public CartData createCart(CartProductOrderData product) {
        return createCart(Collections.singletonList(product));
    }

    @Step("API: Создать черновик заказа на самовывоз")
    public OrderData createDraftOrder(List<CartProductOrderData> products) {
        return createDraftOrder(products, false);
    }

    @Step("API: Создать черновик заказа")
    public OrderData createDraftOrder(List<CartProductOrderData> products, boolean isDelivery) {
        CartData cartData = createCart(products);

        // Создание черновика заказа
        ReqOrderData reqOrderData = new ReqOrderData();
        reqOrderData.setCartId(cartData.getCartId());
        reqOrderData.setDateOfGiveAway(LocalDateTime.now().plusDays(1));
        reqOrderData.setDocumentVersion(1);

        List<ReqOrderProductData> orderProducts = new ArrayList<>();
        for (CartProductOrderData cartProduct : cartData.getProducts()) {
            ReqOrderProductData postProductData = new ReqOrderProductData();
            postProductData.setLineId(cartProduct.getLineId());
            postProductData.setLmCode(cartProduct.getLmCode());
            postProductData.setQuantity(cartProduct.getQuantity());
            postProductData.setPrice(cartProduct.getPrice());
            orderProducts.add(postProductData);
        }

        reqOrderData.setProducts(orderProducts);
        reqOrderData.setWithDelivery(isDelivery);

        Response<OrderData> orderResp = orderClient.createOrder(reqOrderData);
        return orderClient.assertThatIsCreatedAndGetData(orderResp);
    }

    @Step("API: Создать подвтержденный заказ")
    public OrderData createConfirmedOrder(
            List<CartProductOrderData> products, GiveAwayPoints giveAwayPoint,
            boolean isWaitForAllowedForPicking) {
        // Создание черновика
        OrderData orderData = createDraftOrder(products);

        // Установка ПИН кода
        String validPinCode = getValidPinCode(
                orderClient.getOnlineOrder(orderData.getOrderId()).asJson()
                        .getDelivery());//TODO: due to issue with POST_Order
        Response<JsonNode> response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        if (response.getStatusCode() == StatusCodes.ST_409_CONFLICT) {
            response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        }
        if (response.getStatusCode() == StatusCodes.ST_400_BAD_REQ) {
            validPinCode = getValidPinCode(orderData.getDelivery());
            response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        }
        orderClient.assertThatPinCodeIsSet(response);
        orderData.setPinCode(validPinCode);
        orderData.increasePaymentVersion();

        // Подтверждение заказа
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        OrderCustomerData orderCustomerData = new OrderCustomerData();
        orderCustomerData.setFirstName(ParserUtil.parseFirstName(customerData.getName()));
        orderCustomerData.setLastName(ParserUtil.parseLastName(customerData.getName()));
        orderCustomerData.setRoles(Collections.singletonList(CustomerConst.Role.RECEIVER.name()));
        orderCustomerData.setType(CustomerConst.Type.PERSON.name());
        orderCustomerData.setPhone(new PhoneData(customerData.getPhoneNumber()));

        OrderData confirmOrderData = new OrderData();
        confirmOrderData.setPriority(SalesDocumentsConst.Priorities.HIGH.getApiVal());
        confirmOrderData.setPinCode(orderData.getPinCode());
        confirmOrderData
                .setShopId(ContextProvider.getContext().getUserSessionData().getUserShopId());
        confirmOrderData.setSolutionVersion(orderData.getSolutionVersion());
        confirmOrderData.setPaymentVersion(orderData.getPaymentVersion());
        confirmOrderData.setFulfillmentVersion(orderData.getFulfillmentVersion());
        confirmOrderData.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        confirmOrderData.setPaymentTaskId(orderData.getPaymentTaskId());
        confirmOrderData.setProducts(orderData.getProducts());
        confirmOrderData.setCustomers(Collections.singletonList(orderCustomerData));

        confirmOrderData.setGiveAway(makeGiveAwayData(giveAwayPoint));

        Response<OrderData> resp = orderClient
                .confirmOrder(orderData.getOrderId(), confirmOrderData);
        orderClient.assertThatIsConfirmed(resp, orderData);
        if (isWaitForAllowedForPicking) {
            orderClient.waitUntilOrderGetStatus(orderData.getOrderId(),
                    SalesDocumentsConst.States.ALLOWED_FOR_PICKING, null);
        }
        return orderData;
    }

    @Step("Создаем подтвержденный заказ на самовывоз")
    public OrderData createConfirmedOrder(
            List<CartProductOrderData> products, boolean isWaitForAllowedForPicking) {
        return createConfirmedOrder(products, GiveAwayPoints.PICKUP, isWaitForAllowedForPicking);
    }

    @Step("Создаем подтвержденный заказ на доставку")
    public OrderData createConfirmedDeliveryOrder(
            List<CartProductOrderData> products, boolean isWaitForAllowedForPicking) {
        return createConfirmedOrder(products, GiveAwayPoints.DELIVERY, isWaitForAllowedForPicking);
    }

    public OrderData createConfirmedOrder(CartProductOrderData product,
            boolean isWaitForAllowedForPicking) {
        return createConfirmedOrder(Collections.singletonList(product), isWaitForAllowedForPicking);
    }

    private GiveAwayData makeGiveAwayData(GiveAwayPoints giveAwayPoints) {
        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDateAsLocalDateTime(LocalDateTime.now().plusDays(1));
        giveAwayData.setPoint(giveAwayPoints.getApiVal());
        giveAwayData.setShopId(
                Integer.valueOf(ContextProvider.getContext().getUserSessionData().getUserShopId()));
        return giveAwayData;
    }

    @Step("Создаем черновик Сметы через API")
    private String createDraftEstimateAndGetId(
            List<EstimateProductOrderData> products, CustomerData customerData) {
        EstimateCustomerData estimateCustomerData = new EstimateCustomerData();
        estimateCustomerData.setCustomerNumber(customerData.getCustomerNumber());
        estimateCustomerData.setFirstName(customerData.getFirstName());
        estimateCustomerData.setLastName(customerData.getLastName());
        estimateCustomerData.setPhone(new PhoneData(customerData.getMainPhoneFromCommunication()));
        estimateCustomerData.setEmail(customerData.getMainEmailFromCommunication());
        estimateCustomerData.setType("PERSON");
        estimateCustomerData.setRoles(Collections.singletonList("PAYER"));
        Response<EstimateData> estimateDataResponse = estimateClient.sendRequestCreate(
                Collections.singletonList(estimateCustomerData), products);
        assertThat(estimateDataResponse, successful());
        return estimateDataResponse.asJson().getEstimateId();
    }

    @Step("API: Создаем подтвержденную Смету через")
    public String createConfirmedEstimateAndGetId(List<EstimateProductOrderData> products,
            CustomerData customerData) {
        String estimateId = createDraftEstimateAndGetId(products, customerData);
        Response<JsonNode> resp = estimateClient.confirm(estimateId);
        estimateClient.assertThatResponseChangeStatusIsOk(resp);
        return estimateId;
    }

    public String createConfirmedEstimateAndGetId(EstimateProductOrderData product,
            CustomerData customerData) {
        return createConfirmedEstimateAndGetId(Collections.singletonList(product), customerData);
    }

    /*protected void cancelOrder(String orderId, String expectedStatusBefore) throws Exception {
        OrderClient orderClient = apiClientProvider.getOrderClient();
        orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderId,
                expectedStatusBefore);
        Response<JsonNode> r = orderClient.cancelOrder(orderId);
        anAssert().isTrue(r.isSuccessful(),
                "Не смогли удалить заказ №" + orderId + ". Ошибка: " + r.toString());
        orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderId,
                SalesDocumentsConst.States.CANCELLED.getApiVal());
    }

    protected void cancelOrder(String orderId) throws Exception {
        cancelOrder(orderId, SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal());
    }

     */

    public String getValidPinCode(boolean isDelivery) {
        int tryCount = 10;
        for (int i = 0; i < tryCount; i++) {
            String generatedPinCode = RandomUtil.randomPinCode(!isDelivery);
            SalesDocumentListResponse salesDocumentsResponse = salesDocSearchClient
                    .getSalesDocumentsByPinCodeOrDocId(generatedPinCode)
                    .asJson();
            if (salesDocumentsResponse.getTotalCount() == 0) {
                Log.info("API: None documents found with PIN: " + generatedPinCode);
                return generatedPinCode;
            }
            List<SalesDocumentResponseData> salesDocs = salesDocumentsResponse.getSalesDocuments();
            if (!generatedPinCode.equals(salesDocs.get(0).getPinCode())) {
                return generatedPinCode;
            }
        }
        throw new RuntimeException("Couldn't find valid pin code for " + tryCount + " trying");
    }

}
