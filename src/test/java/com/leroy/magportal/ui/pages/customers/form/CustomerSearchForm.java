package com.leroy.magportal.ui.pages.customers.form;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SalesHistoryUnitsModalPage;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.magportal.ui.pages.cart_estimate.CartEstimatePage;
import com.leroy.magportal.ui.pages.cart_estimate.widget.CustomerPuzWidget;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.customers.CreateCustomerForm;
import com.leroy.magportal.ui.webelements.commonelements.PuzComboBox;
import com.leroy.magportal.ui.webelements.commonelements.PuzSelectControl;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.openqa.selenium.By;

public class CustomerSearchForm extends MagPortalBasePage {

    @WebFindBy(xpath = "//button[descendant::span[text()='Добавить клиента']]",
            metaName = "Текст на кнопке 'Добавить клиента'")
    Element addCustomerBtnLbl;

    @WebFindBy(xpath = "//div[contains(@class, 'CustomerControl__error-tooltip')]//span",
            metaName = "Тултип с ошибкой")
    Element errorTooltipLbl;

    @WebFindBy(xpath = "//button[descendant::span[text()='Физ. лица и профи']]",
            metaName = "Кнопка-опция 'Физ. лица и профи'")
    Element naturalPersonBtn;

    @WebFindBy(xpath = "//button[descendant::span[text()='Юр. лица']]",
            metaName = "Кнопка-опция 'Юр. лица'")
    Element legalPersonBtn;

    @WebFindBy(xpath = "//span[contains(text(), 'Телефон')]/ancestor::button", metaName = "Кнопка открытия выбора поиска")
    Button searchTypeOpenBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'lmui-SearchString__desktop-options-dropdown')]//button", metaName = "Контроль выбора типа поиска")
    ElementList<Element> searchTypeComboBox;

    //TODO Поправить на нормальный поиск локатора, как только его сделают. Было: //input[@name='phone']
    @WebFindBy(xpath = "(//div[contains(@class, 'lmui-SearchString__desktop-input')])[2]//input", metaName = "Поле для ввода телефона клиента")
    EditBox customerPhoneSearchFld;

    //TODO Поправить на нормальный поиск локатора, как только его сделают. Было: //input[@name='card']
    @WebFindBy(xpath = "(//div[contains(@class, 'lmui-SearchString__desktop-input')])[2]//input", metaName = "Поле для ввода номера карты клиента")
    EditBox customerCardSearchFld;

    //TODO Поправить на нормальный поиск локатора, как только его сделают. Было: //input[@name='email']
    @WebFindBy(xpath = "(//div[contains(@class, 'lmui-SearchString__desktop-input')])[2]//input", metaName = "Поле для ввода email клиента")
    EditBox customerEmailSearchFld;

    @WebFindBy(xpath = "//div[contains(@class, 'CustomerControl-SearchMode__menu')]//button[descendant::span[text()='Создать клиента']]",
            metaName = "Кнопка 'Создать клиента'")
    Element createCustomerBtn;

    //TODO Первая часть xpath - костыльное решение, должно быть исправлено после внесения изменений в магпортал
    @WebFindBy(xpath = "(//div[contains(@class, 'lmui-SearchString__desktop-input')])[1]//div[substring(@class, string-length(@class) - 19) = 'SearchResultListItem']")
    ElementList<Element> customerSearchItems;

    @WebFindBy(xpath = "//div[contains(@class, 'CustomerControl-ViewCard__action-btn')]",
            metaName = "Кнопка '...' для раскрытия меню с действиями над клиентом")
    Element customerActionBtn;

    @WebFindBy(xpath = "(//div[contains(@class, 'CustomerControl')]//button)[1]", metaName = "Опция 'Редактировать данные клиента'")
    Element editCustomerOptionBtn;

    @WebFindBy(xpath = "(//div[contains(@class, 'CustomerControl')]//button)[2]", metaName = "Опция 'Выбрать другого клиента'")
    Element searchCustomerOptionBtn;

    @WebFindBy(xpath = "(//div[contains(@class, 'CustomerControl')]//button)[3]", metaName = "Опция 'Удалить клиента'")
    Element clearCustomerOptionBtn;

    // When Customer is selected
    @WebFindBy(xpath = "//div[contains(@class, 'CustomerControl__mode-VIEW')]", metaName = "Карточка клиента")
    CustomerPuzWidget selectedCustomerCard;

    public boolean isCustomerSelected() {
        return selectedCustomerCard.isVisible();
    }

    // Actions

    public SimpleCustomerData getCustomerData() {
        return selectedCustomerCard.collectDataFromPage();
    }

    @Step("Нажать на кнопку 'Добавить клиента'")
    public CustomerSearchForm clickAddCustomer() {
        addCustomerBtnLbl.scrollTo();
        addCustomerBtnLbl.click();
        legalPersonBtn.waitForVisibility();
        return this;
    }

    @Step("Действия с клиентом: Редактировать данные клиента")
    public CreateCustomerForm clickOptionEditCustomer() {
        editCustomerOptionBtn.click();
        return new CreateCustomerForm();
    }

    @Step("Действия с клиентом: Выбрать другого клиента")
    public void clickOptionSelectAnotherCustomer() {
        customerActionBtn.click();
        searchCustomerOptionBtn.click();
    }

    @Step("Действия с клиентом: Удалить клиента")
    public void clickOptionRemoveCustomer() {
        customerActionBtn.click();
        clearCustomerOptionBtn.click();
    }

    @Step("Выбираем '{value}' тип поиска клиента")
    public void selectSearchType(String value) throws Exception {
        searchTypeComboBox.findElemByText(value, true).click();
    }

    private void enterTextInSearchCustomerField(EditBox inputBox, String value) {
        if (value.startsWith("+7"))
            value = value.substring(2);
        inputBox.scrollTo();
        inputBox.clear();
        inputBox.click();
        inputBox.fill(value);
        inputBox.submit();
        waitForSpinnerAppearAndDisappear();
    }

    @Step("Вводим номер телефона {value} для поиска клиента")
    public void enterPhoneInSearchCustomerField(String value) {
        enterTextInSearchCustomerField(customerPhoneSearchFld, value);
    }

    @Step("Вводим номер карты {value} для поиска клиента")
    public void enterCardNumberInSearchCustomerField(String value) {
        enterTextInSearchCustomerField(customerCardSearchFld, value);
    }

    @Step("Вводим email {value} для поиска клиента")
    public void enterEmailInSearchCustomerField(String value) {
        enterTextInSearchCustomerField(customerEmailSearchFld, value);
    }

    @Step("Выбираем клиента по номеру телефона {phone}")
    public CustomerSearchForm selectCustomerByPhone(String phone) throws Exception {
        if (phone.startsWith("+7"))
            phone = phone.substring(2);
        enterPhoneInSearchCustomerField(phone);
        int foundCustomerAccount = customerSearchItems.getCount();
        if (foundCustomerAccount == 0)
            customerPhoneSearchFld.waitForInvisibility(short_timeout);
        anAssert.isTrue(foundCustomerAccount > 0 || !customerPhoneSearchFld.isVisible(),
                "Клиент с номером +7" + phone + " не удалось выбрать");
        if (foundCustomerAccount > 0) {
            customerSearchItems.get(0).click();
            customerSearchItems.waitUntilElementCountEquals(0);
        }
        customerPhoneSearchFld.waitForInvisibility();
        waitForSpinnerDisappear();
        return this;
    }

    @Step("Выбираем клиента по email {email}")
    public void selectCustomerByEmail(String email) throws Exception {
        enterEmailInSearchCustomerField(email);
        int foundCustomerAccount = customerSearchItems.getCount();
        if (foundCustomerAccount == 0)
            customerPhoneSearchFld.waitForInvisibility(short_timeout);
        customerSearchItems.get(0).click();
        anAssert.isTrue(foundCustomerAccount > 0 || !customerPhoneSearchFld.isVisible(),
                "Клиент с email " + email + " не удалось выбрать");
    }

    @Step("Нажать кнопку 'Создать клиента' в всплывающем окне")
    public CreateCustomerForm clickCreateCustomerButton() {
        anAssert.isTrue(createCustomerBtn.isVisible(tiny_timeout),
                createCustomerBtn.getMetaName() + " не отображается");
        createCustomerBtn.click();
        return new CreateCustomerForm();
    }

    // Verifications
    @Step("Проверить, что все необходимые элементы для добавления клиента доступны")
    public CustomerSearchForm shouldAddingNewUserAvailable() {
        softAssert.areElementsVisible(naturalPersonBtn, legalPersonBtn, customerPhoneSearchFld);
        customerPhoneSearchFld.scrollTo();
        customerPhoneSearchFld.click();
        softAssert.isEquals(customerPhoneSearchFld.getText(), "+7 (___) ___-__-__",
                "Поле для заполнения телефона должно быть пустым");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в выпадающем списке корректные опции")
    public CustomerSearchForm shouldSearchTypeOptionsAreCorrected() throws Exception {
        searchTypeOpenBtn.click();
        List<String> optionsList = searchTypeComboBox.getTextList().stream().map((e) -> e.replaceAll("[/\n>]", "")).collect(Collectors.toList());
        
        anAssert.isEquals(optionsList,
            Arrays.asList(CartEstimatePage.SearchType.PHONE, CartEstimatePage.SearchType.CARD, CartEstimatePage.SearchType.EMAIL),
            "Ожидались другие опции выбора типа поиска");
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет телефон {val}")
    public CustomerSearchForm shouldSelectedCustomerHasPhone(String val) {
        if (!val.startsWith("+7"))
            val = "+7" + val;
        softAssert.isEquals(selectedCustomerCard.getPhone(),
                val, "Ожидался другой номер телефона у выбранного клиента");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет email {val}")
    public CustomerSearchForm shouldSelectedCustomerHasEmail(String val) {
        softAssert.isEquals(selectedCustomerCard.getEmail(),
                val, "Ожидался другой email у выбранного клиента");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет имя {val}")
    public CustomerSearchForm shouldSelectedCustomerHasName(String val) {
        softAssert.isEquals(selectedCustomerCard.getName(),
                val, "Ожидалось другое имя у выбранного клиента");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет номер карты {val}")
    public CustomerSearchForm shouldSelectedCustomerHasCardNumber(String val) {
        softAssert.isEquals(selectedCustomerCard.getCardNumber(),
                val, "Ожидался другой номер телефона у выбранного клиента");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент имеет следующие данные: {expectedCustomerData}")
    public CustomerSearchForm shouldSelectedCustomerIs(SimpleCustomerData expectedCustomerData) {
        softAssert.isElementNotVisible(naturalPersonBtn);
        softAssert.isElementNotVisible(legalPersonBtn);
        softAssert.isElementNotVisible(customerPhoneSearchFld);
        softAssert.verifyAll();
        shouldSelectedCustomerHasPhone(expectedCustomerData.getPhoneNumber());
        if (expectedCustomerData.getEmail() != null)
            shouldSelectedCustomerHasEmail(expectedCustomerData.getEmail());
        shouldSelectedCustomerHasName(expectedCustomerData.getName());
        softAssert.verifyAll();
        return this;
    }

    @Step("Кликнуть и проверить, что поле 'Карта' и содержит значение: {value}")
    public CustomerSearchForm clickCardSearchFieldAndCheckThatDefaultValueIs(String value) {
        customerCardSearchFld.click();
        anAssert.isEquals(customerCardSearchFld.getText().replaceAll("_", "").trim(),
                value.replaceAll(" ", ""),
                "Ожидалось другое значение в поле поиска по Карте");
        return this;
    }

    @Step("Проверить, что тултип ошибки 'Необходимо добавить клиента' отображается")
    public CustomerSearchForm shouldErrorTooltipCustomerIsRequiredVisible() {
        anAssert.isElementTextEqual(errorTooltipLbl, "Необходимо добавить клиента");
        return this;
    }

}
