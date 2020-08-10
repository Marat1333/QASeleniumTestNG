package com.leroy.magportal.ui.pages.common.modal;

import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;

public abstract class CommonYesNoModal extends MagPortalBasePage {

    protected Button yesBtn() {
        return E("//div[contains(@class, 'Modal')]//button[descendant::span[text()='Да']]", Button.class);
    }

    protected Button noBtn() {
        return E("//div[contains(@class, 'Modal')]//button[descendant::span[text()='Нет']]", Button.class);
    }

    // Actions

    @Step("Нажать 'Да'")
    public void clickYesButton() {
        yesBtn().click();
    }

    @Step("Нажать 'Нет'")
    public void clickNoButton() {
        noBtn().click();
    }

}
