package com.leroy.magportal.ui.pages.orders.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.pages.common.modal.CommonYesNoModal;
import io.qameta.allure.Step;

public class AllowRefundModal extends CommonYesNoModal {

    @WebFindBy(xpath = "//button[@data-testid='aao-partially-delivered-modal-yes-button']",
            metaName = "Кнопка Да")
    Button yesBtn;

    @WebFindBy(xpath = "//button[@data-testid='aao-partially-delivered-modal-no-button']",
            metaName = "Кнопка Нет")
    Button noBtn;


    // Actions

    @Step("Нажать кнопку Да")
    public MainReturnDeliveryValueModal clickYesBtn() {
        yesBtn.click();
        return new MainReturnDeliveryValueModal();
    }

    @Step("Нажать кнопку Нет")
    public void clickNoBtn() {
        noBtn.click();
    }

}
