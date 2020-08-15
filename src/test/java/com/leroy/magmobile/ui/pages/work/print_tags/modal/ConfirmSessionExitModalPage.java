package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class ConfirmSessionExitModalPage extends CommonMagMobilePage {
    @AppFindBy(containsText = "Выйти из сессии")
    Element header;

    @AppFindBy(text = "ОТМЕНА")
    Button cancelExitingBtn;

    @AppFindBy(text = "ВЫЙТИ")
    Button confirmExiting;

    @Override
    protected void waitForPageIsLoaded() {
        cancelExitingBtn.waitForVisibility();
        confirmExiting.waitForVisibility();
    }

    @Step("Подтвердить выход из сессии")
    public void exit() {
        confirmExiting.click();
    }

    public void verifyRequiredElements() {
        softAssert.areElementsVisible(header, cancelExitingBtn, confirmExiting);
        softAssert.verifyAll();
    }
}
