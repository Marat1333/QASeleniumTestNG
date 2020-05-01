package com.leroy.magportal.ui.tests;

import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.salesdoc.EstimatePuzData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardPuzData;
import com.leroy.magportal.ui.pages.cart_estimate.CreateEstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.modal.SubmittedEstimateModal;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        // Step #1
        step("Нажмите на активную кнопку '+Создать корзину' в правом верхнем углу экрана");
        CreateEstimatePage createEstimatePage = estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements()
                .shouldEstimateDoesNotHaveNumber();

        // Step #2
        step("Нажмите на поле 'Добавление товара'");
        createEstimatePage.enterTextInSearchProductField(testProductLmCode);
        createEstimatePage.shouldEstimateHasProducts(Collections.singletonList(testProductLmCode));
        String estimateNumber = createEstimatePage.getEstimateNumber();

        // Step 3
        step("Введите ЛМ товара и нажмите 'Enter'");

        // Step 4
        step("Нажмите на кнопку 'Добавить клиента'");
        createEstimatePage.clickAddCustomer()
                .shouldAddingNewUserAvailable();

        // Step 5
        step("Введите номер телефона и нажмите 'Enter'");
        createEstimatePage.selectCustomerByPhone(customerPhone)
                .shouldSelectedCustomerHasPhone(customerPhone);

        // Step 6
        step("Нажмите на мини-карточку нужного клиента");

        // Step 7
        step("Нажмите на кнопку 'Создать'");
        SubmittedEstimateModal submittedEstimateModal = createEstimatePage.clickCreateButton();
        submittedEstimateModal.verifyRequiredElements()
                .shouldEstimateNumberIs(estimateNumber)
                .shouldPricesAreFixedAt("5");

        // Step 8
        step("Кликнете правой кнопкой мышки по экрану");
        estimatePage = submittedEstimateModal.closeWindow();
        String s = "";
    }

    @Test(description = "C3302208 Search product by lm code")
    public void testSearchProductByLmCodeInEstimate() throws Exception {
        ProductItemData testProduct = productList.get(0);
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        // Step #1
        step("Нажмите на активную кнопку '+Создать смету' в правом верхнем углу экрана");
        CreateEstimatePage createEstimatePage = estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements()
                .shouldEstimateDoesNotHaveNumber();

        // Step #2
        step("Нажмите на поле 'Добавление товара'");
        createEstimatePage.enterTextInSearchProductField(testProduct.getLmCode());
        createEstimatePage.shouldEstimateHasProducts(Collections.singletonList(testProduct.getLmCode()));
        String estimateNumber = createEstimatePage.getEstimateNumber();

        // Step 3
        step("Введите ЛМ товара и нажмите 'Enter'");
        ProductOrderCardPuzData expectedProduct = new ProductOrderCardPuzData();
        expectedProduct.setTitle(testProduct.getTitle());
        expectedProduct.setBarCode(testProduct.getBarCode());
        expectedProduct.setLmCode(testProduct.getLmCode());
        expectedProduct.setPrice(testProduct.getPrice());
        expectedProduct.setSelectedQuantity(1.0);
        expectedProduct.setTotalPrice(testProduct.getPrice());

        EstimatePuzData expectedEstimateData = new EstimatePuzData();
        expectedEstimateData.setProductCount(1);
        expectedEstimateData.setTotalPrice(testProduct.getPrice());
        expectedEstimateData.setProductCardDataList(Collections.singletonList(expectedProduct));
        createEstimatePage.shouldEstimateHasData(expectedEstimateData);
    }

    @Test(description = "C3302209 Search product by barcode")
    public void testSearchProductByBarcodeInEstimate() throws Exception {
        ProductItemData testProduct = productList.get(0);
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        // Step #1
        step("Нажмите на активную кнопку '+Создать смету' в правом верхнем углу экрана");
        CreateEstimatePage createEstimatePage = estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements()
                .shouldEstimateDoesNotHaveNumber();

        // Step #2
        step("Нажмите на поле 'Добавление товара'");
        createEstimatePage.enterTextInSearchProductField(testProduct.getBarCode());
        createEstimatePage.shouldEstimateHasProducts(Collections.singletonList(testProduct.getLmCode()));
        String estimateNumber = createEstimatePage.getEstimateNumber();

        // Step 3
        step("Введите ЛМ товара и нажмите 'Enter'");
        ProductOrderCardPuzData expectedProduct = new ProductOrderCardPuzData();
        expectedProduct.setTitle(testProduct.getTitle());
        expectedProduct.setBarCode(testProduct.getBarCode());
        expectedProduct.setLmCode(testProduct.getLmCode());
        expectedProduct.setPrice(testProduct.getPrice());
        expectedProduct.setSelectedQuantity(1.0);
        expectedProduct.setTotalPrice(testProduct.getPrice());

        EstimatePuzData expectedEstimateData = new EstimatePuzData();
        expectedEstimateData.setProductCount(1);
        expectedEstimateData.setTotalPrice(testProduct.getPrice());
        expectedEstimateData.setProductCardDataList(Collections.singletonList(expectedProduct));
        createEstimatePage.shouldEstimateHasData(expectedEstimateData);
    }

    @Test(description = "C3302211 Add new product to estimate")
    public void testAddNewProductToEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        CreateEstimatePage createEstimatePage = estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements()
                .shouldEstimateDoesNotHaveNumber();

        createEstimatePage.enterTextInSearchProductField(testProduct1.getLmCode());

        EstimatePuzData estimateData = createEstimatePage.getEstimateData();

        step("Нажмите на поле 'Добавление товара'");
        createEstimatePage.enterTextInSearchProductField(testProduct2.getLmCode());
        List<ProductOrderCardPuzData> products = createEstimatePage.getEstimateProducts();
        estimateData.setProductCardDataList(Arrays.asList(products.get(0),
                estimateData.getProductCardDataList().get(0)));
        estimateData.setProductCount(estimateData.getProductCount() + 1);
        estimateData.setTotalWeight(estimateData.getTotalWeight() + products.get(0).getWeight());
        estimateData.setTotalPrice(estimateData.getTotalPrice() + products.get(0).getTotalPrice());
        createEstimatePage.shouldEstimateHasData(estimateData);
    }

    @Test(description = "C3302212 Copy existing product to estimate")
    public void testCopyExistingProductToEstimate() throws Exception {
        // Pre-condition
        ProductItemData testProduct1 = productList.get(0);
        ProductItemData testProduct2 = productList.get(1);
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        CreateEstimatePage createEstimatePage = estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements()
                .shouldEstimateDoesNotHaveNumber();

        createEstimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
        createEstimatePage.enterTextInSearchProductField(testProduct2.getLmCode());

        step("Нажмите на кнопку \"Добавить еще раз\" любого товара в списке товаров сметы");
        createEstimatePage.copyProductByIndex(1);
        String s = "";

    }

}
