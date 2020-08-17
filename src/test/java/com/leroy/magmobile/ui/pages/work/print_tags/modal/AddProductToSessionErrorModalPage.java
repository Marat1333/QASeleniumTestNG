package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class AddProductToSessionErrorModalPage extends CommonMagMobilePage {
    @AppFindBy(containsText = "Не получается добавить")
    Element header;

    @AppFindBy(text = "ПОНЯТНО")
    Button confirmBtn;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        confirmBtn.waitForVisibility();
    }

    @Step("Подтвердить")
    public void confirm() {
        confirmBtn.click();
        confirmBtn.waitForInvisibility();
    }

    public void verifyRequiredElements() {
        softAssert.areElementsVisible(header, confirmBtn);
        softAssert.verifyAll();
    }

}
