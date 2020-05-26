package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.estimate.EstimatePage;
import io.qameta.allure.Step;

public class EditCustomerContactDetailsPage extends CommonMagMobilePage {

    public EditCustomerContactDetailsPage(Context context) {
        super(context);
    }

    @AppFindBy(containsText = "Изменение контактных", metaName = "Основной заголовок")
    Element headerLbl;

    @AppFindBy(accessibilityId = "newPhoneValue", metaName = "Поле для добавления нового телефонного номера")
    EditBox newPhoneFld;

    @AppFindBy(text = "СОХРАНИТЬ", metaName = "Кнопка 'Сохранить'")
    MagMobGreenSubmitButton saveBtn;

    @Override
    public void waitForPageIsLoaded() {
        saveBtn.waitForVisibility();
    }

    @Step("Проверить, что страница 'Изменение контактных данных' отображается корректно")
    public EditCustomerContactDetailsPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, saveBtn);
        softAssert.verifyAll();
        return this;
    }

    // Actions

    @Step("Ввести новый номер телефона")
    public void fillInPhoneNumber(String value) {
        E("$phone").click();
        newPhoneFld.click();
        newPhoneFld.fill(value);
        newPhoneFld.submit();
    }

    @Step("Нажать кнопку Сохранить")
    public EstimatePage clickSaveButton() {
        saveBtn.click();
        return new EstimatePage(context);
    }

    // Verifications

    @Step("Только что добавленный телефон отображается в поле и равен {value}")
    public EditCustomerContactDetailsPage shouldNewPhoneEqualTo(String value) {
        if (!value.startsWith("+7"))
            value = "+7" + value;
        String actualValue = newPhoneFld.getText();
        anAssert.isEquals(actualValue.replaceAll(" |-",""), value,
                "В поле для добавления нового телефонна ожидался другой номер");
        return this;
    }

}
