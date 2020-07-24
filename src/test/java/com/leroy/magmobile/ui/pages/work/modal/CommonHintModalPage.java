package com.leroy.magmobile.ui.pages.work.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class CommonHintModalPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "CloseModal")
    Button closeModalBtn;

    @AppFindBy(text = "Справка")
    Element header;

    @Step("Закрыть модалку")
    public void closeModal() {
        closeModalBtn.click();
    }

    @Override
    protected void waitForPageIsLoaded() {
        closeModalBtn.waitForVisibility();
        header.waitForVisibility();
    }

    public CommonHintModalPage verifyRequiredElements() {
        return this;
    }
}
