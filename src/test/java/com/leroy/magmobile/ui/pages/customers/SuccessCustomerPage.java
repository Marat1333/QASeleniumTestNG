package com.leroy.magmobile.ui.pages.customers;

import com.leroy.magmobile.ui.pages.common.SuccessPage;
import io.qameta.allure.Step;

public class SuccessCustomerPage extends SuccessPage {
    @Override
    protected String getExpectedMainBodyMessage() {
        return "Новый клиент создан";
    }

    @Override
    protected String getExpectedSubBodyMessage() {
        return "Клиент создан. Ты можешь найти его\nв списке недавних клиентов.";
    }

    @Override
    protected String getExpectedSubmitText() {
        return "ПЕРЕЙТИ К СПИСКУ КЛИЕНТОВ";
    }

    // Action

    @Step("Нажать на 'Перейти к списку клиентов'")
    public MainCustomerPage clickGoToCustomerListButton() {
        getSubmitBtn().click();
        return new MainCustomerPage();
    }
}
