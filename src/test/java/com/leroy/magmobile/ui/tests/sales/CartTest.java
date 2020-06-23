package com.leroy.magmobile.ui.tests.sales;

import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.ui.models.sales.OrderAppData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.models.sales.SalesDocumentData;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.*;
import com.leroy.magmobile.ui.pages.sales.orders.cart.modal.ChangeProductModal;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.utils.ParserUtil;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class CartTest extends SalesBaseTest {

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

        // Step 4
        step("Введите ЛМ код товара");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page<Cart35Page> addProduct35Page = new AddProduct35Page<>(Cart35Page.class);
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);

        // Step 5
        step("Нажмите на Добавить в корзину");
        cart35Page = addProduct35Page.clickAddIntoBasketButton();
        cart35Page.verifyRequiredElements(Cart35Page.PageState.builder()
                .productIsAdded(true)
                .build());

        // TODO
        // Шаги 6 и 7 пропущены пока что, так как номер корзины на странице корзины не отображается, а
        // по каким другим критериям можно найти именно созданную корзину в списке - не понятно.
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

    @Test(description = "C22797091 Добавить существующий товар из поиска", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testAddTheSameProductIntoBasketFromSearch() throws Exception {
        step("Pre-condition: Создание корзины");
        startFromScreenWithCreatedCart();

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();
        OrderAppData orderData = salesDocumentData.getOrderAppDataList().get(0);
        ProductOrderCardAppData product1 = orderData.getProductCardDataList().get(0);
        // Step 1
        step("Нажмите на кнопку +Товар");
        SearchProductPage searchProductPage = cart35Page.clickAddProductButton()
                .verifyRequiredElements();

        // Step 2
        step("Введите ЛМ код товара, который добавлен ранее в корзину");
        searchProductPage.enterTextInSearchFieldAndSubmit(product1.getLmCode());
        AddProduct35Page<Cart35Page> addProduct35Page = new AddProduct35Page<>(Cart35Page.class)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);

        // Step 3
        step("Нажмите на Добавить в корзину");
        orderData.addFirstProduct(product1);
        orderData.setTotalWeight(ParserUtil.multiply(orderData.getTotalWeight(), 2, 2));
        cart35Page = addProduct35Page.clickAddIntoBasketButton();
        cart35Page.verifyRequiredElements(Cart35Page.PageState.builder()
                .productIsAdded(true)
                .manyOrders(null)
                .build());
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    @Test(description = "C22797092 Изменить количество товара (товар остается в том же заказе)",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeQuantityProductInCartWhenProductCountIsAvailable() throws Exception {
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

    @Test(description = "C22797094 Изменить количество товара (товар переносится в другой заказ)",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testChangeQuantityProductInCartWhenCountOfProductMoreThanAvailable() throws Exception {
        // Test data
        List<String> lmCodes = apiClientProvider.getProductLmCodes(1);

        step("Pre-condition: Создание корзины");
        startFromScreenWithCreatedCart(lmCodes, false);

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();
        double weightPerOneProductBefore = salesDocumentData.getOrderAppDataList().get(0).getTotalWeight() /
                salesDocumentData.getOrderAppDataList().get(0).getProductCardDataList()
                        .get(0).getSelectedQuantity();
        // Step 1
        step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModal modalPage = cart35Page.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        step("Выберите параметр Изменить количество");
        EditProduct35Page<Cart35Page> editProduct35Page = modalPage.clickChangeQuantityMenuItem();
        editProduct35Page.verifyRequiredElements();
        ProductOrderCardAppData productData = editProduct35Page.getProductOrderDataFromPage();

        // Step 3, 4, 5
        step("Измените количество товара (на большее, чем доступно для заказа)");
        int newQuantity = productData.getAvailableTodayQuantity() + 10;
        editProduct35Page.enterQuantityOfProduct(newQuantity, true);
        editProduct35Page.shouldAvailableStockAlertMessageIsVisible();

        // Step 6
        step("Нажмите на кнопку Сохранить");
        OrderAppData order = salesDocumentData.getOrderAppDataList().get(0);
        order.changeProductQuantity(0, newQuantity);
        order.setTotalWeight(weightPerOneProductBefore * newQuantity);
        order.setDate(LocalDate.now().plusDays(14));
        cart35Page = editProduct35Page.clickSaveButton();
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);

        // Step 7
        step("Нажмите на мини-карточку товара в списке товаров корзины");
        modalPage = cart35Page.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 8
        step("Выберите параметр Изменить количество");
        editProduct35Page = modalPage.clickChangeQuantityMenuItem();
        editProduct35Page.verifyRequiredElements();

        // Step 9, 10, 11
        step("Измените количество товара (на меньшее, чем доступно для заказа)");
        editProduct35Page.enterQuantityOfProduct(1, true);
        editProduct35Page.shouldAvailableStockAlertMessageIsNotVisible();

        // Step 12
        step("Нажмите на кнопку Сохранить");
        order.changeProductQuantity(0, 1);
        order.setTotalWeight(weightPerOneProductBefore);
        order.setDate(LocalDate.now());
        cart35Page = editProduct35Page.clickSaveButton();
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    @Test(description = "C22797095 Добавить товар AVS или Топ ЕМ (количество товара достаточно)",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testAddAvsOrTopEmProductIntoBasket() throws Exception {
        // Test data
        boolean oddDay = LocalDate.now().getDayOfMonth() % 2 == 1;
        String lmCode = oddDay ? getAnyLmCodeProductWithTopEM(true) : getAnyLmCodeProductWithAvs(true);

        startFromScreenWithCreatedCart();

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();
        salesDocumentData.getOrderAppDataList().get(0).setTotalWeight(null);
        // Step 1
        step("Нажмите на кнопку +Товар");
        SearchProductPage searchProductPage = cart35Page.clickAddProductButton()
                .verifyRequiredElements();

        // Step 2
        step("Введите ЛМ код " + (oddDay ? "TOP EM" : "AVS") + " товара");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page<Cart35Page> addProduct35Page = new AddProduct35Page<>(Cart35Page.class)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);
        ProductOrderCardAppData expectedProductCardData = addProduct35Page.getProductOrderDataFromPage();
        expectedProductCardData.setAvailableTodayQuantity(null);

        // Step 3
        step("Нажмите на Добавить в корзину");
        cart35Page = addProduct35Page.clickAddIntoBasketButton();
        cart35Page.verifyRequiredElements(Cart35Page.PageState.builder()
                .productIsAdded(true)
                .manyOrders(null)
                .build());
        salesDocumentData.getOrderAppDataList().get(0).addFirstProduct(expectedProductCardData);
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

    @Test(description = "C22797096 Добавить товар AVS или Топ ЕМ (количество товара меньше необходимого)",
            groups = NEED_ACCESS_TOKEN_GROUP)
    public void testAddAvsOrTopEmProductIntoBasketLessThanAvailable() throws Exception {
        // Test data
        boolean oddDay = LocalDate.now().getDayOfMonth() % 2 == 1;
        String lmCode = oddDay ? getAnyLmCodeProductWithTopEM(false) :
                getAnyLmCodeProductWithAvs(false);

        startFromScreenWithCreatedCart();

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();
        salesDocumentData.getOrderAppDataList().get(0).setTotalWeight(null);
        // Step 1
        step("Нажмите на кнопку +Товар");
        SearchProductPage searchProductPage = cart35Page.clickAddProductButton()
                .verifyRequiredElements();

        // Step 2
        step("Введите ЛМ код " + (oddDay ? "TOP EM" : "AVS") + " товара c количеством товара меньше необходимого)");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page<Cart35Page> addProduct35Page = new AddProduct35Page<>(Cart35Page.class)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);
        ProductOrderCardAppData expectedProductCardData = addProduct35Page.getProductOrderDataFromPage();
        expectedProductCardData.setAvailableTodayQuantity(null);
        expectedProductCardData.setHasAvailableQuantity(false);

        // Step 3
        step("Нажмите на Добавить в корзину");
        if (oddDay)
            expectedProductCardData.setTopEm(true);
        else
            expectedProductCardData.setAvs(true);
        salesDocumentData.getOrderAppDataList().get(0).addFirstProduct(expectedProductCardData);
        cart35Page = addProduct35Page.clickAddIntoBasketButton()
                .shouldCartCanNotBeConfirmed()
                .shouldSalesDocumentDataIs(salesDocumentData);

        // Step 4
        step("Нажмите на Изменить");
        ChangeProductModal changeProductModal = cart35Page.clickChangeByProductLmCode(
                expectedProductCardData.getLmCode())
                .verifyRequiredElements();

        // Step 5
        step("Выберите параметр Товара достаточно в магазине");
        expectedProductCardData.setHasAvailableQuantity(true);
        expectedProductCardData.setAvailableTodayQuantity(1);
        expectedProductCardData.setTopEm(null);

        // Почему-то порядок товаров меняется, поэтому приходится и ожидаемый порядок менять тоже:
        OrderAppData orderAppData = salesDocumentData.getOrderAppDataList().get(0);
        Collections.reverse(orderAppData.getProductCardDataList());

        changeProductModal.clickEnoughProductInStore()
                .shouldCartCanBeConfirmed()
                .shouldSalesDocumentDataIs(salesDocumentData);
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

    @Test(description = "C22847028 Объединение заказов на позднюю дату", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testConsolidateOrders() throws Exception {
        step("Pre-condition: Поиск подходящих товаров и создание корзины с ними");
        List<CartProductOrderData> products = findProductsForSeveralOrdersInCart();

        startFromScreenWithCreatedCart(products);

        Cart35Page cart35Page = new Cart35Page();
        SalesDocumentData salesDocumentData = cart35Page.getSalesDocumentData();

        // Step 1, 2
        step("Объедините заказы");
        salesDocumentData.consolidateOrders();
        cart35Page.clickConsolidateOrdersAndConfirm();
        cart35Page.shouldSalesDocumentDataIs(salesDocumentData);
    }

}
