package com.leroy.magportal.ui.tests;

import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.DiscountConst;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.CartEstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.modal.DiscountModal;
import com.leroy.magportal.ui.pages.cart_estimate.modal.ExtendedSearchModal;
import com.leroy.magportal.ui.pages.customers.CreateCustomerForm;
import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.time.LocalDate;
import java.util.Random;

public class CartTest extends BasePAOTest {

    String discountReason = DiscountConst.Reasons.PRODUCT_SAMPLE.getName();

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

    @Test(description = "C22797249 Delete item from cart", groups = NEED_PRODUCTS_GROUP)
    public void testDeleteItemFromCart() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            stepLoginAndGoToCartPage();
            stepClickCreateCartButton();
            stepSearchForProduct(testProduct1.getLmCode());
            stepSearchForProduct(testProduct2.getLmCode());
        }

        SalesDocWebData documentData = cartPage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку 'Удалить' в мини-карточке выбранного товара и подтвердите удаление");
        cartPage.removeProductByIndex(1);
        documentData.getOrders().get(0).removeProduct(0, true);
        cartPage.shouldCartHasData(documentData);
    }

    @Test(description = "C22797250 Delete last item from cart", groups = NEED_PRODUCTS_GROUP)
    public void testDeleteLastItemFromCart() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            stepLoginAndGoToCartPage();
            stepClickCreateCartButton();
            stepSearchForProduct(testProduct1.getLmCode());
        }

        String docNumber = cartPage.getDocumentNumber();

        // "Удаляем 'лишние' продукты из корзины, если они есть"
        int productCount = cartPage.getProductDataList().size();
        for (int i = 0; i < productCount - 1; i++) {
            cartPage.removeProductByIndex(1);
        }

        // Step 1
        step("Нажмите на кнопку 'Удалить' в мини-карточке выбранного товара и подтвердите удаление");
        cartPage.removeProductByIndex(1);
        cartPage.verifyEmptyCartPage();
        // workaround - небольшой баг - корзина не сразу удаляется
        Thread.sleep(3000);
        cartPage.reloadPage();
        cartPage = new CartPage();
        cartPage.shouldDocumentIsNotPresent(docNumber);
    }

    @Test(description = "C22797258 Search customer (by telephone, service card number, email, barcode scanning)")
    public void testSearchClientByPhoneNumberInEstimate() throws Exception {
        SimpleCustomerData customerData = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;

        if (isStartFromScratch()) {
            stepLoginAndGoToCartPage();
            stepClickCreateCartButton();
        }

        // Step 1
        step("Нажмите на кнопку 'Добавить клиента'");
        cartPage.clickAddCustomer()
                .shouldAddingNewUserAvailable();

        // Step 2 and 3
        step("Введите номер телефона, нажмите, 'Enter', кликните по мини-карточке нужного клиента");
        cartPage.selectCustomerByPhone(customerData.getPhoneNumber())
                .shouldSelectedCustomerIs(customerData);
    }

    @Test(description = "C22797337 Edit customer data", groups = NEED_ACCESS_TOKEN_GROUP)
    public void testEditCustomerDataInCart() throws Exception {
        SimpleCustomerData customerData = createCustomerByApi();
        step("Выполнение предусловий: авторизуемся, заходим на страницу корзины, выбираем пользователя");
        if (isStartFromScratch()) {
            stepLoginAndGoToCartPage();
            stepClickCreateCartButton();
        }
        cartPage.clickAddCustomer()
                .selectCustomerByPhone(customerData.getPhoneNumber());

        // Step 1, 2
        step("Нажмите на '...' справа в карточке клиента и Выберите параметр Редактировать данные клиента");
        CreateCustomerForm createCustomerForm = cartPage.clickOptionEditCustomer();

        // Step 3
        step("Нажмите на Добавить еще телефон");
        createCustomerForm.clickAddPhoneButton();

        // Step 4
        step("Введите телефон, выберите параметр Рабочий и проставьте чекбокс Основной.");
        String secondPhone = "+7" + RandomStringUtils.randomNumeric(10);
        createCustomerForm.enterTextInPhoneInputField(2, secondPhone)
                .clickTypePhone(2, CreateCustomerForm.CommunicationType.WORK)
                .makePhoneAsMain(2);

        // Step 5
        step("Нажмите на кнопку 'Сохранить'");
        createCustomerForm.clickCreateButton();
        customerData.setPhoneNumber(secondPhone);
        cartPage.shouldSelectedCustomerIs(customerData);
    }

    @Test(description = "C22797260 Change customer")
    public void testChangeCustomerToAnotherOneInCart() throws Exception {
        SimpleCustomerData customer1 = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        SimpleCustomerData customer2 = TestDataConstants.SIMPLE_CUSTOMER_DATA_2;
        step("Выполнение предусловий: авторизуемся, заходим на страницу корзины, выбираем пользователя");
        if (isStartFromScratch()) {
            stepLoginAndGoToCartPage();
            stepClickCreateCartButton();
        }
        cartPage.clickAddCustomer()
                .selectCustomerByPhone(customer1.getPhoneNumber());

        // Step 1 and 2
        step("Нажмите на '...' справа в карточке клиента и Выберите параметр 'Выбрать другого клиента'");
        cartPage.clickOptionSelectAnotherCustomer()
                .shouldAddingNewUserAvailable();

        // Step 3 and 4
        step("Введите номер телефона другого клиента и нажмите Enter");
        cartPage.selectCustomerByPhone(customer2.getPhoneNumber())
                .shouldSelectedCustomerIs(customer2);
    }

    @Test(description = "C22797247 Add AVS, Топ EM items (sufficient stock)")
    public void testAddAVSOrTopEMItemsSufficientStock() throws Exception {
        // Test data
        boolean oddDay = LocalDate.now().getDayOfMonth() % 2 == 1;
        String lmCode = oddDay ? getAnyLmCodeProductWithTopEM(true) : getAnyLmCodeProductWithAvs(true);

        step("Выполнение предусловий: авторизуемся, заходим на страницу корзины");
        if (isStartFromScratch()) {
            stepLoginAndGoToCartPage();
            stepClickCreateCartButton();
        }

        // Step 1
        step("Введите ЛМ код товара в поле 'Добавление товара' (товар AVS или Топ ЕМ, количество товара достаточно)");
        stepSearchForProduct(lmCode);
    }

    @Test(description = "C22797248 Add AVS, Топ EM items (insufficient stock)")
    public void testAddAVSOrTopEMItemsInsufficientStock() throws Exception {
        // Test data
        boolean oddDay = false;//LocalDate.now().getDayOfMonth() % 2 == 1;
        String lmCode = oddDay ? getAnyLmCodeProductWithTopEM(false) : getAnyLmCodeProductWithAvs(false);

        step("Выполнение предусловий: авторизуемся, заходим на страницу корзины");
        if (isStartFromScratch()) {
            stepLoginAndGoToCartPage();
            stepClickCreateCartButton();
        }

        // Step 1
        step("Введите ЛМ код товара в поле 'Добавление товара' (товар AVS или Топ ЕМ, количество товара НЕ достаточно)");
        stepSearchForProduct(lmCode);
        String s = "";

        // Step 2
        step("Нажмите на 'Изменить'");

        // Step 3
        step("Выберите параметр Товара достаточно в магазине");

        // TODO
    }

    @Test(description = "C22797254 Create discount", groups = NEED_PRODUCTS_GROUP)
    public void testCreateDiscount() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);

        stepLoginAndGoToCartPage();
        stepClickCreateCartButton();
        stepSearchForProduct(testProduct1.getLmCode());

        SalesDocWebData cartData = cartPage.getSalesDocData();

        // Step 1 and 2
        step("Нажмите на 'Сделать скидку' в мини-карточке любого товара в списке товаров корзины");
        DiscountModal discountModal = cartPage.clickDiscountIcon(1, 1)
                .verifyAvailableDiscountReasonOptions();

        // Step 3
        step("Выбираем причину скидки");
        discountModal.selectReasonDiscount(discountReason)
                .shouldDiscountReasonIs(discountReason);

        // Step 4
        step("Нажимаем на Скидка, изменяем процент скидки");
        double discountPercent = 10.0;
        double productTotalPrice = testProduct1.getPrice();
        discountModal.enterDiscountPercent(discountPercent)
                .shouldProductTotalPriceWithoutDiscount(productTotalPrice)
                .shouldDiscountPercentIs(discountPercent)
                .shouldDiscountCalculatedCorrectly(productTotalPrice);

        // Step 5
        step("Нажмите на кнопку Применить");
        cartPage = discountModal.clickConfirmButton();
        cartData.getOrders().get(0).setDiscountPercentToProduct(0, discountPercent);
        cartPage.shouldCartHasData(cartData);

    }

    @Test(description = "C22797256 Edit discount", groups = NEED_PRODUCTS_GROUP)
    public void testEditDiscount() throws Exception {
        // Test data
        ProductItemData testProduct1 = productList.get(0);
        double discountPercent = 10.0;

        // Pre-condition
        if (isStartFromScratch()) {
            step("Выполнение предусловий");
            stepLoginAndGoToCartPage();
            stepClickCreateCartButton();
            stepSearchForProduct(testProduct1.getLmCode());
            cartPage.clickDiscountIcon(1, 1)
                    .selectReasonDiscount(discountReason)
                    .enterDiscountPercent(discountPercent)
                    .clickConfirmButton();
        }

        SalesDocWebData cartData = cartPage.getSalesDocData();
        double totalPriceWithoutDiscount = cartData.getOrders().get(0).getProductCardDataList().get(0).getTotalPrice();

        // Step 1
        step("Выберите параметр Изменить скидку в мини-карточке выбранного товара");
        DiscountModal discountModal = cartPage.clickDiscountIcon(1, 1)
                .shouldDiscountCalculatedCorrectly(totalPriceWithoutDiscount)
                .shouldDiscountReasonIs(discountReason);

        // Step 2
        step("Нажмите на 'Скидка', измените процент скидки товара");
        double editDiscountPercent = 6.0;
        cartData.getOrders().get(0).setDiscountPercentToProduct(0, editDiscountPercent);
        discountModal.enterDiscountPercent(editDiscountPercent)
                .shouldProductTotalPriceWithoutDiscount(totalPriceWithoutDiscount)
                .shouldDiscountPercentIs(editDiscountPercent)
                .shouldDiscountCalculatedCorrectly(totalPriceWithoutDiscount);

        // Step 3
        step("Нажмите на кнопку 'Сохранить'");
        cartPage = discountModal.clickConfirmButton()
                .shouldCartHasData(cartData);
    }

    @Test(description = "C22797255 Delete discount", groups = NEED_PRODUCTS_GROUP)
    public void testDeleteDiscount() throws Exception {
        // Test data
        ProductItemData testProduct1 = productList.get(0);
        double discountPercent = 10.0;

        // Pre-condition
        if (isStartFromScratch()) {
            step("Выполнение предусловий");
            stepLoginAndGoToCartPage();
            stepClickCreateCartButton();
            stepSearchForProduct(testProduct1.getLmCode());
            cartPage.clickDiscountIcon(1, 1)
                    .selectReasonDiscount(discountReason)
                    .enterDiscountPercent(discountPercent)
                    .clickConfirmButton();
        }

        SalesDocWebData cartData = cartPage.getSalesDocData();

        // Step 1
        step("Выберите параметр Изменить скидку в мини-карточке выбранного товара");
        DiscountModal discountModal = cartPage.clickDiscountIcon(1, 1);

        // Step 2
        step("Нажмите на кнопку 'Удалить скидку'");
        cartData.getOrders().get(0).removeDiscountProduct(0);
        cartPage = discountModal.clickRemoveDiscount()
                .shouldCartHasData(cartData);
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
