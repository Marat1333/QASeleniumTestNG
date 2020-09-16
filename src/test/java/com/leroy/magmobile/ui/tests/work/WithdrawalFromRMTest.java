package com.leroy.magmobile.ui.tests.work;

import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.models.work.WithdrawalProductCardData;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.work.*;
import com.leroy.magmobile.ui.pages.work.modal.QuantityProductsForWithdrawalModalPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class WithdrawalFromRMTest extends AppBaseSteps {

    @Test(description = "C3132493 Создание заявки на Отзыв RM из раздела Работа", groups = OLD_SHOP_GROUP)
    public void testC3132493() throws Exception {
        // Pre-condition
        MainProductAndServicesPage mainProductAndServicesPage = loginSelectShopAndGoTo(
                MainProductAndServicesPage.class);

        // Step #1
        step("Зайти в раздел Работа");
        WorkPage workPage = mainProductAndServicesPage.goToWork()
                .verifyRequiredElements();

        // Step #2
        step("Нажать на иконку + рядом с Отзыв с RM");
        StockProductsPage stockProductsPage = workPage.clickWithdrawalFromRMPlusIcon();
        stockProductsPage.shouldAnyProductAvailableOnPage();

        // Step #3
        step("Выбрать первый товар, который поштучно хранится на складе");
        WithdrawalProductCardData selectedProductDataBefore = stockProductsPage.getProductInfoByIndex(0);
        StockProductCardPage productCardPage = stockProductsPage.clickFirstPieceProduct()
                .verifyRequiredElements();

        // Step #4
        step("Нажать кнопку ОТОЗВАТЬ");
        QuantityProductsForWithdrawalModalPage modalPage = productCardPage.clickWithdrawalBtnForEnterQuantity();
        modalPage.verifyRequiredElements();

        // Step #5
        step("Ввести количество товара для отзыва");
        String numberForRM = String.valueOf(new Random().nextInt(11) + 1);
        selectedProductDataBefore.minusAvailableQuantity(Double.parseDouble(numberForRM));
        selectedProductDataBefore.setSelectedQuantity(Double.valueOf(numberForRM));

        modalPage.enterCountOfItems(numberForRM)
                .shouldWithdrawalButtonHasQuantity(numberForRM);

        // Step #6
        step("Нажать кнопку ОТОЗВАТЬ");
        modalPage.clickSubmitBtn()
                .verifyRequiredElements()
                .shouldCountOfSelectedProductsIs(1)
                .shouldSelectedProductIs(1, selectedProductDataBefore);

        // Step #7
        step("Нажать кнопку ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ");
        OrderPage orderPage = stockProductsPage.clickSubmitBtn()
                .verifyRequiredElements();
        String orderNumber = orderPage.getOrderNumber();

        // Step #8
        step("Нажать на поле даты поставки и меняем дату и подтвердить изменение");
        LocalDate testDate = LocalDate.now().plusDays(1);
        orderPage.editDeliveryDate(testDate)
                .shouldDateFieldIs(testDate);

        // Step #9
        step("Изменить ожидаемое время доставки и подтвердить его");
        LocalTime timeForSelect = LocalTime.now().plusHours(1).plusMinutes(5);
        orderPage.editDeliveryTime(timeForSelect)
                .shouldTimeFieldIs(timeForSelect);

        // Step #10
        step("Ввести комментарий и подтвердить его");
        String testText = RandomStringUtils.randomAlphanumeric(10);
        orderPage.editComment(testText)
                .shouldCommentFieldIs(testText);

        // Step #11
        step("Нажать кнопку ОТПРАВИТЬ ЗАЯВКУ");
        SubmittedWithdrawalOrderPage submittedWithdrawalOrderPage = orderPage.clickSubmitBtn()
                .verifyRequiredElements();

        // Step #12
        step("Нажать кнопку ПЕРЕЙТИ В СПИСОК ЗАЯВОК");
        OrdersListPage ordersListPage = submittedWithdrawalOrderPage.clickSubmitBtn()
                .shouldOrderByIndexIs(1, orderNumber, null, "Создана");

        // Step #13
        step("Открыть заявку и проверить заполненные поля и товары");
        OrderDetailsPage orderDetailsPage = ordersListPage.clickOrderByIndex(0)
                .shouldFormDataIs("Торговый зал", testDate,
                        timeForSelect, testText)
                .shouldProductByIndexIs(1, selectedProductDataBefore);
    }

}
