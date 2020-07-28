package com.leroy.magportal.ui.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.StatusCodes;
import com.leroy.constants.customer.CustomerConst;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.clients.CartClient;
import com.leroy.magmobile.api.data.customer.PhoneData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartData;
import com.leroy.magmobile.api.data.sales.cart_estimate.cart.CartProductOrderData;
import com.leroy.magmobile.api.data.sales.orders.*;
import com.leroy.magmobile.ui.constants.TestDataConstants;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.clients.PickingTaskClient;
import com.leroy.magportal.api.data.picking.PickingTaskDataList;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.PickingTaskData;
import com.leroy.magportal.ui.models.picking.ShortPickingTaskData;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import com.leroy.magportal.ui.pages.picking.PickingPage;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep1;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep2;
import com.leroy.magportal.ui.pages.picking.modal.SuccessfullyCreatedAssemblyModal;
import com.leroy.utils.ParserUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class PickingTest extends WebBaseSteps {

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("35");
        return sessionData;
    }

    private String orderId;
    private String pickingTaskId;

    private void initCreateOrder(int productCount) throws Exception {
        orderId = createConfirmedOrder(null, null, productCount);
    }

    private void initFindPickingTask() throws Exception {
        OrderClient orderClient = apiClientProvider.getOrderClient();
        orderClient.waitUntilOrderHasStatusAndReturnOrderData(orderId,
                SalesDocumentsConst.States.ALLOWED_FOR_PICKING.getApiVal());
        PickingTaskClient pickingTaskClient = apiClientProvider.getPickingTaskClient();
        Response<PickingTaskDataList> respPickingTasks = pickingTaskClient.searchForPickingTasks(orderId);
        assertThat(respPickingTasks, successful());
        pickingTaskId = respPickingTasks.asJson().getItems().get(0).getTaskId();
    }

    @AfterMethod
    private void cancelConfirmedOrder() throws Exception {
        if (orderId != null) {
            OrderClient orderClient = apiClientProvider.getOrderClient();
            Response<JsonNode> resp = orderClient.cancelOrder(orderId);
            assertThat(resp, successful());
        }
    }

    protected String createConfirmedOrder(
            List<String> lmCodes, List<CartProductOrderData> productDataList, Integer productCount) {
        // Создание корзины
        List<CartProductOrderData> productOrderDataList = productDataList == null ? new ArrayList<>() : productDataList;
        if (productDataList == null) {
            if (lmCodes == null)
                lmCodes = apiClientProvider.getProductLmCodes(productCount);
            for (String lmCode : lmCodes) {
                CartProductOrderData productOrderData = new CartProductOrderData();
                productOrderData.setLmCode(lmCode);
                productOrderData.setQuantity(2.0);
                productOrderDataList.add(productOrderData);
            }
        }
        CartClient cartClient = apiClientProvider.getCartClient();
        getUserSessionData().setAccessToken(getAccessToken());
        Response<CartData> cartDataResponse = cartClient.sendRequestCreate(productOrderDataList);
        assertThat(cartDataResponse, successful());

        CartData cartData = cartDataResponse.asJson();

        // Создание черновика заказа
        ReqOrderData reqOrderData = new ReqOrderData();
        reqOrderData.setCartId(cartData.getCartId());
        reqOrderData.setDateOfGiveAway(LocalDateTime.now().plusDays(1));
        reqOrderData.setDocumentVersion(1);

        List<ReqOrderProductData> orderProducts = new ArrayList<>();
        for (CartProductOrderData cartProduct : cartData.getProducts()) {
            ReqOrderProductData postProductData = new ReqOrderProductData();
            postProductData.setLineId(cartProduct.getLineId());
            postProductData.setLmCode(cartProduct.getLmCode());
            postProductData.setQuantity(cartProduct.getQuantity());
            postProductData.setPrice(cartProduct.getPrice());
            orderProducts.add(postProductData);
        }

        reqOrderData.setProducts(orderProducts);

        OrderClient orderClient = apiClientProvider.getOrderClient();
        Response<OrderData> orderResp = orderClient.createOrder(reqOrderData);
        OrderData orderData = orderClient.assertThatIsCreatedAndGetData(orderResp);

        // Установка ПИН кода
        String validPinCode = apiClientProvider.getValidPinCode();
        Response<JsonNode> response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        if (response.getStatusCode() == StatusCodes.ST_409_CONFLICT) {
            response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        }
        if (response.getStatusCode() == StatusCodes.ST_400_BAD_REQ) {
            validPinCode = apiClientProvider.getValidPinCode();
            response = orderClient.setPinCode(orderData.getOrderId(), validPinCode);
        }
        orderClient.assertThatPinCodeIsSet(response);
        orderData.setPinCode(validPinCode);
        orderData.increasePaymentVersion();

        // Подтверждение заказа
        MagCustomerData customerData = TestDataConstants.CUSTOMER_DATA_1;
        OrderCustomerData orderCustomerData = new OrderCustomerData();
        orderCustomerData.setFirstName(ParserUtil.parseFirstName(customerData.getName()));
        orderCustomerData.setLastName(ParserUtil.parseLastName(customerData.getName()));
        orderCustomerData.setRoles(Collections.singletonList(CustomerConst.Role.RECEIVER.name()));
        orderCustomerData.setType(CustomerConst.Type.PERSON.name());
        orderCustomerData.setPhone(new PhoneData(customerData.getPhone()));

        OrderData confirmOrderData = new OrderData();
        confirmOrderData.setPriority(SalesDocumentsConst.Priorities.HIGH.getApiVal());
        confirmOrderData.setShopId(getUserSessionData().getUserShopId());
        confirmOrderData.setSolutionVersion(orderData.getSolutionVersion());
        confirmOrderData.setPaymentVersion(orderData.getPaymentVersion());
        confirmOrderData.setFulfillmentVersion(orderData.getFulfillmentVersion());
        confirmOrderData.setFulfillmentTaskId(orderData.getFulfillmentTaskId());
        confirmOrderData.setPaymentTaskId(orderData.getPaymentTaskId());
        confirmOrderData.setProducts(orderData.getProducts());
        confirmOrderData.setCustomers(Collections.singletonList(orderCustomerData));

        GiveAwayData giveAwayData = new GiveAwayData();
        giveAwayData.setDate(LocalDateTime.now().plusDays(1));
        giveAwayData.setPoint(SalesDocumentsConst.GiveAwayPoints.PICKUP.getApiVal());
        giveAwayData.setShopId(Integer.valueOf(getUserSessionData().getUserShopId()));
        confirmOrderData.setGiveAway(giveAwayData);

        Response<OrderData> resp = orderClient.confirmOrder(orderData.getOrderId(), confirmOrderData);
        if (!resp.isSuccessful())
            resp = orderClient.confirmOrder(orderData.getOrderId(), confirmOrderData);
        orderClient.assertThatIsConfirmed(resp, orderData);
        return orderData.getOrderId();
    }

    @Test(description = "C23408356 Сплит сборки (зона сборки Торговый зал)")
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
                .shouldSelectAllOptionIsSelected();

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

    @Test(description = "C23408338 Сплит сборки (зона сборки СС)")
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
                .shouldSelectAllOptionIsSelected();

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
        shortPickingTaskData.setClient(TestDataConstants.CUSTOMER_DATA_1.getName());
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
                .shouldSelectAllOptionIsSelected();

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

    @Test(description = "C23408737 Сплит сборки с несколькими товарами")
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
                .shouldSelectAllOptionIsSelected();

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

}
