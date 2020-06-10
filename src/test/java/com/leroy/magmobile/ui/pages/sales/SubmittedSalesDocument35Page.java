package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class SubmittedSalesDocument35Page extends CommonMagMobilePage {

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
    private MagMobButton goToDocumentListBtn;

    @AppFindBy(text = "ПЕРЕЙТИ В КОРЗИНУ")
    private MagMobButton goToCartBtn;

    @Override
    public void waitForPageIsLoaded() {
        titleMsgLbl.waitForVisibility(long_timeout);
    }

    public String getDocumentNumber() {
        return ParserUtil.strWithOnlyDigits(orderNumberVal.getText());
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажмите кнопку Перейти в список документов")
    public SalesDocumentsPage clickGoToDocumentListButton() {
        goToDocumentListBtn.click();
        return new SalesDocumentsPage();
    }

    @Step("Нажмите кнопку Перейти в корзину")
    public Cart35Page clickGoToCartButton() {
        goToCartBtn.click();
        return new Cart35Page();
    }

    /* ---------------------- Verifications -------------------------- */

    @Step("Проверить, что страница подтверждения заказа отображается корректно")
    public SubmittedSalesDocument35Page verifyRequiredElements(boolean severalOrdersInCart) {
        String ps = getPageSource();
        softAssert.areElementsVisible(ps, titleMsgLbl, orderNumberLbl,
                orderNumberVal, pinCodeLbl, pinCodeVal, offerCustomerToTakeScreenshotLbl);
        if (severalOrdersInCart) {
            softAssert.isElementVisible(goToCartBtn, ps);
        } else {
            softAssert.areElementsVisible(ps, statusCanBeMonitoringInDocumentListLbl, goToDocumentListBtn);
        }
        softAssert.verifyAll();
        return this;
    }

    public SubmittedSalesDocument35Page verifyRequiredElements() {
        return verifyRequiredElements(false);
    }

    @Step("Проверить, что Пин-код = {text}")
    public SubmittedSalesDocument35Page shouldPinCodeIs(String text) {
        anAssert.isElementTextEqual(pinCodeVal, text);
        return this;
    }

}
