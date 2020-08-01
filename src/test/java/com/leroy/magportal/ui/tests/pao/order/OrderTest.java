package com.leroy.magportal.ui.tests.pao.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
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
import com.leroy.magportal.ui.pages.orders.OrderDraftDeliveryWayPage;
import com.leroy.magportal.ui.pages.orders.modal.SubmittedOrderModal;
import com.leroy.magportal.ui.tests.BasePAOTest;
import com.leroy.utils.RandomUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrderTest extends BasePAOTest {

    @Inject
    PAOHelper helper;

    String orderId;

    @AfterClass(enabled = true)
    private void cancelConfirmedOrder() throws Exception {
        if (orderId != null) {
            OrderClient orderClient = apiClientProvider.getOrderClient();
            orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderId,
                    SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal());
            Response<JsonNode> resp = orderClient.cancelOrder(orderId);
            assertThat(resp, successful());
        }
    }

    @BeforeClass
    private void searchProducts() {
        productList = helper.getProducts(1);
    }

    @Test(description = "C23410896 Создать заказ из корзины с одним заказом")
    public void testCreateOrderWithOneOrder() throws Exception {
        // Prepare data
        SalesDocumentsConst.GiveAwayPoints giveAwayPoints = SalesDocumentsConst.GiveAwayPoints.PICKUP;

        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        CartProductOrderData cartProductOrderData = new CartProductOrderData(productList.get(0));
        cartProductOrderData.setQuantity(1.0);

        String cartId = helper.createCart(cartProductOrderData).getFullDocId();

        CartPage cartPage = loginAndGoTo(CartPage.class);
        cartPage.clickDocumentInLeftMenu(cartId);

        // Step 1
        step("Нажмите на кнопку 'Оформить заказ'");
        cartPage = new CartPage();
        SalesDocWebData orderData = cartPage.getSalesDocData();
        OrderDraftDeliveryWayPage orderDraftPage = cartPage.clickConfirmButton()
                .verifyRequiredElements(new OrderDraftDeliveryWayPage.PageState());
        orderDraftPage.shouldOrderStatusIs(SalesDocumentsConst.States.DRAFT.getUiVal());

        // Step 2
        step("Нажмите на кнопку 'Добавить клиента'");
        CustomerSearchForm customerSearchForm = orderDraftPage.getCustomerSearchForm()
                .clickAddCustomer()
                .shouldAddingNewUserAvailable();

        // Step 3
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        customerSearchForm.selectCustomerByPhone(customerData.getPhoneNumber())
                .shouldSelectedCustomerIs(customerData);

        // Step 4
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты");
        orderData.setPinCode(RandomUtil.randomPinCode(true));
        orderDraftPage.enterPinCode(orderData)
                .shouldPinCodeFieldIs(orderData.getPinCode());

        // Step 5
        step("Нажмите на кнопку Подтвердить заказ");
        SubmittedOrderModal submittedOrderModal = orderDraftPage.clickConfirmOrderButton()
                .verifyRequiredElements(giveAwayPoints);

        orderId = submittedOrderModal.getDocumentNumber();
        orderData.setNumber(orderId);

        // Step 6
        step("Нажмите на 'Перейти в список заказов'");
        orderData.setStatus(SalesDocumentsConst.States.IN_PROGRESS.getUiVal());
        OrderCreatedContentPage orderCreatedPage = submittedOrderModal.clickGoToOrderListButton()
                .shouldOrderContentDataIs(orderData);

        // Step 7
        step("Обновите список документов слева");
        ShortOrderDocWebData shortOrderDocWebData = orderData.getShortOrderData();
        orderCreatedPage.refreshDocumentList();
        orderCreatedPage.shouldDocumentListContainsThis(shortOrderDocWebData);
    }

}
