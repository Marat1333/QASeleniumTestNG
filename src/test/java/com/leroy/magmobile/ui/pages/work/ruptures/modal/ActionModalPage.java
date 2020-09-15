package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;

public class ActionModalPage extends CommonMagMobilePage {
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

    public AcceptRecallFromRmModalPage recallFromRm(){
        recallFromRm.click();
        return new AcceptRecallFromRmModalPage();
    }

    public ActionModalPage verifyRequiredElements(){
        softAssert.areElementsVisible(header, recallFromRm, stockCorrection);
        softAssert.verifyAll();
        return this;
    }

}
