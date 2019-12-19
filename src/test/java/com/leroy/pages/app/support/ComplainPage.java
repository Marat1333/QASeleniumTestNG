package com.leroy.pages.app.support;

import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.elements.MagMobButton;
import io.qameta.allure.Step;
import org.openqa.selenium.support.Color;

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

    @AppFindBy(text = "Эл. почта для ответа")
    private Element emailLbl;
    @AppFindBy(accessibilityId = "email", metaName = "Поле ввода 'email'")
    private EditBox emailFld;
    @AppFindBy(xpath = "//android.widget.EditText[@content-desc='email']/following::android.widget.TextView[1]")
    private Element emailDomainLbl;

    @AppFindBy(text = "ОТПРАВИТЬ", cacheLookup = false)
    private MagMobButton submitBtn;

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

    public ComplainPage shouldAllElementsVisibility() throws Exception {
        softAssert.isElementVisible(whatHappenLbl);
        softAssert.isElementTextEqual(whatHappenFld, "Не найден товар");
        softAssert.isElementImageMatches(whatHappenEditPen, MagMobElementTypes.EditPen.getPictureName());
        softAssert.isElementVisible(moreInfoLbl);
        softAssert.isElementVisible(emailLbl);
        softAssert.isElementTextEqual(submitBtn, "ОТПРАВИТЬ");
        softAssert.verifyAll();
        return this;
    }

    public ComplainPage shouldMoreInformationFieldHasText(String text) {
        softAssert.isElementTextEqual(moreInfoFld, text);
        softAssert.verifyAll();
        return this;
    }

}
