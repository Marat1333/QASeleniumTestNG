package com.leroy.magportal.ui.tests;

import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.modal.SubmittedEstimateModal;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EstimateTest extends WebBaseSteps {

    private List<ProductItemData> productList;

    private String customerPhone = "1111111111";

    @BeforeClass
    private void findProducts() {
        productList = apiClientProvider.getProducts(3);
    }

    @Test(description = "C3302188 Create estimate")
    public void testCreateEstimate() throws Exception {
        String testProductLmCode = productList.get(0).getLmCode();
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        // Step 1
        step("Нажмите на активную кнопку '+Создать корзину' в правом верхнем углу экрана");
        estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements(EstimatePage.PageState.CREATING_EMPTY);

        // Step 2
        step("Введите ЛМ товара в поле 'Добавление товара' и нажмите Enter");
        estimatePage.enterTextInSearchProductField(testProductLmCode);
        estimatePage.shouldEstimateHasProducts(Collections.singletonList(testProductLmCode));
        SalesDocWebData estimateData = estimatePage.getSalesDocData();
        anAssert.isFalse(estimateData.getNumber().isEmpty(),
                "Номер сметы отсутствует");
        anAssert.isEquals(estimateData.getAuthorName(), EnvConstants.BASIC_USER_FIRST_NAME,
                "Ожидалось другое имя создателя сметы");

        // Step 3
        step("Нажмите на кнопку 'Добавить клиента'");
        estimatePage.clickAddCustomer()
                .shouldAddingNewUserAvailable();

        // Step 4
        step("Введите номер телефона, нажмите Enter, нажмите на мини-карточку нужного клиента");
        estimatePage.selectCustomerByPhone(customerPhone)
                .shouldSelectedCustomerHasPhone(customerPhone);

        // Step 5
        step("Нажмите на кнопку 'Создать'");
        SubmittedEstimateModal submittedEstimateModal = estimatePage.clickCreateButton();
        submittedEstimateModal.verifyRequiredElements()
                .shouldEstimateNumberIs(estimateData.getNumber())
                .shouldPricesAreFixedAt("5");

        // Step 6
        step("Кликнете правой кнопкой мышки по экрану");
        estimatePage = submittedEstimateModal.closeWindow();
        estimateData.setStatus(SalesDocumentsConst.States.CONFIRMED.getUiVal());
        estimatePage.shouldEstimateHasData(estimateData)
                .shouldDocumentIsPresent(estimateData.getNumber());
    }

    @Test(description = "C3302208 Search product by lm code")
    public void testSearchProductByLmCodeInEstimate() throws Exception {
        ProductItemData testProduct = productList.get(0);
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        // Step #1
        step("Нажмите на активную кнопку '+Создать смету' в правом верхнем углу экрана");
        estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements(EstimatePage.PageState.CREATING_EMPTY);

        // Step #2
        step("Введите ЛМ товара (например 11590966) в поле 'Добавление товара' и нажмите Enter");
        estimatePage.enterTextInSearchProductField(testProduct.getLmCode());

        SalesDocWebData expectedEstimateData = new SalesDocWebData();
        expectedEstimateData.setAuthorName(EnvConstants.BASIC_USER_FIRST_NAME);
        expectedEstimateData.setStatus(SalesDocumentsConst.States.DRAFT.getUiVal());

        ProductOrderCardWebData expectedProduct = new ProductOrderCardWebData();
        expectedProduct.setTitle(testProduct.getTitle());
        expectedProduct.setBarCode(testProduct.getBarCode());
        expectedProduct.setLmCode(testProduct.getLmCode());
        expectedProduct.setPrice(testProduct.getPrice());
        expectedProduct.setSelectedQuantity(1.0);
        expectedProduct.setTotalPrice(testProduct.getPrice());

        OrderWebData expOrderData = new OrderWebData();
        expOrderData.setProductCount(1);
        expOrderData.setTotalPrice(testProduct.getPrice());
        expOrderData.setProductCardDataList(Collections.singletonList(expectedProduct));

        expectedEstimateData.setOrders(Collections.singletonList(expOrderData));
        estimatePage.shouldEstimateHasData(expectedEstimateData);
    }

    @Test(description = "C3302209 Search product by barcode")
    public void testSearchProductByBarcodeInEstimate() throws Exception {
        ProductItemData testProduct = productList.get(0);
        EstimatePage estimatePage = isStartFromScratch() ? loginAndGoTo(EstimatePage.class) :
                new EstimatePage(context);

        // Step #1
        step("Нажмите на активную кнопку '+Создать смету' в правом верхнем углу экрана");
        estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements(EstimatePage.PageState.CREATING_EMPTY);

        // Step #2
        step("Введите баркод товара (например 5411 510 510 203) в поле 'Добавление товара' и нажмите Enter");
        estimatePage.enterTextInSearchProductField(testProduct.getBarCode());

        SalesDocWebData expectedEstimateData = new SalesDocWebData();
        expectedEstimateData.setAuthorName(EnvConstants.BASIC_USER_FIRST_NAME);
        expectedEstimateData.setStatus(SalesDocumentsConst.States.DRAFT.getUiVal());

        ProductOrderCardWebData expectedProduct = new ProductOrderCardWebData();
        expectedProduct.setTitle(testProduct.getTitle());
        expectedProduct.setBarCode(testProduct.getBarCode());
        expectedProduct.setLmCode(testProduct.getLmCode());
        expectedProduct.setPrice(testProduct.getPrice());
        expectedProduct.setSelectedQuantity(1.0);
        expectedProduct.setTotalPrice(testProduct.getPrice());

        OrderWebData expOrderData = new OrderWebData();
        expOrderData.setProductCount(1);
        expOrderData.setTotalPrice(testProduct.getPrice());
        expOrderData.setProductCardDataList(Collections.singletonList(expectedProduct));

        expectedEstimateData.setOrders(Collections.singletonList(expOrderData));
        estimatePage.shouldEstimateHasData(expectedEstimateData);
    }

    @Test(description = "C3302211 Add new product to estimate")
    public void testAddNewProductToEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        SalesDocWebData estimateData = estimatePage.getSalesDocData();

        // Step 1
        step("Нажмите на поле 'Добавление товара'");
        estimatePage.enterTextInSearchProductField(testProduct2.getLmCode());

        ProductOrderCardWebData newProduct = new ProductOrderCardWebData();
        newProduct.setTitle(testProduct2.getTitle());
        newProduct.setBarCode(testProduct2.getBarCode());
        newProduct.setLmCode(testProduct2.getLmCode());
        newProduct.setPrice(testProduct2.getPrice());
        newProduct.setSelectedQuantity(1.0);
        newProduct.setTotalPrice(testProduct2.getPrice());

        OrderWebData order = estimateData.getOrders().get(0);
        order.setProductCardDataList(Arrays.asList(newProduct,
                order.getProductCardDataList().get(0)));
        order.setProductCount(order.getProductCount() + 1);
        order.setTotalWeight(null);
        order.setTotalPrice(order.getTotalPrice() + newProduct.getTotalPrice());
        estimatePage.shouldEstimateHasData(estimateData);
    }

    @Test(description = "C3302212 Copy existing product to estimate")
    public void testCopyExistingProductToEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
            estimatePage.enterTextInSearchProductField(testProduct2.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        SalesDocWebData estimateData = estimatePage.getSalesDocData();
        OrderWebData orderWebData = estimateData.getOrders().get(0);
        ProductOrderCardWebData copyProduct = orderWebData.getProductCardDataList().get(0);
        orderWebData.addFirstProduct(copyProduct, true);

        // Step 1
        step("Нажмите на кнопку 'Добавить еще раз' любого товара в списке товаров сметы");
        estimatePage.copyProductByIndex(1);
        estimatePage.shouldEstimateHasData(estimateData);
    }

    @Test(description = "C3302213 Change quantity of product")
    public void testChangeQuantityOfProductInEstimate() throws Exception {
        // Pre-condition
        int newQuantity = 5;
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
            estimatePage.enterTextInSearchProductField(testProduct2.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        SalesDocWebData estimateData = estimatePage.getSalesDocData();
        estimateData.getOrders().get(0).changeProductQuantity(0, newQuantity, true);

        // Step 1
        step("Нажмите на поле изменения количества товара и введите новое значение");
        estimatePage.changeQuantityProductByIndex(newQuantity, 1);
        estimatePage.shouldEstimateHasData(estimateData);

        // Step 2
        step("Нажмите на плашку '+' или '-'");
        boolean clickPlus = new Random().nextBoolean();
        if (clickPlus) {
            estimatePage.increaseQuantityProductByIndex(1);
            estimateData.getOrders().get(0).changeProductQuantity(
                    0, newQuantity + 1, true);
        } else {
            estimatePage.decreaseQuantityProductByIndex(1);
            estimateData.getOrders().get(0).changeProductQuantity(
                    0, newQuantity - 1, true);
        }
        estimatePage.shouldEstimateHasData(estimateData);

        // Step 3
        step("Обновите страницу");
        // Если сразу после изменения кол-ва обновить страницу, то данные могут не сохраниться
        Thread.sleep(3000); // Так делать плохо!
        estimatePage.reloadPage();
        new EstimatePage(context).shouldEstimateHasData(estimateData);
    }

    @Test(description = "C3302216 Ordered quantity of product more than existing")
    public void testOrderedQuantityOfProductMoreThanExisting() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        Number newQuantity = testProduct1.getAvailableStock() + 1;
        SalesDocWebData estimateData = estimatePage.getSalesDocData();
        estimateData.getOrders().get(0).changeProductQuantity(0, newQuantity, true);

        // Step 4
        step("Измените количество товара на число, большее доступного для заказа");
        estimatePage.changeQuantityProductByIndex(newQuantity, 1);
        estimatePage.shouldEstimateHasData(estimateData);
        estimatePage.shouldProductAvailableStockLabelIsRed(1,1);
    }

    @Test(description = "C3302214 Remove product from estimate")
    public void testRemoveProductFromEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
            estimatePage.enterTextInSearchProductField(testProduct2.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        SalesDocWebData estimateData = estimatePage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку 'Удалить' в мини-карточке выбранного товара и подтвердите удаление");
        estimatePage.removeProductByIndex(1);
        estimateData.getOrders().get(0).removeProduct(0, true);
        estimatePage.shouldEstimateHasData(estimateData);
    }

    @Test(description = "C3302215 Remove last product from estimate")
    public void testRemoveLastProductFromEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        EstimatePage estimatePage;
        step("Выполнение предусловий:");
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class).clickCreateEstimateButton();
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
        } else
            estimatePage = new EstimatePage(context);

        String docNumber = estimatePage.getDocumentNumber();

        step("Удаляем 'лишние' продукты из сметы, если они есть");
        int productCount = estimatePage.getProductDataList().size();
        for (int i = 0; i < productCount - 1; i++) {
            estimatePage.removeProductByIndex(1);
        }

        // Step 1
        step("Нажмите на кнопку 'Удалить' в мини-карточке выбранного товара и подтвердите удаление");
        estimatePage.removeProductByIndex(1)
                .verifyRequiredElements(EstimatePage.PageState.EMPTY)
                .shouldDocumentIsNotPresent(docNumber);
    }

}
