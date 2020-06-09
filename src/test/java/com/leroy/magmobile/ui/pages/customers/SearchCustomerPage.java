package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.MagCustomerData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.EstimatePage;
import com.leroy.magmobile.ui.pages.search.widgets.SearchCustomerWidget;
import io.qameta.allure.Step;

import java.util.List;

public class SearchCustomerPage extends CommonMagMobilePage {

    public enum SearchType {
        BY_PHONE, BY_CARD, BY_EMAIL;
    }

    AndroidScrollView<MagCustomerData> mainScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.view.ViewGroup[android.widget.TextView[@index='1']]",
            SearchCustomerWidget.class);

    @AppFindBy(accessibilityId = "BackCloseModal")
    Element backCloseBtn;

    private static final String searchOptionsXpath = "(//android.widget.HorizontalScrollView//android.widget.TextView)";

    @AppFindBy(xpath = searchOptionsXpath + "[1]")
    Element phoneOptionLbl;

    @AppFindBy(xpath = searchOptionsXpath + "[2]")
    Element customerCardOptionLbl;

    @AppFindBy(xpath = searchOptionsXpath + "[3]")
    Element emailOptionLbl;

    private static final String screenHeaderId = "ScreenHeader-CustomerSearchScreen ";

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='" + screenHeaderId + "']/android.widget.TextView",
            metaName = "Префикс для поля поиска")
    Element searchPrefixLbl;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='" + screenHeaderId + "']/android.widget.EditText",
            metaName = "Поле поиска")
    EditBox searchFld;

    @Override
    public void waitForPageIsLoaded() {
        searchFld.waitForVisibility();
    }

    // Grab data from page

    @Step("Получаем данные о {index}-ом клиенте")
    public MagCustomerData getCustomerDataFromSearchListByIndex(int index) {
        List<MagCustomerData> customerDataList = mainScrollView.getFullDataList(index);
        MagCustomerData customerData = customerDataList.get(customerDataList.size() - 1);
        anAssert.isFalse(customerData.getName().isEmpty(), "Клиент не содержит имени");
        return customerData;
    }

    // ACTIONS

    @Step("Выберите {index}-ого клиента из списка поиска")
    public EstimatePage selectCustomerFromSearchList(int index) throws Exception {
        index--;
        mainScrollView.clickElemByIndex(index);
        return new EstimatePage(); // возможно, надо этот метод сделать просто void.
    }

    @Step("Введите {text} в поле поиска")
    public SearchCustomerPage enterTextInSearchField(String text) {
        searchFld.clearFillAndSubmit(text);
        waitUntilProgressBarIsVisible();
        waitUntilProgressBarIsInvisible();
        shouldNotAnyErrorVisible();
        return this;
    }

    @Step("Выбрать тип поиска по {searchType}")
    public SearchCustomerPage selectSearchType(SearchType searchType) {
        switch (searchType) {
            case BY_CARD:
                customerCardOptionLbl.click();
                break;
            case BY_EMAIL:
                emailOptionLbl.click();
                break;
            case BY_PHONE:
                phoneOptionLbl.click();
                break;
        }
        return this;
    }

    @Step("Найдите клиента по номеру телефона: {value}")
    public void searchCustomerByPhone(String value) throws Exception {
        phoneOptionLbl.click();
        if (value.startsWith("+7"))
            value = value.substring(2);
        enterTextInSearchField(value);
        mainScrollView.clickElemByIndex(0);
    }

    @Step("Найдите клиента по номеру карты: {value}")
    public SearchCustomerPage searchCustomerByCard(String value) throws Exception {
        customerCardOptionLbl.click();
        if (value.length() == 17)
            value = value.substring(7);
        enterTextInSearchField(value);
        mainScrollView.clickElemByIndex(0);
        return this;
    }

    @Step("Найдите клиента по почте: {value}")
    public SearchCustomerPage searchCustomerByEmail(String value) {
        emailOptionLbl.click();
        enterTextInSearchField(value);
        return this;
    }

    // VERIFICATIONS

    @Step("Проверить, что страница 'Поиска клиентов' отображается корректно")
    public SearchCustomerPage verifyRequiredElements() {
        String ps = getPageSource();
        softAssert.areElementsVisible(ps, backCloseBtn, searchFld);
        softAssert.isElementTextEqual(phoneOptionLbl, "Телефон");
        softAssert.isElementTextEqual(customerCardOptionLbl, "№ карты клиента");
        softAssert.isElementTextEqual(emailOptionLbl, "Эл. почта");
        softAssert.verifyAll();
        return this;
    }

}
