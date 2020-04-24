package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import io.qameta.allure.Step;

public class EditCustomerContactDetailsPage extends CommonMagMobilePage {

    public EditCustomerContactDetailsPage(Context context) {
        super(context);
    }

    @AppFindBy(containsText = "Изменение контактных", metaName = "Основной заголовок")
    Element headerLbl;

    @AppFindBy(accessibilityId = "phone", metaName = "Поле для добавления телефонного номера")
    EditBox phoneFld;

    @AppFindBy(text = "СОХРАНИТЬ", metaName = "Кнопка 'Сохранить'")
    MagMobGreenSubmitButton saveBtn;

    @Step("Проверить, что страница 'Изменение контактных данных' отображается корректно")
    public EditCustomerContactDetailsPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, phoneFld, saveBtn);
        softAssert.verifyAll();
        return this;
    }

    // Actions

    @Step("Ввести новый номер телефона")
    public void fillInPhoneNumber(String value) {
        phoneFld.clearFillAndSubmit(value);
        //newPhoneValue
        String s = "";
    }

    @Step("Нажать кнопку Сохранить")
    public void clickSaveButton() {
        saveBtn.click();
    }

}
