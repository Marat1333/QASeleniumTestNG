package com.leroy.magmobile.ui.pages.sales.orders.cart;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import io.qameta.allure.Step;

public class CartStep2Page extends CartPage {

    @AppFindBy(text = "ПАРАМЕТРЫ ДОКУМЕНТА")
    private Element documentParametersLbl;

    @AppFindBy(text = "Место выдачи")
    private Element issuePlaceLbl;

    @AppFindBy(accessibilityId = "placeOfDelivery")
    private EditBox issuePlaceFld;

    @AppFindBy(text = "Дата выдачи")
    private Element issueDateLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Дата выдачи']/following-sibling::android.widget.TextView")
    private Element issueDateFld;

    @AppFindBy(text = "Комментарий (не обязательно)")
    private Element commentLbl;

    @AppFindBy(accessibilityId = "comment")
    private Element commentFld;

    @AppFindBy(text = "СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ")
    private MagMobGreenSubmitButton createSalesDocumentBtn;

    @Override
    public void waitForPageIsLoaded() {
        documentParametersLbl.waitForVisibility();
    }

    // -------------Action steps ------------------//

    @Step("Нажмите кнопку Создать документ продажи")
    public CartStep3Page clickCreateSalesDocumentButton() {
        createSalesDocumentBtn.click();
        return new CartStep3Page();
    }

    // ------------ Verifications ------------------//

    @Override
    @Step("Убедиться, что мы находимся на странице Корзина - Шаг 2, и все необходимые элементы отражаются корректно")
    public CartStep2Page verifyRequiredElements() {
        super.verifyRequiredElements();
        softAssert.isElementVisible(documentParametersLbl);
        softAssert.isElementVisible(issuePlaceLbl);
        softAssert.isElementVisible(issueDateLbl);
        softAssert.isElementVisible(commentLbl);
        softAssert.isElementVisible(createSalesDocumentBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что поля 'Место выдачи', 'Дата выдачи' предзаполнены, а поле 'Комментарий' пустое")
    public CartStep2Page shouldFieldsHaveDefaultValues() {
        softAssert.isElementTextEqual(issuePlaceFld, "Линия касс");
        softAssert.isElementTextEqual(issueDateFld, "Сейчас");
        softAssert.isElementTextEqual(commentFld, "");
        softAssert.verifyAll();
        return this;
    }
}
