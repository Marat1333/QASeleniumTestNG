package com.leroy.magmobile.ui.pages.sales.orders.estimate;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.customers.EditCustomerContactDetailsPage;
import com.leroy.magmobile.ui.pages.customers.EditCustomerInfoPage;
import com.leroy.magmobile.ui.pages.customers.SearchCustomerPage;
import io.qameta.allure.Step;

public class EditCustomerModalPage extends CommonMagMobilePage {

    @AppFindBy(xpath = "//android.widget.TextView")
    Element headerLbl;

    @AppFindBy(text = "Изменить контактные данные")
    Element changeContactDetails;

    @AppFindBy(text = "Редактировать данные клиента")
    Element editCustomerInfo;

    @AppFindBy(text = "Выбрать другого клиента")
    Element selectAnotherCustomer;

    @Override
    public void waitForPageIsLoaded() {
        changeContactDetails.waitForVisibility();
    }

    // Actions
    @Step("Нажмите 'Изменить контактные данные'")
    public EditCustomerContactDetailsPage clickChangeContactDetails() {
        changeContactDetails.click();
        return new EditCustomerContactDetailsPage();
    }

    @Step("Нажмите 'Редактировать данные клиента'")
    public EditCustomerInfoPage clickEditCustomerInfo() {
        editCustomerInfo.click();
        return new EditCustomerInfoPage();
    }

    @Step("Нажмите 'Выбрать другого клиента'")
    public SearchCustomerPage clickSelectAnotherCustomer() {
        selectAnotherCustomer.click();
        return new SearchCustomerPage();
    }

    // Verifications

    @Step("Проверить, что модальное окно для редактирования данных о клиенте отображается корректно")
    public EditCustomerModalPage verifyRequiredElements() {
        softAssert.areElementsVisible(changeContactDetails, editCustomerInfo, selectAnotherCustomer);
        softAssert.verifyAll();
        return this;
    }

}
