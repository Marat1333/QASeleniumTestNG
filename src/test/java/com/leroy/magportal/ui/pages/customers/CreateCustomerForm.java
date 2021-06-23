package com.leroy.magportal.ui.pages.customers;

import com.leroy.constants.Gender;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.models.customers.CustomerWebData;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.pages.customers.modal.CustomersFoundWithThisPhoneModalWindow;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.support.Color;
import org.testng.util.Strings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateCustomerForm extends MagPortalBasePage {

    static final String HEADER = "Создание клиента";

    public enum CommunicationType {
        PERSONAL, WORK
    }

    private enum PageControls {
        MaleBtn, FemaleBtn, NameFld, PhoneFld, PersonalPhoneBtn, WorkPhoneBtn
    }

    @WebFindBy(xpath = "//div[contains(@class, 'Modal')]",
            metaName = "Модальное окно о найденных клиентах с этим телефоном")
    CustomersFoundWithThisPhoneModalWindow modalWindow;

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

    @WebFindBy(xpath = "//input[contains(@name, 'phones')]", clazz = EditBox.class)
    ElementList<EditBox> phoneFields;

    @WebFindBy(xpath = "//input[contains(@name, 'phones')]/following::span[contains(@class, 'lmui-Input-Bordered__tooltip')]",
            metaName = "Поле подсказка об ошибке под полем 'Телефон'")
    ElementList<Element> phoneTooltipErrorLabels;

    @WebFindBy(xpath = "//button[contains(@id, 'phones') and contains(@id, '_personal')]", clazz = Button.class)
    ElementList<Button> phonePersonalOptionButtons;

    @WebFindBy(xpath = "//button[contains(@id, 'phones') and contains(@id, '_work')]", clazz = Button.class)
    ElementList<Button> phoneWorkOptionButtons;

    @WebFindBy(xpath = "//div[contains(@class, 'row') and descendant::label[text()='Телефон']]//button[descendant::span[text()='Основной']]",
            clazz = PuzCheckBox.class, metaName = "Чек боксы телефонов 'Основной'")
    ElementList<PuzCheckBox> phoneMainCheckBoxes;

    @WebFindBy(xpath = "//button[descendant::span[text()='Добавить ещё телефон']]",
            metaName = "Кнопка 'Добавить еще телефон'")
    Element addPhoneMoreBtn;

    @WebFindBy(xpath = "//input[contains(@name, 'email')]", clazz = EditBox.class)
    ElementList<EditBox> emailFields;

    @WebFindBy(xpath = "//button[contains(@id, 'emails') and contains(@id, '_personal')]", clazz = Button.class)
    ElementList<Button> emailPersonalOptionButtons;

    @WebFindBy(xpath = "//button[contains(@id, 'emails') and contains(@id, '_work')]", clazz = Button.class)
    ElementList<Button> emailWorkOptionButtons;

    @WebFindBy(xpath = "//div[contains(@class, 'lmui-View-row') and descendant::label[text()='Email']]//button[descendant::span[text()='Основной']]",
            clazz = PuzCheckBox.class, metaName = "Чек боксы email'ов 'Основной'")
    ElementList<PuzCheckBox> emailMainCheckBoxes;

    @WebFindBy(xpath = "//button[descendant::span[text()='Добавить ещё email']]",
            metaName = "Кнопка 'Добавить еще email'")
    Element addMoreEmailBtn;

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

    @WebFindBy(xpath = "//form[contains(@class, 'lm-clients-Common-CustomerEditForm')]//button[contains(@class, 'lmui-Button-shadow')]")
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
        return new CustomerPage();
    }

    @Step("Введите {text} в поле 'Имя'")
    public CreateCustomerForm enterTextInFirstNameInputField(String text) throws Exception {
        firstNameFld.clearAndFill(text);
        return this;
    }

    @Step("Введите {text} в поле 'Фамилия'")
    public CreateCustomerForm enterTextInLastNameInputField(String text) throws Exception {
        lastNameFld.clearAndFill(text);
        return this;
    }

    @Step("Введите {text} в поле 'Телефон' #{index}")
    public CreateCustomerForm enterTextInPhoneInputField(int index, String text) throws Exception {
        index--;
        if (text.startsWith("+7"))
            text = text.substring(2);
        phoneFields.get(index).clear();
        phoneFields.get(index).click();
        phoneFields.get(index).fill(text);
        return this;
    }

    public CreateCustomerForm enterTextInPhoneInputField(String text) throws Exception {
        return enterTextInPhoneInputField(1, text);
    }

    @Step("Введите {text} в поле 'email' #{index}")
    public CreateCustomerForm enterEmail(int index, String text) throws Exception {
        index--;
        EditBox emailFld = emailFields.get(index);
        emailFld.clear();
        emailFld.click();
        emailFld.fill(text);
        return this;
    }

    public CreateCustomerForm enterEmail(String text) throws Exception {
        return enterEmail(1, text);
    }

    @Step("Обозначить телефон #{index} как {type}")
    public CreateCustomerForm clickTypePhone(int index, CommunicationType type) throws Exception {
        index--;
        if (type.equals(CommunicationType.PERSONAL))
            phonePersonalOptionButtons.get(index).click();
        else
            phoneWorkOptionButtons.get(index).click();
        return this;
    }

    @Step("Сделать телефон #{index} основным")
    public CreateCustomerForm makePhoneAsMain(int index) throws Exception {
        index--;
        anAssert.isTrue(phoneMainCheckBoxes.getCount() > index,
                "Чекбокс #" + (index + 1) + " отсутствует на странице");
        PuzCheckBox checkBox = phoneMainCheckBoxes.get(index);
        checkBox.setValue(true);
        return this;
    }

    @Step("Обозначить email #{index} как {type}")
    public CreateCustomerForm clickTypeEmail(int index, CommunicationType type) throws Exception {
        index--;
        if (type.equals(CommunicationType.PERSONAL))
            emailPersonalOptionButtons.get(index).click();
        else
            emailWorkOptionButtons.get(index).click();
        return this;
    }

    @Step("Сделать email #{index} основным")
    public CreateCustomerForm makeEmailAsMain(int index) throws Exception {
        index--;
        anAssert.isTrue(emailMainCheckBoxes.getCount() > index,
                "Чекбокс #" + (index + 1) + " отсутствует на странице");
        PuzCheckBox checkBox = emailMainCheckBoxes.get(index);
        checkBox.setValue(true);
        return this;
    }

    @Step("Нажмите 'Добавить еще телефон'")
    public CreateCustomerForm clickAddPhoneButton() {
        addPhoneMoreBtn.click();
        return this;
    }

    @Step("Нажмите 'Добавить еще email'")
    public CreateCustomerForm clickAddEmailButton() {
        addMoreEmailBtn.click();
        return this;
    }

    @Step("Нажмите кнопку 'Создать/Сохранить'")
    public void clickConfirmButton() {
        createBtn.waitForVisibility(3);
        createBtn.click();
        waitForSpinnerAppearAndDisappear();
        waitForSpinnerDisappear();
    }

    @Step("Нажмите кнопку 'Показать все поля'")
    public CreateCustomerForm clickShowAllFieldsButton() {
        showHideAdditionalFieldsBtn.click();
        lastNameFld.waitForVisibility();
        return this;
    }

    @Step("Нажмите кнопку 'Скрыть все поля'")
    public CreateCustomerForm clickHideAllFieldsButton() {
        showHideAdditionalFieldsBtn.click();
        lastNameFld.waitForInvisibility();
        return this;
    }

    @Step("Нажмите кнопку Вернуться для всплывающего окна")
    public CreateCustomerForm clickModalWindowReturnButton() {
        modalWindow.returnBtn.click();
        modalWindow.returnBtn.waitForInvisibility();
        return this;
    }

    @Step("Заполните все необходимые поля для клиента")
    public CreateCustomerForm enterCustomerData(CustomerWebData customerData) throws Exception {
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
            phonePersonalOptionButtons.get(0).click();
        }
        if (customerData.isWorkPhone()) {
            phoneWorkOptionButtons.get(0).click();
        }
        if (Strings.isNotNullAndNotEmpty(customerData.getPhoneNumber()))
            enterTextInPhoneInputField(customerData.getPhoneNumber());
        // Email
        if (customerData.isPersonalEmail()) {
            emailPersonalOptionButtons.get(0).click();
        }
        if (customerData.isWorkEmail()) {
            emailWorkOptionButtons.get(0).click();
        }
        if (customerData.getLastName() != null) {
            clickShowAllFieldsButton();
            enterTextInLastNameInputField(customerData.getLastName());
            clickHideAllFieldsButton();
        }
        return this;
    }

    // VERIFICATIONS

    public CreateCustomerForm verifyRequiredElements(boolean emailShouldBeVisible) throws Exception {
        softAssert.isElementTextEqual(subHeaderLbl, HEADER);
        softAssert.isElementVisible(helpInfoLbl);
        softAssert.isElementVisible(maleOptionBtn);
        softAssert.isElementVisible(femaleOptionBtn);
        softAssert.isElementVisible(firstNameFld);
        softAssert.isElementVisible(contactsLbl);
        softAssert.isTrue(phoneFields.get(0).isVisible(), "Поле 'Телефон' не отображается");
        softAssert.isTrue(phonePersonalOptionButtons.get(0).isVisible(), "Кнопка 'Личный' телефон не видна");
        softAssert.isTrue(phoneWorkOptionButtons.get(0).isVisible(), "Кнопка 'Рабочий' телефон не видна");
        // Дополнительные поля должны быть не видны по-умолчанию
        softAssert.isElementNotVisible(middleNameFld);
        softAssert.isElementNotVisible(lastNameFld);
        if (emailShouldBeVisible) {
            softAssert.isTrue(emailFields.get(0).isVisible(), "Поле email не отображается");
            softAssert.isTrue(emailPersonalOptionButtons.get(0).isVisible(), "Кнопка 'Личный' email не видна");
            softAssert.isTrue(emailWorkOptionButtons.get(0).isVisible(), "Кнопка 'Рабочий' email не видна");
        } else {
            softAssert.isTrue(emailFields.getCount() == 0, "Поле email отображается");
            softAssert.isTrue(emailPersonalOptionButtons.getCount() == 0, "Кнопка 'Личный' email видна");
            softAssert.isTrue(emailWorkOptionButtons.getCount() == 0, "Кнопка 'Рабочий' email видна");
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

    public CreateCustomerForm verifyRequiredElements() throws Exception {
        return verifyRequiredElements(false);
    }

    public CreateCustomerForm verifyAllAdditionalFields() throws Exception {
        softAssert.isElementVisible(middleNameFld);
        softAssert.isElementVisible(lastNameFld);
        softAssert.isTrue(emailFields.get(0).isVisible(), "Поле email не отображается");
        softAssert.isTrue(emailPersonalOptionButtons.get(0).isVisible(), "Кнопка 'Личный' email не видна");
        softAssert.isTrue(emailWorkOptionButtons.get(0).isVisible(), "Кнопка 'Рабочий' email не видна");
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

    public CreateCustomerForm shouldErrorTooltipUnderFirstNameFldHasValidText() throws Exception {
        shouldControlsHighlightedInRed(Arrays.asList(PageControls.NameFld));
        anAssert.isElementTextEqual(firstNameTooltipErrorLbl,
                "Используй только буквы русского алфавита");
        return this;
    }

    @Step("Проверить подсказку-ошибку о введенном телефоне")
    public CreateCustomerForm shouldErrorTooltipUnderPhoneFldHasValidText() throws Exception {
        shouldControlsHighlightedInRed(Collections.singletonList(PageControls.PhoneFld));
        anAssert.isEquals(phoneTooltipErrorLabels.get(0).getText(),
                "Введи номер телефона в формате +7 ХХХ ХХX-ХХ-ХХ.",
                "Неверное сообщение об ошибке введенных данных в поле Телефон");
        return this;
    }

    public CreateCustomerForm shouldAllRequiredFieldsHighlightedInRed() throws Exception {
        return shouldControlsHighlightedInRed(Arrays.asList(PageControls.MaleBtn,
                PageControls.FemaleBtn, PageControls.NameFld, PageControls.PhoneFld,
                PageControls.PersonalPhoneBtn, PageControls.WorkPhoneBtn));
    }

    public CreateCustomerForm shouldControlsHighlightedInRed(List<PageControls> dangerPageControls)
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
                            phoneFields.get(0).findChildElement("/../fieldset").getBorderColor(),
                            dangerColor, "Цвет поля 'Телефон' должно быть подсвечено красным");
                    softAssert.isTrue(phoneTooltipErrorLabels.get(0).isVisible(),
                            "Подсказка об ошибке рядом с телефоном не отображается");
                    break;
                case PersonalPhoneBtn:
                    softAssert.isEquals(
                            phonePersonalOptionButtons.get(0).findChildElement("//span[contains(@class, 'Button-Text')]")
                                    .getColor(), dangerColor,
                            "Цвет кнопки 'Личный' должен быть подсвечен красным");
                    break;
                case WorkPhoneBtn:
                    softAssert.isEquals(
                            phoneWorkOptionButtons.get(0).findChildElement("//span[contains(@class, 'Button-Text')]")
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

    public CreateCustomerForm shouldBeEnteredDataMatchThis(CustomerWebData customerData) throws Exception {
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
            if (expectedPhoneNumber.startsWith("+7"))
                expectedPhoneNumber = expectedPhoneNumber.substring(2);
            if (expectedPhoneNumber.length() == 10)
                expectedPhoneNumber = String.format("+7 (%s) %s-%s-%s",
                        expectedPhoneNumber.substring(0, 3), expectedPhoneNumber.substring(3, 6),
                        expectedPhoneNumber.substring(6, 8), expectedPhoneNumber.substring(8, 10));
            softAssert.isEquals(phoneFields.get(0).getText(), expectedPhoneNumber,
                    "Ожидалось другое значение в поле телефона");
        }
        if (customerData.isPersonalPhone()) {
            softAssert.isTrue(isButtonActive(phonePersonalOptionButtons.get(0)),
                    "Должен быть указан Личный телефон");
        }
        if (customerData.isWorkPhone()) {
            softAssert.isTrue(isButtonActive(phoneWorkOptionButtons.get(0)),
                    "Должен быть указан Рабочий телефон");
        }
        softAssert.verifyAll();
        return this;
    }

    // Modal window verifications
    public CreateCustomerForm verifyModalWindowRequiredElements() throws Exception {
        modalWindow.returnBtn.waitForVisibility();
        softAssert.isElementVisible(modalWindow.customersFoundWithThisPhoneLbl);
        softAssert.isElementVisible(modalWindow.subHeaderMsgLbl);
        softAssert.isElementVisible(modalWindow.returnBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что в модальном окне отобразились записи о клиентах")
    public CreateCustomerForm shouldCustomerRecordsArePresentInModalWindow() {
        anAssert.isTrue(modalWindow.customerRows.getCount() > 0,
                "В модальном окне должны быть записи о клиентах с именем и телефоном");
        return this;
    }

    public CreateCustomerForm shouldModalWindowInvisible() {
        anAssert.isElementNotVisible(modalWindow);
        return this;
    }

}
