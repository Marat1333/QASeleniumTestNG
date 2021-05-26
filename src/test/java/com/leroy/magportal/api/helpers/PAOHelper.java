package com.leroy.magportal.api.helpers;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.data.CatalogSearchFilter;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.customer_accounts.clients.CustomerClient;
import com.leroy.common_mashups.customer_accounts.data.CustomerData;
import com.leroy.common_mashups.customer_accounts.data.CustomerListData;
import com.leroy.common_mashups.customer_accounts.data.CustomerResponseBodyData;
import com.leroy.common_mashups.customer_accounts.data.CustomerSearchFilters;
import com.leroy.common_mashups.customer_accounts.data.PhoneData;
import com.leroy.common_mashups.helpers.CustomerHelper;
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
import com.leroy.magmobile.api.data.sales.SalesDocDiscountData;
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
import com.leroy.magportal.api.data.timeslot.TimeslotData;
import com.leroy.magportal.api.data.timeslot.TimeslotResponseData;
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
import java.util.Random;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PAOHelper extends BaseHelper {

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private CustomerClient customerClient;
    @Inject
    private CartClient cartClient;
    @Inject
    private EstimateClient estimateClient;
    @Inject
    private OrderClient orderClient;
    @Inject
    private SalesDocSearchClient salesDocSearchClient;
    @Inject
    private CustomerHelper customerHelper;

    @Step("Создает Список CartProductOrderData для СОЗДАНИЯ коозины")
    public List<CartProductOrderData> makeCartProducts(int necessaryCount) {
        return makeCartProducts(necessaryCount, 10.0);
    }

    @Step("Создает Список CartProductOrderData для СОЗДАНИЯ коозины")
    public List<CartProductOrderData> makeCartProducts(int productsCount, double count) {
        List<CartProductOrderData> cartsProducts = new ArrayList<>();
        for (ProductData productData : searchProductHelper.getProducts(productsCount)) {
            CartProductOrderData productCard = new CartProductOrderData(productData);
            cartsProducts.add(convertItemToCartProduct(productCard.getLmCode(), count));
        }
        return cartsProducts;
    }

    @Step("Создает CartProductOrderData для СОЗДАНИЯ корзины из ЛМкода")
    public CartProductOrderData makeCartProductByLmCode(String lmCode) {
        return convertItemToCartProduct(lmCode, 10.0);
    }

    @Step("Создает CartProductOrderData для СОЗДАНИЯ корзины из ЛМкода с дробным количеством")
    public CartProductOrderData makeDimensionalCartProductByLmCode(String lmCode) {
        return convertItemToCartProduct(lmCode, 9.99);
    }

    private CartProductOrderData convertItemToCartProduct(String lmCode, Double count) {
        CartProductOrderData productCard = new CartProductOrderData();
        productCard.setQuantity(count);
        productCard.setLmCode(lmCode);
        return productCard;
    }

    @Step("API: Ищем подходящие товары для создания корзины с несколькими заказами")
    public List<CartProductOrderData> findProductsForSeveralOrdersInCart() {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setAvs(false);
        filtersData.setTopEM(false);
        filtersData.setHasAvailableStock(true);
        List<ProductData> productIDataList = searchProductHelper.getProducts(2, filtersData);
        CartProductOrderData productWithNegativeBalance = new CartProductOrderData(
                productIDataList.get(0));
        productWithNegativeBalance
                .setQuantity(productIDataList.get(0).getAvailableStock() + 10.0);
        CartProductOrderData productWithPositiveBalance = new CartProductOrderData(
                productIDataList.get(1));
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
        Response<CartData> cartDataResponse = cartClient.createCartRequest(products);
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
        reqOrderData.setShopId(userSessionData().getUserShopId());

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
//        return orderClient.assertThatIsCreatedAndGetData(orderResp);//TODO recover when issue with PAO Order_POST fixed
        OrderData orderData = orderClient.assertThatIsCreatedAndGetData(orderResp);
        orderData.setDelivery(isDelivery);
        return orderData;
    }

    @Step("API: Создать подвтержденный заказ")
    public OrderData createConfirmedOrder(
            List<CartProductOrderData> products, GiveAwayPoints giveAwayPoint,
            boolean isWaitForAllowedForPicking) {
        // Создание черновика
        OrderData orderData = createDraftOrder(products);

        // Установка ПИН кода
        String validPinCode = getValidPinCode(orderData.getDelivery());
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
        SimpleCustomerData customerData = TestDataConstants.CORPORATE_CUSTOMER;
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
    public OrderData createConfirmedPickupOrder(
            List<CartProductOrderData> products, boolean isWaitForAllowedForPicking) {
        return createConfirmedOrder(products, GiveAwayPoints.PICKUP, isWaitForAllowedForPicking);
    }

    @Step("Создаем подтвержденный заказ на доставку")
    public OrderData createConfirmedDeliveryOrder(
            List<CartProductOrderData> products, boolean isWaitForAllowedForPicking) {
        return createConfirmedOrder(products, GiveAwayPoints.DELIVERY, isWaitForAllowedForPicking);
    }

    @Step("Создаем подтвержденный заказ на самовывоз")
    public OrderData createConfirmedPickupOrder(CartProductOrderData product,
            boolean isWaitForAllowedForPicking) {
        return createConfirmedPickupOrder(Collections.singletonList(product), isWaitForAllowedForPicking);
    }

    @Step("Создаем подтвержденный заказ на самовывоз с 1 продуктом")
    public OrderData createDefaultConfirmedPickupOrder() {
        CartProductOrderData productOrderData = new CartProductOrderData(searchProductHelper.getRandomProduct());
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);
        return createConfirmedPickupOrder(productOrderData, true);
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

    @Step("Создаем черновик Сметы через API")
    private String createDraftEstimateAndGetCartId(
            CustomerData newCustomerData, List<String> lmCodes, int productCount) {
        if (lmCodes == null) {
            lmCodes = searchProductHelper.getProductLmCodes(productCount);
        }
        CustomerData customerData;
        if (newCustomerData == null) {
            customerData = customerHelper.getAnyCustomer();
        } else {
            Response<CustomerResponseBodyData> respCustomer = customerClient
                    .createCustomer(newCustomerData);
            assertThat(respCustomer, successful());
            customerData = respCustomer.asJson().getEntity();
        }
        List<EstimateProductOrderData> productOrderDataList = new ArrayList<>();
        for (int i = 1; i <= lmCodes.size(); i++) {
            EstimateProductOrderData productOrderData = new EstimateProductOrderData();
            productOrderData.setLmCode(lmCodes.get(i - 1));
            productOrderData.setQuantity((double) i);
            productOrderDataList.add(productOrderData);
        }
        EstimateCustomerData estimateCustomerData = new EstimateCustomerData();
        estimateCustomerData.setCustomerNumber(customerData.getCustomerNumber());
        estimateCustomerData.setFirstName(customerData.getFirstName());
        estimateCustomerData.setLastName(customerData.getLastName());
        estimateCustomerData.setType("PERSON");
        estimateCustomerData.setRoles(Collections.singletonList("PAYER"));
        Response<EstimateData> estimateDataResponse = estimateClient.sendRequestCreate(
                Collections.singletonList(estimateCustomerData), productOrderDataList);
        assertThat(estimateDataResponse, successful());
        return estimateDataResponse.asJson().getEstimateId();
    }

    public String createDraftEstimateAndGetCartId(CustomerData customerData, int productCount) {
        return createDraftEstimateAndGetCartId(customerData, null, productCount);
    }

    public String createDraftEstimateAndGetCartId(List<String> lmCodes) {
        return createDraftEstimateAndGetCartId(null, lmCodes, 1);
    }

    public String createDraftEstimateAndGetCartId(int productCount) {
        return createDraftEstimateAndGetCartId(null, null, productCount);
    }

    public String createDraftEstimateAndGetCartId() {
        return createDraftEstimateAndGetCartId(1);
    }

    @Step("Создаем подтвержденную Смету через API")
    public String createConfirmedEstimateAndGetCartId(CustomerData customerData,
            List<String> lmCodes) {
        String cartId = createDraftEstimateAndGetCartId(customerData, lmCodes, 1);
        Response<JsonNode> resp = estimateClient.confirm(cartId);
        estimateClient.assertThatResponseChangeStatusIsOk(resp);
        return cartId;
    }

    @Step("Создаем подтвержденную Смету через API")
    public String createConfirmedEstimateAndGetCartId(List<String> lmCodes) {
        return createConfirmedEstimateAndGetCartId(null, lmCodes);
    }

    public String createConfirmedEstimateAndGetCartId() {
        return createConfirmedEstimateAndGetCartId(searchProductHelper.getProductLmCodes(1));
    }

    public TimeslotData getLatestTimeslot(Response<TimeslotResponseData> response) {
        assertThatResponseIsOk(response);
        List<TimeslotData> responseData = response.asJson().getData();
        return responseData.get(responseData.size() - 1);
    }

    public int getDiscountReasonId() {
        Response<SalesDocDiscountData> req = cartClient.getDiscountReasons();
        assertThatResponseIsOk(req);
        SalesDocDiscountData salesDocDiscountData = req.asJson();
        assertThat("There ara NO reasons available", salesDocDiscountData.getReasons().size(), greaterThan(0));
        return salesDocDiscountData.getReasons().stream().findFirst().get().getId();
    }
}
