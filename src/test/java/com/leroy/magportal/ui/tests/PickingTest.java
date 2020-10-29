package com.leroy.magportal.ui.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.GiveAwayData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.data.picking.PickingTaskDataList;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.ui.constants.TestDataConstants;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.models.picking.PickingTaskData;
import com.leroy.magportal.ui.models.picking.ShortPickingTaskData;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.pages.orders.AssemblyOrderPage;
import com.leroy.magportal.ui.pages.orders.GiveAwayShipOrderPage;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import com.leroy.magportal.ui.pages.picking.PickingPage;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep1;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep2;
import com.leroy.magportal.ui.pages.picking.modal.SuccessfullyCreatedAssemblyModal;
import com.leroy.utils.ParserUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class PickingTest extends BasePAOTest {

    @Inject
    PAOHelper helper;
    @Inject
    OrderClient orderHelper;

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("35");
        return sessionData;
    }

    private String orderId;
    private String pickingTaskId;

    private void initCreateOrder(int productCount, SalesDocumentsConst.States orderStatus) throws Exception {
        List<CartProductOrderData> productOrderDataList = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            CartProductOrderData productOrderData = new CartProductOrderData(productList.get(i));
            productOrderData.setQuantity(2.0);
            productOrderDataList.add(productOrderData);
        }
        switch (orderStatus) {
            case ALLOWED_FOR_PICKING:
                orderId = helper.createConfirmedOrder(productOrderDataList, true).getOrderId();
                break;
            case PICKED:
                orderId = helper.createConfirmedOrder(productOrderDataList, true).getOrderId();
                orderHelper.moveNewOrderToStatus(orderId, orderStatus);
                break;
            default:
                orderId = helper.createConfirmedOrder(productOrderDataList, false).getOrderId();
                break;
        }

    }

    private void initCreateOrder(int productCount, SalesDocumentsConst.GiveAwayPoints giveAwayPoint) throws Exception {
        List<CartProductOrderData> productOrderDataList = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            CartProductOrderData productOrderData = new CartProductOrderData(productList.get(i));
            productOrderData.setQuantity(2.0);
            productOrderDataList.add(productOrderData);
        }
        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDate(LocalDateTime.now().plusDays(1));
        giveAwayData.setShopId(
                Integer.valueOf(ContextProvider.getContext().getUserSessionData().getUserShopId()));
        if (giveAwayPoint != null) {
            giveAwayData.setPoint(giveAwayPoint.getApiVal());
        } else {
            giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        }
        orderId = helper.createConfirmedOrder(productOrderDataList, giveAwayData, false).getOrderId();
    }

    private void initCreateOrder(int productCount) throws Exception {
        initCreateOrder(productCount, SalesDocumentsConst.States.CONFIRMED);
    }

    private void initFindPickingTask() throws Exception {
        OrderClient orderClient = apiClientProvider.getOrderClient();
        orderClient.waitUntilOrderGetStatus(orderId,
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING, null);
        PickingTaskClient pickingTaskClient = apiClientProvider.getPickingTaskClient();
        Response<PickingTaskDataList> respPickingTasks = pickingTaskClient.searchForPickingTasks(orderId);
        assertThat(respPickingTasks, successful());
        pickingTaskId = respPickingTasks.asJson().getItems().get(0).getTaskId();
    }

    @AfterClass(enabled = true)
    private void cancelConfirmedOrder() throws Exception {
        if (orderId != null) {
            OrderClient orderClient = apiClientProvider.getOrderClient();
            Response<JsonNode> resp = orderClient.cancelOrder(orderId);
            assertThat(resp, successful());
        }
    }

    @Test(description = "C23408356 Сплит сборки (зона сборки Торговый зал)", groups = NEED_PRODUCTS_GROUP)
    public void testSplitAssemblyShoppingRoom() throws Exception {
        initCreateOrder(1);
        // Test data
        PickingConst.AssemblyType assemblyType = PickingConst.AssemblyType.SHOPPING_ROOM;

        PickingPage pickingPage = loginSelectShopAndGoTo(PickingPage.class);
        initFindPickingTask();
        String assemblyNumber = pickingTaskId.substring(pickingTaskId.length() - 4);
        String orderNumber = orderId.substring(orderId.length() - 4);
        String fullNumber = assemblyNumber + " *" + orderNumber;
        pickingPage.enterOrderNumberInSearchFld(orderId);
        pickingPage.clickDocumentInLeftMenu(fullNumber);
        PickingContentPage pickingContentPage = new PickingContentPage();

        // Step 1
        step("Нажать на кнопку редактирования (карандаш) в нижней части экрана");
        PickingTaskData pickingTaskData = pickingContentPage.getPickingTaskData();
        pickingContentPage.clickEditAssemblyButton();

        // Step 2
        step("Нажать на чекбокс в правом верхнем углу карточки товара");
        pickingContentPage.setSplitForProductCard(1, true)
                .checkSelectAllOptionIsSelected(true);

        // Step 3
        step("Нажать на кнопку Разделить");
        SplitPickingModalStep1 splitPickingModalStep1 = pickingContentPage.clickSplitAssemblyButton()
                .verifyRequiredElements()
                .shouldContainsProducts(pickingTaskData.getSplitPickingProductDataList())
                .shouldContinueButtonIsDisabled();

        // Step 4
        step("Выбрать зону сборки Торговый зал");
        splitPickingModalStep1.selectAssemblyType(assemblyType)
                .shouldContainsProducts(pickingTaskData.getSplitPickingProductDataList())
                .shouldContinueButtonIsDisabled();

        // Step 5
        step("Нажать чекбокс Собрать из торгового зала (LS)");
        splitPickingModalStep1.selectConfirmCheckBox(true)
                .shouldContinueButtonIsEnabled();

        // Step 6
        step("Нажать кнопку Продолжить");
        SplitPickingModalStep2 splitPickingModalStep2 = splitPickingModalStep1.clickContinueButton()
                .verifyRequiredElements()
                .shouldIamResponsibleForAssemblyOptionSelected();

        // Step 7
        step("Заполнить комментарий и Нажать на кнопку Создать сборку");
        String comment = RandomStringUtils.randomAlphabetic(15);
        splitPickingModalStep2.enterComment(comment);
        SuccessfullyCreatedAssemblyModal successfullyCreatedAssemblyModal = splitPickingModalStep2
                .clickCreateAssemblyButton()
                .verifyRequiredElements()
                .shouldOrderNumberIs(orderNumber);
        pickingTaskData.setNumber(successfullyCreatedAssemblyModal.getAssemblyNumber());
        pickingTaskData.setAssemblyType(assemblyType);
        pickingTaskData.setCreationDate(null);

        // Step 8
        step("Нажать кнопку Перейти к новому заданию на сборку");
        pickingContentPage = successfullyCreatedAssemblyModal.clickNavigateToNewTaskButton();
        pickingContentPage.shouldPickingTaskDataIs(pickingTaskData);

        ShortPickingTaskData shortPickingTaskData = pickingContentPage.getPickingTaskData()
                .getShortData();
        shortPickingTaskData.setCollector("Я");

        pickingContentPage.refreshDocumentList();
        pickingContentPage.shouldDocumentListContainsThis(shortPickingTaskData);

        pickingContentPage.switchToCommentTab()
                .shouldCommentIs(comment);
    }

    @Test(description = "C23408338 Сплит сборки (зона сборки СС)", groups = NEED_PRODUCTS_GROUP)
    public void testSplitAssemblySS() throws Exception {
        initCreateOrder(1);
        // Test data
        PickingConst.AssemblyType assemblyType = PickingConst.AssemblyType.SS;

        PickingPage pickingPage = loginSelectShopAndGoTo(PickingPage.class);
        initFindPickingTask();
        String assemblyNumber = pickingTaskId.substring(pickingTaskId.length() - 4);
        String orderNumber = orderId.substring(orderId.length() - 4);
        String fullNumber = assemblyNumber + " *" + orderNumber;
        pickingPage.enterOrderNumberInSearchFld(orderId);
        pickingPage.clickDocumentInLeftMenu(fullNumber);
        PickingContentPage pickingContentPage = new PickingContentPage();

        PickingTaskData pickingTaskDataBefore = pickingContentPage.getPickingTaskData();
        PickingTaskData newPickingTaskData = pickingTaskDataBefore.clone();

        // Step 1
        step("Нажать на кнопку редактирования (карандаш) в нижней части экрана");
        pickingContentPage.clickEditAssemblyButton();

        // Step 2
        step("Нажать на чекбокс в правом верхнем углу карточки товара");
        pickingContentPage.setSplitForProductCard(1, true)
                .checkSelectAllOptionIsSelected(true);

        // Step 3
        step("Нажать на кнопку Разделить");
        SplitPickingModalStep1 splitPickingModalStep1 = pickingContentPage.clickSplitAssemblyButton()
                .verifyRequiredElements()
                .shouldContainsProducts(newPickingTaskData.getSplitPickingProductDataList())
                .shouldContinueButtonIsDisabled();

        // Step 4
        step("Выбрать зону сборки СС");
        splitPickingModalStep1.selectAssemblyType(assemblyType)
                .shouldContainsProducts(newPickingTaskData.getSplitPickingProductDataList())
                .shouldContinueButtonIsDisabled();

        // Step 5
        step("Нажать чекбокс Нужно вручную создать заказ в RMS под заказ клиента");
        splitPickingModalStep1.selectConfirmCheckBox(true)
                .shouldContinueButtonIsEnabled();

        // Step 6
        step("Нажать кнопку Продолжить");
        SplitPickingModalStep2 splitPickingModalStep2 = splitPickingModalStep1.clickContinueButton()
                .verifyRequiredElements()
                .shouldIamResponsibleForAssemblyOptionSelected();

        // Step 7
        step("Выбрать другой отдел, Заполнить комментарий и Нажать на кнопку Создать сборку");
        splitPickingModalStep2.selectDepartment("1");
        String comment = RandomStringUtils.randomAlphabetic(15);
        splitPickingModalStep2.enterComment(comment);
        SuccessfullyCreatedAssemblyModal successfullyCreatedAssemblyModal = splitPickingModalStep2
                .clickCreateAssemblyButton()
                .verifyRequiredElements()
                .shouldOrderNumberIs(orderNumber);
        newPickingTaskData.setNumber(successfullyCreatedAssemblyModal.getAssemblyNumber());
        newPickingTaskData.setAssemblyType(assemblyType);
        newPickingTaskData.setCreationDate(null);

        // Step 8
        step("Нажать кнопку Вернуться к оригинальной сборке");
        pickingContentPage = successfullyCreatedAssemblyModal.clickRemainOldTaskButton();
        pickingTaskDataBefore.setStatus(SalesDocumentsConst.States.CANCELLED.getUiVal());
        pickingTaskDataBefore.getProducts().get(0).setOrderedQuantity(0);
        pickingContentPage.shouldPickingTaskDataIs(pickingTaskDataBefore);

        // Step 9
        step("Открыть новую сборку");
        pickingContentPage.enterOrderNumberInSearchFld(newPickingTaskData.getOrderLinkNumber());
        pickingContentPage.clickDocumentInLeftMenu(newPickingTaskData.getNumber());

        pickingContentPage.shouldPickingTaskDataIs(newPickingTaskData);

        ShortPickingTaskData shortPickingTaskData = newPickingTaskData
                .getShortData();
        String customerName = TestDataConstants.SIMPLE_CUSTOMER_DATA_1.getName();
        shortPickingTaskData.setClient(ParserUtil.parseLastName(customerName) + " " + ParserUtil.parseFirstName(customerName));
        pickingContentPage.shouldDocumentListContainsThis(shortPickingTaskData);

        pickingContentPage.switchToCommentTab()
                .shouldCommentIs(comment);
    }

    @Test(description = "C23408355 Сплит сборки (зона сборки Склад)", enabled = false)
    // TODO нужны в наличии товары ТОП ЕМ с наличием на складе
    public void testSplitAssemblyStock() throws Exception {
        initCreateOrder(1);
        // Test data
        PickingConst.AssemblyType assemblyType = PickingConst.AssemblyType.STOCK;

        PickingPage pickingPage = loginSelectShopAndGoTo(PickingPage.class);
        initFindPickingTask();
        String assemblyNumber = pickingTaskId.substring(pickingTaskId.length() - 4);
        String orderNumber = orderId.substring(orderId.length() - 4);
        String fullNumber = assemblyNumber + " *" + orderNumber;
        pickingPage.enterOrderNumberInSearchFld(orderId);
        pickingPage.clickDocumentInLeftMenu(fullNumber);
        PickingContentPage pickingContentPage = new PickingContentPage();

        PickingTaskData pickingTaskDataBefore = pickingContentPage.getPickingTaskData();
        PickingTaskData newPickingTaskData = pickingTaskDataBefore.clone();

        // Step 1
        step("Нажать на кнопку редактирования (карандаш) в нижней части экрана");
        pickingContentPage.clickEditAssemblyButton();

        // Step 2
        step("Нажать на чекбокс в правом верхнем углу карточки товара");
        pickingContentPage.setSplitForProductCard(1, true)
                .checkSelectAllOptionIsSelected(true);

        // Step 3
        step("Нажать на кнопку Разделить");
        SplitPickingModalStep1 splitPickingModalStep1 = pickingContentPage.clickSplitAssemblyButton()
                .verifyRequiredElements()
                .shouldContainsProducts(newPickingTaskData.getSplitPickingProductDataList())
                .shouldContinueButtonIsDisabled();

        // Step 4
        step("Выбрать зону сборки Склад");
        splitPickingModalStep1.selectAssemblyType(assemblyType)
                .shouldContainsProducts(newPickingTaskData.getSplitPickingProductDataList())
                .shouldContinueButtonIsDisabled();
        // TODO
    }

    @Test(description = "C23408737 Сплит сборки с несколькими товарами", groups = NEED_PRODUCTS_GROUP)
    public void testSplitAssemblyWithDifferentProducts() throws Exception {
        initCreateOrder(2);
        // Test data
        PickingConst.AssemblyType assemblyType = PickingConst.AssemblyType.SHOPPING_ROOM;
        int editQuantity = 1;

        PickingPage pickingPage = loginSelectShopAndGoTo(PickingPage.class);
        initFindPickingTask();
        String assemblyNumber = pickingTaskId.substring(pickingTaskId.length() - 4);
        String orderNumber = orderId.substring(orderId.length() - 4);
        String fullNumber = assemblyNumber + " *" + orderNumber;
        pickingPage.enterOrderNumberInSearchFld(orderId);
        pickingPage.clickDocumentInLeftMenu(fullNumber);
        PickingContentPage pickingContentPage = new PickingContentPage();

        PickingTaskData pickingTaskDataBefore = pickingContentPage.getPickingTaskData();
        PickingTaskData newPickingTaskData = pickingTaskDataBefore.clone();

        // Step 1
        step("Нажать на кнопку редактирования (карандаш) в нижней части экрана");
        pickingContentPage.clickEditAssemblyButton();

        // Step 2
        step("Нажать на 'Выбрать все'");
        pickingContentPage.setSelectAllOption(true)
                .checkSelectAllOptionIsSelected(true);

        // Step 3
        step("Нажать на кнопку Разделить");
        SplitPickingModalStep1 splitPickingModalStep1 = pickingContentPage.clickSplitAssemblyButton()
                .verifyRequiredElements()
                .shouldContainsProducts(pickingTaskDataBefore.getSplitPickingProductDataList())
                .shouldContinueButtonIsDisabled();

        // Step 4
        step("Выбрать зону сборки");
        splitPickingModalStep1.selectAssemblyType(assemblyType)
                .shouldContainsProducts(pickingTaskDataBefore.getSplitPickingProductDataList())
                .shouldContinueButtonIsDisabled();

        // Step 5
        step("Изменить количество для одного из товаров");
        List<SplitPickingModalStep1.SplitProductCardData> splitProductDataList = newPickingTaskData.getSplitPickingProductDataList();
        SplitPickingModalStep1.SplitProductCardData editProduct = splitProductDataList.get(0);
        editProduct.setWantToMoveQuantity(editQuantity);
        editProduct.setMoveToNewQuantity(editQuantity);
        editProduct.setRemainInOriginalQuantity(editProduct.getOriginalAssemblyQuantity() - editQuantity);
        splitPickingModalStep1.clickEditButton()
                .editWantToMoveQuantity(1, editQuantity)
                .clickSaveButton()
                .shouldContainsProducts(splitProductDataList);

        // Step 6
        step("Нажать чекбокс, соответствующий зоне сборки");
        splitPickingModalStep1.selectConfirmCheckBox(true)
                .shouldContinueButtonIsEnabled();

        // Step 7
        step("Нажать кнопку Продолжить");
        SplitPickingModalStep2 splitPickingModalStep2 = splitPickingModalStep1.clickContinueButton()
                .verifyRequiredElements()
                .shouldIamResponsibleForAssemblyOptionSelected();

        // Step 8
        step("Заполнить комментарий и Нажать на кнопку Создать сборку");
        String comment = RandomStringUtils.randomAlphabetic(15);
        splitPickingModalStep2.enterComment(comment);
        SuccessfullyCreatedAssemblyModal successfullyCreatedAssemblyModal = splitPickingModalStep2
                .clickCreateAssemblyButton()
                .verifyRequiredElements()
                .shouldOrderNumberIs(orderNumber);
        newPickingTaskData.setNumber(successfullyCreatedAssemblyModal.getAssemblyNumber());
        newPickingTaskData.setAssemblyType(assemblyType);
        newPickingTaskData.setCreationDate(null);
        newPickingTaskData.getProducts().get(0).decreaseOrderedQuantity(editQuantity);

        // Step 9
        step("Нажать кнопку Вернуться к оригинальной сборке");
        pickingTaskDataBefore.getProducts().get(0).decreaseOrderedQuantity(editQuantity);
        pickingTaskDataBefore.getProducts().get(1).setOrderedQuantity(0);
        pickingContentPage = successfullyCreatedAssemblyModal.clickRemainOldTaskButton();
        pickingContentPage.shouldPickingTaskDataIs(pickingTaskDataBefore);

        // Step 10
        step("Открыть новую сборку");
        pickingContentPage.enterOrderNumberInSearchFld(newPickingTaskData.getOrderLinkNumber());
        pickingContentPage.clickDocumentInLeftMenu(newPickingTaskData.getNumber());

        pickingContentPage.shouldPickingTaskDataIs(newPickingTaskData);

        ShortPickingTaskData shortPickingTaskData = pickingContentPage.getPickingTaskData()
                .getShortData();
        shortPickingTaskData.setCollector("Я");

        pickingContentPage.switchToCommentTab()
                .shouldCommentIs(comment);

        pickingContentPage.refreshDocumentList();
        pickingContentPage.shouldDocumentListContainsThis(shortPickingTaskData);
    }

    @Test(description = "C23185844 Частичная сборка заказа", groups = NEED_PRODUCTS_GROUP)
    public void testPartialOrderAssembly() throws Exception {
        initCreateOrder(3);

        PickingPage pickingPage = loginSelectShopAndGoTo(PickingPage.class);
        initFindPickingTask();
        String assemblyNumber = pickingTaskId.substring(pickingTaskId.length() - 4);
        String orderNumber = orderId.substring(orderId.length() - 4);
        String fullNumber = assemblyNumber + " *" + orderNumber;
        pickingPage.enterOrderNumberInSearchFld(orderId);
        pickingPage.clickDocumentInLeftMenu(fullNumber);
        PickingContentPage pickingContentPage = new PickingContentPage();

        PickingTaskData pickingTaskDataBefore = pickingContentPage.getPickingTaskData();

        // Step 1
        step("Нажать на кнопку Начать сборку");
        pickingContentPage.clickStartAssemblyButton();

        // Step 2
        step("Товар 1: Ввести в инпут Собрано количество больше, чем указано в Заказано");
        int collectedQuantityProduct1 = pickingTaskDataBefore.getProducts().get(0).getOrderedQuantity();
        pickingContentPage.shouldProductCollectedQuantityIs(1, 0)
                .editCollectQuantity(1, collectedQuantityProduct1 + 1)
                .shouldProductCollectedQuantityIs(1, collectedQuantityProduct1);

        // Step 3
        step("Товар 1: Ввести в инпут Собрано количество меньше, чем указано в Заказано. " +
                "Указать причину отсутствия");
        collectedQuantityProduct1 = 1;
        PickingContentPage.ReasonForLackOfProductModal.Reasons reason1 =
                PickingContentPage.ReasonForLackOfProductModal.Reasons.EXAMPLE_PRODUCT;
        pickingContentPage
                .editCollectQuantity(1, collectedQuantityProduct1)
                .selectReasonForLackOfProduct(1, reason1)
                .shouldProductReasonIs(1, reason1)
                .shouldProductCollectedQuantityIs(1, collectedQuantityProduct1);

        // Step 4
        step("Товар 2: Оставить в инпуте Собрано дефолтное значение 0");
        pickingContentPage.shouldProductCollectedQuantityIs(2, 0);

        // Step 5
        step("Товар 3: Ввести в инпут Собрано количество равное указанному в Заказано");
        int collectedQuantityProduct3 = pickingTaskDataBefore.getProducts().get(2).getOrderedQuantity();
        pickingContentPage
                .editCollectQuantity(3, collectedQuantityProduct3)
                .shouldProductCollectedQuantityIs(3, collectedQuantityProduct3);

        // Step 6
        step("Проверить кнопку Завершить");
        pickingContentPage.shouldFinishButtonCountIs(1, 3)
                .checkIfFinishButtonIsEnabled(false);

        // Step 7
        step("Товар 2: Указать причину отсутствия");
        PickingContentPage.ReasonForLackOfProductModal.Reasons reason2 =
                PickingContentPage.ReasonForLackOfProductModal.Reasons.DEFECTIVE_PRODUCT;
        pickingContentPage.selectReasonForLackOfProduct(2, reason2)
                .shouldProductReasonIs(2, reason2);

        // Step 8
        step("Проверить кнопку Завершить");
        pickingContentPage.shouldFinishButtonCountIs(1, 3)
                .checkIfFinishButtonIsEnabled(true);

        // Step 9        pickingTaskDataBefore.setStatus(SalesDocumentsConst.States.PARTIALLY_PICKED.getUiVal() + " 1/3");
        step("Нажать на кнопку Завершить");
        List<PickingProductCardData> pickingProducts = pickingTaskDataBefore.getProducts();
        pickingProducts.get(0).setCollectedQuantity(collectedQuantityProduct1);
        pickingProducts.get(0).setReasonOfLack(reason1.getTitle());
        pickingProducts.get(2).setCollectedQuantity(collectedQuantityProduct3);
        pickingProducts.get(1).setReasonOfLack(reason2.getTitle());
        pickingContentPage.clickFinishAssemblyButton()
                .shouldPickingTaskDataIs(pickingTaskDataBefore);

        // Step 10
        step("Перейти в заказ, кликнув на ссылку в названии сборки");
        pickingContentPage.clickOrderLinkAndGoToOrderPage()
                .shouldOrderStatusIs("ЧАСТИЧНО СОБРАН");
    }

    @Test(description = "C23408358 Сплит сборки с изменением количества товара", groups = NEED_PRODUCTS_GROUP)
    public void testSplitAssemblyWithChangingProductQuantity() throws Exception {
        if (isStartFromScratch())
            testPartialOrderAssembly();

        // Test data
        PickingConst.AssemblyType assemblyType = PickingConst.AssemblyType.SHOPPING_ROOM;
        PickingPage pickingPage;
        if (isStartFromScratch()) {
            pickingPage = loginSelectShopAndGoTo(PickingPage.class);
        } else {
            new MenuPage().goToPage(PickingPage.class);
            pickingPage = new PickingPage();
        }
        String assemblyNumber = pickingTaskId.substring(pickingTaskId.length() - 4);
        String orderNumber = orderId.substring(orderId.length() - 4);
        String fullNumber = assemblyNumber + " *" + orderNumber;
        pickingPage.clickDocumentInLeftMenu(fullNumber);

        // Step 1
        step("Нажать на кнопку редактирования (карандаш) в нижней части экрана");
        PickingContentPage pickingContentPage = new PickingContentPage();
        PickingTaskData pickingTaskDataBefore = pickingContentPage.getPickingTaskData();
        PickingTaskData newPickingTaskData = pickingTaskDataBefore.clone();
        pickingContentPage.clickEditAssemblyButton();

        // Step 2
        step("Нажать на чекбокс в правом верхнем углу карточки товара");
        pickingContentPage.setSplitForProductCard(2, true)
                .checkSelectAllOptionIsSelected(false);

        // Step 3
        step("Нажать на кнопку Разделить");
        PickingProductCardData movePickingProduct = newPickingTaskData.getProducts().get(1);
        SplitPickingModalStep1.SplitProductCardData splitProductData = new SplitPickingModalStep1.SplitProductCardData(movePickingProduct);

        SplitPickingModalStep1 splitPickingModalStep1 = pickingContentPage.clickSplitAssemblyButton()
                .verifyRequiredElements()
                .shouldContainsProducts(Collections.singletonList(splitProductData))
                .shouldContinueButtonIsDisabled();

        // Step 4
        step("Выбрать зону сборки Торговый зал");
        splitPickingModalStep1.selectAssemblyType(assemblyType)
                .shouldContainsProducts(Collections.singletonList(splitProductData))
                .shouldContinueButtonIsDisabled();

        // Step 5
        step("Нажать на кнопку редактирования (карандаш)");
        splitPickingModalStep1.clickEditButton();

        // Step 6
        step("Ввести число меньшее, чем товара в сборке. Нажать на кнопку Сохранить");
        int editQuantity = 1;
        splitProductData.setWantToMoveQuantity(editQuantity);
        splitProductData.setMoveToNewQuantity(editQuantity);
        splitProductData.setRemainInOriginalQuantity(splitProductData.getOriginalAssemblyQuantity() - editQuantity);
        splitPickingModalStep1.editWantToMoveQuantity(1, editQuantity)
                .clickSaveButton()
                .shouldContainsProducts(Collections.singletonList(splitProductData));

        // Step 7
        step("Нажать чекбокс Собрать из торгового зала (LS)");
        splitPickingModalStep1.selectConfirmCheckBox(true)
                .shouldContinueButtonIsEnabled();

        // Step 8
        step("Нажать кнопку Продолжить");
        SplitPickingModalStep2 splitPickingModalStep2 = splitPickingModalStep1.clickContinueButton()
                .verifyRequiredElements()
                .shouldIamResponsibleForAssemblyOptionSelected();

        // Step 9
        step("Заполнить комментарий и Нажать на кнопку Создать сборку");
        String comment = RandomStringUtils.randomAlphabetic(15);
        splitPickingModalStep2.enterComment(comment);
        SuccessfullyCreatedAssemblyModal successfullyCreatedAssemblyModal = splitPickingModalStep2
                .clickCreateAssemblyButton()
                .verifyRequiredElements()
                .shouldOrderNumberIs(orderNumber);
        newPickingTaskData.setStatus(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        newPickingTaskData.setNumber(successfullyCreatedAssemblyModal.getAssemblyNumber());
        newPickingTaskData.setAssemblyType(assemblyType);
        newPickingTaskData.setCreationDate(null);
        newPickingTaskData.setProducts(Collections.singletonList(movePickingProduct));
        movePickingProduct.decreaseOrderedQuantity(editQuantity);

        // Step 10
        step("Нажать кнопку Перейти к исходной сборке");
        pickingContentPage = successfullyCreatedAssemblyModal.clickRemainOldTaskButton();
        pickingTaskDataBefore.getProducts().get(1).decreaseOrderedQuantity(editQuantity);
        pickingContentPage.shouldPickingTaskDataIs(pickingTaskDataBefore);

        // Step 11
        step("Перейти к новой сборке");
        pickingContentPage.enterOrderNumberInSearchFld(newPickingTaskData.getOrderLinkNumber());
        pickingContentPage.clickDocumentInLeftMenu(newPickingTaskData.getNumber());

        pickingContentPage.shouldPickingTaskDataIs(newPickingTaskData);

        ShortPickingTaskData shortPickingTaskData = newPickingTaskData
                .getShortData();
        shortPickingTaskData.setCollector("Я");
        pickingContentPage.shouldDocumentListContainsThis(shortPickingTaskData);

        pickingContentPage.switchToCommentTab()
                .shouldCommentIs(comment);
    }


    @Test(description = "C23416311 Заказы. Переход из статуса Готов к сборке в статус Собран", groups = NEED_PRODUCTS_GROUP)
    public void testMoveFromReadyPickingToPicked() throws Exception {

        initCreateOrder(1);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);
        initFindPickingTask();

        // Step 2:
        step("Ввести номер заказа из корзины и нажать кнопку 'Показать заказы'" + "Заказ" + " " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 3:

        step("Кликнуть на заказ" + " " + orderId);
        orderPage.clickDocumentInLeftMenu(orderId);
        OrderCreatedContentPage createdContentPage = new OrderCreatedContentPage();
        createdContentPage.shouldOrderProductCountIs(1);

        // Step 4:

        step("Перейти на Сборки");
        AssemblyOrderPage pickingTab = createdContentPage.clickGoToPickings();

        // Step5: нажать на Сборку
        step("нажать на Сборку");
        PickingContentPage pickingContentPage = pickingTab.clickToPickingTask(1);

        // Step6: Начать сборку
        step("Нажать на кнопку Начать сборку");
        pickingContentPage.clickStartAssemblyButton();

        // Step7:
        step("Товар 1: Ввести в инпут Собрано количество равное,  указанному в Заказано");
        pickingContentPage.editCollectQuantity(1, 2)
                .shouldProductCollectedQuantityIs(1, 2);

        // Step 8:
        step("Завершить сборку");
        pickingContentPage.clickFinishAssemblyButton();

        // Step 9:
        step("Вернуться на страницу заказов ");
        PickingPage pickingPage = new PickingPage();
        pickingPage.clickOrderLinkAndGoToOrderPage();


        // Step 10:
        step("Проверить статус собранного заказа");
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.PICKED.getUiVal());
        orderPage.shouldDocumentCountIs(1);
    }

    @Test(description = "C23428132 Заказы. Переход из статуса Собран в статус Выдан", groups = NEED_PRODUCTS_GROUP)
    public void testMoveFromPickedToGivenAway() throws Exception {

        // Создать заказ и перевести его в статус "Собран"

        initCreateOrder(1, SalesDocumentsConst.States.PICKED);

        // Step 1:
        step("Открыть страницу с Заказами");
        OrderHeaderPage orderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);

        // Step 2:
        step("Найти созданный заказ с статусе Собран с номером" + " " + orderId);
        orderPage.enterSearchTextAndSubmit(orderId);
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.PICKED.getUiVal());
        orderPage.shouldDocumentCountIs(1);

        // Step 3:
        step("Кликнуть на заказ" + " " + orderId);
        orderPage.clickDocumentInLeftMenu(orderId);
        OrderCreatedContentPage createdContentPage = new OrderCreatedContentPage();
        createdContentPage.shouldOrderProductCountIs(1);

        // Step 4:
        step("Перейти на вкладку 'К выдаче и возврату'");
        GiveAwayShipOrderPage giveAwayShipOrderPage = createdContentPage.clickGoToShipRefund();

        // Step 5
        step("Товар 1: Ввести в инпут 'К выдаче' количество равное,  указанному в Заказано");
        giveAwayShipOrderPage.editToShipQuantity(1, 2)
                .shouldProductToShipQuantityIs(1, 2);

        // Step 6
        step("Нажать на кнопку 'Выдать'");
        giveAwayShipOrderPage.clickGiveAwayButton();

        // Step 7:
        step("Обновить список документов и проверить статус выданного заказа");
        orderPage.refreshDocumentList();
        orderPage.shouldDocumentIsPresent(orderId);
        orderPage.shouldDocumentListContainsOnlyWithStatuses(SalesDocumentsConst.States.GIVEN_AWAY.getUiVal());
        orderPage.shouldDocumentCountIs(1);
    }
}
