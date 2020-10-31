package com.leroy.magmobile.ui.tests.work;

import com.google.inject.Inject;
import com.leroy.constants.DefectConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.Smoke;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.TransferClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.sales.transfer.*;
import com.leroy.magmobile.api.helpers.TransferHelper;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.customer.MagLegalCustomerData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ActionWithProduct35ModalPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.transfer.*;
import com.leroy.magmobile.ui.pages.work.transfer.data.DetailedTransferTaskData;
import com.leroy.magmobile.ui.pages.work.transfer.data.ShortTransferTaskData;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magmobile.ui.pages.work.transfer.enums.TransferTaskTypes;
import com.leroy.magmobile.ui.pages.work.transfer.modal.TransferActionWithProductCardModal;
import com.leroy.magmobile.ui.pages.work.transfer.modal.TransferExitWarningModal;
import com.leroy.magportal.api.helpers.PAOHelper;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransferTest extends AppBaseSteps {

    @Data
    private static class CustomTransferProduct {
        private String lmCode;
        private String barCode;
        private String title;
        private Integer monoPalletCapacity;
        private Integer mixPalletCapacity;
        private Integer totalStock;
    }

    @Inject
    TransferHelper transferHelper;
    @Inject
    PAOHelper paoHelper;

    List<CustomTransferProduct> products = new ArrayList<>();

    @BeforeClass
    private void findProducts() {
        TransferClient transferClient = apiClientProvider.getTransferClient();
        CatalogSearchClient catalogSearchClient = apiClientProvider.getCatalogSearchClient();
        Response<TransferSearchProductDataList> resp = transferClient
                .searchForTransferProducts(SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR);
        assertThat(resp, successful());
        List<TransferSearchProductData> transferProducts = resp.asJson().getItems();
        for (TransferSearchProductData transferProduct : transferProducts) {
            Response<ProductItemDataList> respProduct = catalogSearchClient
                    .searchProductsBy(new GetCatalogSearch().setByLmCode(transferProduct.getLmCode()));
            assertThat(respProduct, successful());
            ProductItemData productItemData = respProduct.asJson().getItems().get(0);
            CustomTransferProduct customProduct = new CustomTransferProduct();
            customProduct.setLmCode(productItemData.getLmCode());
            customProduct.setBarCode(productItemData.getBarCode());
            customProduct.setTitle(productItemData.getTitle());
            TransferSearchProductData.Source source = transferProduct.getSource().get(0);
            if (source.getMonoPallets() != null) {
                customProduct.setMonoPalletCapacity(transferProduct.getSource().get(0)
                        .getMonoPallets().get(0).getCapacity());
            } else if (source.getMixPallets() != null) {
                customProduct.setMixPalletCapacity(transferProduct.getSource().get(0)
                        .getMixPallets().get(0).getCapacity());
            }
            customProduct.setTotalStock(transferProduct.getTotalQuantity());
            products.add(customProduct);
        }
    }

    @AfterClass
    public void cancelTransferTasks() {
        TransferClient transferClient = apiClientProvider.getTransferClient();
        TransferSearchFilters filters = new TransferSearchFilters();
        filters.setStatus(SalesDocumentsConst.States.CONFIRMED.getApiVal());
        filters.setCreatedBy(getUserSessionData().getUserLdap());
        Response<TransferDataList> resp = transferClient.searchForTasks(filters);
        assertThat("Cancel transfer task failed: couldn't find tasks", resp, successful());
        List<TransferSalesDocData> transferDataList = resp.asJson().getItems();
        for (TransferSalesDocData transferSalesDocData : transferDataList) {
            transferHelper.cancelConfirmedTransferTask(transferSalesDocData.getTaskId());
        }
    }

    @Smoke
    @Test(description = "C3268357 Создание отзыва с RM клиенту")
    public void testCreateTransferFromRMToClient() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
        // Pre-condition
        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);

        // Step 1
        step("Нажать кнопку Оформить продажу");
        SaleTypeModalPage saleTypeModalPage = mainSalesDocumentsPage.<SaleTypeModalPage>clickCreateSalesDocumentButton()
                .verifyRequiredElementsWhenFromSalesDocuments();

        // Step 2
        step("Выбрать параметр Со склада клиенту");
        TransferOrderStep1Page transferOrderStep1Page = saleTypeModalPage.clickFromStockToClient()
                .verifyElementsWhenEmpty();

        // Step 3
        step("Нажмите на кнопку +Товары со склада");
        TransferSearchPage searchProductPage = transferOrderStep1Page.clickAddProductFromStockButton();

        // Step 4
        step("Выбрать первый товар, который поштучно хранится на складе");
        AddProduct35Page<TransferSearchPage> addProductPage = searchProductPage.clickProductCard(1)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_TASK);

        // Step 5, 6, 7
        step("Ввести количество товара для заявки со склада");
        int oneMonoPallet = addProductPage.getByOneMonoPalletQuantity();
        ProductOrderCardAppData productOrderCardAppData = addProductPage.getProductOrderDataFromPage();
        addProductPage.enterQuantityOfProduct(oneMonoPallet, true);
        ProductOrderCardAppData productData = addProductPage.getProductOrderDataFromPage();

        // Step 8
        step("Нажмите на кнопку Добавить в заявку");
        searchProductPage = addProductPage.clickSubmitButton();

        // Step 9
        step("Нажмите на Товары на отзыв");
        TransferProductData transferProductData = new TransferProductData(productData);
        transferOrderStep1Page = searchProductPage.clickTransferProductPanel()
                .verifyElementsWhenProductsAdded()
                .shouldTransferProductIs(1, transferProductData);

        // Step 10
        step("Нажмите на Далее");
        transferOrderStep1Page.clickNextButton();
        TransferOrderToClientStep2Page transferOrderStep2Page = new TransferOrderToClientStep2Page()
                .verifyRequiredElements();

        // Step 11 - 13
        step("Найдите клиента и выберите его");
        transferOrderStep2Page.clickClientField()
                .searchCustomerByPhone(customerData.getPhone());
        transferOrderStep2Page.shouldSelectedCustomerIs(customerData);

        // Step 14
        step("Нажмите на Оформить продажу");
        TransferToClientSuccessPage successPage = transferOrderStep2Page.clickSubmitButton();
        successPage.verifyRequiredElements();

        // Step 15
        step("Нажмите на кнопку Перейти в список заявок");
        TransferRequestsPage transferRequestsPage = successPage.clickSubmitButton();

        // Step 16
        step("Открыть заявку и проверить заполненные поля и товары");
        transferRequestsPage.searchForRequestAndOpenIt(productOrderCardAppData.getTitle(),
                SalesDocumentsConst.States.TRANSFER_CONFIRMED.getUiVal());
        TransferConfirmedTaskToClientPage transferConfirmedTaskToClientPage = new TransferConfirmedTaskToClientPage();
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setPickupPlace(TransferTaskTypes.CLIENT_IN_SHOP_ROOM);
        detailedTransferTaskData.setDeliveryDate(LocalDate.now());
        detailedTransferTaskData.setClient(customerData);
        detailedTransferTaskData.setProducts(Collections.singletonList(transferProductData));
        transferConfirmedTaskToClientPage.shouldTransferTaskDataIs(detailedTransferTaskData);
    }

    @Test(description = "C22782861 Создание отзыва с RM клиенту (юр.лицо)", enabled = false)
    public void testCreateTransferFromRMToLegalClient() throws Exception {
        MagLegalCustomerData legalCustomerData = TestDataConstants.LEGAL_ENTITY_2;

        // Pre-condition
        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);

        // Step 1
        step("Нажать кнопку Оформить продажу");
        SaleTypeModalPage saleTypeModalPage = mainSalesDocumentsPage.<SaleTypeModalPage>clickCreateSalesDocumentButton()
                .verifyRequiredElementsWhenFromSalesDocuments();

        // Step 2
        step("Выбрать параметр Со склада клиенту");
        TransferOrderStep1Page transferOrderStep1Page = saleTypeModalPage.clickFromStockToClient()
                .verifyElementsWhenEmpty();

        // Step 3
        step("Нажмите на кнопку +Товары со склада");
        TransferSearchPage searchProductPage = transferOrderStep1Page.clickAddProductFromStockButton();

        // Step 4
        step("Выбрать первый товар, который поштучно хранится на складе");
        AddProduct35Page<TransferSearchPage> addProductPage = searchProductPage.clickProductCard(1)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_TASK);

        // Step 5, 6, 7
        step("Ввести количество товара для заявки со склада");
        int oneMonoPallet = addProductPage.getByOneMonoPalletQuantity();
        ProductOrderCardAppData productOrderCardAppData = addProductPage.getProductOrderDataFromPage();
        addProductPage.enterQuantityOfProduct(oneMonoPallet, true);
        ProductOrderCardAppData productData = addProductPage.getProductOrderDataFromPage();

        // Step 8
        step("Нажмите на кнопку Добавить в заявку");
        searchProductPage = addProductPage.clickSubmitButton();

        // Step 9
        step("Нажмите на Товары на отзыв");
        TransferProductData transferProductData = new TransferProductData(productData);
        transferOrderStep1Page = searchProductPage.clickTransferProductPanel()
                .verifyElementsWhenProductsAdded()
                .shouldTransferProductIs(1, transferProductData);

        // Step 10
        step("Нажмите на Далее");
        transferOrderStep1Page.clickNextButton();
        TransferOrderToClientStep2Page transferOrderStep2Page = new TransferOrderToClientStep2Page()
                .verifyRequiredElements();

        // Step 11 - 14
        step("Найдите клиента (Юр. лицо) и выберите его");
        transferOrderStep2Page.clickClientField()
                .searchLegalCustomerByCardNumber(legalCustomerData.getOrgCard());
        transferOrderStep2Page.shouldSelectedCustomerIs(legalCustomerData);

        // Step 15
        step("Нажмите на Оформить продажу");
        TransferToClientSuccessPage successPage = transferOrderStep2Page.clickSubmitButton();
        successPage.verifyRequiredElements();

        // Step 16
        step("Нажмите на кнопку Перейти в список заявок");
        TransferRequestsPage transferRequestsPage = successPage.clickSubmitButton();

        // Step 17
        step("Открыть заявку и проверить заполненные поля и товары");
        transferRequestsPage.searchForRequestAndOpenIt(productOrderCardAppData.getTitle(),
                SalesDocumentsConst.States.TRANSFER_CONFIRMED.getUiVal());
        TransferConfirmedTaskToClientPage transferConfirmedTaskToClientPage = new TransferConfirmedTaskToClientPage();
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setPickupPlace(TransferTaskTypes.CLIENT_IN_SHOP_ROOM);
        detailedTransferTaskData.setDeliveryDate(LocalDate.now());
        detailedTransferTaskData.setLegalClient(legalCustomerData);
        detailedTransferTaskData.setProducts(Collections.singletonList(transferProductData));
        transferConfirmedTaskToClientPage.shouldTransferTaskDataIs(detailedTransferTaskData);
    }

    @Smoke
    @Test(description = "C3268358 Создание отзыва с RM для пополнения торгового зала")
    public void testCreateTransferFromRMToShopRoom() throws Exception {
        // Pre-condition
        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);

        // Step 1
        step("Нажать на иконку + рядом с Отзыв товара со склада");
        TransferOrderStep1Page transferOrderStep1Page = workPage.<TransferOrderStep1Page>clickWithdrawalFromRMPlusIcon()
                .verifyElementsWhenEmpty();

        // Step 2
        step("Нажмите на кнопку +Товары со склада");
        TransferSearchPage searchProductPage = transferOrderStep1Page.clickAddProductFromStockButton();

        // Step 3 - 5
        step("Нажмите на + в правом нижнем углу мини-карточки выбранного товара и введите новое кол-во товара");
        int newQuantity = 1;
        TransferProductData transferProductData = searchProductPage.getTransferProduct(1);
        transferProductData.setOrderedQuantity(newQuantity);
        transferProductData.setReviewCompositionAsNull(); // С данного экрана мы не знаем какое количество на одном моно-паллете
        searchProductPage.editProductQuantityForProduct(1, newQuantity)
                .shouldProductQuantityIs(1, newQuantity);

        // Step 6
        step("Нажмите на Товары на отзыв");
        transferOrderStep1Page = searchProductPage.clickTransferProductPanel()
                .verifyElementsWhenProductsAdded()
                .shouldTransferProductIs(1, transferProductData);

        // Step 7
        step("Нажмите на Далее");
        transferOrderStep1Page.clickNextButton();
        TransferShopRoomStep2Page transferShopRoomStep2Page = new TransferShopRoomStep2Page()
                .verifyRequiredElements();

        // Step 8
        step("Нажать на поле даты поставки и меняем дату и подтвердить изменение");
        LocalDate testDate = LocalDate.now().plusDays(1);
        transferShopRoomStep2Page.editDeliveryDate(testDate)
                .shouldDateFieldIs(testDate);

        // Step 9
        step("Изменить ожидаемое время доставки и подтвердить его");
        LocalTime timeForSelect = LocalTime.now().plusHours(1).plusMinutes(5).truncatedTo(ChronoUnit.MINUTES);
        transferShopRoomStep2Page.editDeliveryTime(timeForSelect, true)
                .shouldTimeFieldIs(timeForSelect);

        // Step 10
        step("Нажмите на кнопку Отправить заявку на отзыв");
        transferShopRoomStep2Page.clickSubmitBtn();
        TransferToShopRoomSuccessPage successPage = new TransferToShopRoomSuccessPage();
        successPage.verifyRequiredElements();

        // Step 11
        step("Нажмите на кнопку Перейти в список заявок");
        TransferRequestsPage transferRequestsPage = successPage.clickSubmitButton();

        // Step 12
        step("Открыть заявку и проверить заполненные поля и товары");
        transferRequestsPage.searchForRequestAndOpenIt(transferProductData.getTitle(),
                SalesDocumentsConst.States.TRANSFER_CONFIRMED.getUiVal());
        TransferConfirmedTaskToShopRoomPage transferConfirmedTaskToShopRoomPage = new TransferConfirmedTaskToShopRoomPage();
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setDeliveryDate(testDate);
        detailedTransferTaskData.setDeliveryTime(timeForSelect);
        detailedTransferTaskData.setProducts(Collections.singletonList(transferProductData));
        transferConfirmedTaskToShopRoomPage.shouldTransferTaskDataIs(detailedTransferTaskData);
    }

    @Test(description = "C3268360 Создание отзыва с RM из карточки товара", enabled = false)
    public void testCreateTransferTaskFromProductCard() throws Exception {
        // Pre-condition
        step("Выполнение предусловий теста");
        TransferClient transferClient = apiClientProvider.getTransferClient();
        Response<TransferSearchProductDataList> response = transferClient.searchForTransferProducts(
                SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR);
        assertThat(response, successful());
        String lmCode = response.asJson().getItems().get(0).getLmCode();

        MainProductAndServicesPage productAndServicesPage = loginSelectShopAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = productAndServicesPage.clickSearchBar(true);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);

        // Step 1
        step("Нажмите на кнопку Действия с товаром");
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage();
        productDescriptionPage.clickActionWithProductButton();
        ActionWithProduct35ModalPage modalPage = new ActionWithProduct35ModalPage();

        // Step 2
        step("Выберите параметр Пополнить торговый зал");
        AddProduct35Page<TransferOrderStep1Page> addProduct35Page = modalPage.clickFillShoppingRoomButton();
        TransferProductData transferProductData = new TransferProductData(addProduct35Page.getProductOrderDataFromPage());

        // Step 3
        step("Нажмите на кнопку Добавить в заявку");
        TransferOrderStep1Page transferOrderStep1Page = addProduct35Page.clickSubmitButton()
                .verifyElementsWhenProductsAdded();

        // Step 4
        step("Нажмите на Далее");
        transferOrderStep1Page.clickNextButton();
        TransferShopRoomStep2Page transferShopRoomStep2Page = new TransferShopRoomStep2Page()
                .verifyRequiredElements();

        // Step 5
        step("Нажать на поле даты поставки и меняем дату и подтвердить изменение");
        LocalDate testDate = LocalDate.now().plusDays(2);
        transferShopRoomStep2Page.editDeliveryDate(testDate)
                .shouldDateFieldIs(testDate);

        // Step 6
        step("Изменить ожидаемое время доставки и подтвердить его");
        LocalTime timeForSelect = LocalTime.now().plusHours(1).plusMinutes(5).truncatedTo(ChronoUnit.MINUTES);
        transferShopRoomStep2Page.editDeliveryTime(timeForSelect, true)
                .shouldTimeFieldIs(timeForSelect);

        // Step 7
        step("Нажмите на кнопку Отправить заявку на отзыв");
        transferShopRoomStep2Page.clickSubmitBtn();
        TransferToShopRoomSuccessPage successPage = new TransferToShopRoomSuccessPage();
        successPage.verifyRequiredElements();

        // Step 8
        step("Нажмите на кнопку Перейти в список заявок");
        TransferRequestsPage transferRequestsPage = successPage.clickSubmitButton();

        // Step 12
        step("Открыть заявку и проверить заполненные поля и товары");
        transferRequestsPage.searchForRequestAndOpenIt(transferProductData.getTitle(),
                SalesDocumentsConst.States.TRANSFER_CONFIRMED.getUiVal());
        TransferConfirmedTaskToShopRoomPage transferConfirmedTaskToShopRoomPage = new TransferConfirmedTaskToShopRoomPage();
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setDeliveryDate(testDate);
        detailedTransferTaskData.setDeliveryTime(timeForSelect);
        detailedTransferTaskData.setProducts(Collections.singletonList(transferProductData));
        transferConfirmedTaskToShopRoomPage.shouldTransferTaskDataIs(detailedTransferTaskData);
    }

    @Test(description = "C3268361 Редактирование заявки в статусе Черновик")
    public void testEditTransferInDraftStatus() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
        CustomTransferProduct product = products.get(5);
        TransferOrderStep1Page transferOrderStep1Page;
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData = new TransferProductOrderData();
            transferProductData.setLmCode(product.getLmCode());
            transferProductData.setOrderedQuantity(1);
            String taskId = transferHelper.createDraftTransferTask(
                    transferProductData, SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(product.getTitle(),
                    SalesDocumentsConst.States.DRAFT.getUiVal());
            transferOrderStep1Page = new TransferOrderStep1Page();
            transferOrderStep1Page.shouldTaskNumberIs(taskId);
        } else {
            transferOrderStep1Page = new TransferOrderStep1Page();
        }

        TransferProductData transferProductData = transferOrderStep1Page.getTransferProductData(1);

        // Step 3
        step("Нажмите на мини-карточку товара");
        TransferActionWithProductCardModal actionModal = transferOrderStep1Page.clickProductCard(1, true)
                .verifyRequiredElements();

        // Step 4
        step("Нажмите на Изменить количество");
        EditProduct35Page<TransferOrderStep1Page> editQuantityPage = actionModal.clickChangeQuantityMenuItem()
                .verifyRequiredElements();

        // Step 5
        step("Измените количество товара плашкой и нажмите Сохранить");
        int newQuantity = product.getMonoPalletCapacity() == null ?
                product.getMixPalletCapacity() : product.getMonoPalletCapacity();
        transferProductData.setOrderedQuantity(newQuantity, true);
        transferProductData.setSelectedMonoPalletQuantity(newQuantity);
        editQuantityPage.enterQuantityOfProduct(newQuantity, true);
        transferOrderStep1Page = editQuantityPage.clickSaveButton()
                .shouldTransferProductIs(1, transferProductData)
                .shouldTotalPriceIs(transferProductData.getTotalPrice());

        // Step 6
        step("Нажмите на кнопку Далее");
        transferOrderStep1Page.clickNextButton();
        TransferOrderToClientStep2Page transferOrderStep2Page = new TransferOrderToClientStep2Page()
                .verifyRequiredElements();

        // Step 7
        step("Нажать кнопку назад на телефоне");
        transferOrderStep1Page = transferOrderStep2Page.clickBackButton()
                .verifyElementsWhenProductsAdded();

        // Step 8
        step("Нажать кнопку назад на телефоне или на экране");
        TransferExitWarningModal modal = transferOrderStep1Page.clickBackButton()
                .verifyRequiredElements();

        // Step 9
        step("Нажать 'Отмена'");
        transferOrderStep1Page = modal.clickCancelButton()
                .verifyElementsWhenProductsAdded();

        // Step 10
        step("Перейти на экран с параметрами заявки с помощью индикаторов шагов в шапке заявки");
        transferOrderStep1Page.clickStep2Icon();
        transferOrderStep2Page = new TransferOrderToClientStep2Page()
                .verifyRequiredElements();

        // Step 11
        step("Нажмите на поле Место выдачи. Выберите параметр, отличный от указанного ранее");
        TransferTaskTypes newPickupPoint = TransferTaskTypes.OVER_SIZED_CHECKOUT;
        transferOrderStep2Page.selectPickupPoint(newPickupPoint)
                .shouldPickupPointIs(newPickupPoint);

        // Step 12
        step("Добавить клиента в заявку");
        transferOrderStep2Page.clickClientField()
                .searchCustomerByPhone(customerData.getPhone());
        transferOrderStep2Page.shouldSelectedCustomerIs(customerData);

        // Step 13
        step("Ввести комментарий");
        String comment = RandomStringUtils.randomAlphanumeric(10);
        transferOrderStep2Page.enterTextInCommentField(comment)
                .shouldCommentFieldIs(comment);

        // Step 14
        step("Нажать кнопку Оформить заявку");
        TransferToClientSuccessPage successPage = transferOrderStep2Page.clickSubmitButton();
        successPage.verifyRequiredElements();
    }

    @Test(description = "C3268363 Удаление товара из заявки", enabled = false)
    public void testRemoveProductFromTransferTask() throws Exception {
        TransferOrderStep1Page transferOrderStep1Page;
        CustomTransferProduct product1 = products.get(6);
        CustomTransferProduct product2 = products.get(7);
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData1 = new TransferProductOrderData();
            transferProductData1.setLmCode(product1.getLmCode());
            transferProductData1.setOrderedQuantity(1);

            TransferProductOrderData transferProductData2 = new TransferProductOrderData();
            transferProductData2.setLmCode(product2.getLmCode());
            transferProductData2.setOrderedQuantity(1);

            String taskId = transferHelper.createDraftTransferTask(
                    Arrays.asList(transferProductData1, transferProductData2),
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(product2.getTitle(),
                    SalesDocumentsConst.States.DRAFT.getUiVal());
            transferOrderStep1Page = new TransferOrderStep1Page();
            transferOrderStep1Page.shouldTaskNumberIs(taskId);
        } else {
            transferOrderStep1Page = new TransferOrderStep1Page();
        }

        DetailedTransferTaskData transferTaskData = transferOrderStep1Page.getTransferTaskData();

        // Step 1
        step("Нажмите на мини-карточку товара");
        TransferActionWithProductCardModal actionModal = transferOrderStep1Page.clickProductCard(1, true)
                .verifyRequiredElements();

        // Step 2
        step("Выберите параметр Удалить товар");
        actionModal.clickRemoveProductMenuItem();
        ConfirmRemovingProductModal confirmModal = new ConfirmRemovingProductModal()
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на Удалить");
        confirmModal.clickConfirmButton();
        transferOrderStep1Page = new TransferOrderStep1Page();

        transferTaskData.removeProduct(0);
        transferOrderStep1Page.shouldTransferTaskDataIs(transferTaskData);

    }

    @Test(description = "C3268364 Удаление последнего товара из заявки", enabled = false)
    public void testRemoveLastProductFromTransferTask() throws Exception {
        TransferOrderStep1Page transferOrderStep1Page;
        CustomTransferProduct product1 = products.get(8);
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData1 = new TransferProductOrderData();
            transferProductData1.setLmCode(product1.getLmCode());
            transferProductData1.setOrderedQuantity(1);

            String taskId = transferHelper.createDraftTransferTask(
                    transferProductData1,
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(product1.getTitle(),
                    SalesDocumentsConst.States.DRAFT.getUiVal());
            transferOrderStep1Page = new TransferOrderStep1Page();
            transferOrderStep1Page.shouldTaskNumberIs(taskId);
        } else {
            transferOrderStep1Page = new TransferOrderStep1Page();
        }

        // Step 1
        step("Нажмите на мини-карточку товара");
        TransferActionWithProductCardModal actionModal = transferOrderStep1Page.clickProductCard(1, false)
                .verifyRequiredElements();

        // Step 2
        step("Выберите параметр Удалить товар");
        actionModal.clickRemoveProductMenuItem();
        ConfirmRemovingProductModal confirmModal = new ConfirmRemovingProductModal()
                .verifyRequiredElements();

        // Step 3
        step("Нажмите на Удалить");
        confirmModal.clickConfirmButton();
        new TransferOrderStep1Page().verifyElementsWhenEmpty();
    }

    @Smoke
    @Test(description = "C3268365 Изменение количества товара")
    public void testChangeProductQuantityInTransferTask() throws Exception {
        TransferOrderStep1Page transferOrderStep1Page;
        CustomTransferProduct product1 = products.get(9);
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData1 = new TransferProductOrderData();
            transferProductData1.setLmCode(product1.getLmCode());
            transferProductData1.setOrderedQuantity(1);

            String taskId = transferHelper.createDraftTransferTask(
                    transferProductData1,
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(product1.getTitle(),
                    SalesDocumentsConst.States.DRAFT.getUiVal());
            transferOrderStep1Page = new TransferOrderStep1Page();
            transferOrderStep1Page.shouldTaskNumberIs(taskId);
        } else {
            transferOrderStep1Page = new TransferOrderStep1Page();
        }

        DetailedTransferTaskData transferTaskData = transferOrderStep1Page.getTransferTaskData();

        // Step 1
        step("Нажмите на мини-карточку товара");
        TransferActionWithProductCardModal actionModal = transferOrderStep1Page.clickProductCard(1, false);

        // Step 2
        step("Нажмите на Изменить количество");
        EditProduct35Page<TransferOrderStep1Page> editProduct35Page = actionModal.clickChangeQuantityMenuItem()
                .verifyRequiredElements();

        // Step 3
        step("Измените количество товара плашкой и нажмите Сохранить");
        int newQuantity = 2;
        transferTaskData.changeProductQuantity(0, newQuantity);
        transferOrderStep1Page = editProduct35Page.enterQuantityOfProduct(newQuantity, true)
                .clickSubmitButton();
        transferOrderStep1Page.shouldTransferTaskDataIs(transferTaskData);
    }

    @Test(description = "C3268366 Изменение количества товара в поиске товаров", enabled = false)
    public void testChangeProductQuantityWhenSearchProductsOnStock() throws Exception {
        // Pre-condition
        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        TransferSearchPage transferSearchPage = workPage.goToTransferProductFromStock()
                .clickFillShoppingRoomButton().clickAddProductFromStockButton();

        // Step 1 - 3
        step("Нажмите на + в правом нижнем углу мини-карточки товара и " +
                "введите количество товара больше, чем доступно для отзыва");
        TransferProductData transferProductData = transferSearchPage.getTransferProduct(1);
        transferSearchPage.editProductQuantityForProduct(1, transferProductData.getTotalStock() + 10)
                .shouldProductQuantityIs(1, 0);

        // Step 4-5
        step("Нажмите на + в правом нижнем углу мини-карточки товара, добавленного в заявку\n" +
                "Введите количество товара");
        int newQuantity = 3;
        transferSearchPage.editProductQuantityForProduct(1, newQuantity)
                .shouldProductQuantityIs(1, newQuantity);
        transferProductData.setOrderedQuantity(newQuantity, false);
        transferProductData.setReviewCompositionAsNull(); // С данного экрана мы не знаем какое количество на одном моно-паллете

        // Step 6
        step("Нажмите на поле Товары на отзыв");
        transferSearchPage.clickTransferProductPanel()
                .verifyElementsWhenProductsAdded()
                .shouldTransferProductIs(1, transferProductData);
    }

    @Smoke
    @Test(description = "C3268367 Добавление товара в заявку из поиска")
    public void testAddProductFromSearch() throws Exception {
        TransferOrderStep1Page transferOrderStep1Page;
        CustomTransferProduct product1 = products.get(10);

        // UI класс 1-ого товара, который должен быть в заявке:
        TransferProductData transferProductData1 = new TransferProductData();
        transferProductData1.setOrderedQuantity(1);
        transferProductData1.setLmCode(product1.getLmCode());
        transferProductData1.setBarCode(product1.getBarCode());
        transferProductData1.setTitle(product1.getTitle());
        transferProductData1.setTotalStock(product1.getTotalStock());
        if (isStartFromScratch()) {
            TransferProductOrderData trProduct = new TransferProductOrderData();
            trProduct.setLmCode(product1.getLmCode());
            trProduct.setOrderedQuantity(1);

            String taskId = transferHelper.createDraftTransferTask(
                    trProduct,
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(product1.getTitle(),
                    SalesDocumentsConst.States.DRAFT.getUiVal());
            transferOrderStep1Page = new TransferOrderStep1Page();
            transferOrderStep1Page.shouldTaskNumberIs(taskId);
        } else {
            transferOrderStep1Page = new TransferOrderStep1Page();
        }

        TransferSearchPage transferSearchPage = transferOrderStep1Page.clickAddProduct();

        // Step 1
        // Проверить отображение информации о товаре, доступном для отзыва - to_do

        // Step 2 - 3
        step("Введите количество товара больше, чем доступно на отзыва для товара не добавленного в заявку");
        TransferProductData transferProductData2 = transferSearchPage.getTransferProduct(2);
        transferSearchPage.editProductQuantityForProduct(2, transferProductData2.getTotalStock() + 10);
        transferSearchPage.shouldProductQuantityIs(2, 0);

        // Step 4 - 5
        step("Введите количество товара меньше или равно, чем доступно для отзыва");
        int newQuantity = 1;
        transferSearchPage.editProductQuantityForProduct(2, newQuantity);
        transferSearchPage.shouldProductQuantityIs(2, newQuantity);
        transferSearchPage.shouldProductCountOnPanelIs(2);
        transferProductData2.setOrderedQuantity(newQuantity);

        // Step 6
        step("Нажмите поле Товары на отзыв");
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setProducts(Arrays.asList(transferProductData1, transferProductData2));
        transferOrderStep1Page = transferSearchPage.clickTransferProductPanel();
        transferOrderStep1Page.shouldTransferTaskDataIs(detailedTransferTaskData);
        transferOrderStep1Page.shouldTotalPriceCalculatedCorrectly();
    }

    @Test(description = "C3268368 Редактирование параметров заявки", enabled = false)
    public void testEditParametersTransferTask() throws Exception {
        TransferOrderStep1Page transferOrderStep1Page;
        CustomTransferProduct product1 = products.get(11);
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData1 = new TransferProductOrderData();
            transferProductData1.setLmCode(product1.getLmCode());
            transferProductData1.setOrderedQuantity(1);

            String taskId = transferHelper.createDraftTransferTask(
                    transferProductData1,
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(product1.getTitle(),
                    SalesDocumentsConst.States.DRAFT.getUiVal());
            transferOrderStep1Page = new TransferOrderStep1Page();
            transferOrderStep1Page.shouldTaskNumberIs(taskId);
        } else {
            transferOrderStep1Page = new TransferOrderStep1Page();
        }

        DetailedTransferTaskData detailedTransferTaskData = transferOrderStep1Page.getTransferTaskData();

        // Step 1
        step("Нажмите на мини-карточку товара");
        TransferActionWithProductCardModal actionWithProductModal = transferOrderStep1Page
                .clickProductCard(1)
                .verifyRequiredElements();

        // Step 2
        EditProduct35Page<TransferOrderStep1Page> editProduct35Page = actionWithProductModal
                .clickChangeQuantityMenuItem()
                .verifyRequiredElements();

        int oneMonoPalletQuantity = editProduct35Page.getByOneMonoPalletQuantity();

        // Step 3
        detailedTransferTaskData.changeProductQuantity(0, oneMonoPalletQuantity);
        editProduct35Page.enterQuantityOfProduct(oneMonoPalletQuantity, true);
        transferOrderStep1Page = editProduct35Page.clickSaveButton();
        transferOrderStep1Page.shouldTransferTaskDataIs(detailedTransferTaskData);

        // Step 4
        step("Нажмите на кнопку Далее");
        transferOrderStep1Page.clickNextButton();
        TransferOrderToClientStep2Page transferOrderToClientStep2Page = new TransferOrderToClientStep2Page()
                .verifyRequiredElements();

        // Steps 5 - 6
        step("Нажмите на поле Место выдачи и Выберите параметр, отличный от указанного ранее");
        TransferTaskTypes newPickupPoint = TransferTaskTypes.OVER_SIZED_CHECKOUT;
        transferOrderToClientStep2Page.selectPickupPoint(newPickupPoint)
                .shouldPickupPointIs(newPickupPoint);

        // Step 7
        step("Нажмите на поле клиента");

        // Step 8
        step("Выберите параметр Удалить клиента из документа");
        // TODO NEED_to_update_steps


    }

    @Test(description = "C3268369 Поиск товара (по ЛМ коду и штрих коду)")
    public void testSearchForProductsForTransferTask() throws Exception {
        TransferSearchProductData productData = transferHelper.searchForProductsForTransfer().get(0);
        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
        TransferSearchPage transferSearchPage = transferRequestsPage.clickFillShoppingRoomButton()
                .clickAddProductFromStockButton();

        // Step 1 - 2
        step("Нажмите на поле поиска товара по ЛМ или штрих коду и введите ЛМ код товара");
        transferSearchPage.searchForProductByLmCode(productData.getLmCode());
        transferSearchPage.shouldTransferProductsAre(Collections.singletonList(new TransferProductData(productData)));
    }

    @Test(description = "C3268371 Фильтрация списка заявок")
    public void testFilterTransferTasks() throws Exception {
        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();

        // Step 1
        step("Нажмите на кнопку Фильтр");
        FilterTransferTaskPage filterTransferTaskPage = transferRequestsPage.clickFilterBtn()
                .verifyRequiredElements();

        // Step 2 - 3
        step("Выберите статус Черновик и нажмите на кнопку галочки внизу окна Статус заявки");
        filterTransferTaskPage.selectTaskStatus(FilterTransferTaskPage.TaskStatusModal.Options.DRAFT)
                .shouldTaskStatusFilterCountIs(1);

        // Step 4 - 5
        step("Нажмите на поле Дата создания заявки, Выберите нужную дату создания заявки и нажмите Ок");
        LocalDate filteredDate = LocalDate.now().minusDays(2);
        filterTransferTaskPage.selectCreationTaskDate(filteredDate)
                .shouldCreationTaskDateFilterIs(filteredDate);

        // Step 6
        step("Нажмите на активную кнопку Показать заявки");
        transferRequestsPage = filterTransferTaskPage.applyFilters();
        transferRequestsPage.shouldFilterCountIs(2)
                .verifyTransferTaskFilters(filteredDate, SalesDocumentsConst.States.DRAFT.getUiVal());
    }

    @Test(description = "C3268372 Расчет количества товара на простом калькуляторе")
    public void testCalculateProductCountOnCalculator() throws Exception {
        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        AddProduct35Page<TransferSearchPage> addProduct35Page = workPage.goToTransferProductFromStock()
                .clickFillShoppingRoomButton()
                .clickAddProductFromStockButton().clickProductCard(1);
        step("Проверить правильность расчета на калькуляторе выражения 5+2");
        addProduct35Page.verifyCalculator("5+2");
    }

    @Test(description = "C3268374 Отзыв товара на моно-палете")
    public void testTransferProductOnMonoPallet() throws Exception {
        step("Выполнение предусловий:");
        TransferSearchProductData productData = transferHelper.searchForProductsForTransfer(
                new TransferHelper.SearchFilters().setStockType(TransferHelper.StockType.MONO_PALLET)).get(0);

        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);

        // Step 1
        step("Нажать кнопку Оформить продажу");
        SaleTypeModalPage saleTypeModalPage = mainSalesDocumentsPage.<SaleTypeModalPage>clickCreateSalesDocumentButton()
                .verifyRequiredElementsWhenFromSalesDocuments();

        // Step 2
        step("Выбрать параметр Со склада клиенту");
        TransferOrderStep1Page transferOrderStep1Page = saleTypeModalPage.clickFromStockToClient()
                .verifyElementsWhenEmpty();

        // Step 3
        step("Нажмите на кнопку +Товары со склада");
        TransferSearchPage searchProductPage = transferOrderStep1Page.clickAddProductFromStockButton();

        // Step 4
        step("Выбрать первый товар, который хранится на складе на моно-палете и нажать на карточку");
        searchProductPage.searchForProductByLmCode(productData.getLmCode());
        TransferProductData transferProductData = searchProductPage.getTransferProduct(1);
        AddProduct35Page<TransferSearchPage> addProductPage = searchProductPage.clickProductCard(1)
                .verifyRequiredElements(AddProduct35Page.SubmitBtnCaptions.ADD_TO_TASK);

        // Step 5
        step("Ввести количество товара для заявки со склада, НЕ кратное количеству на моно-палете\n" +
                "Нажмите Добавить в заявку");
        int oneMonoPallet = addProductPage.getByOneMonoPalletQuantity();
        int newQuantity = oneMonoPallet - 1;
        searchProductPage = addProductPage.enterQuantityOfProduct(newQuantity, true)
                .clickSubmitButton();
        searchProductPage.shouldReviewCompositionIs(1, 0, 0, 0, newQuantity);
        if (!DefectConst.LFRONT_3695)
            searchProductPage.shouldProductHasWrongQuantityTooltip(1);

        // Step 6 - 7
        step("Ввести количество товара для заявки со склада, кратное количеству на моно-палете");
        searchProductPage.editProductQuantityForProduct(1, oneMonoPallet);
        searchProductPage.shouldReviewCompositionIs(1, 0, oneMonoPallet, 0, oneMonoPallet);

        // Step 8
        step("Нажмите на Товары на отзыв");
        transferProductData.setOrderedQuantity(oneMonoPallet);
        transferProductData.setSelectedMonoPalletQuantity(oneMonoPallet);
        searchProductPage.clickTransferProductPanel()
                .verifyElementsWhenProductsAdded()
                .shouldTransferProductIs(1, transferProductData);
    }

    @Test(description = "C3268376 Удаление заявки в статусе Черновик")
    public void testRemoveDraftTransferTask() throws Exception {
        // Pre-condition
        TransferOrderStep1Page transferOrderStep1Page;
        String taskId = null;
        CustomTransferProduct product1 = products.get(12);
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData1 = new TransferProductOrderData();
            transferProductData1.setLmCode(product1.getLmCode());
            transferProductData1.setOrderedQuantity(1);

            taskId = transferHelper.createDraftTransferTask(
                    transferProductData1,
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(product1.getTitle(),
                    SalesDocumentsConst.States.DRAFT.getUiVal());
            transferOrderStep1Page = new TransferOrderStep1Page();
            transferOrderStep1Page.shouldTaskNumberIs(taskId);
        } else {
            transferOrderStep1Page = new TransferOrderStep1Page();
        }

        // Step 1 - 2
        step("Нажмите на кнопку удаления заявки в правом верхнем углу экрана и подтвердите удаление");
        transferOrderStep1Page.removeTransferTask()
                .verifyRequiredElements();

        TransferClient transferClient = apiClientProvider.getTransferClient();
        Response<TransferSalesDocData> resp = transferClient.sendRequestGet(taskId);
        transferClient.assertThatDocumentIsNotExist(resp);
    }

    @Test(description = "C3268373 Обновление списка заявок (pull to refresh)")
    public void testPullRefresh() throws Exception {
        // Pre-condition
        CustomTransferProduct product1 = products.get(13);
        step("Заходим на экран  Отзыв товаров со склада");
        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();

        step("API: Создаем новую заявку");
        TransferProductOrderData transferProductData1 = new TransferProductOrderData();
        transferProductData1.setLmCode(product1.getLmCode());
        transferProductData1.setOrderedQuantity(1);
        transferHelper.createDraftTransferTask(
                transferProductData1,
                SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM);

        step("Обновляем экран и проверяем, что данные изменились");
        List<ShortTransferTaskData> tasksBefore = transferRequestsPage.getTransferTaskDataList(5);
        List<ShortTransferTaskData> tasksAfter = transferRequestsPage.makePullToRefresh()
                .getTransferTaskDataList(5);
        anAssert().isNotEquals(tasksBefore.get(0), tasksAfter.get(0), "Данные не были обновлены");
    }


}
