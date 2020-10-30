package com.leroy.magmobile.ui.pages.work.ruptures.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class DeleteModalPage extends CommonMagMobilePage {
    @AppFindBy(text = "УДАЛИТЬ")
    Button deleteBtn;

    @AppFindBy(text = "НЕТ, ОСТАВИТЬ")
    Button cancelBtn;

    @Override
    protected void waitForPageIsLoaded() {
        cancelBtn.waitForVisibility();
        deleteBtn.waitForVisibility();
    }

    @Step("Удалить перебой")
    public void confirmDelete() {
        deleteBtn.click();
    }

    @Step("Тапнуть на кнопку 'нет, оставить'")
    public void cancelDelete() {
        cancelBtn.click();
    }

    public DeleteModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(deleteBtn, cancelBtn);
        softAssert.verifyAll();
        return this;
    }
}
