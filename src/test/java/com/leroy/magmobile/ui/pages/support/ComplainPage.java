package com.leroy.magmobile.ui.pages.support;

import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import io.qameta.allure.Step;

public class ComplainPage extends BaseAppPage {

    private static final String WHAT_HAPPEN_TEXT = "В чем проблема?";

    public ComplainPage(TestContext context) {
        super(context);
    }

    @AppFindBy(text = WHAT_HAPPEN_TEXT)
    private Element whatHappenLbl;
    @AppFindBy(accessibilityId = "title", metaName = "Поле ввода '" + WHAT_HAPPEN_TEXT + "'")
    private EditBox whatHappenFld;
    @AppFindBy(xpath = "//android.widget.TextView[@text='" + WHAT_HAPPEN_TEXT + "']/following::android.view.ViewGroup[1]/android.view.ViewGroup")
    private Element whatHappenEditPen;

    @AppFindBy(text = "Чуть больше подробностей")
    private Element moreInfoLbl;
    @AppFindBy(accessibilityId = "moreInfo", metaName = "Поле ввода 'Чуть больше подробностей'")
    private EditBox moreInfoFld;
    @AppFindBy(xpath = "//android.widget.TextView[@text='Чуть больше подробностей']/following::android.view.ViewGroup[1]/android.view.ViewGroup",
            metaName = "Иконка поля 'Чуть больше подробностей'")
    private EditBox moreInfoIcon;

    @AppFindBy(text = "Email для ответа")
    private Element emailLbl;
    @AppFindBy(accessibilityId = "email", metaName = "Поле ввода 'email'")
    private EditBox emailFld;
    @AppFindBy(xpath = "//android.widget.EditText[@content-desc='email']/following::android.widget.TextView[1]")
    private Element emailDomainLbl;

    @AppFindBy(text = "ОТПРАВИТЬ", cacheLookup = false)
    private MagMobGreenSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        whatHappenLbl.waitForVisibility();
        submitBtn.waitForVisibility();
    }

    public String getEmail() {
        return emailFld.getText() + emailDomainLbl.getText().trim();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажмите на поле 'Чуть больше подробностей'")
    public ComplainPage clickMoreInformationField() {
        moreInfoFld.click();
        return this;
    }

    @Step("Введите текст '{text}' в поле 'Чуть больше подробностей'")
    public ComplainPage enterTextIntoMoreInformationField(String text) {
        moreInfoFld.clearFillAndSubmit(text);
        return this;
    }

    @Step("Нажмите кнопку 'Отправить'")
    public SubmittedIncidentPage clickSendButton() {
        submitBtn.click();
        return new SubmittedIncidentPage(context);
    }

    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что страница с жалобами отображается корректно")
    public ComplainPage verifyRequiredElements() {
        // Видна метка "В чем проблема?"
        softAssert.isElementVisible(whatHappenLbl);
        // В правой части поля видна иконка ручки (редактирования)
        softAssert.isElementImageMatches(whatHappenEditPen, MagMobElementTypes.EDIT_PEN.getPictureName());
        // Видна метка "Чуть больше подробностей"
        softAssert.isElementVisible(moreInfoLbl);
        // Видна метка "Эл. почта для ответа"
        softAssert.isElementVisible(emailLbl);
        // Видна кнопка "ОТПРАВИТЬ"
        softAssert.isElementTextEqual(submitBtn, "ОТПРАВИТЬ");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что поля заполнены корректно. Что случилось - {whatHappen}, Больше подробностей - {moreInfo}, Email - {email}")
    public ComplainPage shouldMainFieldsAre(String whatHappen, String moreInfo, String email) {
        softAssert.isElementTextEqual(whatHappenFld, whatHappen);
        shouldMoreInformationFieldHasText(moreInfo);
        softAssert.isElementTextEqual(moreInfoFld, moreInfo);
        if (email != null) {
            softAssert.isEquals(emailFld.getText() + emailDomainLbl.getText(), email,
                    "email должен быть %s");
        } else {
            softAssert.isTrue(!emailFld.getText().isEmpty() && !emailDomainLbl.getText().isEmpty(),
                    "email должен быть предзаполнен");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что поле 'Чуть больше подробностей содержит текст: {text}'")
    public ComplainPage shouldMoreInformationFieldHasText(String text) {
        String moreInfoText = moreInfoFld.getText();
        softAssert.isEquals(moreInfoText, text,
                "Поле 'Чуть больше подробностей' должно иметь текст %s");
        MagMobElementTypes elementType = moreInfoText.isEmpty() ?
                MagMobElementTypes.PLUS : MagMobElementTypes.EDIT_PEN;
        softAssert.isElementImageMatches(moreInfoIcon, elementType.getPictureName());
        softAssert.verifyAll();
        return this;
    }

}
