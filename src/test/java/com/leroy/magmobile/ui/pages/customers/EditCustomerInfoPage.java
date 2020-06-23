package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class EditCustomerInfoPage extends CommonCustomerInfoPage {

    @AppFindBy(text = "СОХРАНИТЬ И ДОБАВИТЬ")
    Element saveAndAddBtn;

    @Override
    public void waitForPageIsLoaded() {
        nameFld.waitForVisibility();
    }

    // Actions

    /*@Step("Заполнить / Изменить поле с номером телефона")
    public EditCustomerInfoPage editPhoneNumber(String value) {
        mainPhoneEditFld.click();
        return new EditPhoneModalPage(context)
                .verifyRequiredElements()
                .editPhoneNumber(value);
    }*/

    @Step("Нажмите кнопку 'СОХРАНИТЬ И ДОБАВИТЬ'")
    public EditCustomerInfoPage clickSaveButton() {
        saveAndAddBtn.click();
        return this;
    }

    // Verifications
    @Step("Проверить, что страница 'Редактирования клиента' отображается корректно")
    public EditCustomerInfoPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, backBtn, nameFld, lastNameFld, middleNameFld,
                maleOptionBtn, femaleOptionBtn, mainPhoneEditFld);
        softAssert.verifyAll();
        return this;
    }
}
