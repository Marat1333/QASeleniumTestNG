package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class EditPhoneModalPage extends CommonMagMobilePage {

    @AppFindBy(text = "Телефон")
    Element headerLbl;

    @AppFindBy(accessibilityId = "value")
    EditBox phoneFld;

    @AppFindBy(accessibilityId = "Button-container", metaName = "Кнопка подтверждения")
    Element confirmBtn;

    // Actions

    @Step("Изменить номер телефона в модальном окне")
    public void editPhoneNumber(String value) {
        if (value.startsWith("+7"))
            value = value.substring(2);
        phoneFld.clearFillAndSubmit(value);
        confirmBtn.click();
    }

    @Step("Проверить, что модальное окно для редактирования телефонного номера отображается корректно")
    public EditPhoneModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, phoneFld);
        softAssert.verifyAll();
        return this;
    }

}
