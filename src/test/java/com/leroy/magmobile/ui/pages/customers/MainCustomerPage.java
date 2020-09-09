package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.pages.common.TopMenuPage;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

// Раздел "Продажа" -> Страница "Клиенты"
public class MainCustomerPage extends TopMenuPage {

    AndroidScrollView<MagCustomerData> recentCustomerScrollView = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.view.ViewGroup[android.widget.TextView[@text='НЕДАВНИЕ КЛИЕНТЫ']]]/following-sibling::android.view.ViewGroup[android.view.ViewGroup]",
            CustomerWidget.class
    );

    @AppFindBy(accessibilityId = "MainScreenTitle", metaName = "Поле поиска клиента")
    Element searchFld;

    @AppFindBy(text = "СОЗДАТЬ НОВОГО КЛИЕНТА")
    MagMobButton createNewCustomerBtn;

    // Actions

    @Step("Нажать в поле поиска клиента")
    public SearchCustomerPage clickSearchCustomerField() {
        searchFld.click();
        return new SearchCustomerPage();
    }

    @Step("Нажать кнопку 'Создать нового клиента'")
    public NewCustomerInfoPage clickCreateNewCustomer() throws Exception {
        createNewCustomerBtn.click();
        return new NewCustomerInfoPage();
    }

    // Verifications

    @Step("Проверить, что {index} клиент соответствуют ожидаемым данным")
    public MainCustomerPage shouldRecentCustomerIs(int index, MagCustomerData expectedData) throws Exception {
        index--;
        recentCustomerScrollView.getDataObj(index).assertEqualsNotNullExpectedFields(expectedData);
        return this;
    }

    // Widget

    private static class CustomerWidget extends CardWidget<MagCustomerData> {

        public CustomerWidget(WebDriver driver, CustomLocator locator) {
            super(driver, locator);
        }

        @AppFindBy(xpath = ".//android.widget.TextView[1]", metaName = "Имя клиента")
        Element name;

        @AppFindBy(xpath = ".//android.widget.TextView[2]", metaName = "Телефон клиента")
        Element phone;

        @Override
        public MagCustomerData collectDataFromPage(String pageSource) {
            MagCustomerData magCustomerData = new MagCustomerData();
            magCustomerData.setName(name.getText(pageSource));
            magCustomerData.setPhone(ParserUtil.standardPhoneFmt(phone.getText(pageSource)));
            return magCustomerData;
        }

        @Override
        public boolean isFullyVisible(String pageSource) {
            return phone.isVisible(pageSource) && name.isVisible(pageSource);
        }
    }

}
