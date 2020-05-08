package com.leroy.magportal.ui.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.StatusCodes;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.clients.OrderClient;
import com.leroy.magmobile.api.data.sales.cart_estimate.CartEstimateProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderData;
import com.leroy.magmobile.api.data.sales.orders.ReqOrderProductData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.salesdoc.ShortOrderDocWebData;
import com.leroy.magportal.ui.pages.orders.OrdersPage;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDateTime;
import java.util.List;

import static com.leroy.magportal.ui.constants.OrderConst.DeliveryType.DELIVERY_TK;
import static com.leroy.magportal.ui.constants.OrderConst.DeliveryType.PICKUP;
import static com.leroy.magportal.ui.constants.OrderConst.Status.*;

public class OrderTest extends WebBaseSteps {

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @Test(description = "C22829624 Ордерс. Фильтрация по статусу заказа")
    public void testOrderFilterByStatus() throws Exception {
        // Step 1
        step("Открыть страницу с Заказами");
        OrdersPage ordersPage = loginAndGoTo(OrdersPage.class);
        List<ShortOrderDocWebData> documentListFirst = ordersPage.getDocumentDataList();

        // Step 2
        step("В фильтре Статус заказа выставить значение = Создан, нажать кнопку 'Показать заказы'");
        ordersPage.selectStatusFilters(CREATED)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithStatuses(CREATED);

        // Step 3
        step("Деактивировать фильтр Создан, убрав чекбокс, нажать кнопку 'Показать заказы'");
        ordersPage.deselectStatusFilters(CREATED)
                .clickApplyFilters()
                .shouldDocumentListIs(documentListFirst);

        // Step 4
        step("Повторить шаг 2 для статусов Сборка, Сборка(пауза), Собран, Частично собран, Выдан, Отменен");
        String[] step4Filters = {ASSEMBLY, ASSEMBLY_PAUSE, PICKED, PICKED_PARTIALLY, ISSUED, CANCELLED};
        ordersPage.selectStatusFilters(step4Filters)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithStatuses(step4Filters);

        // Step 5
        step("Активировать статусы Создан, Сборка, Сборка(пауза), Собран, Частично собран, Выдан, Частично выдан, Отменен");
        String[] step5Filters = {CREATED, ASSEMBLY, ASSEMBLY_PAUSE, PICKED,
                PICKED_PARTIALLY, ISSUED, ISSUED_PARTIALLY, CANCELLED};
        ordersPage.selectStatusFilters(step5Filters)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithStatuses(step5Filters);

        // Step 6
        step("Нажать на кнопку 'Очистить фильтры' (изображена метла), нажать 'Показать заказы'");
        ordersPage.clearFiltersAndSubmit()
                .shouldDocumentListIs(documentListFirst);
    }

    @Test(description = "C22893768 Ордерс. Фильтрация по Способу получения")
    public void testOrderFilterByDeliveryType() throws Exception {
        // Step 1
        step("Открыть страницу с Заказами");
        OrdersPage ordersPage = loginAndGoTo(OrdersPage.class);
        List<ShortOrderDocWebData> documentListFirst = ordersPage.getDocumentDataList();

        // Step 2
        step("В фильтре Способ получения выставить значение = Самовывоз, нажать кнопку 'Показать заказы'");
        ordersPage.selectDeliveryTypeFilters(PICKUP)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithDeliveryTypes(PICKUP);

        // Step 3
        step("В фильтре Способ получения выставить значение = Доставка, нажать кнопку 'Показать заказы'");
        ordersPage.clearFiltersAndSubmit()
                .selectDeliveryTypeFilters(DELIVERY_TK)
                .clickApplyFilters()
                .shouldDocumentListContainsOnlyWithDeliveryTypes(DELIVERY_TK);

        // Step 4
        step("Нажать на кнопку 'Очистить фильтры' (изображена метла), нажать 'Показать заказы'");
        ordersPage.clearFiltersAndSubmit()
                .shouldDocumentListIs(documentListFirst);
    }

    @Test
    public void t() {

        /*CartClient cartClient = apiClientProvider.getCartClient();
        OrderClient orderClient = apiClientProvider.getOrderClient();
        // Prepare request data
        CartProductOrderData productOrderData = new CartProductOrderData(
                apiClientProvider.getProducts(1).get(0));
        productOrderData.setQuantity(1.0);

        // Create
        step("Create Cart");
        Response<CartData> response = cartClient.sendRequestCreate(productOrderData);
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

        Response<OrderData> orderResp = orderClient.createOrder(reqOrderData);
        OrderData orderData = orderResp.asJson();

        String validPinCode = apiClientProvider.getValidPinCode();
        Response<JsonNode> response1 = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        if (response1.getStatusCode() == StatusCodes.ST_400_BAD_REQ) {
            validPinCode = apiClientProvider.getValidPinCode();
            response1 = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        }

        String s = "";*/

    }
}
