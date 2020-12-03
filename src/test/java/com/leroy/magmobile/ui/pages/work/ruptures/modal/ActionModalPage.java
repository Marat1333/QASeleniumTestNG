package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.ruptures.RuptureCardPage;
import io.qameta.allure.Step;

public class ActionModalPage extends CommonMagMobilePage {
    @AppFindBy(xpath = "//*[contains(@text,'Действия с')]/preceding-sibling::*[@content-desc='Button-container']")
    Button closeModal;

    @AppFindBy(containsText = "Действия")
    Element header;

    @AppFindBy(containsText = "отзыв с RM")
    Button recallFromRm;

    @AppFindBy(containsText = "коррекцию стока")
    Button stockCorrection;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        recallFromRm.waitForVisibility();
    }

    @Step("Закрыть модалку")
    public RuptureCardPage closeModal(){
        closeModal.click();
        return new RuptureCardPage();
    }

    @Step("Сделать отзыв с RM")
    public void recallFromRm(){
        recallFromRm.click();
    }

    @Step("Сделать коррекцию стока")
    public void stockCorrection() {
        stockCorrection.click();
    }

    public ActionModalPage verifyRequiredElements(){
        softAssert.areElementsVisible(header, recallFromRm, stockCorrection);
        softAssert.verifyAll();
        return this;
    }

}
