package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class EditCustomerInfoPage extends CommonCustomerInfoPage<EditCustomerInfoPage> {

    public EditCustomerInfoPage() {
        super(EditCustomerInfoPage.class);
    }

    @AppFindBy(text = "СОХРАНИТЬ")
    Element saveBtn;

    // Actions

    @Step("Нажмите кнопку 'СОХРАНИТЬ'")
    public PersonalInfoPage clickSaveButton() {
        saveBtn.click();
        return new PersonalInfoPage();
    }

    // Verifications

}
