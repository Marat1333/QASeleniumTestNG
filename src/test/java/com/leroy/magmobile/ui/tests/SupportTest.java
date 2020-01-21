package com.leroy.magmobile.ui.tests;

import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.support.ComplainPage;
import com.leroy.magmobile.ui.pages.support.SupportPage;
import com.leroy.magmobile.ui.AppBaseSteps;
import org.testng.annotations.Test;

public class SupportTest extends AppBaseSteps {

    @Test(description = "C3201017 Создание тикета из приложения (пожаловаться)")
    public void testC3201017() throws Exception {
        // Step #1
        log.step("Зайдите в раздел Поддержка");
        SupportPage supportPage = loginAndGoTo(SupportPage.class);
        supportPage.verifyRequiredElements()
                .shouldSelectedTypeRequestIs(SupportPage.COMPLAIN_REQUEST);

        // Step #2
        log.step("Нажмите на плашку 'Товар не найден'");
        String productNotFoundText = "Не найден товар";
        ComplainPage complainPage = supportPage.clickButton(productNotFoundText)
                .verifyRequiredElements()
                .shouldMainFieldsAre(productNotFoundText, "", null);

        // Step #3
        log.step("Нажмите на поле 'Чуть больше подробностей' или на иконку + этого поля");
        complainPage.clickMoreInformationField()
                .shouldKeyboardVisible();

        // Step #4
        log.step("Введите текст 'проблема с поиском товара 1245678, " +
                "продавец-консультант 5 магазина' и закройте клавиатуру");
        String textToEnter = "проблема с поиском товара C3132493, продавец-консультант 5 магазина";
        complainPage.enterTextIntoMoreInformationField(textToEnter)
                .shouldMoreInformationFieldHasText(textToEnter);
        String emailFromField = complainPage.getEmail();

        // Step #5
        log.step("Нажмите на кнопку Отправить внизу экрана");
        complainPage.clickSendButton()
                .verifyVisibilityOfAllElements()
                .verifyDataOnThePage(null, emailFromField);
        // TODO - Тикет из приложения создан.
    }

}
