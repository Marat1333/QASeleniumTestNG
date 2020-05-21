package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class SendEmailPage extends CommonMagMobilePage {

    public SendEmailPage(Context context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "BackCloseModal", metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Загаловок экрана")
    Element screenTitle;

    @AppFindBy(accessibilityId = "email", metaName = "Поле 'Email'")
    EditBox emailFld;

    @AppFindBy(accessibilityId = "email2", metaName = "Поле 'Другой email'")
    EditBox anotherEmailFld;

    @AppFindBy(accessibilityId = "comment", metaName = "Поле 'Комментарий'")
    EditBox commentFld;

    @AppFindBy(text = "ОТПРАВИТЬ", metaName = "Кнопка Отправить")
    MagMobGreenSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        emailFld.waitForVisibility();
        submitBtn.waitForVisibility();
    }

    // Actions

    @Step("Ввести {text} в поле 'Email клиента'")
    public SendEmailPage enterTextInEmailField(String text) {
        emailFld.click();
        emailFld.clearFillAndSubmit(text);
        return this;
    }

    @Step("Ввести {text} в поле 'Другой email'")
    public SendEmailPage enterTextInAnotherEmailField(String text) {
        emailFld.clearFillAndSubmit(text);
        return this;
    }

    @Step("Ввести {text} в поле 'Комментарий'")
    public SendEmailPage enterTextInCommentField(String text) {
        emailFld.clearFillAndSubmit(text);
        return this;
    }

    @Step("Нажать кнопку 'Отправить'")
    public SubmittedSendEmailPage clickSubmitButton() {
        submitBtn.click();
        return new SubmittedSendEmailPage(context);
    }

    // Verifications

    @Step("Проверить, что страница 'Отправка сметы на email' отображается корректно")
    public SendEmailPage verifyRequiredElements() {
        softAssert.areElementsVisible(screenTitle, emailFld, anotherEmailFld, commentFld, submitBtn);
        softAssert.verifyAll();
        return this;
    }
}
