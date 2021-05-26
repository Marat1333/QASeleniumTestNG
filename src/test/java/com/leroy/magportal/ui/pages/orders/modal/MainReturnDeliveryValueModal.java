package com.leroy.magportal.ui.pages.orders.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.magportal.ui.pages.common.modal.CommonYesNoModal;
import io.qameta.allure.Step;

/**
 * Описывает модальное окно возврата стоимости доставки, вызываемое с вкладки  "Содержание"
 */

public class MainReturnDeliveryValueModal extends CommonYesNoModal {

    @WebFindBy(xpath = "//button[@data-testid='aao-confirm-delivery-modal-save-btn']",
            metaName = "Кнопка Сохранить")
    Button saveBtn;

    @WebFindBy(xpath = "//button[@data-testid='aao-refund-delivery-modal-cancel-btn']",
            metaName = "Кнопка Отмена")
    Button cancelBtn;

    @WebFindBy(xpath = "//div[@id='newDeliveryPrice']//input",
            metaName = "Новая стоимость доставки")
    EditBox inputDeliveryFinalPrice;

    // Actions

    @Step("Нажать кнопку Отмены")
    public MainReturnDeliveryValueModal clickCancelOrderButton() {
        cancelBtn.click();
        return new MainReturnDeliveryValueModal();
    }

    @Step("Нажать кнопку Сохранить")
    public MainReturnDeliveryValueModal clickSaveOrderButton() {
        saveBtn.click();
        return new MainReturnDeliveryValueModal();
    }

    @Step("Изменить Новую стоимость доставки")
    public void editInputDeliveryFinalPrice(double value) {
        inputDeliveryFinalPrice.hoverOver();
        inputDeliveryFinalPrice.clear(true);
        inputDeliveryFinalPrice.fill(String.valueOf(value));
        inputDeliveryFinalPrice.sendBlurEvent();
    }

    @Step("Проверить, что Новая стоимость доставки равна {value}")
    public MainReturnDeliveryValueModal shouldInputDeliveryFinalPrice(double value) throws Exception {
        anAssert.isEquals(Double.parseDouble(inputDeliveryFinalPrice.getText()), value,
                "Неверное кол-во 'К выдаче'");
        return this;
    }
}
