package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.TopMenuPage;
import io.qameta.allure.Step;

// Раздел "Продажа" -> Страница "Клиенты"
public class CustomerPage extends TopMenuPage {

    @AppFindBy(text = "СОЗДАТЬ НОВВОГО КЛИЕНТА")
    MagMobButton createNewCustomerBtn;

    // Actions

    @Step("Нажать кнопку 'Создать нового клиента'")
    public NewCustomerInfoPage clickCreateNewCustomer() throws Exception {
        createNewCustomerBtn.click();
        return new NewCustomerInfoPage();
    }

}
