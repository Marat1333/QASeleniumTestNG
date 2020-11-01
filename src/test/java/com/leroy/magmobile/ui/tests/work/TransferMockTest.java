package com.leroy.magmobile.ui.tests.work;

import com.google.inject.Inject;
import com.leroy.constants.DefectConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.TransferClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchProductData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchProductDataList;
import com.leroy.magmobile.api.helpers.TransferHelper;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.customer.MagLegalCustomerData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.ActionWithProduct35ModalPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.transfer.*;
import com.leroy.magmobile.ui.pages.work.transfer.data.DetailedTransferTaskData;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magmobile.ui.pages.work.transfer.modal.TransferActionWithProductCardModal;
import com.leroy.magmobile.ui.tests.BaseUiMagMobMockTest;
import lombok.Data;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransferMockTest extends BaseUiMagMobMockTest {

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

    List<CustomTransferProduct> products = new ArrayList<>();

    @BeforeMethod
    private void setUpMock() throws Exception {
        setUpMockForTestCase();
    }

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
            List<ProductItemData> items = respProduct.asJson().getItems();
            if (items.size() == 0)
                continue;
            ProductItemData productItemData = items.get(0);
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

    @Test(description = "C22782861 Создание отзыва с RM клиенту (юр.лицо)")
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
        addProductPage.enterQuantityOfProduct(3, true);
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
        successPage.clickSubmitButton()
                .verifyRequiredElements();
    }

    @Test(description = "C3268360 Создание отзыва с RM из карточки товара")
    public void testCreateTransferTaskFromProductCard() throws Exception {
        // Pre-condition
        step("Выполнение предусловий теста");
        String lmCode = "10008938";

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
        transferShopRoomStep2Page.editDeliveryTime(timeForSelect, false)
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
        detailedTransferTaskData.setDeliveryDate(LocalDate.of(2020, 10, 27));
        detailedTransferTaskData.setDeliveryTime(LocalTime.of(17, 57));
        detailedTransferTaskData.setProducts(Collections.singletonList(transferProductData));
        transferConfirmedTaskToShopRoomPage.shouldTransferTaskDataIs(detailedTransferTaskData);
    }

    @Test(description = "C3268363 Удаление товара из заявки")
    public void testRemoveProductFromTransferTask() throws Exception {
        String productTitle2 = "Блок газобетонный Ytong D500 625х250х100 мм";

        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
        transferRequestsPage.searchForRequestAndOpenIt(productTitle2,
                SalesDocumentsConst.States.DRAFT.getUiVal());
        TransferOrderStep1Page transferOrderStep1Page = new TransferOrderStep1Page();

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
        String productTitle = "Смесь универсальная Каменный цветок М150, 25 кг";

        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
        transferRequestsPage.searchForRequestAndOpenIt(productTitle,
                SalesDocumentsConst.States.DRAFT.getUiVal());
        TransferOrderStep1Page transferOrderStep1Page = new TransferOrderStep1Page();

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

    @Test(description = "C3268366 Изменение количества товара в поиске товаров")
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

    @Test(description = "C3268369 Поиск товара (по ЛМ коду и штрих коду)")
    public void testSearchForProductsForTransferTask() throws Exception {
        String lmCode = "32683690";
        TransferProductData transferProductData = new TransferProductData();
        transferProductData.setLmCode("32683690");
        transferProductData.setTitle("Пескобетон М300, 40 кг");
        transferProductData.setBarCode("4607122390963");
        transferProductData.setTotalStock(540);

        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);
        TransferRequestsPage transferRequestsPage = workPage.goToTransferProductFromStock();
        TransferSearchPage transferSearchPage = transferRequestsPage.clickFillShoppingRoomButton()
                .clickAddProductFromStockButton();

        // Step 1 - 2
        step("Нажмите на поле поиска товара по ЛМ или штрих коду и введите ЛМ код товара");
        transferSearchPage.searchForProductByLmCode(lmCode);
        transferSearchPage.shouldTransferProductsAre(Collections.singletonList(transferProductData));
    }

    @Test(description = "C3268374 Отзыв товара на моно-палете")
    public void testTransferProductOnMonoPallet() throws Exception {
        String lmCode = "32683740";

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
        searchProductPage.searchForProductByLmCode(lmCode);
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

}
