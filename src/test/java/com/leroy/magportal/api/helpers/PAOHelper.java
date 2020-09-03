package com.leroy.magportal.api.helpers;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.StatusCodes;
import com.leroy.constants.customer.CustomerConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.ContextProvider;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.CustomerClient;
import com.leroy.magmobile.api.clients.EstimateClient;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.customer.CustomerData;
import com.leroy.magmobile.api.data.customer.CustomerListData;
import com.leroy.magmobile.api.data.customer.CustomerSearchFilters;
import com.leroy.magmobile.api.data.customer.PhoneData;
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
import com.leroy.magportal.api.clients.CatalogSearchClient;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PAOHelper extends BaseHelper {

    // Поиск товаров (Вынести в отдельный Helper или нет?)

    public List<ProductItemData> getProducts(int necessaryCount, CatalogSearchFilter filtersData) {
        CatalogSearchClient catalogSearchClient = getCatalogSearchClient();
        String[] badLmCodes = {"10008698",
                "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        Response<ProductItemDataList> resp = catalogSearchClient.searchProductsBy(filtersData);
        assertThat("Catalog search request:", resp, successful());
        List<ProductItemData> items = resp.asJson().getItems();
        List<ProductItemData> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemData item : items) {
            if (!Arrays.asList(badLmCodes).contains(item.getLmCode())) {
                if (filtersData.getAvs() == null
                        || !filtersData.getAvs() && item.getAvsDate() == null
                        ||
                        filtersData.getAvs() && item.getAvsDate() != null) {
                    if (filtersData.getHasAvailableStock() == null ||
                            (filtersData.getHasAvailableStock() && item.getAvailableStock() > 0 ||
                                    !filtersData.getHasAvailableStock()
                                            && item.getAvailableStock() <= 0)) {
                        resultList.add(item);
                        i++;
                    }
                }
            }
            if (necessaryCount == i) {
                break;
            }
        }
        assertThat("Catalog search request:", resultList, hasSize(greaterThan(0)));
        return resultList;
    }

    public List<ProductItemData> getProducts(int necessaryCount) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setHasAvailableStock(true);
        return getProducts(necessaryCount, filter);
    }

    @Step("API: Ищем подходящие товары для создания корзины с несколькими заказами")
    public List<CartProductOrderData> findProductsForSeveralOrdersInCart() {
        CatalogSearchFilter filtersData = new CatalogSearchFilter();
        filtersData.setAvs(false);
        filtersData.setTopEM(false);
        filtersData.setHasAvailableStock(true);
        List<ProductItemData> productItemDataList = getProducts(2, filtersData);
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
        CustomerClient customerClient = getCustomerClient();
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
        CartClient cartClient = getCartClient();
        Response<CartData> cartDataResponse = cartClient.sendRequestCreate(products);
        assertThat(cartDataResponse, successful());
        return cartDataResponse.asJson();
    }

    public CartData createCart(CartProductOrderData product) {
        return createCart(Collections.singletonList(product));
    }

    @Step("API: Создать подвтержденный заказ")
    public OrderData createConfirmedOrder(
            List<CartProductOrderData> products,
            boolean isWaitForAllowedForPicking) throws Exception {
        // Создание корзины
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

        OrderClient orderClient = getOrderClient();
        Response<OrderData> orderResp = orderClient.createOrder(reqOrderData);
        OrderData orderData = orderClient.assertThatIsCreatedAndGetData(orderResp);

        // Установка ПИН кода
        String validPinCode = getValidPinCode();
        Response<JsonNode> response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        if (response.getStatusCode() == StatusCodes.ST_409_CONFLICT) {
            response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        }
        if (response.getStatusCode() == StatusCodes.ST_400_BAD_REQ) {
            validPinCode = getValidPinCode();
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

        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDate(LocalDateTime.now().plusDays(1));
        giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        giveAwayData.setShopId(
                Integer.valueOf(ContextProvider.getContext().getUserSessionData().getUserShopId()));
        confirmOrderData.setGiveAway(giveAwayData);
        Response<OrderData> resp = orderClient
                .confirmOrder(orderData.getOrderId(), confirmOrderData);
        orderClient.assertThatIsConfirmed(resp, orderData);
        if (isWaitForAllowedForPicking) {
            orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderData.getOrderId(),
                    SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal());
        }
        return orderData;
    }

    public OrderData createConfirmedOrder(CartProductOrderData product,
            boolean isWaitForAllowedForPicking) throws Exception {
        return createConfirmedOrder(Collections.singletonList(product), isWaitForAllowedForPicking);
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
        Response<EstimateData> estimateDataResponse = getEstimateClient().sendRequestCreate(
                Collections.singletonList(estimateCustomerData), products);
        assertThat(estimateDataResponse, successful());
        return estimateDataResponse.asJson().getEstimateId();
    }

    @Step("API: Создаем подтвержденную Смету через")
    public String createConfirmedEstimateAndGetId(List<EstimateProductOrderData> products,
            CustomerData customerData) {
        EstimateClient client = getEstimateClient();
        String estimateId = createDraftEstimateAndGetId(products, customerData);
        Response<JsonNode> resp = client.confirm(estimateId);
        client.assertThatResponseChangeStatusIsOk(resp);
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

}
