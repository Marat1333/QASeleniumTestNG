package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;

public class DeleteSessionModalPage extends DeleteModalPage{
    @AppFindBy(text = "Удалить сессию?")
    Element header;

    @Override
    protected void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
        header.waitForVisibility();
    }

    @Override
    public DeleteSessionModalPage verifyRequiredElements() {
        super.verifyRequiredElements();
        anAssert.isElementVisible(header);
        return this;
    }
}
