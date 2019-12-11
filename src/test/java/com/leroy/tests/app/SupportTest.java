package com.leroy.tests.app;

import com.leroy.pages.app.support.ComplainPage;
import com.leroy.pages.app.support.SubmittedIncidentPage;
import com.leroy.pages.app.support.SupportPage;
import com.leroy.tests.app.helpers.BaseAppSteps;
import org.testng.annotations.Test;

public class SupportTest extends BaseAppSteps {

    @Test(description = "C3201017 Создание тикета из приложения (пожаловаться)")
    public void testC3201017() throws Exception {
        // Step #1
        log.step("Зайдите в раздел Поддержка");
        loginInAndGoTo(SUPPORT_SECTION);
        SupportPage supportPage = new SupportPage(driver);
        softAssert.isElementVisible(supportPage.titleLbl);
        softAssert.isElementVisible(supportPage.complainBtnLbl);
        // TODO need to check - пожаловаться (предвыбрана)
        softAssert.isElementVisible(supportPage.askQuestionBtnLbl);
        softAssert.isElementVisible(supportPage.whatHappenLbl);
        softAssert.isElementVisible(supportPage.productNotFoundLbl);
        softAssert.isElementVisible(supportPage.wrongPriceLbl);
        softAssert.isElementVisible(supportPage.issueWithClientDataLbl);
        softAssert.isElementVisible(supportPage.wrongStockInTradingRoomLbl);
        softAssert.isElementVisible(supportPage.errorWhenRecallingFromStockLbl);
        softAssert.isElementVisible(supportPage.wrongStockInWarehouseLbl);
        softAssert.isElementVisible(supportPage.somethingElseLbl);

        // Step #2
        log.step("Нажмите на плашку 'Товар не найден'");
        supportPage.productNotFoundLbl.click();
        ComplainPage complainPage = new ComplainPage(driver);
        softAssert.isElementVisible(complainPage.whatHappenLbl);
        softAssert.isElementTextEqual(complainPage.whatHappenFld, "Товар не найден");
        // TODO need to check -> В правой части поля видна иконка ручки (редактирования)
        softAssert.isElementVisible(complainPage.moreInfoLbl);
        softAssert.isElementVisible(complainPage.emailLbl);
        softAssert.isElementTextEqual(complainPage.submitBtn, "ОТПРАВИТЬ");

        // Step #3
        log.step("Нажмите на поле 'Чуть больше подробностей' или на иконку + этого поля");
        complainPage.moreInfoFld.click();
        softAssert.isTrue(complainPage.isKeyboardVisible(), "Должна открыться клавиатура");

        // Step #4
        log.step("Введите текст 'проблема с поиском товара 1245678, " +
                "продавец-консультант 5 магазина' и закройте клавиатуру");
        String textToEnter = "проблема с поиском товара C3132493, продавец-консультант 5 магазина";
        complainPage.moreInfoFld.clearFillAndSubmit(
                textToEnter);
        softAssert.isElementTextEqual(complainPage.moreInfoFld, textToEnter);
        // TODO - Вместо иконки + в правой части поля видна иконка ручки (редактирования).
        String emailFromField = complainPage.emailFld.getText() + complainPage.emailDomainLbl.getText().trim();

        // Step #5
        log.step("Нажмите на кнопку Отправить внизу экрана");
        SubmittedIncidentPage incidentPage = complainPage.clickSubmitBtn();
        softAssert.isElementTextEqual(incidentPage.headerLbl, "Письмо отправлено.\nСпасибо!");
        softAssert.isTrue(incidentPage.isIncidentNumberVisibleAndValid(), "Должен быть виден номер инцидента");
        softAssert.isEquals(incidentPage.getEmail(), emailFromField,
                "Электронная почта должна соответствовать указанной при создании инцидента");
        softAssert.isElementTextEqual(incidentPage.buttonLbl, "ОТПРАВИТЬ ЕЩЕ ОДНО ПИСЬМО");
        // TODO - Тикет из приложения создан.
        softAssert.verifyAll();
    }

}
