package com.leroy.magmobile.ui.tests.sales;

import com.leroy.magmobile.ui.models.sales.OrderAppData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.*;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import java.util.List;

public class CartTest extends SalesBaseTest {

    private void startFromScreenWithCreatedCart() throws Exception {
        startFromScreenWithCreatedCart(null, false);
    }

    private void startFromScreenWithCreatedCart(boolean hasDiscount) throws Exception {
        startFromScreenWithCreatedCart(null, hasDiscount);
    }

    @Step("Pre-condition: Создание корзины")
    private void startFromScreenWithCreatedCart(List<String> lmCodes, boolean hasDiscount) throws Exception {
        if (!Cart35Page.isThisPage()) {
            String cartDocNumber = createDraftCart(lmCodes, hasDiscount);
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    cartDocNumber);
        }
    }

    @Test(description = "C22797089 Создать корзину с экрана Документы продажи")
    public void testCreateBasketFromSalesDocumentsScreen() throws Exception {
        // Test data
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions();
        // Pre-condition
        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);

        // Step 1
        step("Нажать кнопку Оформить продажу");
        SaleTypeModalPage modalPage = mainSalesDocumentsPage.clickCreateSalesDocumentButton();
        modalPage.verifyRequiredElementsWhenFromSalesDocuments();

        // Step 2
        step("Выбрать параметр Корзина");
        Cart35Page cart35Page = modalPage.clickBasketMenuItem();
        cart35Page.verifyRequiredElements(Cart35Page.PageState.builder()
                .productIsAdded(false)
                .build());

        // Step 3
        step("Нажмите на кнопку +товары и услуги");
        SearchProductPage searchProductPage = cart35Page.clickAddProductButton()
                .verifyRequiredElements();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page<Cart35Page> addProduct35Page = new AddProduct35Page<>(Cart35Page.class);
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);

        // Step 4
        step("Нажмите на Добавить в корзину");
        cart35Page = addProduct35Page.clickAddIntoBasketButton();
        cart35Page.verifyRequiredElements(Cart35Page.PageState.builder()
                .productIsAdded(true)
                .build());
    }

    @Test(description = "C22797090 Добавить новый товар в корзину", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testAddNewProductIntoBasket() throws Exception {
        // Test data
        String lmCode = apiClientProvider.getProductLmCodes(1).get(0);

        step("Pre-condition: Создание корзины");
        startFromScreenWithCreatedCart();

        Cart35Page cart35Page = new Cart35Page();
        int productCountInBasket = cart35Page.getCountOfProductCards();
        // Step 1
        step("Нажмите на кнопку +Товар");
        SearchProductPage searchProductPage = cart35Page.clickAddProductButton()
                .verifyRequiredElements();

        // Step 2
        step("Введите ЛМ код товара или название товара или отсканируйте товар");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page<Cart35Page> addProduct35Page = new AddProduct35Page<>(Cart35Page.class)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);
        ProductOrderCardAppData expectedOrderCardData = addProduct35Page.getProductOrderDataFromPage();

        // Step 3
        step("Нажмите на Добавить в корзину");
        cart35Page = addProduct35Page.clickAddIntoBasketButton();
        cart35Page.verifyRequiredElements(Cart35Page.PageState.builder()
                .productIsAdded(true)
                .manyOrders(null)
                .build());
        cart35Page.shouldCountOfCardsIs(productCountInBasket + 1);
        cart35Page.shouldProductCardDataWithTextIs(expectedOrderCardData.getLmCode(),
                expectedOrderCardData);
    }

    @Test(description = "C22797092 Изменить количество товара (товар остается в том же заказе)",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeQuantityProductInCartWhenOneOrder() throws Exception {
        // Test data
        List<String> lmCodes = apiClientProvider.getProductLmCodes(1);

        step("Pre-condition: Создание корзины");
        startFromScreenWithCreatedCart(lmCodes, false);

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();
        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModal modalPage = cart35Page.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        step("Выберите параметр Изменить количество");
        EditProduct35Page<Cart35Page> editProduct35Page = modalPage.clickChangeQuantityMenuItem();
        editProduct35Page.verifyRequiredElements();

        // Step 3, 4, 5
        step("Измените количество товара");
        int newQuantity = 2;
        editProduct35Page.enterQuantityOfProduct(newQuantity, true);

        // Step 6
        step("Нажмите на кнопку Сохранить");
        OrderAppData order = salesDocumentData.getOrderAppDataList().get(0);
        order.changeProductQuantity(0, newQuantity);
        order.setTotalWeight(order.getTotalWeight() * newQuantity);
        cart35Page = editProduct35Page.clickSaveButton();
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    @Test(description = "C22797098 Удалить товар из корзины", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testRemoveProductFromCart() throws Exception {
        if (!Cart35Page.isThisPage()) {
            String cartDocId = createDraftCart(2);
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    cartDocId);
        }

        Cart35Page cart35Page = new Cart35Page();
        ProductOrderCardAppData productOrderCardAppDataBefore = cart35Page.getProductCardDataByIndex(1);

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModal modalPage = cart35Page.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        step("Выберите параметр Удалить товар");
        modalPage.clickRemoveProductMenuItem();
        ConfirmRemovingProductModal confirmRemovingProductModal = new ConfirmRemovingProductModal()
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на Удалить");
        confirmRemovingProductModal.clickConfirmButton();
        cart35Page = new Cart35Page();
        cart35Page.verifyRequiredElements(Cart35Page.PageState.builder()
                .productIsAdded(true)
                .manyOrders(false)
                .build());
        cart35Page.shouldProductBeNotPresentInCart(
                productOrderCardAppDataBefore.getLmCode());
    }

    @Test(description = "C22797099 Удалить последний товар из корзины",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testRemoveTheLastProductFromCart() throws Exception {
        String cartDocNumber = null;
        if (!Cart35Page.isThisPage()) {
            cartDocNumber = createDraftCart(1);
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    cartDocNumber);
        }

        Cart35Page cart35Page = new Cart35Page();
        // if (cartDocId == null) TODO если будет тест запускаться в цепочке

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModal modalPage = cart35Page.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        step("Выберите параметр Удалить товар");
        modalPage.clickRemoveProductMenuItem();
        ConfirmRemoveLastProductCartModal confirmRemovingProductModal = new ConfirmRemoveLastProductCartModal()
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на Выйти");
        confirmRemovingProductModal.clickConfirmButton();
        SalesDocumentsPage salesDocumentsPage = new SalesDocumentsPage();
        salesDocumentsPage.shouldSalesDocumentIsNotPresent(cartDocNumber);
    }

    @Test(description = "C22797109 Добавить существующий товар из модалки действий с товаром",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testAddProductFromActionWithProductModal() throws Exception {
        int newQuantity = 3;

        startFromScreenWithCreatedCart();

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();
        OrderAppData orderAppData = salesDocumentData.getOrderAppDataList().get(0);
        orderAppData.addFirstProduct(orderAppData.getProductCardDataList().get(0).copy());
        orderAppData.changeProductQuantity(0, newQuantity);
        orderAppData.setTotalWeight(orderAppData.getTotalWeight() * 4);
        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModal modalPage = cart35Page.clickCardByIndex(1);
        modalPage.verifyRequiredElements();

        // Step 2
        step("Выберите параметр Добавить товар еще раз");
        AddProduct35Page<Cart35Page> addProduct35Page = modalPage.clickAddProductAgainMenuItem()
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);

        // Step 3, 4, 5
        step("Измените количество товара");
        addProduct35Page.enterQuantityOfProduct(newQuantity, true);

        // Step 6
        step("Нажмите на кнопку Добавить в корзину");
        cart35Page = addProduct35Page.clickAddIntoBasketButton();
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    @Test(description = "C22797101 Создать скидку", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testCreateDiscount() throws Exception {
        startFromScreenWithCreatedCart();

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();
        double productTotalPrice = salesDocumentData.getOrderAppDataList().get(0)
                .getProductCardDataList().get(0).getTotalPrice();

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModal modalPage = cart35Page.clickCardByIndex(1);
        modalPage.verifyRequiredElements();

        // Step 2
        step("Выберите параметр Создать скидку");
        DiscountPage discountPage = modalPage.clickCreateDiscountMenuItem()
                .shouldProductTotalPriceBeforeIs(productTotalPrice)
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на Причина скидки");
        DiscountReasonModal discountReasonModal = discountPage.clickDiscountReasonFld()
                .verifyRequiredElements();

        // Step 4
        step("Выбираем причину скидки");
        String selectedReason = DiscountReasonModal.PRODUCT_SAMPLE_REASON;
        discountPage = discountReasonModal.selectDiscountReason(selectedReason)
                .shouldDiscountReasonIs(selectedReason);

        // Step 5, 6, 7
        step("Нажимаем на 'Скидка' и Изменяем процент скидки товара");
        double discountPercent = 10.0;
        salesDocumentData.getOrderAppDataList().get(0).setDiscountPercentToProduct(0, discountPercent);
        discountPage.enterDiscountPercent(discountPercent)
                .shouldProductTotalPriceBeforeIs(productTotalPrice)
                .shouldDiscountPercentIs(discountPercent)
                .shouldDiscountCalculatedCorrectly(productTotalPrice);

        // Step 8
        step("Нажмите на кнопку Применить");
        cart35Page = discountPage.clickConfirmButton()
                .verifyRequiredElements(Cart35Page.PageState.builder()
                        .productIsAdded(true)
                        .build());
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    @Test(description = "C22797102 Изменить скидку", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeDiscount() throws Exception {
        startFromScreenWithCreatedCart(true);

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();
        ProductOrderCardAppData productData = salesDocumentData.getOrderAppDataList().get(0)
                .getProductCardDataList().get(0);
        double productTotalPrice = productData.getTotalPrice();
        double productTotalPriceWithDiscount = productData.getTotalPriceWithDiscount();
        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModal modalPage = cart35Page.clickCardByIndex(1);
        modalPage.verifyRequiredElements(true);

        // Step 2
        step("Выберите параметр Изменить скидку");
        DiscountPage discountPage = modalPage.clickChangeDiscountMenuItem()
                .shouldDiscountReasonIs(DiscountReasonModal.PRODUCT_SAMPLE_REASON)
                .shouldProductTotalPriceBeforeIs(productTotalPriceWithDiscount)
                .shouldDiscountNewPriceIs(productTotalPriceWithDiscount)
                .shouldDiscountCalculatedCorrectly(productTotalPrice)
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на Причина скидки");
        DiscountReasonModal discountReasonModal = discountPage.clickDiscountReasonFld()
                .verifyRequiredElements();

        // Step 4
        step("Изменяем причину скидки");
        String newSelectedReason = DiscountReasonModal.NOT_COMPLETE_SET_REASON;
        discountPage = discountReasonModal.selectDiscountReason(newSelectedReason)
                .shouldDiscountReasonIs(newSelectedReason);

        // Step 5, 6, 7
        step("Нажимаем на 'Скидка' и Изменяем процент скидки товара");
        double discountPercent = 6.0;
        salesDocumentData.getOrderAppDataList().get(0).setDiscountPercentToProduct(0, discountPercent);
        discountPage.enterDiscountPercent(discountPercent)
                .shouldProductTotalPriceBeforeIs(productTotalPriceWithDiscount)
                .shouldDiscountPercentIs(discountPercent)
                .shouldDiscountCalculatedCorrectly(productTotalPrice);

        // Step 8
        step("Нажмите на кнопку Применить");
        cart35Page = discountPage.clickConfirmButton()
                .verifyRequiredElements(Cart35Page.PageState.builder()
                        .productIsAdded(true)
                        .build());
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    @Test(description = "C22797103 Удалить скидку", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testRemoveDiscount() throws Exception {
        startFromScreenWithCreatedCart(true);

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();
        ProductOrderCardAppData productData = salesDocumentData.getOrderAppDataList().get(0)
                .getProductCardDataList().get(0);
        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModal modalPage = cart35Page.clickCardByIndex(1);
        modalPage.verifyRequiredElements(true);

        // Step 2
        step("Выберите параметр Изменить скидку");
        DiscountPage discountPage = modalPage.clickChangeDiscountMenuItem()
                .verifyRequiredElements();

        // Step 3, 4
        step("Удаляем скидку");
        salesDocumentData.getOrderAppDataList().get(0).removeDiscountProduct(0);
        discountPage.clickRemoveDiscount()
                .shouldProductDoesNotHaveDiscount(productData)
                .shouldSalesDocumentDataIs(salesDocumentData);
    }

}
