package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class EditCustomerInfoPage extends CommonCustomerInfoPage<EditCustomerInfoPage> {

    public EditCustomerInfoPage() {
        super(EditCustomerInfoPage.class);
    }

    @AppFindBy(text = "СОХРАНИТЬ И ДОБАВИТЬ")
    Element saveAndAddBtn;

    // Actions

    @Step("Нажмите кнопку 'СОХРАНИТЬ И ДОБАВИТЬ'")
    public EditCustomerInfoPage clickSaveButton() {
        saveAndAddBtn.click();
        return this;
    }

    // Verifications

}
