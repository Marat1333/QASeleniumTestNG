package com.leroy.magportal.ui.pages.cart_estimate.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class AddDeliveryModal extends MagPortalBasePage {

    private final static String MODAL_DIV_XPATH = "//div[contains(@class, 'Common-ConfirmModal__modal__container')]";

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//input")
    EditBox deliveryPriceFld;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//button[descendant::*[text()='Не добавлять']]",
            metaName = "Кнопка 'Не добавлять'")
    Button cancelBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//button[contains(@class, 'Common-ConfirmModal__modal__okButton')]",
            metaName = "Кнопка Применить (Сохранить)")
    Button confirmBtn;


    // Actions

    @Step("Ввести сумму доставку в поле стоимость")
    public AddDeliveryModal enterPriceDelivery(Double val) {
        deliveryPriceFld.clearFillAndSubmit(ParserUtil.doubleToStr(val, 2, false));
        return this;
    }

    @Step("Нажать кнопку 'Добавить'")
    public EstimatePage clickConfirmButton() {
        confirmBtn.click();
        return new EstimatePage();
    }
}
