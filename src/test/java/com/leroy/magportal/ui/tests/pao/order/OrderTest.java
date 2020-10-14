package com.leroy.magportal.ui.tests.pao.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.sales.DiscountConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.common_mashups.data.customer.CustomerData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateProductOrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.models.salesdoc.ShortOrderDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.pages.customers.form.CustomerSearchForm;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.magportal.ui.pages.orders.OrderDraftContentPage;
import com.leroy.magportal.ui.pages.orders.OrderDraftDeliveryWayPage;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.magportal.ui.pages.orders.modal.RemoveOrderModal;
import com.leroy.magportal.ui.pages.orders.modal.SubmittedOrderModal;
import com.leroy.magportal.ui.pages.products.form.AddProductForm;
import com.leroy.magportal.ui.tests.BasePAOTest;
import com.leroy.utils.RandomUtil;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.leroy.constants.DefectConst.*;
import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrderTest extends BasePAOTest {

    @Inject
    PAOHelper paoHelper;
    @Inject
    SearchProductHelper searchProductHelper;
    @Inject
    OrderClient orderClient;

    private void cancelConfirmedOrder() throws Exception {
        if (orderData != null && orderData.getNumber() != null && orderData.getStatus() != null &&
                !orderData.getStatus().equals(SalesDocumentsConst.States.DRAFT.getUiVal()) &&
                !orderData.getStatus().equals(SalesDocumentsConst.States.CANCELLED.getUiVal())) {
            OrderClient orderClient = apiClientProvider.getOrderClient();
            orderClient.waitUntilOrderCanBeCancelled(orderData.getNumber());
            Response<JsonNode> resp = orderClient.cancelOrder(orderData.getNumber());
            assertThat(resp, successful());
        }
    }

    // Pages
    CartPage cartPage;
    OrderDraftDeliveryWayPage orderDraftDeliveryWayPage;
    OrderCreatedContentPage orderCreatedContentPage;
    OrderDraftContentPage orderDraftContentPage;
    CustomerSearchForm customerSearchForm;
    SubmittedOrderModal submittedOrderModal;

    SalesDocWebData orderData;

    @Test(description = "C23410896 Создать заказ из корзины с одним заказом", groups = NEED_PRODUCTS_GROUP)
    public void testCreateOrderWithOneOrder() throws Exception {
        // Prepare data
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        CartProductOrderData cartProductOrderData = new CartProductOrderData(productList.get(0));
        cartProductOrderData.setQuantity(1.0);

        String cartId = paoHelper.createCart(cartProductOrderData).getFullDocId();

        cartPage = loginSelectShopAndGoTo(CartPage.class);
        cartPage.clickDocumentInLeftMenu(cartId);

        // Step 1
        step("Нажмите на кнопку 'Оформить заказ'");
        stepClickConfirmOrderButton(null);

        // Step 2
        step("Нажмите на кнопку 'Добавить клиента'");
        stepClickAddCustomerButton();

        // Step 3
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        stepSelectCustomerByPhoneNumber(customerData);

        // Step 4
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 5
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 6
        step("Нажмите на 'Перейти в список заказов'");
        stepGoToTheOrderList();

        // Step 7
        step("Обновите список документов слева");
        stepRefreshDocumentListAndCheckDocument();
    }

    @Test(description = "C23410899 Создать заказ из корзины с авторской сборкой")
    public void testCreateOrderWithAuthorAssembly() throws Exception {
        // Prepare data
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        ProductItemData topEmProduct = searchProductHelper.getProducts(
                1, new CatalogSearchFilter().setTopEM(true)).get(0);
        CartProductOrderData cartProductOrderData = new CartProductOrderData(topEmProduct);
        cartProductOrderData.setQuantity(topEmProduct.getAvailableStock() + 1);

        String cartId = paoHelper.createCart(cartProductOrderData).getFullDocId();

        cartPage = loginSelectShopAndGoTo(CartPage.class);
        cartPage.clickDocumentInLeftMenu(cartId);

        // Step 1
        step("Нажмите на кнопку 'Оформить заказ'");
        stepClickConfirmOrderButton(null);

        // Step 2
        step("Нажмите на кнопку 'Состав заказа'");
        OrderDraftContentPage orderDraftContentPage = orderDraftDeliveryWayPage.goToContentOrderTab()
                .shouldOrderContentDataIs(orderData);

        // Step 3
        step("Нажмите на кнопку 'Способ получения'");
        orderDraftContentPage.goToDeliveryTypeTab();

        // Step 4
        step("Нажмите на кнопку 'Добавить клиента'");
        stepClickAddCustomerButton();

        // Step 5
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        stepSelectCustomerByPhoneNumber(customerData);

        // Step 6
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 7
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 8
        step("Нажмите на 'Перейти в список заказов'");
        stepGoToTheOrderList();

        // Step 7
        step("Обновите список документов слева");
        stepRefreshDocumentListAndCheckDocument();
    }

    @Test(description = "C23410900 Создание заказа из корзины, преобразованной из сметы", groups = NEED_PRODUCTS_GROUP)
    public void testCreateOrderFromCartTransformedFromEstimate() throws Exception {
        step("Pre-condition: Создаем смету и преобразовываем ее в корзину");
        CustomerData customerData = paoHelper.searchForCustomer(TestDataConstants.SIMPLE_CUSTOMER_DATA_2);
        EstimateProductOrderData estimateProductOrderData = new EstimateProductOrderData(productList.get(0));
        estimateProductOrderData.setQuantity(1.0);
        String estimateId = paoHelper.createConfirmedEstimateAndGetId(estimateProductOrderData, customerData);
        EstimatePage estimatePage = loginSelectShopAndGoTo(EstimatePage.class);
        estimatePage.openPageWithEstimate(estimateId)
                .clickTransformToCart();

        // Step 1
        step("Нажмите на кнопку 'Оформить заказ'");
        stepClickConfirmOrderButton(TestDataConstants.SIMPLE_CUSTOMER_DATA_2);

        // Step 2
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 3
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 4
        step("Нажмите на 'Перейти в список заказов'");
        stepGoToTheOrderList();

        // Step 5
        step("Обновите список документов слева");
        stepRefreshDocumentListAndCheckDocument();
    }

    @Test(description = "C23410917 Создать заказ из корзины с клиентом", groups = NEED_PRODUCTS_GROUP)
    public void testCreateOrderFromCartWithClient() throws Exception {
        step("Pre-condition: Создаем корзину с клиентом");
        // Prepare data
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        CartProductOrderData cartProductOrderData = new CartProductOrderData(productList.get(0));
        cartProductOrderData.setQuantity(1.0);

        String cartId = paoHelper.createCart(cartProductOrderData).getFullDocId();

        cartPage = loginSelectShopAndGoTo(CartPage.class);
        cartPage.clickDocumentInLeftMenu(cartId);
        cartPage.clickAddCustomer()
                .selectCustomerByPhone(customerData.getPhoneNumber());

        // Step 1
        step("Нажмите на кнопку 'Оформить заказ'");
        stepClickConfirmOrderButton(customerData);

        // Step 2
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 3
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 4
        step("Нажмите на 'Перейти в список заказов'");
        stepGoToTheOrderList();

        // Step 5
        step("Обновите список документов слева");
        stepRefreshDocumentListAndCheckDocument();
    }

    @Test(description = "C23410898 Создать заказ из корзины со скидкой", groups = NEED_PRODUCTS_GROUP)
    public void testCreateOrderFromCartWithDiscount() throws Exception {
        // Prepare data
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        CartProductOrderData cartProductOrderData = new CartProductOrderData(productList.get(0));
        cartProductOrderData.setQuantity(1.0);

        String cartId = paoHelper.createCart(cartProductOrderData).getFullDocId();

        cartPage = loginSelectShopAndGoTo(CartPage.class);
        cartPage.clickDocumentInLeftMenu(cartId);
        cartPage.clickDiscountIcon(1, 1)
                .selectReasonDiscount(DiscountConst.Reasons.PRODUCT_SAMPLE.getName())
                .enterDiscountPercent(10.0)
                .clickConfirmButton();

        // Step 1
        step("Нажмите на кнопку 'Оформить заказ'");
        stepClickConfirmOrderButton(null);

        // Step 2
        step("Нажмите на кнопку 'Состав заказа'");
        OrderDraftContentPage orderDraftContentPage = orderDraftDeliveryWayPage.goToContentOrderTab()
                .shouldOrderContentDataIs(orderData);

        // Step 3
        step("Нажмите на кнопку 'Способ получения'");
        orderDraftContentPage.goToDeliveryTypeTab();

        // Step 4
        step("Нажмите на кнопку 'Добавить клиента'");
        stepClickAddCustomerButton();

        // Step 5
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        stepSelectCustomerByPhoneNumber(customerData);

        // Step 6
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 7
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 8
        step("Нажмите на 'Перейти в список заказов'");
        stepGoToTheOrderList();

        // Step 9
        step("Обновите список документов слева");
        stepRefreshDocumentListAndCheckDocument();
    }

    @Test(description = "C23410897 Создать последовательно заказы из корзины с двумя заказами")
    public void testCreateOrdersFromCartWithTwoOrders() throws Exception {
        // Prepare data
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        List<CartProductOrderData> products = paoHelper.findProductsForSeveralOrdersInCart();
        String cartId = paoHelper.createCart(products).getFullDocId();

        cartPage = loginSelectShopAndGoTo(CartPage.class);
        cartPage.clickDocumentInLeftMenu(cartId);

        cartPage = new CartPage();
        SalesDocWebData cartData = cartPage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку 'Оформить Заказ №1'");
        stepClickConfirmOrderButton(null);

        // Step 2
        step("Нажмите на кнопку 'Добавить клиента'");
        stepClickAddCustomerButton();

        // Step 3
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        stepSelectCustomerByPhoneNumber(customerData);

        // Step 4
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 5
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 6
        step("Нажмите на 'Перейти в корзину'");
        cartData.getOrders().remove(0);
        if (PRODUCT_COUNT_WHEN_TWO_ORDERS_IN_CART)
            cartData.getOrders().get(0).setProductCount(1);
        orderData.getOrders().remove(1);
        cartPage = submittedOrderModal.clickGoToCartButton()
                .shouldCartHasData(cartData);

        // Step 7
        step("Вернитесь в раздел заказы и обновите список документов слева");
        new MenuPage().goToPage(OrderHeaderPage.class);
        stepRefreshDocumentListAndCheckDocument();

        // Step 8
        step("Найдите и откройте подтвержденный заказ");
        orderCreatedContentPage.clickDocumentInLeftMenu(orderData.getNumber());
        orderCreatedContentPage = new OrderCreatedContentPage().shouldOrderContentDataIs(orderData);
    }

    // ================== EDIT ORDERS =========================//

    private void preconditionForEditOrderConfirmedTests(
            List<ProductItemData> productItemDataList, double selectedQuantity) throws Exception {
        // Prepare data
        List<CartProductOrderData> cardProducts = new ArrayList<>();
        for (ProductItemData productItemData : productItemDataList) {
            CartProductOrderData cartProductOrderData = new CartProductOrderData(productItemData);
            cartProductOrderData.setQuantity(selectedQuantity);
            cardProducts.add(cartProductOrderData);
        }

        String orderId = paoHelper.createConfirmedOrder(cardProducts, false).getOrderId();

        OrderHeaderPage orderHeaderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);
        orderClient.waitUntilOrderGetStatus(orderId,
                        SalesDocumentsConst.States.ALLOWED_FOR_PICKING, null);
        orderHeaderPage.clickDocumentInLeftMenu(orderId);

        orderCreatedContentPage = new OrderCreatedContentPage();
        orderData = orderCreatedContentPage.getOrderData();
    }

    private void preconditionForEditOrderConfirmedTests() throws Exception {
        if (productList == null)
            throw new Exception("Добавь тесту groups = NEED_PRODUCTS_GROUP");
        preconditionForEditOrderConfirmedTests(Collections.singletonList(productList.get(0)), 1.0);
    }

    private void preconditionForEditOrderDraftTests(
            List<ProductItemData> productItemDataList, boolean isNeedToGoToContentTab, boolean isExceedsAvailableStock) throws Exception {
        // Prepare data
        List<CartProductOrderData> cardProducts = new ArrayList<>();
        for (ProductItemData productItemData : productItemDataList) {
            CartProductOrderData cartProductOrderData = new CartProductOrderData(productItemData);
            cartProductOrderData.setQuantity(isExceedsAvailableStock ? productItemData.getAvailableStock() + 100 : 1.0);
            cardProducts.add(cartProductOrderData);
        }

        String cartId = paoHelper.createCart(cardProducts).getFullDocId();

        cartPage = loginSelectShopAndGoTo(CartPage.class); // TODO ???
        cartPage.clickDocumentInLeftMenu(cartId);

        stepClickConfirmOrderButton(null);

        if (isNeedToGoToContentTab) {
            orderDraftContentPage = orderDraftDeliveryWayPage.goToContentOrderTab()
                    .shouldOrderContentDataIs(orderData);
        }

    }

    private void preconditionForEditOrderDraftTests(
            List<ProductItemData> productItemDataList, boolean isNeedToGoToContentTab) throws Exception {
        preconditionForEditOrderDraftTests(productItemDataList, isNeedToGoToContentTab, false);
    }

    private void preconditionForEditOrderDraftTestsExceedsAvailableStock(
            List<ProductItemData> productItemDataList, boolean isNeedToGoToContentTab) throws Exception {
        preconditionForEditOrderDraftTests(productItemDataList, isNeedToGoToContentTab, true);
    }

    private void preconditionForEditOrderDraftTests() throws Exception {
        preconditionForEditOrderDraftTests(Collections.singletonList(productList.get(0)), true, false);
    }

    // ---------------- EDIT ORDER DRAFT -------------------//

    @Test(description = "C23410901 Добавить товар в неподтвержденный заказ (количества товара достаточно)",
            groups = NEED_PRODUCTS_GROUP)
    public void testAddProductInDraftOrderWithSufficientProductQuantity() throws Exception {
        ProductItemData newProduct = productList.get(1);
        preconditionForEditOrderDraftTests();

        // Step 1
        step("Введите ЛМ товара в поле 'Добавление товара' и нажмите Enter");
        orderDraftContentPage.getAddProductForm().enterTextInSearchProductField(newProduct.getLmCode());
        orderDraftContentPage.shouldProductsHave(Arrays.asList(productList.get(0).getLmCode(),
                newProduct.getLmCode()));
    }

    @Test(description = "C23410902 Добавить товар в неподтвержденный заказ (количества товара недостаточно)",
            groups = NEED_PRODUCTS_GROUP)
    public void testAddProductInDraftOrderWithInsufficientProductQuantity() throws Exception {
        ProductItemData productItemData = productList.get(0);
        preconditionForEditOrderDraftTests();

        // Step 1
        step("Измените кол-во товара таким образом, чтоб его было заказно, больше чем доступно");
        int newQuantity = (int) Math.round(productItemData.getAvailableStock() + 100);
        OrderWebData oneOrderData = orderData.getOrders().get(0);
        oneOrderData.changeProductQuantity(0, newQuantity, true);
        oneOrderData.setTotalWeight(null);
        if (INVISIBLE_AUTHOR_ORDER_DRAFT)
            orderData.setAuthorName(null);
        orderDraftContentPage.editProductQuantity(1, newQuantity)
                .shouldOrderContentDataIs(orderData)
                .shouldProductIsDangerHighlighted(1);
    }

    @Test(description = "C23410904 Добавить Топ ЕМ или AVS товар в неподтвержденный заказ",
            groups = NEED_PRODUCTS_GROUP)
    public void testAddTopEmOrAvsInDraftOrder() throws Exception {
        ProductItemData newProduct = searchProductHelper.getProducts(
                1, new CatalogSearchFilter().setTopEM(true)).get(0);
        preconditionForEditOrderDraftTests();

        // Step 1
        step("Введите ЛМ товара в поле 'Добавление товара' и нажмите Enter");
        orderDraftContentPage.getAddProductForm().enterTextInSearchProductField(newProduct.getLmCode());
        orderDraftContentPage.shouldProductsHave(Arrays.asList(productList.get(0).getLmCode(),
                newProduct.getLmCode()));
    }

    @Test(description = "C23410903 Изменить количество товара в неподтвержденном заказе",
            groups = NEED_PRODUCTS_GROUP)
    public void testEditProductQuantityInDraftOrder() throws Exception {
        preconditionForEditOrderDraftTests();

        // Step 1
        step("В мини-карточке товара в поле 'заказано' измените количество товара");
        int newQuantity = (int) Math.round(
                orderData.getOrders().get(0).getProductCardDataList().get(0).getSelectedQuantity()) + 2;
        OrderWebData oneOrderData = orderData.getOrders().get(0);
        oneOrderData.changeProductQuantity(0, newQuantity, true);
        if (INVISIBLE_AUTHOR_ORDER_DRAFT)
            orderData.setAuthorName(null);
        orderDraftContentPage.editProductQuantity(1, newQuantity)
                .shouldOrderContentDataIs(orderData);
    }

    @Test(description = "C23410905 Удалить товар из неподтвержденного заказа",
            groups = NEED_PRODUCTS_GROUP)
    public void testRemoveProductFromDraftOrder() throws Exception {
        preconditionForEditOrderDraftTests(productList.subList(0, 2), true);

        // Step 1 and 2
        step("Нажмите на иконку корзины в правом верхнем углу мини-карточки товара и подтвердите удаление");
        OrderWebData oneOrderData = orderData.getOrders().get(0);
        oneOrderData.removeProduct(0, true);
        if (INVISIBLE_AUTHOR_ORDER_DRAFT)
            orderData.setAuthorName(null);
        orderDraftContentPage.removeProduct(1)
                .shouldOrderContentDataIs(orderData);
    }

    @Test(description = "C23410914 Удалить последний товар из неподтвержденного заказа", groups = NEED_PRODUCTS_GROUP)
    public void testRemoveLastProductFromDraftOrder() throws Exception {
        preconditionForEditOrderDraftTests();

        // Step 1 and 2
        step("Нажмите на иконку корзины в правом верхнем углу мини-карточки товара и подтвердите удаление");
        orderData = orderDraftContentPage.getOrderData();
        orderDraftContentPage.removeProduct(1)
                .shouldNoOneDocumentIsSelected();

        // Step 3
        step("Обновите список заказов слева");
        orderData.setStatus(SalesDocumentsConst.States.CANCELLED.getUiVal());
        orderData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        stepRefreshDocumentListAndCheckDocument();
    }

    @Test(description = "C23410912 Удалить неподтвержденный заказ", groups = NEED_PRODUCTS_GROUP)
    public void testRemoveOrderDraft() throws Exception {
        preconditionForEditOrderDraftTests();

        // Step 1 and 2
        step("Нажмите на иконку корзины в правом верхнем углу мини-карточки товара и подтвердите удаление");
        orderData = orderDraftContentPage.getOrderData();
        orderDraftContentPage.removeOrder()
                .shouldNoOneDocumentIsSelected();

        // Step 3
        step("Обновите список заказов слева");
        orderData.setStatus(SalesDocumentsConst.States.CANCELLED.getUiVal());
        orderData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        stepRefreshDocumentListAndCheckDocument();
    }

    /// -------- EDIT CONFIRMED ORDER TESTS --------------- ///

    @Test(description = "C23410907 Добавить товар в подтвержденный закакз", groups = NEED_PRODUCTS_GROUP)
    public void testAddProductInConfirmedOrder() throws Exception {
        ProductItemData newProductItem = productList.get(1);
        List<String> expectedProductLmCodes = Arrays.asList(productList.get(0).getLmCode(),
                newProductItem.getLmCode());
        preconditionForEditOrderConfirmedTests();

        // Step 1
        step("Нажмите на иконку редактирования заказа в левом нижнем углу");
        orderCreatedContentPage.clickEditOrderButton();
        AddProductForm addProductForm = orderCreatedContentPage.getAddProductForm();
        addProductForm.shouldSearchFieldIsVisible();

        // Step 2
        step("Введите ЛМ товара в поле 'Добавление товара' и нажмите Enter");
        addProductForm.enterTextInSearchProductField(newProductItem.getLmCode());
        orderCreatedContentPage.shouldProductsHave(expectedProductLmCodes, false);

        // Step 3
        step("Нажмите на кнопку 'Сохранить'");
        orderCreatedContentPage.clickSaveOrderButton(false);
        orderCreatedContentPage.shouldProductsHave(expectedProductLmCodes, true);
    }

    @Test(description = "C23410908 Изменить количество товара в подтвержденном заказе", groups = NEED_PRODUCTS_GROUP)
    public void testChangeProductQuantityInConfirmedOrder() throws Exception {
        preconditionForEditOrderConfirmedTests(Collections.singletonList(productList.get(0)), 2.0);

        // Step 1
        step("Нажмите на иконку редактирования заказа в левом нижнем углу");
        orderCreatedContentPage.clickEditOrderButton();
        orderCreatedContentPage.shouldSelectedProductQuantityIs(1, 2);
        AddProductForm addProductForm = orderCreatedContentPage.getAddProductForm();
        addProductForm.shouldSearchFieldIsVisible();

        // Step 2
        step("Измените количество товара плашкой");
        orderData.getOrders().get(0).changeProductQuantity(0, 1, true);
        orderCreatedContentPage.editSelectedQuantity(1, 1)
                .shouldSelectedProductQuantityIs(1, 1);

        // Step 3
        step("Нажмите на кнопку 'Сохранить'");
        orderCreatedContentPage.clickSaveOrderButton(false);
        orderCreatedContentPage.shouldOrderContentDataIs(orderData);
    }

    @Test(description = "C23410909 Удалить товар из подтвержденного заказа", groups = NEED_PRODUCTS_GROUP)
    public void testRemoveProductFromConfirmedOrder() throws Exception {
        preconditionForEditOrderConfirmedTests(productList.subList(0, 2), 1.0);

        // Step 1
        step("Нажмите на иконку редактирования заказа в левом нижнем углу");
        orderCreatedContentPage.clickEditOrderButton();
        AddProductForm addProductForm = orderCreatedContentPage.getAddProductForm();
        addProductForm.shouldSearchFieldIsVisible();

        // Step 2
        step("Измените количество товара плашкой до значения 0");
        orderData.getOrders().get(0).changeProductQuantity(0, 0, true);
        orderCreatedContentPage.editSelectedQuantity(1, 0)
                .shouldSelectedProductQuantityIs(1, 0);

        // Step 3
        step("Нажмите на кнопку 'Сохранить'");
        orderCreatedContentPage.clickSaveOrderButton(false);
        orderCreatedContentPage.shouldOrderContentDataIs(orderData);
    }

    @Test(description = "C23410915 Удалить последний товар из подтвержденного заказа", groups = NEED_PRODUCTS_GROUP)
    public void testLastRemoveProductFromConfirmedOrder() throws Exception {
        preconditionForEditOrderConfirmedTests();

        // Step 1
        step("Нажмите на иконку редактирования заказа в левом нижнем углу");
        orderCreatedContentPage.clickEditOrderButton();
        AddProductForm addProductForm = orderCreatedContentPage.getAddProductForm();
        addProductForm.shouldSearchFieldIsVisible();

        // Step 2
        step("Измените количество товара плашкой до значения 0");
        orderCreatedContentPage.editSelectedQuantity(1, 0)
                .shouldSelectedProductQuantityIs(1, 0);

        // Step 3
        step("Нажмите на кнопку 'Сохранить'");
        orderCreatedContentPage.clickSaveOrderButton(true);

        // Step 4
        step("Нажмите 'да' в модальном окне");
        RemoveOrderModal removeOrderModal = new RemoveOrderModal();
        removeOrderModal.clickYesButton();
        orderData.setStatus(SalesDocumentsConst.States.CANCELLED.getUiVal());
        orderCreatedContentPage.shouldOrderContentDataIs(orderData)
                .checkEditButtonVisibility(false);

        // Step 5
        step("Обновите список заказов слева");
        ShortOrderDocWebData shortOrderDocWebData = orderData.getShortOrderData();
        shortOrderDocWebData.setTotalPrice(0.0);
        orderCreatedContentPage.refreshDocumentList();
        orderCreatedContentPage.shouldDocumentListContainsThis(shortOrderDocWebData);
    }

    @Test(description = "C23410913 Отменить подтвержденный заказ", groups = NEED_PRODUCTS_GROUP)
    public void testCancelConfirmedOrder() throws Exception {
        preconditionForEditOrderConfirmedTests();

        // Step 1
        step("В правом верхнем углу нажмите на кнопку удаления заказа");
        RemoveOrderModal removeOrderModal = orderCreatedContentPage.clickRemoveOrderButton();

        // Step 2
        step("Нажмите 'да' в модальном окне");
        removeOrderModal.clickYesButton();
        orderData.setStatus(SalesDocumentsConst.States.CANCELLED.getUiVal());
        orderCreatedContentPage.shouldOrderContentDataIs(orderData)
                .checkEditButtonVisibility(false);

        // Step 3
        step("Обновите список заказов слева");
        ShortOrderDocWebData shortOrderDocWebData = orderData.getShortOrderData();
        shortOrderDocWebData.setTotalPrice(0.0);
        orderCreatedContentPage.refreshDocumentList();
        orderCreatedContentPage.shouldDocumentListContainsThis(shortOrderDocWebData);
    }

    @Test(description = "C23398451 Создание заказа с существующим пин кодом", groups = NEED_PRODUCTS_GROUP)
    public void testCreateOrderWithExistedPinCode() throws Exception {
        String toolTypeText = "Уже используется, придумай другой код";
        String existedPinCode = "11111";
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        step("Выполнение preconditions");
        preconditionForEditOrderDraftTests(Collections.singletonList(productList.get(0)), false);

        CustomerSearchForm customerSearchForm2 = orderDraftDeliveryWayPage.getCustomerSearchForm();
        customerSearchForm2.clickAddCustomer();
        customerSearchForm2.selectCustomerByPhone(customerData.getPhoneNumber());

        // Step 1
        step("Введите существующий PIN-код, например 11111 для самовывоза или 99999 для доставки");
        orderDraftDeliveryWayPage.enterPinCode(existedPinCode)
                .shouldPinCodeErrorTooltipIs(toolTypeText);

        //Step 2
        step("Нажмите на кнопку Подтвердить заказ");
        orderDraftDeliveryWayPage.clickConfirmOrderButtonNegativePath()
                .shouldPinCodeErrorTooltipIs(toolTypeText);
    }

    @Test(description = "C23398448 Смена типа получения товара при заполненном пинкоде в неподтвержденном заказе",
            groups = NEED_PRODUCTS_GROUP)
    public void testChangeOfReceiptType() throws Exception {
        SalesDocumentsConst.GiveAwayPoints deliveryWay = SalesDocumentsConst.GiveAwayPoints.DELIVERY;
        SalesDocumentsConst.GiveAwayPoints pickupWay = SalesDocumentsConst.GiveAwayPoints.PICKUP;
        preconditionForEditOrderDraftTests(Collections.singletonList(productList.get(0)), false);
        stepEnterPinCode();

        // Step 1
        step("В поле Выбери способ получения измените Самовывоз (по умолчанию) на Доставка или наоборот");
        orderDraftDeliveryWayPage.selectDeliveryWay(deliveryWay);
        orderData.setDeliveryType(deliveryWay);
        orderData.setPinCode("");
        orderDraftDeliveryWayPage.shouldOrderDataIs(orderData);
        orderDraftDeliveryWayPage.shouldPinCodeFieldIs("");

        // Step 2
        step("Введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 3
        step("В поле Выбери способ получения измените Доставка на Самовывоз (по умолчанию) или наоборот");
        orderDraftDeliveryWayPage.selectDeliveryWay(pickupWay);
        orderData.setDeliveryType(pickupWay);
        orderData.setPinCode("");
        orderDraftDeliveryWayPage.shouldOrderDataIs(orderData);
        orderDraftDeliveryWayPage.shouldPinCodeFieldIs("");
    }


    // ======== Подтверждение заказа ============= //

    @Test(description = "C23410892 Подтвердить заказ на самовывоз сегодня", groups = NEED_PRODUCTS_GROUP)
    public void testConfirmOrderPickupToday() throws Exception {
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        preconditionForEditOrderDraftTests(Collections.singletonList(productList.get(0)), false);

        // Step 1
        step("В поле Выбери способ получения нажмите на кнопу Самовывоз (по умолчанию)");
        orderData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        orderData.setDeliveryDate(LocalDate.now());
        orderDraftDeliveryWayPage.shouldOrderDataIs(orderData);

        // Step 2
        step("Нажмите на кнопку 'Добавить клиента'");
        stepClickAddCustomerButton();

        // Step 3
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        stepSelectCustomerByPhoneNumber(customerData);

        // Step 4
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 5
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 6
        step("Нажмите на 'Перейти в список заказов'");
        stepGoToTheOrderList();

        // Step 7
        step("Обновите список документов слева");
        stepRefreshDocumentListAndCheckDocument();
    }

    @Test(description = "C23410893 Подтвердить заказ на доставку завтра", groups = NEED_PRODUCTS_GROUP)
    public void testConfirmOrderForDeliveryTomorrow() throws Exception {
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        SalesDocumentsConst.GiveAwayPoints deliveryWay = SalesDocumentsConst.GiveAwayPoints.DELIVERY;
        preconditionForEditOrderDraftTests(Collections.singletonList(productList.get(0)), false);

        // Step 1
        step("В поле Выбери способ получения нажмите на кнопу Доставка");
        orderDraftDeliveryWayPage.selectDeliveryWay(deliveryWay);
        orderData.setDeliveryType(deliveryWay);
        orderData.setPinCode("");
        orderData.setClient(new SimpleCustomerData());
        orderData.setRecipient(new SimpleCustomerData());
        orderData.setComment("");
        orderData.setDeliveryDate(LocalDate.now().plusDays(1));
        orderDraftDeliveryWayPage.shouldOrderDataIs(orderData);

        // Step 2
        step("Нажмите на кнопку 'Добавить клиента'");
        stepClickAddCustomerButton();

        // Step 3
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        stepSelectCustomerByPhoneNumber(customerData);

        // Step 4
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 5
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 6
        step("Нажмите на 'Перейти в список заказов'");
        stepGoToTheOrderList();

        // Step 7
        step("Обновите список документов слева");
        stepRefreshDocumentListAndCheckDocument();
    }

    @Test(description = "C23410894 Подтвердить заказ на самовывоз через 14 дней", groups = NEED_PRODUCTS_GROUP)
    public void testConfirmOrderPickupIn14Days() throws Exception {
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        preconditionForEditOrderDraftTestsExceedsAvailableStock(Collections.singletonList(productList.get(0)), false);

        // Step 1
        step("В поле Выбери способ получения нажмите на кнопу Самовывоз (по умолчанию)");
        orderData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        orderData.setDeliveryDate(LocalDate.now().plusDays(14));
        orderDraftDeliveryWayPage.shouldOrderDataIs(orderData);

        // Step 2
        step("Нажмите на кнопку 'Добавить клиента'");
        stepClickAddCustomerButton();

        // Step 3
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        stepSelectCustomerByPhoneNumber(customerData);

        // Step 4
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 5
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 6
        step("Нажмите на 'Перейти в список заказов'");
        stepGoToTheOrderList();

        // Step 7
        step("Обновите список документов слева");
        stepRefreshDocumentListAndCheckDocument();

    }

    @Test(description = "C23410895 Подтвердить заказ на доставку через 15 дней", groups = NEED_PRODUCTS_GROUP)
    public void testConfirmOrderForDeliveryIn15days() throws Exception {
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        SalesDocumentsConst.GiveAwayPoints deliveryWay = SalesDocumentsConst.GiveAwayPoints.DELIVERY;
        preconditionForEditOrderDraftTestsExceedsAvailableStock(Collections.singletonList(productList.get(0)), false);

        // Step 1
        step("В поле Выбери способ получения нажмите на кнопу Доставка");
        orderDraftDeliveryWayPage.selectDeliveryWay(deliveryWay);
        orderData.setDeliveryType(deliveryWay);
        orderData.setPinCode("");
        orderData.setClient(new SimpleCustomerData());
        orderData.setRecipient(new SimpleCustomerData());
        orderData.setComment("");
        orderData.setDeliveryDate(LocalDate.now().plusDays(15));
        orderDraftDeliveryWayPage.shouldOrderDataIs(orderData);

        // Step 2
        step("Нажмите на кнопку 'Добавить клиента'");
        stepClickAddCustomerButton();

        // Step 3
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        stepSelectCustomerByPhoneNumber(customerData);

        // Step 4
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        stepEnterPinCode();

        // Step 5
        step("Нажмите на кнопку Подтвердить заказ");
        stepClickConfirmOrder();

        // Step 6
        step("Нажмите на 'Перейти в список заказов'");
        stepGoToTheOrderList();

        // Step 7
        step("Обновите список документов слева");
        stepRefreshDocumentListAndCheckDocument();
    }

    // ------------ Steps ------------------ //

    /**
     * Нажмите на кнопку "Оформить заказ"
     */
    private void stepClickConfirmOrderButton(SimpleCustomerData customerData) throws Exception {
        cartPage = new CartPage()
                .waitForProductsAreLoaded();
        orderData = cartPage.getSalesDocData();
        anAssert().isTrue(orderData.getOrders().size() > 0,
                "Не удалось получить со страницы информацию о товарах в корзине");
        orderDraftDeliveryWayPage = cartPage.clickConfirmButton()
                .verifyRequiredElements(new OrderDraftDeliveryWayPage.PageState());
        orderDraftDeliveryWayPage.shouldOrderStatusIs(SalesDocumentsConst.States.DRAFT.getUiVal());
        orderData.setNumber(orderDraftDeliveryWayPage.getOrderNumber());
        orderData.setStatus(SalesDocumentsConst.States.DRAFT.getUiVal());
        if (orderData.getDeliveryType() == null)
            orderData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        anAssert().isFalse(orderData.getNumber().isEmpty(), "Номер заказа отсутствует");
        if (customerData != null) {
            orderDraftDeliveryWayPage.shouldReceiverIs(customerData)
                    .getCustomerSearchForm().shouldSelectedCustomerIs(customerData);
        } else {
            anAssert().isFalse(orderDraftDeliveryWayPage.getCustomerSearchForm().isCustomerSelected(),
                    "Клиент не должен быть выбран");
        }
    }

    /**
     * Нажмите на кнопку 'Добавить клиента'
     */
    private void stepClickAddCustomerButton() {
        customerSearchForm = orderDraftDeliveryWayPage.getCustomerSearchForm()
                .clickAddCustomer()
                .shouldAddingNewUserAvailable();
    }

    /**
     * Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента
     */
    private void stepSelectCustomerByPhoneNumber(SimpleCustomerData customerData) throws Exception {
        orderData.setClient(customerData);
        customerSearchForm.selectCustomerByPhone(customerData.getPhoneNumber())
                .shouldSelectedCustomerIs(customerData);
    }

    /**
     * Выберете поле PIN-код для оплаты, введите PIN-код для оплаты
     */
    private void stepEnterPinCode() {
        orderData.setPinCode(RandomUtil.randomPinCode(orderData.getDeliveryType()
                .equals(SalesDocumentsConst.GiveAwayPoints.PICKUP)));
        orderDraftDeliveryWayPage.enterPinCode(orderData)
                .shouldPinCodeFieldIs(orderData.getPinCode());
    }

    /**
     * Нажмите на кнопку Подтвердить заказ
     */
    private void stepClickConfirmOrder() {
        if (orderData.getDeliveryType() == null)
            orderData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        submittedOrderModal = orderDraftDeliveryWayPage.clickConfirmOrderButton()
                .verifyRequiredElements(orderData.getDeliveryType())
                .shouldPinCodeIs(orderData.getPinCode())
                .shouldNumberIs(orderData.getNumber());
        orderData.setStatus(SalesDocumentsConst.States.IN_PROGRESS.getUiVal());
        orderData.setAlternativeStatus(SalesDocumentsConst.States.CONFIRMED.getUiVal());
    }

    /**
     * Нажмите на 'Перейти в список заказов'
     */
    private void stepGoToTheOrderList() throws Exception {
        orderCreatedContentPage = submittedOrderModal.clickGoToOrderListButton()
                .shouldOrderContentDataIs(orderData);
    }

    /**
     * Обновите список документов слева
     */
    private void stepRefreshDocumentListAndCheckDocument() throws Exception {
        if (orderCreatedContentPage == null)
            orderCreatedContentPage = new OrderCreatedContentPage();
        ShortOrderDocWebData shortOrderDocWebData = orderData.getShortOrderData();
        shortOrderDocWebData.setPayType(ShortOrderDocWebData.PayType.OFFLINE);
        orderCreatedContentPage.refreshDocumentList();
        orderCreatedContentPage.shouldDocumentListContainsThis(shortOrderDocWebData);
    }

}
