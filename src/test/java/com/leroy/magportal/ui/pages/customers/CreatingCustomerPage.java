package com.leroy.magportal.ui.pages.customers;

import com.leroy.constants.Gender;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.common.MenuPage;
import com.leroy.magportal.ui.pages.customers.modal.CustomersFoundWithThisPhoneModalWindow;
import com.leroy.magportal.ui.models.customers.CustomerData;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.support.Color;
import org.testng.util.Strings;

import java.util.Arrays;
import java.util.List;

public class CreatingCustomerPage extends MenuPage {

    static final String HEADER = "Создание клиента";

    public CreatingCustomerPage(Context context) {
        super(context);
    }

    private enum PageControls {
        MaleBtn, FemaleBtn, NameFld, PhoneFld, PersonalPhoneBtn, WorkPhoneBtn
    }

    @WebFindBy(xpath = "//div[contains(@class, 'modal-content')]",
            metaName = "Модальное окно о найденных клиентах с этим телефоном")
    CustomersFoundWithThisPhoneModalWindow modalWindow;

    @WebFindBy(xpath = "//span[text()='" + HEADER + "']", metaName = "Основной заголовок страницы")
    Element headerLbl;

    @WebFindBy(xpath = "//button[contains(@class, 'Button-type-empty-main')]", metaName = "Кнопка назад")
    Button backBtn;

    @WebFindBy(xpath = "//h5", metaName = "Подзаголовок страницы")
    Element subHeaderLbl;

    @WebFindBy(text = "Введенные данные нельзя отредактировать или удалить после сохранения.",
            metaName = "Подсказка уведомление")
    Element helpInfoLbl;

    @WebFindBy(id = "male")
    Button maleOptionBtn;

    @WebFindBy(id = "female")
    Button femaleOptionBtn;

    @WebFindBy(xpath = "//input[@name='firstName']", metaName = "Поле Имя")
    EditBox firstNameFld;

    @WebFindBy(xpath = "//input[@name='firstName']/following::span[contains(@class, 'lmui-Input-Bordered__tooltip')]",
            metaName = "Поле подсказка об ошибке под полем 'Имя'")
    Element firstNameTooltipErrorLbl;

    @WebFindBy(xpath = "//input[@name='middleName']")
    EditBox middleNameFld;

    @WebFindBy(xpath = "//input[@name='lastName']")
    EditBox lastNameFld;

    @WebFindBy(text = "Контакты")
    Element contactsLbl;

    @WebFindBy(xpath = "//input[@name='phones[0].value']")
    EditBox phoneFld;

    @WebFindBy(xpath = "//input[@name='phones[0].value']/following::span[contains(@class, 'lmui-Input-Bordered__tooltip')]",
            metaName = "Поле подсказка об ошибке под полем 'Телефон'")
    Element phoneTooltipErrorLbl;

    @WebFindBy(xpath = "//button[contains(@id, 'phones[0]_personal')]")
    Button phonePersonalOptionBtn;

    @WebFindBy(xpath = "//button[contains(@id, 'phones[0]_work')]")
    Button phoneWorkOptionBtn;

    @WebFindBy(xpath = "//input[@name='emails[0].value']")
    Element emailFld;

    @WebFindBy(xpath = "//button[contains(@id, 'emails[0]_personal')]")
    Button emailPersonalOptionBtn;

    @WebFindBy(xpath = "//button[contains(@id, 'emails[0]_work')]")
    Button emailWorkOptionBtn;

    @WebFindBy(text = "Адреса")
    Element addressLbl;

    @WebFindBy(xpath = "//input[@name='addresses[0].addressName']")
    Element addressFld;

    @WebFindBy(xpath = "//input[@name='addresses[0].province']")
    Element regionFld;

    @WebFindBy(xpath = "//input[@name='addresses[0].city']")
    Element cityFld;

    @WebFindBy(xpath = "//input[@name='addresses[0].line1']")
    Element streetFld;

    @WebFindBy(xpath = "//input[@name='addresses[0].line2']")
    Element houseFld;

    @WebFindBy(xpath = "//input[@name='addresses[0].line3']")
    Element buildFld;

    @WebFindBy(xpath = "//input[@name='addresses[0].flat']")
    Element flatFld;

    @WebFindBy(xpath = "//input[@name='addresses[0].entrance']")
    Element entranceFld;

    @WebFindBy(xpath = "//input[@name='addresses[0].floor']")
    Element floorFld;

    @WebFindBy(xpath = "//input[@name='addresses[0].intercome']")
    Element intercomFld;

    @WebFindBy(xpath = "//span[text()='Показать все поля' or text()='Скрыть дополнительные поля']/ancestor::button",
            metaName = "Кнопка 'Показать/Скрыть дополнительные поля'")
    Element showHideAdditionalFieldsBtn;

    @WebFindBy(xpath = "//button[contains(@class, 'lmui-Button-shadow')]")
    Button createBtn;

    @Override
    public void waitForPageIsLoaded() {
        helpInfoLbl.waitForVisibility();
    }

    private boolean isButtonActive(Button button) {
        return button.getBackgroundColor().equals(Color.fromString("#DAF0D8"));
    }

    // ACTIONS

    @Step("Нажмите кнопку вернуться назад")
    public CustomerPage clickBackButton() {
        backBtn.click();
        return new CustomerPage(context);
    }

    @Step("Введите {text} в поле 'Имя'")
    public CreatingCustomerPage enterTextInFirstNameInputField(String text) {
        firstNameFld.clearAndFill(text);
        return this;
    }

    @Step("Введите {text} в поле 'Телефон'")
    public CreatingCustomerPage enterTextInPhoneInputField(String text) {
        phoneFld.clear();
        phoneFld.click();
        phoneFld.fill(text);
        return this;
    }

    @Step("Нажмите кнопку 'Создать'")
    public CreatingCustomerPage clickCreateButtonNegativePath() {
        createBtn.click();
        return this;
    }

    @Step("Нажмите кнопку 'Создать'")
    public CustomerPersonalInfoPage clickCreateButtonHappyPath() {
        createBtn.click();
        return new CustomerPersonalInfoPage(context);
    }

    @Step("Нажмите кнопку 'Показать все поля'")
    public CreatingCustomerPage clickShowAllFieldsButton() {
        showHideAdditionalFieldsBtn.click();
        lastNameFld.waitForVisibility();
        return this;
    }

    @Step("Нажмите кнопку 'Скрыть все поля'")
    public CreatingCustomerPage clickHideAllFieldsButton() {
        showHideAdditionalFieldsBtn.click();
        lastNameFld.waitForInvisibility();
        return this;
    }

    @Step("Нажмите кнопку Вернуться для всплывающего окна")
    public CreatingCustomerPage clickModalWindowReturnButton() {
        modalWindow.returnBtn.click();
        modalWindow.returnBtn.waitForInvisibility();
        return this;
    }

    @Step("Заполните все необходимые поля для клиента")
    public CreatingCustomerPage enterCustomerData(CustomerData customerData) {
        switch (customerData.getGender()) {
            case MALE:
                maleOptionBtn.click();
                break;
            case FEMALE:
                femaleOptionBtn.click();
                break;
        }
        enterTextInFirstNameInputField(customerData.getFirstName());
        // Phone
        if (customerData.isPersonalPhone()) {
            phonePersonalOptionBtn.click();
        }
        if (customerData.isWorkPhone()) {
            phoneWorkOptionBtn.click();
        }
        if (Strings.isNotNullAndNotEmpty(customerData.getPhoneNumber()))
            enterTextInPhoneInputField(customerData.getPhoneNumber());
        // Email
        if (customerData.isPersonalEmail()) {
            emailPersonalOptionBtn.click();
        }
        if (customerData.isWorkEmail()) {
            emailWorkOptionBtn.click();
        }
        return this;
    }

    // VERIFICATIONS

    public CreatingCustomerPage verifyRequiredElements(boolean emailShouldBeVisible) {
        softAssert.isElementTextEqual(headerLbl, HEADER);
        softAssert.isElementTextEqual(subHeaderLbl, HEADER);
        softAssert.isElementVisible(helpInfoLbl);
        softAssert.isElementVisible(maleOptionBtn);
        softAssert.isElementVisible(femaleOptionBtn);
        softAssert.isElementVisible(firstNameFld);
        softAssert.isElementVisible(contactsLbl);
        softAssert.isElementVisible(phoneFld);
        softAssert.isElementVisible(phonePersonalOptionBtn);
        softAssert.isElementVisible(phoneWorkOptionBtn);
        // Дополнительные поля должны быть не видны по-умолчанию
        softAssert.isElementNotVisible(middleNameFld);
        softAssert.isElementNotVisible(lastNameFld);
        if (emailShouldBeVisible) {
            softAssert.isElementVisible(emailFld);
            softAssert.isElementVisible(emailPersonalOptionBtn);
            softAssert.isElementVisible(emailWorkOptionBtn);
        } else {
            softAssert.isElementNotVisible(emailFld);
            softAssert.isElementNotVisible(emailPersonalOptionBtn);
            softAssert.isElementNotVisible(emailWorkOptionBtn);
        }
        softAssert.isElementNotVisible(addressLbl);
        softAssert.isElementNotVisible(addressFld);
        softAssert.isElementNotVisible(regionFld);
        softAssert.isElementNotVisible(cityFld);
        softAssert.isElementNotVisible(streetFld);
        softAssert.isElementNotVisible(houseFld);
        softAssert.isElementNotVisible(buildFld);
        softAssert.isElementNotVisible(flatFld);
        softAssert.isElementNotVisible(entranceFld);
        softAssert.isElementNotVisible(floorFld);
        softAssert.isElementNotVisible(intercomFld);
        //
        softAssert.isElementTextEqual(showHideAdditionalFieldsBtn, "ПОКАЗАТЬ ВСЕ ПОЛЯ");
        softAssert.isElementVisible(createBtn);
        softAssert.verifyAll();
        return this;
    }

    public CreatingCustomerPage verifyRequiredElements() {
        return verifyRequiredElements(false);
    }

    public CreatingCustomerPage verifyAllAdditionalFields() {
        softAssert.isElementVisible(middleNameFld);
        softAssert.isElementVisible(lastNameFld);
        softAssert.isElementVisible(emailFld);
        softAssert.isElementVisible(emailPersonalOptionBtn);
        softAssert.isElementVisible(emailWorkOptionBtn);
        softAssert.isElementVisible(addressLbl);
        softAssert.isElementVisible(addressFld);
        softAssert.isElementVisible(regionFld);
        softAssert.isElementVisible(cityFld);
        softAssert.isElementVisible(streetFld);
        softAssert.isElementVisible(houseFld);
        softAssert.isElementVisible(buildFld);
        softAssert.isElementVisible(flatFld);
        softAssert.isElementVisible(entranceFld);
        softAssert.isElementVisible(floorFld);
        softAssert.isElementVisible(intercomFld);
        softAssert.isElementTextEqual(showHideAdditionalFieldsBtn,
                "СКРЫТЬ ДОПОЛНИТЕЛЬНЫЕ ПОЛЯ");
        softAssert.verifyAll();
        return this;
    }

    public CreatingCustomerPage shouldErrorTooltipUnderFirstNameFldHasValidText() throws Exception {
        shouldControlsHighlightedInRed(Arrays.asList(PageControls.NameFld));
        anAssert.isElementTextEqual(firstNameTooltipErrorLbl,
                "Используй только буквы русского алфавита");
        return this;
    }

    public CreatingCustomerPage shouldErrorTooltipUnderPhoneFldHasValidText() throws Exception {
        shouldControlsHighlightedInRed(Arrays.asList(PageControls.PhoneFld));
        anAssert.isElementTextEqual(phoneTooltipErrorLbl,
                "Введи телефон в формате +7 XXX XXX-XX-XX");
        return this;
    }

    public CreatingCustomerPage shouldAllRequiredFieldsHighlightedInRed() throws Exception {
        return shouldControlsHighlightedInRed(Arrays.asList(PageControls.MaleBtn,
                PageControls.FemaleBtn, PageControls.NameFld, PageControls.PhoneFld,
                PageControls.PersonalPhoneBtn, PageControls.WorkPhoneBtn));
    }

    public CreatingCustomerPage shouldControlsHighlightedInRed(List<PageControls> dangerPageControls)
            throws Exception {
        Color dangerColor = Color.fromString("#FF0000");
        for (PageControls control : dangerPageControls) {
            switch (control) {
                case MaleBtn:
                    softAssert.isEquals(
                            maleOptionBtn.findChildElement("//span[contains(@class, 'lmui-Icon')]")
                                    .getFillColor(), dangerColor,
                            "Цвет кнопки 'Мужской' должен быть подсвечен красным");
                    softAssert.isEquals(
                            maleOptionBtn.findChildElement("//span[contains(@class, 'Button-Text')]")
                                    .getColor(), dangerColor,
                            "Цвет кнопки 'Мужской' должен быть подсвечен красным");
                    break;
                case FemaleBtn:
                    softAssert.isEquals(
                            femaleOptionBtn.findChildElement("//span[contains(@class, 'lmui-Icon')]")
                                    .getFillColor(), dangerColor,
                            "Цвет кнопки 'Женский' должен быть подсвечен красным");
                    softAssert.isEquals(
                            femaleOptionBtn.findChildElement("//span[contains(@class, 'Button-Text')]")
                                    .getColor(), dangerColor,
                            "Цвет кнопки 'Женский' должен быть подсвечен красным");
                    break;
                case NameFld:
                    softAssert.isEquals(
                            firstNameFld.findChildElement("/../fieldset").getBorderColor(),
                            dangerColor, "Цвет поля 'Имя' должно быть подсвечено красным");
                    break;
                case PhoneFld:
                    softAssert.isEquals(
                            phoneFld.findChildElement("/../fieldset").getBorderColor(),
                            dangerColor, "Цвет поля 'Телефон' должно быть подсвечено красным");
                    softAssert.isElementVisible(phoneTooltipErrorLbl);
                    break;
                case PersonalPhoneBtn:
                    softAssert.isEquals(
                            phonePersonalOptionBtn.findChildElement("//span[contains(@class, 'Button-Text')]")
                                    .getColor(), dangerColor,
                            "Цвет кнопки 'Личный' должен быть подсвечен красным");
                    break;
                case WorkPhoneBtn:
                    softAssert.isEquals(
                            phoneWorkOptionBtn.findChildElement("//span[contains(@class, 'Button-Text')]")
                                    .getColor(), dangerColor,
                            "Цвет кнопки 'Рабочий' должен быть подсвечен красным");
                    break;
                default:
                    throw new RuntimeException("Method shouldControlsHighlightedInRed is not implemented for " + control);
            }
        }
        softAssert.verifyAll();
        return this;
    }

    public CreatingCustomerPage shouldBeEnteredDataMatchThis(CustomerData customerData) {
        if (customerData.getGender() != null)
            if (customerData.getGender().equals(Gender.MALE))
                softAssert.isTrue(isButtonActive(maleOptionBtn),
                        "Мужской пол должен быть выбран");
            else
                softAssert.isTrue(isButtonActive(femaleOptionBtn),
                        "Женский пол должен быть выбран");
        if (customerData.getFirstName() != null)
            softAssert.isElementTextEqual(firstNameFld, StringUtils.capitalize(customerData.getFirstName()));
        String expectedPhoneNumber = customerData.getPhoneNumber();
        if (expectedPhoneNumber != null) {
            if (expectedPhoneNumber.length() == 10)
                expectedPhoneNumber = String.format("+7 (%s) %s-%s-%s",
                        expectedPhoneNumber.substring(0, 3), expectedPhoneNumber.substring(3, 6),
                        expectedPhoneNumber.substring(6, 8), expectedPhoneNumber.substring(8, 10));
            softAssert.isElementTextEqual(phoneFld, expectedPhoneNumber);
        }
        if (customerData.isPersonalPhone()) {
            softAssert.isTrue(isButtonActive(phonePersonalOptionBtn),
                    "Должен быть указан Личный телефон");
        }
        if (customerData.isWorkPhone()) {
            softAssert.isTrue(isButtonActive(phoneWorkOptionBtn),
                    "Должен быть указан Рабочий телефон");
        }
        softAssert.verifyAll();
        return this;
    }

    // Modal window verifications
    public CreatingCustomerPage verifyModalWindowRequiredElements() throws Exception {
        modalWindow.returnBtn.waitForVisibility();
        softAssert.isElementVisible(modalWindow.customersFoundWithThisPhoneLbl);
        softAssert.isElementVisible(modalWindow.subHeaderMsgLbl);
        softAssert.isElementVisible(modalWindow.returnBtn);
        softAssert.verifyAll();
        return this;
    }

    public CreatingCustomerPage shouldCustomerRecordsArePresentInModalWindow() {
        anAssert.isTrue(modalWindow.customerRows.getCount() > 0,
                "В модальном окне должны быть записи о клиентах с именем и телефоном");
        return this;
    }

    public CreatingCustomerPage shouldModalWindowInvisible() {
        anAssert.isElementNotVisible(modalWindow);
        return this;
    }
}
