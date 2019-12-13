package com.leroy.tests.app;

import com.leroy.models.ProductCardData;
import com.leroy.pages.app.*;
import com.leroy.pages.app.widgets.OrderWidget;
import com.leroy.pages.app.widgets.ProductCardWidget;
import com.leroy.pages.app.work.OrderDetailsPage;
import com.leroy.pages.app.work.OrdersListPage;
import com.leroy.tests.app.helpers.BaseAppSteps;
import com.leroy.utils.DateTimeUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class SalesDocumentsTest extends BaseAppSteps {

    @Test(description = "C3132493 Создание заявки на Отзыв RM из раздела Работа")
    public void testC3201013() throws Exception {
        // Pre-condition
        loginInAndGoTo(SALES_SECTION);
        UserProfilePage userProfilePage = setShopAndDepartmentForUser("5", "01");

        // Step #1
        log.step("Зайти в раздел Работа");
        WorkPage workPage = userProfilePage.goToWork();
        softAssert.isElementTextEqual(workPage.titleObj, WorkPage.TITLE);
        softAssert.isElementTextEqual(workPage.withdrawalFromRMLabel, "Отзыв с RM");
        softAssert.isElementVisible(workPage.withdrawalFromRMPlusIcon);

        // Step #2
        log.step("Нажать на иконку + рядом с Отзыв с RM");
        StockProductsPage stockProductsPage = workPage.clickWithdrawalFromRMPlusIcon();
        softAssert.isTrue(stockProductsPage.isAnyProductAvailableOnPage(),
                "Открывается список товаров на складе");

        // Step #3
        log.step("Выбрать первый товар, который поштучно хранится на складе");
        ProductCardData selectedProductDataBefore = stockProductsPage.getPieceProductInfoByIndex(0);
        ProductCardPage productCardPage = stockProductsPage.clickFirstPieceProduct();
        softAssert.isElementVisible(productCardPage.productCardHeaderArea);

        // Step #4
        log.step("Нажать кнопку ОТОЗВАТЬ");
        productCardPage.withdrawalBtn.click();
        softAssert.isTrue(productCardPage.isKeyboardVisible(),
                "Клавиатура для ввода должна быть видна");

        // Step #5
        log.step("Ввести количество товара для отзыва");
        String numberForRM = String.valueOf(new Random().nextInt(11) + 1);
        productCardPage.enterCountOfItems(numberForRM);
        softAssert.isElementTextEqual(productCardPage.withdrawalBtnLabel,
                String.format("ОТОЗВАТЬ %s шт.", numberForRM));

        // Step #6
        log.step("Нажать кнопку ОТОЗВАТЬ");
        productCardPage.withdrawalBtn.click();
        stockProductsPage = new StockProductsPage(driver);
        softAssert.isElementVisible(stockProductsPage.selectedProductsLabel);
        softAssert.isEquals(stockProductsPage.getCountSelectedProducts(), 1,
                "Должен быть %s товар в секции ВЫБРАННЫЕ ТОВАРЫ");
        ProductCardData selectedProductDataAfter = stockProductsPage.getSelectedProductInfoByIndex(0);
        softAssert.isEquals(selectedProductDataAfter.getNumber(), selectedProductDataBefore.getNumber(),
                "Выбранный товар должен иметь номер %s");
        softAssert.isEquals(selectedProductDataAfter.getName(), selectedProductDataBefore.getName(),
                "Выбранный товар должен иметь название %s");
        softAssert.isEquals(selectedProductDataAfter.getSelectedQuantity(), numberForRM,
                "Выбранный товар должен иметь кол-во на отзыв %s");
        softAssert.isEquals(stockProductsPage.getCountOfProductsInBasket(), 1,
                "Внизу экрана есть иконка корзинки с цифрой %s");
        softAssert.isElementTextEqual(stockProductsPage.submitBtnLabel, "ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ");

        // Step #7
        log.step("Нажать кнопку ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ");
        OrderPage orderPage = stockProductsPage.clickSubmitBtn();
        String orderNumber = orderPage.getOrderNumber();
        softAssert.isElementVisible(orderPage.headerObj);
        softAssert.isTrue(orderPage.isOrderNumberVisibleAndValid(), "Номер заявки должен быть валиден");

        // Step #8
        log.step("Нажать на поле даты поставки и меняем дату и подтвердить изменение");
        LocalDate testDate = LocalDate.now().plusDays(1);
        orderPage.editDeliveryDate(testDate);
        LocalDate dateFromPage = DateTimeUtil.strToLocalDate(orderPage.deliveryDateLbl.getText(),
                "dd-го MMM");
        softAssert.isNotNull(dateFromPage, "Выбранная дата должна быть валидной");
        softAssert.isEquals(testDate.getDayOfMonth(), dateFromPage.getDayOfMonth(),
                "Выбранный день должен быть равен %s");
        softAssert.isEquals(testDate.getMonth(), dateFromPage.getMonth(),
                "Выбранный месяц должен быть равен %s");

        // Step #9
        log.step("Изменить ожидаемое время доставки и подтвердить его");
        LocalTime timeForSelect = LocalTime.now().plusHours(1).plusMinutes(5);
        orderPage.editDeliveryTime(timeForSelect);
        softAssert.isElementTextEqual(orderPage.deliveryTimeLbl,
                timeForSelect.format(DateTimeFormatter.ofPattern("HH:mm")));

        // Step #10
        log.step("Ввести комментарий и подтвердить его");
        String testText = RandomStringUtils.randomAlphanumeric(10);
        orderPage.commentFld.clearFillAndSubmit(testText);
        softAssert.isElementTextEqual(orderPage.commentFld, testText);

        // Step #11
        log.step("Нажать кнопку ОТПРАВИТЬ ЗАЯВКУ");
        SubmittedWithdrawalOrderPage submittedWithdrawalOrderPage = orderPage.clickSubmitBtn();
        softAssert.isElementTextEqual(submittedWithdrawalOrderPage.headerLbl,
                "Заявка на отзыв отправлена");
        softAssert.isElementTextEqual(submittedWithdrawalOrderPage.messageLbl,
                "Статус заявки можно отслеживать в списке заявок.");
        softAssert.isElementTextEqual(submittedWithdrawalOrderPage.submitBtnLbl,
                "ПЕРЕЙТИ В СПИСОК ЗАЯВОК");

        // Step #12
        log.step("Нажать кнопку ПЕРЕЙТИ В СПИСОК ЗАЯВОК");
        OrdersListPage ordersListPage = submittedWithdrawalOrderPage.clickSubmitBtn();
        OrderWidget orderWidget = ordersListPage.orderList.get(0);
        softAssert.isEquals(orderWidget.numberLbl.getText(), orderNumber,
                "Номер первой заявки должен быть %s");
        softAssert.isEquals(orderWidget.typeLbl.getText(), "Создана",
                "Статус первой завяки должен быть %s");

        // Step #13
        log.step("Открыть заявку и проверить заполненные поля и товары");
        orderWidget.click();
        OrderDetailsPage orderDetailsPage = new OrderDetailsPage(driver);
        softAssert.isElementTextEqual(orderDetailsPage.replenishmentMethod, "Торговый зал");
        softAssert.isElementTextEqual(orderDetailsPage.deliveryDate,
                testDate.format(DateTimeFormatter.ofPattern("dd-го MMM", new Locale("ru"))));
        softAssert.isElementTextEqual(orderDetailsPage.deliveryTime,
                timeForSelect.format(DateTimeFormatter.ofPattern("HH:mm")));
        softAssert.isElementTextEqual(orderDetailsPage.comment, testText);
        ProductCardWidget productCardWidget = orderDetailsPage.productsForWithdrawal.get(0);
        softAssert.isEquals(productCardWidget.getNumber(), selectedProductDataBefore.getNumber(),
                "Номер товара на отзыв должен быть %s");
        softAssert.isEquals(productCardWidget.getName(), selectedProductDataBefore.getName(),
                "Название товара на отзыв должно быть %s");
        softAssert.isEquals(productCardWidget.getQuantity(), selectedProductDataAfter.getSelectedQuantity(),
                "Кол-во товара на отзыв должно быть %s");
        softAssert.isEquals(productCardWidget.getQuantityType(), "шт.",
                "Тип кол-ва должен быть %s");
        softAssert.verifyAll();
    }

}
