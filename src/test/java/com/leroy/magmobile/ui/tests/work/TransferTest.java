package com.leroy.magmobile.ui.tests.work;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
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
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.EditProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.transfer.*;
import com.leroy.magmobile.ui.pages.work.transfer.data.DetailedTransferTaskData;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magmobile.ui.pages.work.transfer.enums.TransferTaskTypes;
import com.leroy.magmobile.ui.pages.work.transfer.modal.TransferActionWithProductCardModal;
import com.leroy.magmobile.ui.pages.work.transfer.modal.TransferExitWarningModal;
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
        private String title;
        private Integer monoPalletCapacity;
    }

    @Inject
    TransferHelper transferHelper;

    List<CustomTransferProduct> products = new ArrayList<>();

    private final String SEND_STATUS = "Отправлен";

    @BeforeClass
    private void findProducts() {
        TransferClient transferClient = apiClientProvider.getTransferClient();
        CatalogSearchClient catalogSearchClient = apiClientProvider.getCatalogSearchClient();
        Response<TransferSearchProductDataList> resp = transferClient
                .searchForTransferProducts(SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR);
        assertThat(resp, successful());
        List<TransferSearchProductData> transferProducts = resp.asJson().getItems();
        for (int i = 0; i < transferProducts.size(); i++) {
            Response<ProductItemDataList> respProduct = catalogSearchClient
                    .searchProductsBy(new GetCatalogSearch().setByLmCode(transferProducts.get(i).getLmCode()));
            assertThat(resp, successful());
            ProductItemData productItemData = respProduct.asJson().getItems().get(0);
            CustomTransferProduct customProduct = new CustomTransferProduct();
            customProduct.setLmCode(productItemData.getLmCode());
            customProduct.setTitle(productItemData.getTitle());
            customProduct.setMonoPalletCapacity(transferProducts.get(i).getSource().get(0)
                    .getMonoPallets().get(0).getCapacity());
            products.add(customProduct);
            if (i > 1)
                break;
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
        transferRequestsPage.searchForRequestAndOpenIt(productOrderCardAppData.getTitle(), SEND_STATUS);
        TransferConfirmedTaskToClientPage transferConfirmedTaskToClientPage = new TransferConfirmedTaskToClientPage();
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setPickupPlace(TransferTaskTypes.CLIENT_IN_SHOP_ROOM);
        detailedTransferTaskData.setDeliveryDate(LocalDate.now());
        detailedTransferTaskData.setClient(customerData);
        detailedTransferTaskData.setProducts(Collections.singletonList(transferProductData));
        transferConfirmedTaskToClientPage.shouldTransferTaskDataIs(detailedTransferTaskData);
    }

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
        int newQuantity = 5;
        TransferProductData transferProductData = searchProductPage.getTransferProduct(1);
        transferProductData.setOrderedQuantity(newQuantity);
        searchProductPage.editProductQuantityForFirstProduct(newQuantity);

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
        TransferToShopRoomSuccessPage successPage = transferShopRoomStep2Page.clickSubmitBtn();
        successPage.verifyRequiredElements();

        // Step 11
        step("Нажмите на кнопку Перейти в список заявок");
        TransferRequestsPage transferRequestsPage = successPage.clickSubmitButton();

        // Step 12
        step("Открыть заявку и проверить заполненные поля и товары");
        transferRequestsPage.searchForRequestAndOpenIt(transferProductData.getTitle(), SEND_STATUS);
        TransferConfirmedTaskToShopRoomPage transferConfirmedTaskToShopRoomPage = new TransferConfirmedTaskToShopRoomPage();
        DetailedTransferTaskData detailedTransferTaskData = new DetailedTransferTaskData();
        detailedTransferTaskData.setDeliveryDate(testDate);
        detailedTransferTaskData.setDeliveryTime(timeForSelect);
        detailedTransferTaskData.setProducts(Collections.singletonList(transferProductData));
        transferConfirmedTaskToShopRoomPage.shouldTransferTaskDataIs(detailedTransferTaskData);
    }

    @Test(description = "C3268360 Создание отзыва с RM из карточки товара (поиск товара по штрихкоду)", enabled = false)
    public void test111() {
        // TODO
    }

    @Test(description = "C3268361 Редактирование заявки в статусе Черновик")
    public void testEditTransferInDraftStatus() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
        CustomTransferProduct product = products.get(0);
        TransferOrderStep1Page transferOrderStep1Page;
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData = new TransferProductOrderData();
            transferProductData.setLmCode(product.getLmCode());
            transferProductData.setOrderedQuantity(1);
            String taskId = transferHelper.createTransferTask(
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
        int newQuantity = product.getMonoPalletCapacity();
        transferProductData.setOrderedQuantity(newQuantity, true);
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

    @Test(description = "C3268363 Удаление товара из заявки")
    public void testRemoveProductFromTransferTask() throws Exception {
        TransferOrderStep1Page transferOrderStep1Page;
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData1 = new TransferProductOrderData();
            transferProductData1.setLmCode(products.get(0).getLmCode());
            transferProductData1.setOrderedQuantity(1);

            TransferProductOrderData transferProductData2 = new TransferProductOrderData();
            transferProductData2.setLmCode(products.get(1).getLmCode());
            transferProductData2.setOrderedQuantity(1);

            String taskId = transferHelper.createTransferTask(
                    Arrays.asList(transferProductData1, transferProductData2),
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(products.get(1).getTitle(),
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

    @Test(description = "C3268364 Удаление последнего товара из заявки")
    public void testRemoveLastProductFromTransferTask() throws Exception {
        TransferOrderStep1Page transferOrderStep1Page;
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData1 = new TransferProductOrderData();
            transferProductData1.setLmCode(products.get(0).getLmCode());
            transferProductData1.setOrderedQuantity(1);

            String taskId = transferHelper.createTransferTask(
                    transferProductData1,
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(products.get(0).getTitle(),
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

    @Test(description = "C3268365 Изменение количества товара")
    public void testChangeProductQuantityInTransferTask() throws Exception {
        TransferOrderStep1Page transferOrderStep1Page;
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData1 = new TransferProductOrderData();
            transferProductData1.setLmCode(products.get(0).getLmCode());
            transferProductData1.setOrderedQuantity(1);

            String taskId = transferHelper.createTransferTask(
                    transferProductData1,
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(products.get(0).getTitle(),
                    SalesDocumentsConst.States.DRAFT.getUiVal());
            transferOrderStep1Page = new TransferOrderStep1Page();
            transferOrderStep1Page.shouldTaskNumberIs(taskId);
        } else {
            transferOrderStep1Page = new TransferOrderStep1Page();
        }

        DetailedTransferTaskData transferTaskData = transferOrderStep1Page.getTransferTaskData();

        // Step 1
        step("Нажмите на мини-карточку товара");
        TransferActionWithProductCardModal actionModal = transferOrderStep1Page.clickProductCard(1, true);

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

    @Test(description = "C3268366 Изменение количества товара в поиске товаров")
    public void testChangeProductQuantityWhenSearchProductsOnStock() throws Exception {
        // Pre-condition
        step("Precondition: Авторизуемся и заходим на страницу поиска товаров на складе");
        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        TransferOrderStep1Page transferOrderStep1Page = workPage.clickWithdrawalFromRMPlusIcon();
        TransferSearchPage searchProductPage = transferOrderStep1Page.clickAddProductFromStockButton();

        // Step 1 - 3
        step("Нажмите на + в правом нижнем углу мини-карточки выбранного товара и введите новое кол-во товара");
        searchProductPage.editProductQuantityForFirstProduct(5);

        // Step 4-5
        step("Повторите предыдущие шаги, но введите другое кол-во");
        searchProductPage.editProductQuantityForFirstProduct(10);

    }

    @Test(description = "C3268376 Удаление заявки в статусе Черновик")
    public void testRemoveDraftTransferTask() throws Exception {
        // Pre-condition
        TransferOrderStep1Page transferOrderStep1Page;
        if (isStartFromScratch()) {
            TransferProductOrderData transferProductData1 = new TransferProductOrderData();
            transferProductData1.setLmCode(products.get(0).getLmCode());
            transferProductData1.setOrderedQuantity(1);

            String taskId = transferHelper.createTransferTask(
                    transferProductData1,
                    SalesDocumentsConst.GiveAwayPoints.FOR_CLIENT_TO_SHOP_ROOM).getTaskId();
            WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
            TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
            transferRequestsPage.searchForRequestAndOpenIt(products.get(0).getTitle(),
                    SalesDocumentsConst.States.DRAFT.getUiVal());
            transferOrderStep1Page = new TransferOrderStep1Page();
            transferOrderStep1Page.shouldTaskNumberIs(taskId);
        } else {
            transferOrderStep1Page = new TransferOrderStep1Page();
        }

        // Step 1 - 2
        step("Нажмите на кнопку удаления заявки в правом верхнем углу экрана и подтвердите удаление");
        TransferRequestsPage transferRequestsPage = transferOrderStep1Page.removeTransferTask();

        String s = "";

    }


}
