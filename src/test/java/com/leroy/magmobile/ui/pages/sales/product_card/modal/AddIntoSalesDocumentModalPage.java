package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import io.qameta.allure.Step;

public class AddIntoSalesDocumentModalPage extends CommonMagMobilePage {

    @Override
    public void waitForPageIsLoaded() {
        createSalesDocumentBtn.waitForVisibility();
    }

    @AppFindBy(accessibilityId = "Button",
            metaName = "Кнопка для закрытия модального окна")
    Element closeBtn;

    @AppFindBy(text = "Добавить в документ продажи")
    Element headerLbl;

    @AppFindBy(text = "СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ")
    private MagMobGreenSubmitButton createSalesDocumentBtn;

    // ---- ACTIONS ---------//

    @Step("Нажать кнопку 'СОЗДАТЬ ДОКУМЕНТ ПРОДАЖИ'")
    public AddProductPage clickCreateSalesDocumentBtn() {
        createSalesDocumentBtn.click();
        return new AddProductPage();
    }

    @Step("Выберите черновик документа с номером {number}")
    public AddProductPage selectDraftWithNumber(String number) {
        Element documentDraftCard = E("contains(" + number + ")");
        anAssert.isTrue(documentDraftCard.isVisible(),
                String.format("Документ с номером %s отсутствует", number));
        documentDraftCard.click();
        return new AddProductPage();
    }

    // Verifications

    @Step("Проверить, что модальное окно отображается корректно")
    public AddIntoSalesDocumentModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(
                closeBtn, headerLbl, createSalesDocumentBtn);
        softAssert.verifyAll();
        return this;
    }

}
