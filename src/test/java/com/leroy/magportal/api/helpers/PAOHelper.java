package com.leroy.magportal.api.helpers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.CustomerClient;
import com.leroy.magmobile.api.clients.EstimateClient;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class PAOHelper extends BaseHelper {

    // Поиск товаров (Вынести в отдельный Helper или нет?)

    public List<ProductItemData> getProducts(int necessaryCount, CatalogSearchFilter filtersData) {
        CatalogSearchClient catalogSearchClient = getCatalogSearchClient();
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        Response<ProductItemDataList> resp = catalogSearchClient.searchProductsBy(filtersData);
        assertThat("Catalog search request:", resp, successful());
        List<ProductItemData> items = resp.asJson().getItems();
        List<ProductItemData> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemData item : items) {
            if (!Arrays.asList(badLmCodes).contains(item.getLmCode()))
                if (filtersData.getAvs() == null || !filtersData.getAvs() && item.getAvsDate() == null ||
                        filtersData.getAvs() && item.getAvsDate() != null) {
                    if (filtersData.getHasAvailableStock() == null ||
                            (filtersData.getHasAvailableStock() && item.getAvailableStock() > 0 ||
                                    !filtersData.getHasAvailableStock() && item.getAvailableStock() <= 0)) {
                        resultList.add(item);
                        i++;
                    }
                }
            if (necessaryCount == i)
                break;
        }
        assertThat("Catalog search request:", resultList, hasSize(greaterThan(0)));
        return resultList;
    }

    public List<ProductItemData> getProducts(int necessaryCount) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setHasAvailableStock(true);
        return getProducts(necessaryCount, filter);
    }

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

    /*protected String createConfirmedOrder(List<String> lmCodes, List<CartProductOrderData> productDataList) {
        // Создание корзины
        List<CartProductOrderData> productOrderDataList = productDataList == null ? new ArrayList<>() : productDataList;
        if (productDataList == null) {
            if (lmCodes == null)
                lmCodes = apiClientProvider.getProductLmCodes(1);
            for (String lmCode : lmCodes) {
                CartProductOrderData productOrderData = new CartProductOrderData();
                productOrderData.setLmCode(lmCode);
                productOrderData.setQuantity(2.0);
                productOrderDataList.add(productOrderData);
            }
        }
        CartClient cartClient = apiClientProvider.getCartClient();
        //getUserSessionData().setAccessToken(getAccessToken());
        Response<CartData> cartDataResponse = cartClient.sendRequestCreate(productOrderDataList);
        assertThat(cartDataResponse, successful());

        CartData cartData = cartDataResponse.asJson();

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

        OrderClient orderClient = apiClientProvider.getOrderClient();
        Response<OrderData> orderResp = orderClient.createOrder(reqOrderData);
        OrderData orderData = orderClient.assertThatIsCreatedAndGetData(orderResp);

        // Установка ПИН кода
        String validPinCode = apiClientProvider.getValidPinCode();
        Response<JsonNode> response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        if (response.getStatusCode() == StatusCodes.ST_409_CONFLICT) {
            response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        }
        if (response.getStatusCode() == StatusCodes.ST_400_BAD_REQ) {
            validPinCode = apiClientProvider.getValidPinCode();
            response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        }
        orderClient.assertThatPinCodeIsSet(response);
        orderData.setPinCode(validPinCode);
        orderData.increasePaymentVersion();

        // Подтверждение заказа
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
        OrderCustomerData orderCustomerData = new OrderCustomerData();
        orderCustomerData.setFirstName(ParserUtil.parseFirstName(customerData.getName()));
        orderCustomerData.setLastName(ParserUtil.parseLastName(customerData.getName()));
        orderCustomerData.setRoles(Collections.singletonList(CustomerConst.Role.RECEIVER.name()));
        orderCustomerData.setType(CustomerConst.Type.PERSON.name());
        orderCustomerData.setPhone(new PhoneData(customerData.getPhone()));

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

        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDate(LocalDateTime.now().plusDays(1));
        giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        giveAwayData.setShopId(Integer.valueOf(getUserSessionData().getUserShopId()));
        confirmOrderData.setGiveAway(giveAwayData);

        Response<OrderData> resp = orderClient.confirmOrder(orderData.getOrderId(), confirmOrderData);
        //if (!resp.isSuccessful())
        //    resp = orderClient.confirmOrder(orderData.getOrderId(), confirmOrderData);
        orderClient.assertThatIsConfirmed(resp, orderData);
        return orderData.getOrderId();
    }

    protected void cancelOrder(String orderId, String expectedStatusBefore) throws Exception {
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
