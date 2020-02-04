package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import io.qameta.allure.Step;

public class SubmittedSalesDocumentPage extends BaseAppPage {

    public SubmittedSalesDocumentPage(TestContext context) {
        super(context);
    }

    @AppFindBy(xpath = "//android.widget.TextView[1]", metaName = "Номер документа")
    private Element documentNumber;

    @AppFindBy(text = "документ продажи сохранен")
    private Element headerLbl;

    @AppFindBy(text = "PIN-код для оплаты")
    private Element pinCodeLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='PIN-код для оплаты']/following-sibling::android.widget.TextView")
    private Element pinCode;

    @AppFindBy(text = "Статус документа можно отслеживать\nв списке документов продажи.")
    private Element bodyMessageLbl;

    @AppFindBy(text = "ПЕРЕЙТИ В СПИСОК ДОКУМЕНТОВ")
    private MagMobGreenSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        submitBtn.waitForVisibility();
    }

    private String getDocumentNumber() {
        return documentNumber.getText()
                .replaceAll("№", "").trim();
    }

    private boolean isDocumentNumberVisibleAndValid() {
        String number = documentNumber.getText();
        return number.matches("№ \\d{8}");
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажмите кнопку Перейти в список документов")
    public SalesDocumentsPage clickSubmitButton() {
        submitBtn.click();
        return new SalesDocumentsPage(context);
    }

    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что страница подтверждения заказа отображается корректно")
    public SubmittedSalesDocumentPage verifyRequiredElements() {
        softAssert.isTrue(isDocumentNumberVisibleAndValid(),
                "Неправильный формат номера документа");
        softAssert.isElementVisible(headerLbl);
        softAssert.isElementVisible(pinCodeLbl);
        softAssert.isElementVisible(pinCode);
        softAssert.isElementVisible(bodyMessageLbl);
        softAssert.isElementVisible(submitBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что Пин-код = {text}")
    public SubmittedSalesDocumentPage shouldPinCodeIs(String text) {
        anAssert.isElementTextEqual(pinCode, text);
        return this;
    }

    @Step("Проверить, что номер документа = {text}")
    public SubmittedSalesDocumentPage shouldDocumentNumberIs(String text) {
        anAssert.isEquals(getDocumentNumber(), text, "Номер документа должен быть %s");
        return this;
    }
}
