package com.leroy.magportal.ui.pages.orders.modal;

import com.leroy.magportal.ui.pages.common.modal.CommonYesNoModal;

public class RemoveOrderModal extends CommonYesNoModal {

    @Override
    public void clickYesButton() {
        super.clickYesButton();
        waitForSpinnerAppearAndDisappear();
        waitForSpinnerAppearAndDisappear(1);
    }
}
