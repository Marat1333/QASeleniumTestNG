package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class DeleteSessionModalPage extends CommonMagMobilePage {
    @AppFindBy(text = "Удаление сессии")
    Element header;

    @AppFindBy(text = "УДАЛИТЬ")
    Button deleteSessionBtn;

    @AppFindBy(text = "НЕТ, ОСТАВИТЬ")
    Button cancelBtn;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        deleteSessionBtn.waitForVisibility();
        cancelBtn.waitForVisibility();
    }

    @Step("Удалить сессию")
    public void confirmDelete(){
        deleteSessionBtn.click();
        deleteSessionBtn.waitForInvisibility();
    }
}
