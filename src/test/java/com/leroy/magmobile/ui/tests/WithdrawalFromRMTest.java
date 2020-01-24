package com.leroy.magmobile.ui.tests;

import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.more.UserProfilePage;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.work.*;
import com.leroy.magmobile.ui.pages.work.modal.QuantityProductsForWithdrawalModalPage;
import com.leroy.models.ProductCardData;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class WithdrawalFromRMTest extends AppBaseSteps {

    @Test(description = "C3132493 Создание заявки на Отзыв RM из раздела Работа")
    public void testC3132493() throws Exception {
        // Pre-condition
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        UserProfilePage userProfilePage = setShopAndDepartmentForUser(
                salesPage, "5", "01");

        // Step #1
        log.step("Зайти в раздел Работа");
        WorkPage workPage = userProfilePage.goToWork()
                .verifyRequiredElements();

        // Step #2
        log.step("Нажать на иконку + рядом с Отзыв с RM");
        StockProductsPage stockProductsPage = workPage.clickWithdrawalFromRMPlusIcon()
                .shouldAnyProductAvailableOnPage();

        // Step #3
        log.step("Выбрать первый товар, который поштучно хранится на складе");
        ProductCardData selectedProductDataBefore = stockProductsPage.getPieceProductInfoByIndex(0);
        StockProductCardPage productCardPage = stockProductsPage.clickFirstPieceProduct()
                .verifyRequiredElements();

        // Step #4
        log.step("Нажать кнопку ОТОЗВАТЬ");
        QuantityProductsForWithdrawalModalPage modalPage = productCardPage.clickWithdrawalBtnForEnterQuantity();
        modalPage.shouldKeyboardVisible();

        // Step #5
        log.step("Ввести количество товара для отзыва");
        String numberForRM = String.valueOf(new Random().nextInt(11) + 1);
        selectedProductDataBefore.setSelectedQuantity(numberForRM);
        modalPage.enterCountOfItems(numberForRM)
                .shouldWithdrawalButtonHasQuantity(numberForRM);

        // Step #6
        log.step("Нажать кнопку ОТОЗВАТЬ");
        modalPage.clickSubmitBtn()
                .verifyRequiredElements()
                .shouldCountOfSelectedProductsIs(1)
                .shouldSelectedProductIs(0, selectedProductDataBefore);

        // Step #7
        log.step("Нажать кнопку ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ");
        OrderPage orderPage = stockProductsPage.clickSubmitBtn()
                .verifyRequiredElements();
        String orderNumber = orderPage.getOrderNumber();

        // Step #8
        log.step("Нажать на поле даты поставки и меняем дату и подтвердить изменение");
        LocalDate testDate = LocalDate.now().plusDays(1);
        orderPage.editDeliveryDate(testDate)
                .shouldDateFieldIs(testDate);

        // Step #9
        log.step("Изменить ожидаемое время доставки и подтвердить его");
        LocalTime timeForSelect = LocalTime.now().plusHours(1).plusMinutes(5);
        orderPage.editDeliveryTime(timeForSelect)
                .shouldTimeFieldIs(timeForSelect);

        // Step #10
        log.step("Ввести комментарий и подтвердить его");
        String testText = RandomStringUtils.randomAlphanumeric(10);
        orderPage.editComment(testText)
                .shouldCommentFieldIs(testText);

        // Step #11
        log.step("Нажать кнопку ОТПРАВИТЬ ЗАЯВКУ");
        SubmittedWithdrawalOrderPage submittedWithdrawalOrderPage = orderPage.clickSubmitBtn()
                .verifyRequiredElements();

        // Step #12
        log.step("Нажать кнопку ПЕРЕЙТИ В СПИСОК ЗАЯВОК");
        OrdersListPage ordersListPage = submittedWithdrawalOrderPage.clickSubmitBtn()
                .shouldOrderByIndexIs(1, orderNumber, null, "Создана");

        // Step #13
        log.step("Открыть заявку и проверить заполненные поля и товары");
        OrderDetailsPage orderDetailsPage = ordersListPage.clickOrderByIndex(0)
                .shouldFormDataIs("Торговый зал", testDate,
                        timeForSelect, testText)
                .shouldProductByIndexIs(1, selectedProductDataBefore);
    }

}
