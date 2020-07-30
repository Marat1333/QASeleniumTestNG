package com.leroy.magportal.ui.tests.pao.order;

import com.google.inject.Inject;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.customers.form.CustomerSearchForm;
import com.leroy.magportal.ui.pages.orders.OrderDraftPage;
import com.leroy.magportal.ui.tests.BasePAOTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OrderTest extends BasePAOTest {

    @Inject
    PAOHelper helper;

    @BeforeClass
    private void searchProducts() {
        productList = helper.getProducts(1);
    }

    @Test(description = "C23410896 Создать заказ из корзины с одним заказом")
    public void testCreateOrderWithOneOrder() throws Exception {
        // Prepare data
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        CartProductOrderData cartProductOrderData = new CartProductOrderData(productList.get(0));
        cartProductOrderData.setQuantity(1.0);

        String cartId = helper.createCart(cartProductOrderData).getFullDocId();

        CartPage cartPage = loginAndGoTo(CartPage.class);
        cartPage.clickDocumentInLeftMenu(cartId);

        // Step 1
        step("Нажмите на кнопку 'Оформить заказ'");
        cartPage = new CartPage();
        OrderDraftPage orderDraftPage = cartPage.clickConfirmButton();

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
        step("Выберете поле PIN-код для оплаты, введите PIN-код для оплаты (код начинается не с 9 для Самовывоз и с 9 для Доставка)");

    }

}
