package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import lombok.Getter;

public class DeleteSessionByBtnModalPage extends DeleteSessionByDeletingProductModalPage{
    @Getter
    @AppFindBy(text = "Удалить сессию?")
    private Element header;

    @Override
    protected void waitForPageIsLoaded() {
        super.waitForPageIsLoaded();
    }

    @Override
    public void confirmDelete() {
        super.confirmDelete();
    }
}
