package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class NewCustomerInfoPage extends CommonCustomerInfoPage<NewCustomerInfoPage> {

    public NewCustomerInfoPage() {
        super(NewCustomerInfoPage.class);
    }

    @AppFindBy(text = "СОЗДАТЬ")
    Element submitBtn;

    // Actions

    @Step("Нажмите кнопку 'Создать'")
    public SuccessCustomerPage clickSubmitButton() {
        submitBtn.click();
        return new SuccessCustomerPage();
    }

}
