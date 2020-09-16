package com.leroy.magmobile.ui.pages.work.recall_from_rm;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;

public class SuccessfullyCreatedReplenishmentRequestFromRupturesPage extends SubmittedWithdrawalOrderPage {
    @AppFindBy(accessibilityId = "Button-text")
    Button backToRupturesSession;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
        backToRupturesSession.waitForVisibility();
    }

    public void returnToRuptureSession() {
        backToRupturesSession.click();
        backToRupturesSession.waitForInvisibility();
    }

    @Override
    public SuccessfullyCreatedReplenishmentRequestFromRupturesPage verifyRequiredElements() {
        softAssert.isElementTextEqual(headerLbl, "Заявка на пополнение \n" +
                " торгового зала отправлена");
        softAssert.isElementTextEqual(backToRupturesSession, "ВЕРНУТЬСЯ В СЕССИЮ ПЕРЕБОЕВ");
        softAssert.verifyAll();
        return this;
    }
}
