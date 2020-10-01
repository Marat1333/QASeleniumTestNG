package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.FinishedSessionPage;
import com.leroy.magmobile.ui.pages.work.ruptures.ActiveSessionPage;
import io.qameta.allure.Step;

public class FinishSessionAcceptModalPage extends CommonMagMobilePage {
    @AppFindBy(text = "Завершить сессию?")
    Element header;

    @AppFindBy(text = "ОТМЕНА")
    Button cancelBtn;

    @AppFindBy(text = "ЗАВЕРШИТЬ")
    Button finishBtn;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        finishBtn.waitForVisibility();
    }

    @Step("Отменить завершение")
    public ActiveSessionPage cancel(){
        cancelBtn.click();
        return new ActiveSessionPage();
    }

    @Step("Подтвердить завершение")
    public FinishedSessionPage finish(){
        finishBtn.click();
        return new FinishedSessionPage();
    }

    public FinishSessionAcceptModalPage verifyRequiredElements(){
        softAssert.areElementsVisible(header, cancelBtn, finishBtn);
        softAssert.verifyAll();
        return this;
    }
}
