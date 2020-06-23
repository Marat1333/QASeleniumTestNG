package com.leroy.magmobile.ui.pages.common.modal;

import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public abstract class CommonConfirmModal extends CommonMagMobilePage {

    protected abstract Element confirmBtn();
    protected abstract Element cancelBtn();

    @Override
    protected void waitForPageIsLoaded() {
        confirmBtn().waitForVisibility();
        cancelBtn().waitForVisibility();
    }

    @Step("Подтверить действие")
    public void clickConfirmButton() {
        Element btn = confirmBtn();
        btn.click();
        btn.waitForInvisibility();
    }

    @Step("Проверить, что модальное окно с подтверждение отображается корректно")
    public CommonConfirmModal verifyRequiredElements() {
        softAssert.areElementsVisible(confirmBtn(), cancelBtn());
        softAssert.verifyAll();
        return this;
    }

}
