package com.leroy.tests.app;

import com.leroy.constants.EnvConstants;
import com.leroy.models.UserData;
import com.leroy.pages.LoginPage;
import com.leroy.pages.app.ProductCardPage;
import com.leroy.pages.app.StockProductsPage;
import com.leroy.pages.app.WorkPage;
import com.leroy.pages.app.common.BottomMenu;
import com.leroy.tests.BaseState;
import org.testng.annotations.Test;

public class SalesDocumentsTest extends BaseState  {

    @Test(description = "C3132493 Создание заявки на Отзыв RM из раздела Работа")
    public void testC3201013() throws Exception {
        // Pre-condition
        LoginPage loginPage = new LoginPage(driver);
        loginPage.logIn(new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS));

        BottomMenu bottomMenu = new BottomMenu(driver);
        bottomMenu.goToMoreSection().goToUserProfile().goToEditShopForm().searchForShopAndSelectById("5")
                .goToEditDepartmentForm().selectDepartmentById("01");

        // Step #1
        log.step("Зайти в раздел Работа");
        bottomMenu.goToWork();

        WorkPage workPage = new WorkPage(driver);
        softAssert.isElementTextEqual(workPage.titleObj, WorkPage.TITLE);
        softAssert.isElementTextEqual(workPage.withdrawalFromRMLabel, "Отзыв с RM1");
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
        ProductCardPage productCardPage = stockProductsPage.clickFirstPieceProduct();
        softAssert.isElementVisible(productCardPage.productCardHeaderArea);
        softAssert.verifyStep();

        // Step #4
        log.step("Нажать кнопку ОТОЗВАТЬ");

    }

}
