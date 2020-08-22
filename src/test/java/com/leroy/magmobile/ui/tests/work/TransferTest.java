package com.leroy.magmobile.ui.tests.work;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.TransferClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.sales.transfer.TransferProductOrderData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchProductData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchProductDataList;
import com.leroy.magmobile.api.helpers.TransferHelper;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.pages.sales.AddProduct35Page;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SaleTypeModalPage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.transfer.*;
import com.leroy.magmobile.ui.pages.work.transfer.data.TransferProductData;
import com.leroy.magportal.api.helpers.PAOHelper;
import lombok.Data;
import lombok.var;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Test(description = "C3268357 Создание отзыва с RM клиенту")
    public void testCreateTransferFromRMToClient() throws Exception {
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
        // Pre-condition
        MainSalesDocumentsPage mainSalesDocumentsPage = loginSelectShopAndGoTo(
                MainSalesDocumentsPage.class);

        // Step 1
        step("Нажать кнопку Оформить продажу");
        SaleTypeModalPage saleTypeModalPage = mainSalesDocumentsPage.clickCreateSalesDocumentButton();

        // Step 2
        step("Выбрать параметр Со склада клиенту");
        TransferOrderStep1Page transferOrderStep1Page = saleTypeModalPage.clickFromStockToClient();

        // Step 3
        step("Нажмите на кнопку +Товары со склада");
        TransferSearchPage searchProductPage = transferOrderStep1Page.clickAddProductFromStockButton();

        // Step 4
        step("Выбрать первый товар, который поштучно хранится на складе");
        AddProduct35Page<TransferSearchPage> addProductPage = searchProductPage.clickProductCard(1);

        // Step 5, 6, 7
        step("Ввести количество товара для заявки со склада");
        int oneMonoPallet = addProductPage.getByOneMonoPalletQuantity();
        ProductOrderCardAppData productOrderCardAppData = addProductPage.getProductOrderDataFromPage();
        addProductPage.enterQuantityOfProduct(oneMonoPallet, true);

        // Step 8
        step("Нажмите на кнопку Добавить в заявку");
        searchProductPage = addProductPage.clickSubmitButton();

        // Step 9
        step("Нажмите на Товары на отзыв");
        transferOrderStep1Page = searchProductPage.clickTransferProductPanel();

        // Step 10
        step("Нажмите на Далее");
        transferOrderStep1Page.clickNextButton();
        TransferOrderStep2Page transferOrderStep2Page = new TransferOrderStep2Page();

        // Step 11 - 13
        step("Найдите клиента и выберите его");
        transferOrderStep2Page.clickClientField()
                .searchCustomerByPhone(customerData.getPhone());

        // Step 14
        step("Нажмите на Оформить продажу");
        TransferSuccessPage successPage = transferOrderStep2Page.clickSubmitButton();

        // Step 15
        step("Нажмите на кнопку Перейти в список заявок");
        TransferRequestsPage transferRequestsPage = successPage.clickSubmitButton();

        // Step 16
        step("Открыть заявку и проверить заполненные поля и товары");
        transferRequestsPage.searchForRequestAndOpenIt(productOrderCardAppData.getTitle(), "Отправлен");


    }

    @Test(description = "C3268358 Создание отзыва с RM для пополнения торгового зала")
    public void testCreateTransferFromRMToShopRoom() throws Exception {
        // Pre-condition
        WorkPage workPage = loginSelectShopAndGoTo(WorkPage.class);

        // Step 1
        step("Нажать на иконку + рядом с Отзыв товара со склада");
        TransferOrderStep1Page transferOrderStep1Page = workPage.clickWithdrawalFromRMPlusIcon();

        // Step 2
        step("Нажмите на кнопку +Товары со склада");
        TransferSearchPage searchProductPage = transferOrderStep1Page.clickAddProductFromStockButton();

        // Step 3 - 5
        step("Нажмите на + в правом нижнем углу мини-карточки выбранного товара и введите новое кол-во товара");
        TransferProductData transferProductData = searchProductPage.getTransferProduct(1);
        searchProductPage.editProductQuantityForFirstProduct(5);

        // Step 6
        step("Нажмите на Товары на отзыв");
        transferOrderStep1Page = searchProductPage.clickTransferProductPanel();

        // Step 7
        step("Нажмите на Далее");
        transferOrderStep1Page.clickNextButton();
        TransferShopRoomStep2Page transferShopRoomStep2Page = new TransferShopRoomStep2Page();

        // Step 8
        step("Нажать на поле даты поставки и меняем дату и подтвердить изменение");
        LocalDate testDate = LocalDate.now().plusDays(1);
        transferShopRoomStep2Page.editDeliveryDate(testDate)
                .shouldDateFieldIs(testDate);

        // Step 9
        step("Изменить ожидаемое время доставки и подтвердить его");
        LocalTime timeForSelect = LocalTime.now().plusHours(1).plusMinutes(5);
        transferShopRoomStep2Page.editDeliveryTime(timeForSelect)
                .shouldTimeFieldIs(timeForSelect);

        // Step 10
        step("Нажмите на кнопку Отправить заявку на отзыв");
        TransferSuccessPage successPage = transferShopRoomStep2Page.clickSubmitBtn();

        // Step 11
        step("Нажмите на кнопку Перейти в список заявок");
        TransferRequestsPage transferRequestsPage = successPage.clickSubmitButton();

        // Step 12
        step("Открыть заявку и проверить заполненные поля и товары");
        transferRequestsPage.searchForRequestAndOpenIt(transferProductData.getTitle(), "Отправлен");


    }

    @Test(description = "C3268360 Создание отзыва с RM из карточки товара (поиск товара по штрихкоду)", enabled = false)
    public void test111() {
        // TODO
    }

    @Test(description = "C3268361 Редактирование заявки в статусе Черновик")
    public void testEditTransferInDraftStatus() throws Exception {
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

        // Step 2
        step("Нажмите на мини-карточку товара");
        var actionModal = transferOrderStep1Page.clickProductCard(1);

        // Step 3
        step("Нажмите на Изменить количество");
        var editQuantityPage = actionModal.clickChangeQuantityMenuItem();

        // Step 4
        step("Измените количество товара плашкой и нажмите Сохранить");
        editQuantityPage.enterQuantityOfProduct(product.getMonoPalletCapacity(), true);
        transferOrderStep1Page = editQuantityPage.clickSaveButton();

        // Step 5
        step("Нажмите на кнопку Далее");
        transferOrderStep1Page.clickNextButton();
        TransferOrderStep2Page transferOrderStep2Page = new TransferOrderStep2Page();

        // Step 6
        step("Нажмите на поле Место выдачи");


        // Step 7
        step("Выберите параметр, отличный от указанного ранее");

        // Step 8
        step("Нажмите на поле клиента");

        // Step 9
        step("Выберите параметр Удалить клиента из документа");

        String s = "";
    }


}
