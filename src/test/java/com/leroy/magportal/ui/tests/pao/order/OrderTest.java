package com.leroy.magportal.ui.tests.pao.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.models.salesdoc.ShortOrderDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.customers.form.CustomerSearchForm;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.magportal.ui.pages.orders.OrderDraftContentPage;
import com.leroy.magportal.ui.pages.orders.OrderDraftDeliveryWayPage;
import com.leroy.magportal.ui.pages.orders.modal.SubmittedOrderModal;
import com.leroy.magportal.ui.tests.BasePAOTest;
import com.leroy.utils.RandomUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrderTest extends BasePAOTest {

    @Inject
    PAOHelper helper;

    @AfterClass(enabled = true)
    private void cancelConfirmedOrder() throws Exception {
        if (orderData != null && orderData.getNumber() != null && orderData.getStatus() != null) {
            OrderClient orderClient = apiClientProvider.getOrderClient();
            orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderData.getNumber(),
                    SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal());
            Response<JsonNode> resp = orderClient.cancelOrder(orderData.getNumber());
            assertThat(resp, successful());
        }
    }

    // Pages
    CartPage cartPage;
    OrderDraftDeliveryWayPage orderDraftDeliveryWayPage;
    OrderCreatedContentPage orderCreatedPage;
    CustomerSearchForm customerSearchForm;
    SubmittedOrderModal submittedOrderModal;

    SalesDocWebData orderData;

    @Test(description = "C23410896 Создать заказ из корзины с одним заказом", groups = NEED_PRODUCTS_GROUP)
    public void testCreateOrderWithOneOrder() throws Exception {
        // Prepare data
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        CartProductOrderData cartProductOrderData = new CartProductOrderData(productList.get(0));
        cartProductOrderData.setQuantity(1.0);

        String cartId = helper.createCart(cartProductOrderData).getFullDocId();

        cartPage = loginAndGoTo(CartPage.class);
        cartPage.clickDocumentInLeftMenu(cartId);

        // Step 1
        step("Нажмите на кнопку 'Оформить заказ'");
        stepClickConfirmOrderButton();

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
        ProductItemData topEmProduct = helper.getProducts(
                1, new CatalogSearchFilter().setTopEM(true)).get(0);
        CartProductOrderData cartProductOrderData = new CartProductOrderData(topEmProduct);
        cartProductOrderData.setQuantity(topEmProduct.getAvailableStock() + 1);

        String cartId = helper.createCart(cartProductOrderData).getFullDocId();

        cartPage = loginAndGoTo(CartPage.class);
        cartPage.clickDocumentInLeftMenu(cartId);

        // Step 1
        step("Нажмите на кнопку 'Оформить заказ'");
        stepClickConfirmOrderButton();

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

    // ------------ Steps ------------------ //

    /**
     * Нажмите на кнопку "Оформить заказ"
     */
    private void stepClickConfirmOrderButton() throws Exception {
        cartPage = new CartPage();
        orderData = cartPage.getSalesDocData();
        orderDraftDeliveryWayPage = cartPage.clickConfirmButton()
                .verifyRequiredElements(new OrderDraftDeliveryWayPage.PageState());
        orderDraftDeliveryWayPage.shouldOrderStatusIs(SalesDocumentsConst.States.DRAFT.getUiVal());
        orderData.setNumber(orderDraftDeliveryWayPage.getOrderNumber());
        anAssert().isFalse(orderData.getNumber().isEmpty(), "Номер заказа отсутствует");
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
        orderData.setPinCode(RandomUtil.randomPinCode(true));
        orderDraftDeliveryWayPage.enterPinCode(orderData)
                .shouldPinCodeFieldIs(orderData.getPinCode());
    }

    /**
     * Нажмите на кнопку Подтвердить заказ
     */
    private void stepClickConfirmOrder() {
        if (orderData.getDeliveryType() == null)
            orderData.setDeliveryType(SalesDocumentsConst.GiveAwayPoints.PICKUP);
        orderData.setStatus(SalesDocumentsConst.States.IN_PROGRESS.getUiVal());
        submittedOrderModal = orderDraftDeliveryWayPage.clickConfirmOrderButton()
                .verifyRequiredElements(orderData.getDeliveryType())
                .shouldPinCodeIs(orderData.getPinCode())
                .shouldNumberIs(orderData.getNumber());
    }

    /**
     * Нажмите на 'Перейти в список заказов'
     */
    private void stepGoToTheOrderList() throws Exception {
        orderCreatedPage = submittedOrderModal.clickGoToOrderListButton()
                .shouldOrderContentDataIs(orderData);
    }

    /**
     * Обновите список документов слева
     */
    private void stepRefreshDocumentListAndCheckDocument() throws Exception {
        ShortOrderDocWebData shortOrderDocWebData = orderData.getShortOrderData();
        shortOrderDocWebData.setPayType(ShortOrderDocWebData.PayType.OFFLINE);
        orderCreatedPage.refreshDocumentList();
        orderCreatedPage.shouldDocumentListContainsThis(shortOrderDocWebData);
    }

}
