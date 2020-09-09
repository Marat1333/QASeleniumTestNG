package com.leroy.magmobile.ui.pages.common.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class ConfirmRemovingProductModal extends CommonMagMobilePage {

    @Override
    public void waitForPageIsLoaded() {
        yesBtn.waitForVisibility();
    }

    @AppFindBy(text = "Удалить товар?")
    Element headerLbl;

    @AppFindBy(text = "УДАЛИТЬ")
    Element yesBtn;

    @AppFindBy(text = "НЕТ, ОСТАВИТЬ")
    Element noBtn;

    // Actions
    @Step("Нажмите кнопку для подтверждения 'Удалить'")
    public void clickConfirmButton() {
        yesBtn.click();
        yesBtn.waitForInvisibility();
        waitUntilProgressBarIsInvisible();
    }

    // Verifications
    @Step("Проверить, что модальное окно для подтверждения удаления товара отображается корректно")
    public ConfirmRemovingProductModal verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, yesBtn, noBtn);
        softAssert.verifyAll();
        return this;
    }


}
