package com.leroy.tests.app;

import com.leroy.constants.EnvConstants;
import com.leroy.models.ProductCardData;
import com.leroy.models.UserData;
import com.leroy.pages.LoginPage;
import com.leroy.pages.app.ProductCardPage;
import com.leroy.pages.app.StockProductsPage;
import com.leroy.pages.app.WorkPage;
import com.leroy.pages.app.common.BottomMenuPage;
import com.leroy.tests.BaseState;
import org.testng.annotations.Test;

import java.util.Random;

public class SalesDocumentsTest extends BaseState {

    @Test(description = "C3132493 Создание заявки на Отзыв RM из раздела Работа")
    public void testC3201013() throws Exception {
        // Pre-condition
        LoginPage loginPage = new LoginPage(driver);
        loginPage.logIn(new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS));

        BottomMenuPage bottomMenu = new BottomMenuPage(driver);
        bottomMenu.goToMoreSection().goToUserProfile().goToEditShopForm().searchForShopAndSelectById("5")
                .goToEditDepartmentForm().selectDepartmentById("01");

        // Step #1
        log.step("Зайти в раздел Работа");
        WorkPage workPage = bottomMenu.goToWork();
        softAssert.isElementTextEqual(workPage.titleObj, WorkPage.TITLE);
        softAssert.isElementTextEqual(workPage.withdrawalFromRMLabel, "Отзыв с RM");
        softAssert.isElementVisible(workPage.withdrawalFromRMPlusIcon);
        softAssert.verifyStep();

        // Step #2
        log.step("Нажать на иконку + рядом с Отзыв с RM");
        StockProductsPage stockProductsPage = workPage.clickWithdrawalFromRMPlusIcon();
        softAssert.isTrue(stockProductsPage.isAnyProductAvailableOnPage(),
                "Открывается список товаров на складе");
        softAssert.verifyStep();

        // Step #3
        log.step("Выбрать первый товар, который поштучно хранится на складе");
        ProductCardData selectedProductDataBefore = stockProductsPage.getPieceProductInfoByIndex(0);
        ProductCardPage productCardPage = stockProductsPage.clickFirstPieceProduct();
        softAssert.isElementVisible(productCardPage.productCardHeaderArea);
        softAssert.verifyStep();

        // Step #4
        log.step("Нажать кнопку ОТОЗВАТЬ");
        productCardPage.withdrawalBtn.click();
        softAssert.isTrue(productCardPage.isKeyboardVisible(), "Клавиатура для ввода должна быть видна");
        softAssert.verifyStep();

        // Step #5
        log.step("Ввести количество товара для отзыва");
        String numberForRM = String.valueOf(new Random().nextInt(11) + 1);
        productCardPage.enterCountOfItems(numberForRM);
        softAssert.isElementTextEqual(productCardPage.withdrawalBtnLabel, String.format("ОТОЗВАТЬ %s шт.", numberForRM));
        softAssert.verifyStep();

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
        softAssert.verifyStep();

        // Step #7
        log.step("Нажать кнопку ДАЛЕЕ К ПАРАМЕТРАМ ЗАЯВКИ");
        stockProductsPage.clickSubmitBtn();

        String s = "";


    }

}
