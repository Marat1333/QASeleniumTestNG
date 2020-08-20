package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.RuptureCardPage;
import io.qameta.allure.Step;

public class AddDuplicateModalPage extends CommonMagMobilePage {
    @AppFindBy(text = "Товар уже был отсканирован")
    Element header;

    @AppFindBy(containsText = "Этот товар уже был отсканирован")
    Element description;

    @AppFindBy(text = "ПОНЯТНО")
    Button confirmBtn;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility(long_timeout);
        confirmBtn.waitForVisibility();
    }

    @Step("Подтвердить")
    public RuptureCardPage confirm(){
        confirmBtn.click();
        return new RuptureCardPage();
    }

    public AddDuplicateModalPage verifyRequiredElements(){
        softAssert.areElementsVisible(header, description, confirmBtn);
        softAssert.verifyAll();
        return this;
    }
}
