package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class SubmittedSalesDocument35Page extends CommonMagMobilePage {

    public SubmittedSalesDocument35Page(TestContext context) {
        super(context);
    }

    @AppFindBy(text = "Заказ на самовывоз оформлен,\nтовары зарезервированы")
    Element titleMsgLbl;

    private static final String orderNumberText = "Номер заказа";
    @AppFindBy(text = orderNumberText)
    Element orderNumberLbl;
    @AppFindBy(followingTextAfter = orderNumberText)
    Element orderNumberVal;

    private static final String pinCodeText = "PIN-код для оплаты";
    @AppFindBy(text = pinCodeText)
    Element pinCodeLbl;
    @AppFindBy(followingTextAfter = pinCodeText)
    Element pinCodeVal;

    @AppFindBy(text = "предложи клиенту сделать фото экрана")
    Element offerCustomerToTakeScreenshotLbl;

    @AppFindBy(text = "Статус заказа можно отслеживать\nв списке документов.")
    Element statusCanBeMonitoringInDocumentListLbl;

    @AppFindBy(text = "ПЕРЕЙТИ В СПИСОК ДОКУМЕНТОВ")
    private MagMobButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        submitBtn.waitForVisibility(long_timeout);
    }

    public String getDocumentNumber(boolean withoutSpaces) {
        if (withoutSpaces)
            return orderNumberVal.getText().replaceAll(" ", "");
        else
            return orderNumberVal.getText();
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажмите кнопку Перейти в список документов")
    public SalesDocumentsPage clickSubmitButton() {
        submitBtn.click();
        return new SalesDocumentsPage(context);
    }

    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что страница подтверждения заказа отображается корректно")
    public SubmittedSalesDocument35Page verifyRequiredElements() {
        softAssert.areElementsVisible(titleMsgLbl, orderNumberLbl,
                orderNumberVal, pinCodeLbl, pinCodeVal, offerCustomerToTakeScreenshotLbl,
                statusCanBeMonitoringInDocumentListLbl, submitBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что Пин-код = {text}")
    public SubmittedSalesDocument35Page shouldPinCodeIs(String text) {
        anAssert.isElementTextEqual(pinCodeVal, text);
        return this;
    }

}
