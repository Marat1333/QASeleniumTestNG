package com.leroy.magmobile.ui.tests;

import com.leroy.core.annotations.Smoke;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.support.ComplainPage;
import com.leroy.magmobile.ui.pages.support.SupportPage;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

public class SupportTest extends AppBaseSteps {

    @Smoke
    @Test(description = "C3201017 Создание тикета из приложения (пожаловаться)")
    @TmsLink("2729")
    public void testC3201017() throws Exception {
        // Step #1
        step("Зайдите в раздел Поддержка");
        SupportPage supportPage = loginAndGoTo(SupportPage.class);
        supportPage.verifyRequiredElements()
                .shouldSelectedTypeRequestIs(SupportPage.COMPLAIN_REQUEST);

        // Step #2
        step("Нажмите на плашку 'Товар не найден'");
        String productNotFoundText = "Не найден товар";
        ComplainPage complainPage = supportPage.clickButton(productNotFoundText)
                .verifyRequiredElements()
                .shouldMainFieldsAre(productNotFoundText, "", null);

        // Step #3
        step("Нажмите на поле 'Чуть больше подробностей' или на иконку + этого поля");
        complainPage.clickMoreInformationField()
                .shouldKeyboardVisible();

        // Step #4
        step("Введите текст 'проблема с поиском товара 1245678, " +
                "продавец-консультант 5 магазина' и закройте клавиатуру");
        String textToEnter = "проблема с поиском товара C3132493, продавец-консультант 5 магазина";
        complainPage.enterTextIntoMoreInformationField(textToEnter)
                .shouldMoreInformationFieldHasText(textToEnter);
        String emailFromField = complainPage.getEmail();

        // Step #5
        step("Нажмите на кнопку Отправить внизу экрана");
        complainPage.clickSendButton()
                .verifyVisibilityOfAllElements()
                .verifyDataOnThePage(null, emailFromField);
        // TODO - Тикет из приложения создан.
    }

}
