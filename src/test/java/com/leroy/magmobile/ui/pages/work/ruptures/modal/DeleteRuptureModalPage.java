package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;

public class DeleteRuptureModalPage extends DeleteModalPage {
    @AppFindBy(text = "Удалить перебой?")
    Element header;

    @Override
    protected void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        header.waitForVisibility();
    }

    @Override
    public DeleteRuptureModalPage verifyRequiredElements() {
        super.verifyRequiredElements();
        anAssert.isElementVisible(header);
        return this;
    }
}
