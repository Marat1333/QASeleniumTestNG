package com.leroy.magmobile.ui.pages.common.modal;

import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public abstract class CommonConfirmModal extends CommonMagMobilePage {

    protected abstract Element confirmBtn();
    protected abstract Element cancelBtn();

    @Step("Подтверить действие")
    public void clickConfirmButton() {
        Element btn = confirmBtn();
        btn.click();
        btn.waitForInvisibility();
    }

}
