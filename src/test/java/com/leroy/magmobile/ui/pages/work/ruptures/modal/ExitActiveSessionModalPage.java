package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import io.qameta.allure.Step;

public class ExitActiveSessionModalPage extends CommonMagMobilePage {
    @AppFindBy(text = "Выйти из активной сессии?")
    Element header;

    @AppFindBy(text = "ОТМЕНА")
    Button cancelBtn;

    @AppFindBy(text = "ВЫЙТИ")
    Button closeBtn;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        cancelBtn.waitForVisibility();
        closeBtn.waitForVisibility();
    }

    @Step("Подтвердить выход")
    public WorkPage confirmExit() {
        closeBtn.click();
        return new WorkPage();
    }

    public ExitActiveSessionModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(header, cancelBtn, closeBtn);
        softAssert.verifyAll();
        return this;
    }
}
