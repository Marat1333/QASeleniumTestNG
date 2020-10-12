package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenCheckBox;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.customers.data.PhoneUiData;
import io.qameta.allure.Step;

public class EditPhoneModalPage extends CommonMagMobilePage {

    @AppFindBy(text = "Телефон")
    Element headerLbl;

    @AppFindBy(accessibilityId = "value")
    EditBox phoneFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'Личный')]]//android.view.ViewGroup",
            metaName = "Чек бокс 'Личный'")
    MagMobGreenCheckBox personalChkBox;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'Рабочий')]]//android.view.ViewGroup",
            metaName = "Чек бокс 'Рабочий'")
    MagMobGreenCheckBox workChkBox;

    @AppFindBy(accessibilityId = "Button-container", metaName = "Кнопка подтверждения")
    Element confirmBtn;

    // Actions

    @Step("Изменить номер телефона в модальном окне")
    public EditPhoneModalPage editPhoneNumber(PhoneUiData phoneData) throws Exception {
        String phoneNumber = phoneData.getPhoneNumber();
        if (phoneNumber.startsWith("+7"))
            phoneNumber = phoneNumber.substring(2);
        phoneFld.clearFillAndSubmit(phoneNumber);
        if (phoneData.getType() != null) {
            selectPhoneType(phoneData.getType());
        }
        confirmBtn.click();
        return this;
    }

    @Step("Выбрать тип телефона Личный / Рабочий")
    public EditPhoneModalPage selectPhoneType(PhoneUiData.Type type) throws Exception {
        switch (type) {
            case PERSONAL:
                personalChkBox.setValue(true);
                break;
            case WORK:
                workChkBox.setValue(true);
                break;
        }
        return this;
    }

    // Verifications

    @Step("Проверить, что модальное окно для редактирования телефонного номера отображается корректно")
    public EditPhoneModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, phoneFld);
        softAssert.verifyAll();
        return this;
    }

}
