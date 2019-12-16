package com.leroy.tests.app;

import com.leroy.pages.LoginPage;
import com.leroy.pages.app.support.ComplainPage;
import com.leroy.pages.app.support.SupportPage;
import com.leroy.tests.BaseState;
import org.testng.annotations.Test;

public class SupportTest extends BaseState {

    @Test(description = "C3201017 Создание тикета из приложения (пожаловаться)")
    public void testC3201017() throws Exception {
        // Step #1
        log.step("Зайдите в раздел Поддержка");
        new LoginPage(context).loginInAndGoTo(LoginPage.SUPPORT_SECTION);
        SupportPage supportPage = new SupportPage(context).verifyAllElementsVisibility();

        // Step #2
        log.step("Нажмите на плашку 'Товар не найден'");
        ComplainPage complainPage = supportPage.clickButton("Не найден товар")
                .shouldAllElementsVisibility(); // TODO

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
        // TODO - Вместо иконки + в правой части поля видна иконка ручки (редактирования).
        String emailFromField = complainPage.emailFld.getText() +
                complainPage.emailDomainLbl.getText().trim();

        // Step #5
        log.step("Нажмите на кнопку Отправить внизу экрана");
        complainPage.clickSendButton().verifyVisibilityOfAllElements()
                .verifyDataOnThePage(null, emailFromField);
        // TODO - Тикет из приложения создан.
    }

}
