package com.leroy.magmobile.ui.pages.sales.product_card.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductCardPage;
import io.qameta.allure.Step;

public class ImpossibleCreateDocumentWithTopEmModalPage extends CommonMagMobilePage {

    @Override
    public void waitForPageIsLoaded() {
        submitBtn.waitForVisibility();
    }

    @AppFindBy(text = "Топ ЕМ")
    private Element headerLbl;

    @AppFindBy(text = "Оформление документа продажи товара Топ ЕМ находится в разработке. Используйте Pixys")
    private Element bodyMessage;

    @AppFindBy(text = "ПОНЯТНО")
    private MagMobButton submitBtn;

    // ---- ACTIONS ---------//

    @Step("Нажать кнопку 'Понятно'")
    public ProductCardPage clickSubmitButton() {
        submitBtn.click();
        return new ProductCardPage();
    }

    // Verifications

    @Step("Проверить, что Модальное окно с сообщением о невозможности оформления документа продажи товара Топ-ЕМ отображается корректно")
    public ImpossibleCreateDocumentWithTopEmModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, bodyMessage, submitBtn);
        softAssert.verifyAll();
        return this;
    }


}
