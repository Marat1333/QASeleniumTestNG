package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import io.qameta.allure.Step;

public class BasketStep3Page extends BasketPage {

    public BasketStep3Page(TestContext context) {
        super(context);
    }

    @AppFindBy(text = "PIN-код для оплаты")
    private Element pinCodeHeaderLbl;

    @AppFindBy(xpath = "//android.widget.EditText[1]", metaName = "Поле для ввода PIN-кода")
    private EditBox pinCodeFld;

    @AppFindBy(text = "ПОДТВЕРДИТЬ")
    private MagMobSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        pinCodeHeaderLbl.waitForVisibility();
    }

    // -----------------Action Steps -------------------//

    @Step("Нажмите кнопку Подтвердить")
    public SubmittedSalesDocumentPage clickSubmitButton() {
        submitBtn.click();
        return new SubmittedSalesDocumentPage(context);
    }

    @Step("Введите {pin} в PIN-код поле")
    public BasketStep3Page enterPinCode(String pin) {
        pinCodeFld.clearFillAndSubmit(pin);
        return this;
    }

    // ---------------- Verifications ------------------//

    @Override
    public BasketStep3Page verifyRequiredElements() {
        softAssert.isElementVisible(pinCodeHeaderLbl);
        softAssert.isElementVisible(pinCodeFld);
        softAssert.isElementVisible(submitBtn);
        softAssert.isFalse(submitBtn.isEnabled(),
                "Кнопка 'Подтвердить должна быть неактивна'");
        softAssert.verifyAll();
        return this;
    }

    public BasketStep3Page shouldPinCodeFieldIs(String text) {
        anAssert.isElementTextEqual(pinCodeFld, text);
        return this;
    }

    public BasketStep3Page shouldSubmitButtonIsActive() {
        anAssert.isTrue(submitBtn.isEnabled(),
                "Кнопка 'Подтвердить должна быть активна'");
        return this;
    }
}
