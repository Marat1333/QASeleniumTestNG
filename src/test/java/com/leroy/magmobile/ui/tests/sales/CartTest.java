package com.leroy.magmobile.ui.tests.sales;

import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.ui.models.sales.SalesOrderCardData;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.SalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.basket.Basket35Page;
import com.leroy.magmobile.ui.pages.sales.basket.CartActionWithProductCardModalPage;
import com.leroy.magmobile.ui.pages.sales.basket.ConfirmRemoveCartModal;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import org.testng.annotations.Test;

public class CartTest extends SalesBaseTest {

    @Test(description = "C22797089 Создать корзину с экрана Документы продажи")
    public void testCreateBasketFromSalesDocumentsScreen() throws Exception {
        // Test data
        String lmCode = getAnyLmCodeProductWithoutSpecificOptions();
        // Pre-condition
        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);

        // Step 1
        log.step("Нажать кнопку Оформить продажу");
        SaleTypeModalPage modalPage = mainSalesDocumentsPage.clickCreateSalesDocumentButton();
        modalPage.verifyRequiredElementsWhenFromSalesDocuments();

        // Step 2
        log.step("Выбрать параметр Корзина");
        Basket35Page basket35Page = modalPage.clickBasketMenuItem();
        basket35Page.verifyRequiredElements(
                new Basket35Page.PageState().setProductIsAdded(false));

        // Step 3
        log.step("Нажмите на кнопку +товары и услуги");
        SearchProductPage searchProductPage = basket35Page.clickAddProductButton()
                .verifyRequiredElements();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page addProduct35Page = new AddProduct35Page(context);
        addProduct35Page.verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);

        // Step 4
        log.step("Нажмите на Добавить в корзину");
        basket35Page = addProduct35Page.clickAddIntoBasketButton();
        basket35Page.verifyRequiredElements(new Basket35Page.PageState().setProductIsAdded(true));
    }

    @Test(description = "C22797090 Добавить новый товар в корзину")
    public void testAddNewProductIntoBasket() throws Exception {
        // Test data
        String lmCode = getAnyLmCodeProductWithTopEM();
        // Если выполняется после "C22797089 Создать корзину с экрана Документы продажи",
        // то можно пропустить pre-condition шаги
        if (!Basket35Page.isThisPage(context)) {
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    SalesDocumentsConst.Types.CART.getUiVal());
        } // TODO через API
        Basket35Page basket35Page = new Basket35Page(context);
        int productCountInBasket = basket35Page.getCountOfOrderCards();
        // Step 1
        log.step("Нажмите на кнопку +Товар");
        SearchProductPage searchProductPage = basket35Page.clickAddProductButton()
                .verifyRequiredElements();

        // Step 2
        log.step("Введите ЛМ код товара или название товара или отсканируйте товар");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        AddProduct35Page addProduct35Page = new AddProduct35Page(context)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_BASKET);
        SalesOrderCardData expectedOrderCardData = addProduct35Page.getOrderRowDataFromPage();

        // Step 3
        log.step("Нажмите на Добавить в корзину");
        basket35Page = addProduct35Page.clickAddIntoBasketButton();
        basket35Page.verifyRequiredElements(
                new Basket35Page.PageState()
                        .setProductIsAdded(true)
                        .setManyOrders(null));
        basket35Page.shouldCountOfCardsIs(productCountInBasket + 1);
        basket35Page.shouldOrderCardDataWithTextIs(expectedOrderCardData.getProductCardData().getLmCode(),
                expectedOrderCardData);
    }

    @Test(description = "C22797098 Удалить товар из корзины")
    public void testRemoveProductFromCart() throws Exception {
        if (!Basket35Page.isThisPage(context)) {
            String cartDocId = createDraftCart(2);
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    cartDocId);
        }

        Basket35Page basket35Page = new Basket35Page(context);
        SalesOrderCardData salesOrderCardDataBefore = basket35Page.getSalesOrderCardDataByIndex(1);

        // Step 1
        log.step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModalPage modalPage = basket35Page.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        log.step("Выберите параметр Удалить товар");
        modalPage.clickRemoveProductMenuItem();
        ConfirmRemovingProductModal confirmRemovingProductModal = new ConfirmRemovingProductModal(context)
                .verifyRequiredElements();

        // Step 3
        log.step("Нажмите на Удалить");
        confirmRemovingProductModal.clickConfirmButton();
        basket35Page = new Basket35Page(context);
        basket35Page.verifyRequiredElements(new Basket35Page.PageState()
                .setProductIsAdded(true)
                .setManyOrders(false));
        basket35Page.shouldProductBeNotPresentInCart(
                salesOrderCardDataBefore.getProductCardData().getLmCode());
    }

    @Test(description = "C22797099 Удалить последний товар из корзины")
    public void testRemoveTheLastProductFromCart() throws Exception {
        String cartDocNumber = null;
        if (!Basket35Page.isThisPage(context)) {
            cartDocNumber = createDraftCart(1);
            MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                    MainSalesDocumentsPage.class);
            SalesDocumentsPage salesDocumentsPage = mainSalesDocumentsPage.goToMySales();
            salesDocumentsPage.searchForDocumentByTextAndSelectIt(
                    cartDocNumber);
        }

        Basket35Page basket35Page = new Basket35Page(context);
        // if (cartDocId == null) TODO если будет тест запускаться в цепочке

        // Step 1
        log.step("Нажмите на мини-карточку любого товара в списке товаров корзины");
        CartActionWithProductCardModalPage modalPage = basket35Page.clickCardByIndex(1)
                .verifyRequiredElements();

        // Step 2
        log.step("Выберите параметр Удалить товар");
        modalPage.clickRemoveProductMenuItem();
        ConfirmRemoveCartModal confirmRemovingProductModal = new ConfirmRemoveCartModal(context)
                .verifyRequiredElements();

        // Step 3
        log.step("Нажмите на Выйти");
        SalesDocumentsPage salesDocumentsPage = confirmRemovingProductModal.clickConfirmButton();
        salesDocumentsPage.shouldSalesDocumentIsNotPresent(cartDocNumber);
    }

}
