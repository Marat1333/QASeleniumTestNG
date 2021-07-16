package com.leroy.magportal.ui.tests.pao.estimate;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.core.ContextProvider;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.magmobile.api.clients.EstimateClient;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateData;
import com.leroy.magmobile.api.data.sales.cart_estimate.estimate.EstimateProductOrderData;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.PrintEstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.modal.SubmittedEstimateModal;
import com.leroy.magportal.ui.pages.customers.CreateCustomerForm;
import com.leroy.magportal.ui.tests.BasePAOTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.qameta.allure.TmsLink;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class EstimatePrintTest extends BasePAOTest {

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private EstimateClient estimateClient;

    @AfterMethod
    public void quiteDriver() {
        ContextProvider.quitDriver();
    }

    private String createEstimateWith50Products() {
        // Prepare request data
        // Search for products request #1
        List<ProductData> products = searchProductHelper.getProducts(50);
        // Search for products request #2
        /*searchResp = catalogSearchClient.searchProductsBy(
                catalogSearchFilter, 2, 50);
        assertThat(searchResp, successful());
        productItems.addAll(searchResp.asJson().getItems());*/

        List<EstimateProductOrderData> estimateProducts = new ArrayList<>();
        for (ProductData productIData : products) {
            EstimateProductOrderData productOrderData = new EstimateProductOrderData(
                    productIData);
            productOrderData.setQuantity((double) new Random().nextInt(6) + 1);
            estimateProducts.add(productOrderData);
        }
        // Create
        getUserSessionData().setAccessToken(getAccessToken());
        Response<EstimateData> response = estimateClient.sendRequestCreate(null, estimateProducts);
        // Check Create
        EstimateData estimateData = estimateClient.assertThatIsCreatedAndGetData(response);
        return estimateData.getEstimateId();
    }

    @Test(description = "C23393377 Печать сметы с одним товаром", groups = NEED_PRODUCTS_GROUP)
    @TmsLink("1122")
    public void testPrintEstimateWithOneProduct() throws Exception {
        // Test Data
        ProductData testProduct1 = productList.get(0);
        SimpleCustomerData customer1 = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        step("Выполнение предусловий");
        EstimatePage estimatePage;
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class)
                    .clickCreateEstimateButton();
            estimatePage.clickAddCustomer()
                    .selectCustomerByPhone(customer1.getPhoneNumber());
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
        } else {
            estimatePage = new EstimatePage();
        }

        SalesDocWebData estimateData = estimatePage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку 'Создать'");
        SubmittedEstimateModal submittedEstimateModal = estimatePage.clickCreateButton();
        submittedEstimateModal.verifyRequiredElements();

        // Step 2
        step("Нажмите на кнопку 'Распечатать'");
        PrintEstimatePage printEstimatePage = submittedEstimateModal.clickPrint();
        printEstimatePage.shouldPrintPreviewAreVisible();

        if (!DriverFactory.isGridProfile()) { // В муне обращение к shadowElement не работает
            // Step 3
            step("Нажмите на кнопку 'Отмена'");
            printEstimatePage.clickCancelButton();
            printEstimatePage.shouldEstimatePrintDataIs(estimateData);
        }
        printEstimatePage.closeCurrentWindowAndSwitchToSpecified(submittedEstimateModal, true);
        submittedEstimateModal = new SubmittedEstimateModal();
        submittedEstimateModal.closeWindow();
    }

    @Test(description = "C23393381 Печать сметы с разными категориями (мерами)")
    @TmsLink("1125")
    public void testPrintEstimateWithDifferentCategories() throws Exception {
        // Test Data
        String[] lmCodes = {"12395900", "11490674", "12344695", "12346092", "12599030"};
        SimpleCustomerData customer1 = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        step("Выполнение предусловий");
        EstimatePage estimatePage;
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class);
        } else {
            estimatePage = new EstimatePage();
        }

        estimatePage.clickCreateEstimateButton();
        estimatePage.clickAddCustomer()
                .selectCustomerByPhone(customer1.getPhoneNumber());
        for (String lmCode : lmCodes) {
            estimatePage.enterTextInSearchProductField(lmCode);
        }

        SalesDocWebData estimateData = estimatePage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку 'Создать'");
        SubmittedEstimateModal submittedEstimateModal = estimatePage.clickCreateButton();
        submittedEstimateModal.verifyRequiredElements();

        // Step 2
        step("Нажмите на кнопку 'Распечатать'");
        PrintEstimatePage printEstimatePage = submittedEstimateModal.clickPrint();
        printEstimatePage.shouldPrintPreviewAreVisible();
        if (!DriverFactory.isGridProfile()) { // В муне обращение к shadowElement не работает
            // Step 3
            step("Нажмите на кнопку 'Отмена'");
            printEstimatePage.clickCancelButton();
            printEstimatePage.shouldEstimatePrintDataIs(estimateData);
        }
        printEstimatePage.closeCurrentWindowAndSwitchToSpecified(submittedEstimateModal, true);
        submittedEstimateModal = new SubmittedEstimateModal();
        submittedEstimateModal.closeWindow();
    }

    @Test(description = "C3302226 Печать сметы в статусе Создан", groups = NEED_PRODUCTS_GROUP)
    @TmsLink("1121")
    public void testPrintEstimateWhenConfirmed() throws Exception {
        // Test Data
        ProductData testProduct1 = productList.get(0);
        SimpleCustomerData customer1 = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        step("Выполнение предусловий");
        EstimatePage estimatePage;
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class)
                    .clickCreateEstimateButton();
            estimatePage.clickAddCustomer()
                    .selectCustomerByPhone(customer1.getPhoneNumber());
            estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());
            SubmittedEstimateModal submittedEstimateModal = estimatePage.clickCreateButton();
            estimatePage = submittedEstimateModal.closeWindow();
        } else {
            estimatePage = new EstimatePage();
        }

        SalesDocWebData estimateData = estimatePage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку печати сметы (принтер)");
        PrintEstimatePage printEstimatePage = estimatePage.clickPrintButton();
        printEstimatePage.shouldPrintPreviewAreVisible();

        if (!DriverFactory.isGridProfile()) { // В муне обращение к shadowElement не работает
            // Step 2
            step("Нажмите на кнопку 'Отмена'");
            printEstimatePage.clickCancelButton();
            printEstimatePage.shouldEstimatePrintDataIs(estimateData);
        }
        printEstimatePage.closeCurrentWindowAndSwitchToSpecified(estimatePage, true);
    }

    @Test(description = "C23398086 Печать сметы физ. лицо", groups = {NEED_PRODUCTS_GROUP,
            NEED_ACCESS_TOKEN_GROUP})
    @TmsLink("1126")
    public void testPrintEstimateWithIndividualPerson() throws Exception {
        // Test Data
        ProductData testProduct1 = productList.get(0);
        SimpleCustomerData customer1 = createCustomerByApi();
        step("Выполнение предусловий");
        EstimatePage estimatePage;
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class);
        } else {
            estimatePage = new EstimatePage();
        }

        estimatePage.clickCreateEstimateButton();
        estimatePage.clickAddCustomer()
                .selectCustomerByPhone(customer1.getPhoneNumber());
        estimatePage.enterTextInSearchProductField(testProduct1.getLmCode());

        SalesDocWebData estimateData = estimatePage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку 'Создать'");
        SubmittedEstimateModal submittedEstimateModal = estimatePage.clickCreateButton();
        submittedEstimateModal.verifyRequiredElements();

        if (!DriverFactory.isGridProfile()) { // В муне обращение к shadowElement не работает
            // Step 2
            step("Нажмите на кнопку 'Распечатать'");
            PrintEstimatePage printEstimatePage = submittedEstimateModal.clickPrint();
            printEstimatePage.shouldPrintPreviewAreVisible();

            // Step 3
            step("Нажмите на кнопку 'Отмена'");
            printEstimatePage.clickCancelButton();
            printEstimatePage.shouldEstimatePrintDataIs(estimateData);

            // Step 4
            step("Закройте вкладку печати сметы");
            printEstimatePage.closeCurrentWindowAndSwitchToSpecified(submittedEstimateModal);
        }

        // Step 5
        step("Кликнете правой кнопкой мышки по экрану");
        submittedEstimateModal = new SubmittedEstimateModal();
        submittedEstimateModal.closeWindow();

        // Step 6 and 7
        step("Нажмите на '...' справа в карточке клиента и Выберите параметр Редактировать данные клиента");
        CreateCustomerForm createCustomerForm = estimatePage.clickOptionEditCustomer();

        // Step 8
        step("Нажмите на Добавить еще телефон");
        createCustomerForm.clickAddPhoneButton();

        // Step 9
        step("Введите телефон, выберите параметр Рабочий и проставьте чекбокс Основной.");
        String secondPhone = "+7" + RandomStringUtils.randomNumeric(10);
        createCustomerForm.enterTextInPhoneInputField(2, secondPhone)
                .clickTypePhone(2, CreateCustomerForm.CommunicationType.WORK)
                .makePhoneAsMain(2);

        // Step 10
        step("Нажмите на кнопку 'Сохранить'");
        createCustomerForm.clickConfirmButton();
        customer1.setPhoneNumber(secondPhone);
        estimatePage.shouldSelectedCustomerIs(customer1);

        estimateData = estimatePage.getSalesDocData();

        // Step 11
        step("Нажмите на кнопку печати сметы (принтер)");
        PrintEstimatePage printEstimatePage = estimatePage.clickPrintButton();
        printEstimatePage.shouldPrintPreviewAreVisible();

        if (!DriverFactory.isGridProfile()) { // В муне обращение к shadowElement не работает
            // Step 12
            step("Нажмите на кнопку 'Отмена'");
            printEstimatePage.clickCancelButton();
            printEstimatePage.shouldEstimatePrintDataIs(estimateData);
        }
        printEstimatePage.closeCurrentWindowAndSwitchToSpecified(estimatePage, true);
    }

    @Test(description = "C23393379 Печать сметы 50 товаров")
    @TmsLink("1123")
    public void testPrintEstimateWith50Products() throws Exception {
        // Test Data
        SimpleCustomerData customer1 = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        step("Выполнение предусловий");
        String estimateId = createEstimateWith50Products();
        EstimatePage estimatePage;
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class);
        } else {
            estimatePage = new EstimatePage();
            estimatePage.refreshDocumentList();
        }
        estimatePage.clickDocumentInLeftMenu(estimateId);
        estimatePage.waitUntilEstimateDataIsLoaded();
        estimatePage.clickAddCustomer()
                .selectCustomerByPhone(customer1.getPhoneNumber());

        SalesDocWebData estimateData = estimatePage.getSalesDocData();
        anAssert().isEquals(estimateData.getOrders().get(0).getProductCardDataList().size(), 50,
                "На странице должно отображаться сто товаров");

        // Step 1
        step("Нажмите на кнопку 'Создать'");
        SubmittedEstimateModal submittedEstimateModal = estimatePage.clickCreateButton();
        submittedEstimateModal.verifyRequiredElements();

        // Step 2
        step("Нажмите на кнопку 'Распечатать'");
        PrintEstimatePage printEstimatePage = submittedEstimateModal.clickPrint();
        printEstimatePage.shouldPrintPreviewAreVisible();

        if (!DriverFactory.isGridProfile()) { // В муне обращение к shadowElement не работает
            // Step 3
            step("Нажмите на кнопку 'Отмена'");
            printEstimatePage.clickCancelButton();
            printEstimatePage.shouldEstimatePrintDataIs(estimateData);
        }
        printEstimatePage.closeCurrentWindowAndSwitchToSpecified(submittedEstimateModal, true);
        submittedEstimateModal = new SubmittedEstimateModal();
        submittedEstimateModal.closeWindow();
    }

    @Test(description = "C23393380 Печать сметы с доставкой", groups = NEED_PRODUCTS_GROUP)
    @TmsLink("1124")
    public void testPrintEstimateWithDelivery() throws Exception {
        // Test Data
        Double deliveryPrice = 1000.0;
        ProductData testProduct1 = productList.get(0);
        SimpleCustomerData customer1 = TestDataConstants.SIMPLE_CUSTOMER_DATA_1;
        step("Выполнение предусловий");
        EstimatePage estimatePage;
        if (isStartFromScratch()) {
            estimatePage = loginAndGoTo(EstimatePage.class);
        } else {
            estimatePage = new EstimatePage();
        }

        estimatePage.clickCreateEstimateButton()
                .clickAddCustomer()
                .selectCustomerByPhone(customer1.getPhoneNumber())
                .enterTextInSearchProductField(testProduct1.getLmCode());
        estimatePage.clickAddDelivery()
                .enterPriceDelivery(deliveryPrice)
                .clickConfirmButton();

        SalesDocWebData estimateData = estimatePage.getSalesDocData();

        // Step 1
        step("Нажмите на кнопку 'Создать'");
        SubmittedEstimateModal submittedEstimateModal = estimatePage.clickCreateButton();
        submittedEstimateModal.verifyRequiredElements();

        // Step 2
        step("Нажмите на кнопку 'Распечатать'");
        PrintEstimatePage printEstimatePage = submittedEstimateModal.clickPrint();
        printEstimatePage.shouldPrintPreviewAreVisible();

        if (!DriverFactory.isGridProfile()) { // В муне обращение к shadowElement не работает
            // Step 3
            step("Нажмите на кнопку 'Отмена'");
            printEstimatePage.clickCancelButton();
            printEstimatePage.shouldEstimatePrintDataIs(estimateData);
        }
        printEstimatePage.closeCurrentWindowAndSwitchToSpecified(submittedEstimateModal, true);
        submittedEstimateModal = new SubmittedEstimateModal();
        submittedEstimateModal.closeWindow();
    }

}
