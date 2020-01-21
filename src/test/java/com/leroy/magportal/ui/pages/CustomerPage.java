package com.leroy.magportal.ui.pages;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.*;
import com.leroy.magportal.ui.pages.common.MenuPage;
import io.qameta.allure.Step;

public class CustomerPage extends MenuPage {

    private static final String HEADER = "Клиенты";
    public static final String PHONE_OPTION = "Телефон";
    public static final String CLIENT_CARD_OPTION = "Карта клиента";
    public static final String EMAIL_OPTION = "Email";

    private String SPECIFIC_SEARCH_OPTION_XPATH = "//div[contains(@class, 'CustomerListSearch__select-menu-item') and contains(.,'%s')]";

    public CustomerPage(TestContext context) {
        super(context);
    }

    @WebFindBy(xpath = "//span[text()='"+HEADER+"']", metaName = "Заголовок страницы")
    private Element headerLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'lmui-View lm-puz2-Customer-CustomerListSearch__mode-opener')]/span[1]",
            metaName = "Текст обозначающий выбранный тип поиска")
    private Element searchTypeLbl;

    Element showSearchOptionsBtn = E("//div[contains(@class, 'CustomerListSearch__mode-opener')]//span[contains(@class, 'Icon')]",
            "Кнопка для отображения опций поиска");

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Input__icon_left-xl-1 with-action')]",
            metaName = "Кнопка поиска")
    private Button searchBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Input__icon_right-xl-1 with-action')]",
            metaName = "Кнопка поиска")
    private Button scanBarCodeBtn;

    @WebFindBy(id = "inputId", metaName = "Поле для ввода телефона")
    private EditBox searchFld;

    @WebFindBy(xpath = "//button[descendant::span[text()='Создать клиента']]",
            metaName = "Кнопка 'Создать клиента'")
    private Element createClientBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'Button-type-border-primaryLight') and contains(.,'Создать клиента')]",
            metaName = "Кнопка 'Создать клиента' на форме, когда клиент не найден")
    private Element notFoundFormCreateClientBtn;

    @Override
    public void waitForPageIsLoaded() {
        createClientBtn.waitForVisibility();
    }

    // ACTIONS

    @Step("Нажмите кнопку 'Создать клиента'")
    public CreatingCustomerPage clickCreateClientButton() {
        createClientBtn.click();
        return new CreatingCustomerPage(context);
    }

    @Step("Нажмите кнопку 'Создать клиента' под формой 'Не найдено'")
    public CreatingCustomerPage clickNotFoundCreateClientButton() {
        notFoundFormCreateClientBtn.click();
        return new CreatingCustomerPage(context);
    }

    @Step("Выберите тип поиска по {text}")
    public CustomerPage selectSearchOption(String text) {
        showSearchOptionsBtn.click();
        E(String.format(SPECIFIC_SEARCH_OPTION_XPATH, text)).click();
        return this;
    }

    @Step("Введите {text} в поле поиска и нажмите иконку поиска")
    public CustomerPage searchClient(String text) {
        searchFld.clear();
        searchFld.click();
        searchFld.fill(text);
        searchBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    // --------------------- Verifications --------------------------//

    public CustomerPage verifyRequiredElements() {
        softAssert.isElementTextEqual(headerLbl, HEADER);
        softAssert.isElementVisible(searchTypeLbl);
        softAssert.isElementVisible(searchBtn);
        softAssert.isElementVisible(searchFld);
        softAssert.isElementVisible(scanBarCodeBtn);
        softAssert.isElementVisible(createClientBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что текущий тип поиска - {text}")
    public CustomerPage shouldCurrentSearchTypeLabelIs(String text) {
        anAssert.isEquals(searchTypeLbl.getText(), text, "Текущий тип поиска должен быть %s");
        return this;
    }

    @Step("Проверить, что форма 'Не найдено клиента с таким...' отображается корректно")
    public CustomerPage verifyClientNotFoundForm() {
        softAssert.isFalse(isAlertErrorMessageVisible(), "Не должно быть видно никаких ошибок");
        notFoundFormCreateClientBtn.waitForVisibility(tiny_timeout);
        softAssert.isElementVisible(E("contains(Не найдено)"));
        if (searchTypeLbl.getText().equals(PHONE_OPTION)) {
            softAssert.isElementTextEqual(E("contains(Клиента с таким телефоном)"),
                    "Клиента с таким телефоном не найдено. Ты можешь создать нового клиента.");
        } else {
            softAssert.isElementTextEqual(E("contains(Клиента с таким email)"),
                    "Клиента с таким email’ом не найдено. Ты можешь создать нового клиента.");
        }
        softAssert.isElementVisible(notFoundFormCreateClientBtn);
        softAssert.verifyAll();
        return this;
    }

}
