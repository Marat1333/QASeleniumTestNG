package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.SessionsListPage;
import io.qameta.allure.Step;

public class UnsuccessfullSessionCreationModalPage extends CommonMagMobilePage {
    @AppFindBy(containsText = "Не получается создать")
    Element header;

    @AppFindBy(text = "ПОНЯТНО")
    Button okBtn;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        okBtn.waitForVisibility();
    }

    @Step("Подтвердить")
    public SessionsListPage confirm() {
        okBtn.click();
        return new SessionsListPage();
    }

    public void verifyRequiredElements() {
        softAssert.areElementsVisible(header, okBtn);
        softAssert.verifyAll();
    }
}
