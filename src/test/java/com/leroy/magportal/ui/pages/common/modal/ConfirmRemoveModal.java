package com.leroy.magportal.ui.pages.common.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;

public class ConfirmRemoveModal extends MagPortalBasePage {

    @WebFindBy(xpath = "//div[contains(@class, 'Modal')]//button[descendant::span[text()='Удалить']]",
            metaName = "Кнопка 'Удалить'")
    Button confirmBtn;

    @Step("Подвтердить удаление, нажав на кнопку 'Удалить'")
    public void clickConfirmBtn() {
        confirmBtn.click(short_timeout);
        confirmBtn.waitForInvisibility();
    }
}
