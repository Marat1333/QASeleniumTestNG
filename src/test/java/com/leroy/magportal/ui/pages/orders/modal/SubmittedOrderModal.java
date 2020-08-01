package com.leroy.magportal.ui.pages.orders.modal;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class SubmittedOrderModal extends MagPortalBasePage {

    private static final String MODAL_DIV_XPATH = "//div[contains(@class, 'OrderCreate__success-modal-container')]";

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//p", metaName = "Основное сообщение")
    Element header;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'OrderCreate__barCodeBlock')]//p[2]", metaName = "Номер заказа")
    Element orderNumber;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'OrderCreate__barCodeBlock')]//p[4]", metaName = "Пин код")
    Element pinCode;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//button", metaName = "Кнопка 'Перейти в список документов'")
    Element goToOrderListBtn;

    @Override
    protected void waitForPageIsLoaded() {
        orderNumber.waitForVisibility();
    }

    // Grab information

    @Step("Получить номер документа со страницы")
    public String getDocumentNumber() {
        return ParserUtil.strWithOnlyDigits(orderNumber.getText());
    }

    /* ------------------------- ACTION STEPS -------------------------- */

    @Step("Нажмите кнопку Перейти в список заказов")
    public OrderCreatedContentPage clickGoToOrderListButton() {
        goToOrderListBtn.click();
        return new OrderCreatedContentPage();
    }

    @Step("Проверить, что модальное окно подтверждения заказа отображается корректно")
    public SubmittedOrderModal verifyRequiredElements(
            SalesDocumentsConst.GiveAwayPoints type) {
        String ps = getPageSource();
        String expectedTitleEnd = true ? "оформлен" : "сохранен";
        String expectedTitle = (SalesDocumentsConst.GiveAwayPoints.PICKUP.equals(type) ?
                "Заказ на самовывоз " : "Заказ на доставку ") + expectedTitleEnd;
        softAssert.isElementTextContains(header, expectedTitle);
        softAssert.areElementsVisible(header, orderNumber, pinCode, goToOrderListBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что Пин-код = {text}")
    public SubmittedOrderModal shouldPinCodeIs(String text) {
        anAssert.isElementTextEqual(pinCode, text);
        return this;
    }
}
