package com.leroy.magmobile.ui.tests.sales;

import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.*;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

public class CartTest extends SalesBaseTest {

    private void startFromScreenWithCreatedCart() throws Exception {
        startFromScreenWithCreatedCart(false);
    }

    @Step("Pre-condition: Создание корзины")
    private void startFromScreenWithCreatedCart(boolean hasDiscount) throws Exception {
        if (!Cart35Page.isThisPage()) {
            String cartDocNumber = createDraftCart(1, hasDiscount);
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
        AddProduct35Page addProduct35Page = new AddProduct35Page();
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
        AddProduct35Page addProduct35Page = new AddProduct35Page()
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
        CartActionWithProductCardModalPage modalPage = cart35Page.clickCardByIndex(1)
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
        CartActionWithProductCardModalPage modalPage = cart35Page.clickCardByIndex(1)
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

    @Test(description = "C22797101 Создать скидку", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testCreateDiscount() throws Exception {
        startFromScreenWithCreatedCart();

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        Cart35Page cart35Page = new Cart35Page();
        double productTotalPrice = cart35Page.getProductCardDataByIndex(1).getTotalPrice();
        CartActionWithProductCardModalPage modalPage = cart35Page.clickCardByIndex(1);
        modalPage.verifyRequiredElements();

        // Step 2
        step("Выберите параметр Создать скидку");
        CreatingDiscountPage creatingDiscountPage = modalPage.clickCreateDiscountMenuItem()
                .shouldProductTotalPriceIs(productTotalPrice)
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на Причина скидки");
        DiscountReasonModal discountReasonModal = creatingDiscountPage.clickDiscountReasonFld()
                .verifyRequiredElements();

        // Step 4
        step("Выбираем причину скидки");
        String selectedReason = DiscountReasonModal.PRODUCT_SAMPLE_REASON;
        creatingDiscountPage = discountReasonModal.selectDiscountReason(selectedReason)
                .shouldDiscountReasonIs(selectedReason);

        // Step 5, 6, 7
        step("Нажимаем на 'Скидка' и Изменяем процент скидки товара");
        double discountPercent = 10.0;
        creatingDiscountPage.enterDiscountPercent(discountPercent)
                .shouldProductTotalPriceIs(productTotalPrice)
                .shouldDiscountPercentIs(discountPercent)
                .shouldDiscountCalculatedCorrectly();

        // Step 8
        step("Нажмите на кнопку Применить");
        cart35Page = creatingDiscountPage.clickConfirmButton()
                .verifyRequiredElements(Cart35Page.PageState.builder()
                        .productIsAdded(true)
                        .build());
        //basket35Page.shouldOrderDataIs()
    }

    @Test(description = "C22797102 Изменить скидку", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeDiscount() throws Exception {
        startFromScreenWithCreatedCart(true);

        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        Cart35Page cart35Page = new Cart35Page();
        ProductOrderCardAppData productData = cart35Page.getProductCardDataByIndex(1);
        double productTotalPrice = productData.getTotalPrice();
        double productTotalPriceWithDiscount = productData.getTotalPriceWithDiscount();
        CartActionWithProductCardModalPage modalPage = cart35Page.clickCardByIndex(1);
        modalPage.verifyRequiredElements(true);

        // Step 2
        step("Выберите параметр Изменить скидку");
        CreatingDiscountPage creatingDiscountPage = modalPage.clickChangeDiscountMenuItem()
                .shouldDiscountReasonIs(DiscountReasonModal.PRODUCT_SAMPLE_REASON)
                .shouldProductTotalPriceIs(productTotalPrice)
                .shouldDiscountNewPriceIs(productTotalPriceWithDiscount)
                .shouldDiscountCalculatedCorrectly()
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на Причина скидки");
        DiscountReasonModal discountReasonModal = creatingDiscountPage.clickDiscountReasonFld()
                .verifyRequiredElements();

        // Step 4
        step("Изменяем причину скидки");
        String newSelectedReason = DiscountReasonModal.NOT_COMPLETE_SET_REASON;
        creatingDiscountPage = discountReasonModal.selectDiscountReason(newSelectedReason)
                .shouldDiscountReasonIs(newSelectedReason);

        // Step 5, 6, 7
        step("Нажимаем на 'Скидка' и Изменяем процент скидки товара");
        double discountPercent = 7.0;
        creatingDiscountPage.enterDiscountPercent(discountPercent)
                .shouldProductTotalPriceIs(productTotalPrice)
                .shouldDiscountPercentIs(discountPercent)
                .shouldDiscountCalculatedCorrectly();

        // Step 8
        step("Нажмите на кнопку Применить");
        cart35Page = creatingDiscountPage.clickConfirmButton()
                .verifyRequiredElements(Cart35Page.PageState.builder()
                        .productIsAdded(true)
                        .build());
        //basket35Page.shouldOrderDataIs()
    }

}
