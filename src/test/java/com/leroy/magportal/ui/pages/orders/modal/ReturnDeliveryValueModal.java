package com.leroy.magportal.ui.pages.orders.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.magportal.ui.pages.common.modal.CommonYesNoModal;
import com.leroy.magportal.ui.pages.orders.GiveAwayShipOrderPage;
import com.leroy.magportal.ui.pages.orders.OrderCreatedContentPage;
import io.qameta.allure.Step;

public class ReturnDeliveryValueModal extends CommonYesNoModal {

    @WebFindBy(xpath = "//button[@data-testid='aao-refund-delivery-modal-save-btn']",
            metaName = "Кнопка Сохранить")
    Button saveBtn;

    @WebFindBy(xpath = "//button[@data-testid='aao-refund-delivery-modal-cancel-btn']",
            metaName = "Кнопка Отмена")
    Button cancelBtn;

    @WebFindBy(xpath = "//input[@ id='deliveryFinalPrice']",
            metaName = "Новая стоимость доставки")
    EditBox inputDeliveryFinalPrice;

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

    @Step("Изменить Новую стоимость доставки")
    public void editInputDeliveryFinalPrice(double value) {
        inputDeliveryFinalPrice.clear(true);
        inputDeliveryFinalPrice.fill(String.valueOf(value));
        inputDeliveryFinalPrice.sendBlurEvent();
    }

    @Step("Проверить, что Новая стоимость доставки равна {value}")

    public ReturnDeliveryValueModal shouldInputDeliveryFinalPrice(double value) throws Exception {
        anAssert.isEquals(Double.parseDouble(inputDeliveryFinalPrice.getText()), value,
                "Неверное кол-во 'К выдаче'");
        return this;
    }
}
