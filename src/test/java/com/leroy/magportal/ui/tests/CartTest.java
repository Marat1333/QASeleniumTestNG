package com.leroy.magportal.ui.tests;

import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.CartEstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.modal.ExtendedSearchModal;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.util.List;
import java.util.Random;

public class CartTest extends WebBaseSteps {

    // Test groups
    private final static String NEED_ACCESS_TOKEN_GROUP = "need_access_token";
    private final static String NEED_PRODUCTS_GROUP = "need_products";

    private List<ProductItemData> productList;

    @BeforeGroups(groups = NEED_PRODUCTS_GROUP)
    private void findProducts() {
        productList = apiClientProvider.getProducts(3);
    }

    @BeforeGroups(NEED_ACCESS_TOKEN_GROUP)
    private void addAccessTokenToSessionData() {
        getUserSessionData().setAccessToken(getAccessToken());
    }

    // Страницы используемые в тестах текущего класса:
    CartPage cartPage;

    private enum ProductSearchType {
        LM_CODE, BAR_CODE, TITLE
    }

    @Test(description = "C22797240 New cart creation", groups = NEED_PRODUCTS_GROUP)
    public void testNewCartCreation() throws Exception {
        SimpleCustomerData customer1 = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        String lmCode = productList.get(0).getLmCode();
        stepLoginAndGoToCartPage();

        // Step 1
        step("Нажмите на кнопку '+Создать корзину' в правом верхнем углу экрана");
        stepClickCreateCartButton();

        // Step 2
        step("Введите ЛМ товара в поле 'Добавление товара' и нажмите 'Enter'");
        stepSearchForProduct(lmCode);

        // Step 3
        step("Нажмите на кнопку 'Добавить клиента'");
        stepClickAddCustomerButton();

        // Step 4
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        stepSearchForCustomer(CartEstimatePage.SearchType.PHONE, customer1);
    }

    @Test(description = "C22797241 Search items by LM code, name, barcode", groups = NEED_PRODUCTS_GROUP)
    public void testSearchItemsByLmCodeByNameByBarcode() throws Exception {
        ProductItemData productItemData1 = productList.get(0);
        ProductItemData productItemData2 = productList.get(1);
        String titleSearch = "покрытие";
        stepLoginAndGoToCartPage();

        // Step 1
        step("Нажмите на кнопку '+Создать корзину' в правом верхнем углу экрана");
        stepClickCreateCartButton();

        // Step 2
        step("Введите ЛМ товара в поле 'Добавление товара' и нажмите Enter");
        stepSearchForProduct(productItemData1, ProductSearchType.LM_CODE);

        // Step 3
        step("Введите баркод товара 'Добавление товара' и нажмите Enter");
        stepSearchForProduct(productItemData2, ProductSearchType.BAR_CODE);

        // Step 4
        step("Введите название товара в поле 'Добавление товара' и нажмите Enter");
        cartPage.enterTextInSearchProductField(titleSearch);
        ExtendedSearchModal extendedSearchModal = new ExtendedSearchModal();

        String s = ""; // TODO не доделан

    }

    @Test(description = "C22797243 Add current item to cart", groups = NEED_PRODUCTS_GROUP)
    public void testAddCurrentItemToCart() throws Exception {
        String lmCode1 = productList.get(0).getLmCode();
        step("Pre-condition: Авторизоваться, добавить 1 товар в корзину");
        stepLoginAndGoToCartPage();
        stepClickCreateCartButton();
        stepSearchForProduct(lmCode1);

        SalesDocWebData documentData = cartPage.getSalesDocData();

        // Step 1
        step("Введите ЛМ товара в поле 'Добавление товара' и нажмите Enter");
        stepSearchForProduct(lmCode1);
        // Доп (специфические) проверки, которые не содержит метод-шаг, и которые вряд ли будут нужны в других тестах
        documentData.getOrders().get(0).changeProductQuantity(0, 2, true);
        cartPage.shouldCartHasData(documentData);
    }

    @Test(description = "C23406349 Copy existing product to cart", groups = NEED_PRODUCTS_GROUP)
    public void testCopyExistingProductToCart() throws Exception {
        String lmCode1 = productList.get(0).getLmCode();
        step("Pre-condition: Авторизоваться, добавить 1 товар в корзину");
        stepLoginAndGoToCartPage();
        stepClickCreateCartButton();
        stepSearchForProduct(lmCode1);

        SalesDocWebData documentData = cartPage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку 'Добавить еще раз' любого товара в списке товаров корзины");
        cartPage.copyProductByIndex(1);

        OrderWebData orderWebData = documentData.getOrders().get(0);
        ProductOrderCardWebData copyProduct = orderWebData.getProductCardDataList().get(0);
        orderWebData.addFirstProduct(copyProduct, true);
        cartPage.shouldCartHasData(documentData);
    }

    @Test(description = "C22797244 Item quantity editing", groups = NEED_PRODUCTS_GROUP)
    public void testItemQuantityEditing() throws Exception {
        int newQuantity = 3;
        String lmCode1 = productList.get(0).getLmCode();
        step("Pre-condition: Авторизоваться, добавить 1 товар в корзину");
        stepLoginAndGoToCartPage();
        stepClickCreateCartButton();
        stepSearchForProduct(lmCode1);

        SalesDocWebData documentData = cartPage.getSalesDocData();
        documentData.getOrders().get(0).changeProductQuantity(0, newQuantity, true);

        // Step 1
        step("Нажмите на поле изменения количества товара и введите новое значение");
        cartPage.changeQuantityProductByIndex(newQuantity, 1);
        cartPage.shouldCartHasData(documentData);

        // Step 2
        step("Нажмите на плашку '+' или '-'");
        boolean clickPlus = new Random().nextBoolean();
        if (clickPlus) {
            cartPage.increaseQuantityProductByIndex(1);
            documentData.getOrders().get(0).changeProductQuantity(
                    0, newQuantity + 1, true);
        } else {
            cartPage.decreaseQuantityProductByIndex(1);
            documentData.getOrders().get(0).changeProductQuantity(
                    0, newQuantity - 1, true);
        }
        cartPage.shouldCartHasData(documentData);

        // Step 3
        step("Обновите страницу");
        cartPage.reloadPage();
        cartPage = new CartPage().shouldCartHasData(documentData);
    }


    // ======================================= STEPS ============================================ //

    /**
     * Авторизоваться и зайти на страницу корзины
     */
    @Step("Авторизоваться в системе и перейти на страницу Корзины")
    private void stepLoginAndGoToCartPage() throws Exception {
        cartPage = loginAndGoTo(CartPage.class);
    }

    /**
     * Нажмите на кнопку '+Создать корзину' в правом верхнем углу экрана"
     */
    private void stepClickCreateCartButton() {
        cartPage.clickCreateCartButton()
                .verificationAfterClickCreateNewCartButton();
    }

    /**
     * Введите ЛМ товара в поле 'Добавление товара' и нажмите 'Enter'
     *
     * @param lmCode
     */
    private void stepSearchForProduct(String lmCode) {
        cartPage.enterTextInSearchProductField(lmCode);
        SalesDocWebData tmpDocumentData = cartPage.getSalesDocData();
        anAssert().isFalse(Strings.isNullOrEmpty(tmpDocumentData.getNumber()),
                "Номер корзины отсутствует");
        anAssert().isEquals(tmpDocumentData.getAuthorName(), EnvConstants.BASIC_USER_FIRST_NAME,
                "Ожидалось другое имя создателя корзины");
        anAssert().isTrue(tmpDocumentData.isDocumentContainsProduct(lmCode),
                "Товар с ЛМ кодом " + lmCode + " не был добавлен в корзину");
    }

    /**
     * Поиск товара с помощью ввода в поле 'Добавление товара' данных о товаре
     */
    private void stepSearchForProduct(ProductItemData expectedProductData, ProductSearchType searchType) {
        String searchText;
        switch (searchType) {
            case LM_CODE:
                searchText = expectedProductData.getLmCode();
                break;
            case BAR_CODE:
                searchText = expectedProductData.getBarCode();
                break;
            default:
                throw new IllegalArgumentException("unknown search type parameter");
        }
        cartPage.enterTextInSearchProductField(searchText);
        SalesDocWebData tmpDocumentData = cartPage.getSalesDocData();
        anAssert().isFalse(Strings.isNullOrEmpty(tmpDocumentData.getNumber()),
                "Номер корзины отсутствует");
        anAssert().isEquals(tmpDocumentData.getAuthorName(), EnvConstants.BASIC_USER_FIRST_NAME,
                "Ожидалось другое имя создателя корзины");
        ProductOrderCardWebData actualProduct = tmpDocumentData.getOrders().get(0).getLastProduct();
        softAssert().isEquals(actualProduct.getLmCode(), expectedProductData.getLmCode(),
                "Неверный ЛМ код добавленного товара");
        softAssert().isEquals(actualProduct.getBarCode(), expectedProductData.getBarCode(),
                "Неверный бар код добавленного товара");
        softAssert().isEquals(actualProduct.getTitle(), expectedProductData.getTitle(),
                "Неверное название добавленного товара");
        softAssert().verifyAll();
    }

    /**
     * Нажмите на кнопку 'Добавить клиента'
     */
    private void stepClickAddCustomerButton() {
        cartPage.clickAddCustomer()
                .shouldAddingNewUserAvailable();
    }

    /**
     * Поиск клиента
     *
     * @param searchType   - тип поиска (по телефону, карточке и т.д.)
     * @param customerData - данные клиента
     */
    private void stepSearchForCustomer(String searchType, SimpleCustomerData customerData) throws Exception {
        cartPage.selectCustomerByPhone(customerData.getPhoneNumber());
        cartPage.shouldSelectedCustomerIs(customerData);
    }
}
