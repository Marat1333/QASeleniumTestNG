package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.ActiveSessionPage;
import io.qameta.allure.Step;

public class ExitActiveSessionModalPage extends CommonMagMobilePage {
    @AppFindBy(text = "Выйти из активной сессии?")
    Element header;

    @AppFindBy(text = "ОТМЕНА")
    Button cancelBtn;

    @AppFindBy(text = "ВЫЙТИ")
    Button exitButton;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        cancelBtn.waitForVisibility();
        exitButton.waitForVisibility();
    }

    @Step("Подтвердить выход")
    public void confirmExit() {
        exitButton.click();
    }

    @Step("Отменить выход")
    public ActiveSessionPage declineExit() {
        cancelBtn.click();
        return new ActiveSessionPage();
    }

    public ExitActiveSessionModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(header, cancelBtn, exitButton);
        softAssert.verifyAll();
        return this;
    }
}
