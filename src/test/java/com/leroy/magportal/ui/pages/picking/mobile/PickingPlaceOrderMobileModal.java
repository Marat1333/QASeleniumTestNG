package com.leroy.magportal.ui.pages.picking.mobile;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.webelements.commonelements.PuzComboBox;
import io.qameta.allure.Step;

import java.util.List;

public class PickingPlaceOrderMobileModal extends MagPortalBasePage {

    @WebFindBy(xpath = "//div[contains(@class, 'Select__container-overflowMode-ellipsis')][1]",
            metaName = "Выбор зоны")
    PuzComboBox zoneCmbBox;

    @WebFindBy(xpath = "//div[contains(@class, 'Select__container-overflowMode-ellipsis')][2]",
            metaName = "Выбор места")
    PuzComboBox placeCmbBox;

    @WebFindBy(xpath = "//button[contains(@class, 'ModalFooter__ConfirmButton')]",
            metaName = "Кнопка подтверждения")
    Button confirmBtn;

    public enum Zone {
        PRODUCT_DELIVERY("Выдачи товара"),
        CUSTOMER_ORDERS("Клиентских заказов"),
        DELIVERY_BUFFER("Доставка.Буфер"),
        TRANSFER_TO_LOGISTIC("Передачи в логистику"),
        INFO_BUREAU("Инфо-бюро"),
        SHOPPING_ROOM("Торговый зал");

        private String value;

        Zone(String val) {
            value = val;
        }

        public String getValue() {
            return value;
        }
    }

    // Grab info

    @Step("Получить со страницы выбранные места размещения заказа")
    public List<String> getSelectedPlaces() throws Exception {
        return placeCmbBox.getOptionList();
    }

    // Actions

    @Step("Выбрать зону размещения")
    public PickingPlaceOrderMobileModal selectZone(Zone zone) throws Exception {
        zoneCmbBox.selectOption(zone.getValue());
        return this;
    }

    @Step("Выбрать {index}-ое место размещения")
    public PickingPlaceOrderMobileModal selectPlace(int index, boolean closeModal) throws Exception {
        index--;
        placeCmbBox.selectOptionByIndex(index);
        if (closeModal)
            placeCmbBox.closeModalWindow();
        return this;
    }

    @Step("Нажать кнопку 'Разместить'")
    public PickingContentMobilePage clickPlaceButton() {
        confirmBtn.click();
        waitForSpinnerAppearAndDisappear();
        return new PickingContentMobilePage();
    }

    // Verifications

    @Step("Проверить, что выбрано в 'Место размещения заказа'")
    public PickingPlaceOrderMobileModal shouldPlaceOptionIs(String value) {
        String selectedValue = placeCmbBox.getSelectedOptionText();
        anAssert.isTrue(selectedValue.contains(value), "Место размещения. Actual: " + selectedValue +
                "; Expected:");
        return this;
    }

    @Step("Проверить, что 'Место размещения заказа' очищено")
    public PickingPlaceOrderMobileModal shouldPlaceOptionIsClear() {
        anAssert.isEquals(placeCmbBox.getSelectedOptionText(), "",
                "Место размещения должно быть пустым");
        return this;
    }

}
