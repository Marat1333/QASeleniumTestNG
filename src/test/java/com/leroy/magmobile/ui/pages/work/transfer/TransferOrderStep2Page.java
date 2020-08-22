package com.leroy.magmobile.ui.pages.work.transfer;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import io.qameta.allure.Step;

public class TransferOrderStep2Page extends TransferOrderPage {

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='Клиент']]", metaName = "Поле 'Клиент'")
    Element clientFld;

    @AppFindBy(text = "ОФОРМИТЬ ПРОДАЖУ")
    MagMobGreenSubmitButton submitButton;

    // Actions
    @Step("Нажать на поле 'Клиент'")
    public SearchCustomerPage clickClientField() {
        clientFld.click();
        return new SearchCustomerPage();
    }

    @Step("Нажать кнопку 'Оформить продажу'")
    public TransferSuccessPage clickSubmitButton() {
        submitButton.click();
        return new TransferSuccessPage();
    }

}
