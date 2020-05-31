package com.leroy.magmobile.ui.pages.sales.orders.cart;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.sales.SubmittedSalesDocumentPage;
import io.qameta.allure.Step;

public class CartStep3Page extends CartPage {

    @AppFindBy(text = "PIN-код для оплаты")
    private Element pinCodeHeaderLbl;

    @AppFindBy(xpath = "//android.widget.EditText[1]", metaName = "Поле для ввода PIN-кода")
    private EditBox pinCodeFld;

    @AppFindBy(text = "ПОДТВЕРДИТЬ")
    private MagMobGreenSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        pinCodeHeaderLbl.waitForVisibility();
    }

    // -----------------Action Steps -------------------//

    @Step("Нажмите кнопку Подтвердить")
    public SubmittedSalesDocumentPage clickSubmitButton() {
        submitBtn.click();
        return new SubmittedSalesDocumentPage();
    }

    @Step("Введите {pin} в PIN-код поле")
    public CartStep3Page enterPinCode(String pin) {
        pinCodeFld.clearFillAndSubmit(pin);
        return this;
    }

    // ---------------- Verifications ------------------//

    @Override
    public CartStep3Page verifyRequiredElements() {
        softAssert.isElementVisible(pinCodeHeaderLbl);
        softAssert.isElementVisible(pinCodeFld);
        softAssert.isElementVisible(submitBtn);
        softAssert.isFalse(submitBtn.isEnabled(),
                "Кнопка 'Подтвердить должна быть неактивна'");
        softAssert.verifyAll();
        return this;
    }

    public CartStep3Page shouldPinCodeFieldIs(String text) {
        anAssert.isElementTextEqual(pinCodeFld, text);
        return this;
    }

    @Step("Проверить, что Кнопка 'Подтвердить' активна")
    public CartStep3Page shouldSubmitButtonIsActive() {
        anAssert.isTrue(submitBtn.isEnabled(),
                "Кнопка 'Подтвердить должна быть активна'");
        return this;
    }
}
