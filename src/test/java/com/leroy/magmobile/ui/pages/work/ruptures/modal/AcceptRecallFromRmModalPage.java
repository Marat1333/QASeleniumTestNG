package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.recall_from_rm.AddProductToRecallFromRmRequestPage;

public class AcceptRecallFromRmModalPage extends CommonMagMobilePage {
    @AppFindBy(containsText = "Будет создана")
    Element header;

    @AppFindBy(containsText = "Сейчас откроется")
    Element description;

    @AppFindBy(text = "ПРОДОЛЖИТЬ")
    Button continueBtn;

    @AppFindBy(text = "ОТМЕНА")
    Button cancel;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        description.waitForVisibility();
    }

    public AddProductToRecallFromRmRequestPage acceptRequest(){
        continueBtn.click();
        return new AddProductToRecallFromRmRequestPage();
    }

    public AcceptRecallFromRmModalPage verifyRequiredElements(){
        softAssert.areElementsVisible(header, description, continueBtn, cancel);
        softAssert.verifyAll();
        return this;
    }
}
