package com.leroy.magmobile.ui.pages.work.recall_from_rm;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;

public class DraftRecallFromRmRequestPage extends OrderPage {
    @AppFindBy(text = "Пополнение торг. зала")
    Element header;

    @AppFindBy(containsText = "ДАЛЕЕ")
    Button continueBtn;

    @Override
    public void waitForPageIsLoaded() {
        header.waitForVisibility();
        continueBtn.waitForVisibility();
    }

    public OrderPage continueFillingRecallRequest(){
        continueBtn.click();
        return new OrderPage();
    }

    public DraftRecallFromRmRequestPage verifyRequiredElements() {
        softAssert.areElementsVisible(header, continueBtn);
        softAssert.verifyAll();
        return this;
    }
}
