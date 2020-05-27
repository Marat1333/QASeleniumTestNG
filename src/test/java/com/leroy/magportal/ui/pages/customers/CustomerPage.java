package com.leroy.magportal.ui.pages.customers;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.webelements.commonelements.PuzComboBox;
import io.qameta.allure.Step;

public class CustomerPage extends MenuPage {

    private static final String HEADER = "Клиенты";
    public static final String PHONE_OPTION = "Телефон";
    public static final String CLIENT_CARD_OPTION = "Карта клиента";
    public static final String EMAIL_OPTION = "Email";

    @WebFindBy(xpath = "//span[text()='" + HEADER + "']", metaName = "Заголовок страницы")
    private Element headerLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'Common-Filter__select')]", metaName = "Контрол выбора типа поиска")
    PuzComboBox searchTypeComboBox;

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Input__icon_left-xl-1 with-action')]",
            metaName = "Кнопка поиска")
    private Button searchBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Input__icon_right-xl-1 with-action')]",
            metaName = "Кнопка поиска")
    private Button scanBarCodeBtn;

    @WebFindBy(xpath = "//input[@name='phone']", metaName = "Поле для ввода телефона")
    private EditBox searchPhoneFld;
    @WebFindBy(xpath = "//input[@name='email']", metaName = "Поле для ввода email")
    private EditBox searchEmailFld;

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
        return new CreatingCustomerPage();
    }

    @Step("Нажмите кнопку 'Создать клиента' под формой 'Не найдено'")
    public CreatingCustomerPage clickNotFoundCreateClientButton() {
        notFoundFormCreateClientBtn.click();
        return new CreatingCustomerPage();
    }

    @Step("Выберите тип поиска по {text}")
    public CustomerPage selectSearchOption(String text) throws Exception{
        searchTypeComboBox.selectOption(text);
        return this;
    }

    @Step("Введите {text} в поле поиска и нажмите иконку поиска")
    public CustomerPage searchClient(String text) {
        EditBox searchFld = searchPhoneFld.isVisible()? searchPhoneFld : searchEmailFld;
        if (text.startsWith("+7"))
            text = text.substring(2);
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
        softAssert.isElementVisible(searchTypeComboBox);
        softAssert.isElementVisible(searchBtn);
        softAssert.isElementVisible(searchPhoneFld);
        softAssert.isElementVisible(scanBarCodeBtn);
        softAssert.isElementVisible(createClientBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что текущий тип поиска - {text}")
    public CustomerPage shouldCurrentSearchTypeLabelIs(String text) {
        anAssert.isEquals(searchTypeComboBox.getSelectedOptionText(), text,
                "Текущий тип поиска должен быть %s");
        return this;
    }

    @Step("Проверить, что форма 'Не найдено клиента с таким...' отображается корректно")
    public CustomerPage verifyClientNotFoundForm() {
        softAssert.isFalse(isAlertErrorMessageVisible(), "Не должно быть видно никаких ошибок");
        notFoundFormCreateClientBtn.waitForVisibility(tiny_timeout);
        softAssert.isElementVisible(E("contains(Не найдено)"));
        if (searchTypeComboBox.getSelectedOptionText().equals(PHONE_OPTION)) {
            softAssert.isElementTextEqual(E("contains(Клиента с таким телефоном)"),
                    "Клиента с таким телефоном не найдено.");
        } else {
            softAssert.isElementTextEqual(E("contains(Клиента с таким email)"),
                    "Клиента с таким email’ом не найдено.");
        }
        softAssert.isElementVisible(notFoundFormCreateClientBtn);
        softAssert.verifyAll();
        return this;
    }

}
