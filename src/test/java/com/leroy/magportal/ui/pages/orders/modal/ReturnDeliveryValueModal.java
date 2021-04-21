package com.leroy.magportal.ui.pages.orders.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.pages.common.modal.CommonYesNoModal;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import io.qameta.allure.Step;

public class ReturnDeliveryValueModal extends CommonYesNoModal {

    @WebFindBy(xpath = "//button[@data-testid='aao-confirm-delivery-modal-save-btn']",
            metaName = "Кнопка Сохранить")
    Button saveBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__buttonsWrapper')]//button[descendant::span[text()='Отмена']]",
            metaName = "Кнопка Отмена")
    Button cancelBtn;

    // Actions

    @Step("Нажать кнопку Отмены")
    public ReturnDeliveryValueModal clickCancelOrderButton() {
        cancelBtn.click();
        return new ReturnDeliveryValueModal();
    }

    @Step("Нажать кнопку Сохранить")
    public ReturnDeliveryValueModal clickSaveOrderButton() {
        saveBtn.click();
        return new ReturnDeliveryValueModal();
    }
}
