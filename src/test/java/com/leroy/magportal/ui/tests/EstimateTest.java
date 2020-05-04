package com.leroy.magportal.ui.tests;

import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
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
        estimatePage.shouldEstimateHasData(estimateData);
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
        EstimatePage estimatePage = loginAndGoTo(EstimatePage.class);

        EstimatePage createEstimatePage = estimatePage.clickCreateEstimateButton()
                .verifyRequiredElements(EstimatePage.PageState.CREATING_EMPTY);

        createEstimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
        createEstimatePage.enterTextInSearchProductField(testProduct2.getLmCode());

        step("Нажмите на кнопку \"Добавить еще раз\" любого товара в списке товаров сметы");
        createEstimatePage.copyProductByIndex(1, 1);
        String s = "";

    }

    @Test
    public void test() throws Exception {
        CartPage estimatePage = loginAndGoTo(CartPage.class);
        getDriver().get("https://dev.prudevlegowp.hq.ru.corp.leroymerlin.com/carts/view/200500032384");
        SalesDocWebData salesDocWebData = estimatePage.getSalesDocData();
        String s = "";
    }

}
