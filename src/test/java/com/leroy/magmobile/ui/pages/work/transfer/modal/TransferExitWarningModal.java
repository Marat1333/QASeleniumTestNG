package com.leroy.magmobile.ui.pages.work.transfer.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.transfer.TransferOrderStep1Page;
import io.qameta.allure.Step;

public class TransferExitWarningModal extends CommonMagMobilePage {

    @AppFindBy(text = "ВЫЙТИ")
    MagMobButton confirmBtn;

    @AppFindBy(text = "ОТМЕНА")
    MagMobButton cancelBtn;

    @Step("Нажать 'Выйти'")
    public void clickConfirmButton() {
        confirmBtn.click();
    }

    @Step("Нажать 'Отмена'")
    public TransferOrderStep1Page clickCancelButton() {
        cancelBtn.click();
        return new TransferOrderStep1Page();
    }

    @Step("Проверить, что модальное окно (Предупреждение о выходе) отображается корректно")
    public TransferExitWarningModal verifyRequiredElements() {
        softAssert.areElementsVisible(confirmBtn, cancelBtn);
        softAssert.verifyAll();
        return this;
    }

}
