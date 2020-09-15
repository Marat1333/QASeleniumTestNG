package com.leroy.magmobile.ui.pages.work.recall_from_rm;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;

public class AddProductToRecallFromRmRequestPage extends CommonMagMobilePage {
    @AppFindBy(text = "Добавление товара")
    Element header;

    @AppFindBy(text = "ДОБАВИТЬ В ЗАЯВКУ")
    Button addToRequest;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        addToRequest.waitForVisibility();
    }

    public DraftRecallFromRmRequestPage createDraftRecallFromRmRequest(){
        addToRequest.click();
        return new DraftRecallFromRmRequestPage();
    }

    public AddProductToRecallFromRmRequestPage verifyRequiredElements(){
        softAssert.areElementsVisible(header, addToRequest);
        softAssert.verifyAll();
        return this;
    }
}
