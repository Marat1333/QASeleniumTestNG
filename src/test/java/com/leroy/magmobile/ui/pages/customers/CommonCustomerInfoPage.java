package com.leroy.magmobile.ui.pages.customers;

import com.leroy.constants.Gender;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.elements.MagMobRadioButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.customers.data.PhoneUiData;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public abstract class CommonCustomerInfoPage<T extends CommonCustomerInfoPage<T>> extends CommonMagMobilePage {

    private Class<T> tClass;

    protected T newThisPage() throws Exception {
        return tClass.getConstructor().newInstance();
    }

    public CommonCustomerInfoPage(Class<T> tClass) {
        super();
        this.tClass = tClass;
    }

    @Override
    public void waitForPageIsLoaded() {
        firstNameFld.waitForVisibility();
    }

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    @AppFindBy(xpath = "//*[@content-desc='ScreenHeader']/android.widget.TextView",
            metaName = "Загаловок")
    Element headerLbl;

    @AppFindBy(accessibilityId = "Button", metaName = "Кнопка назад")
    Element backBtn;

    // Кнопка галочка рядом с полем.
    protected String TICK_XPATH = "//android.view.ViewGroup[descendant::android.widget.EditText[@content-desc='%s']]/following-sibling::android.view.ViewGroup";

    private final static String FIRST_NAME_ID = "firstName";
    @AppFindBy(accessibilityId = FIRST_NAME_ID, metaName = "Поле 'Имя'")
    EditBox firstNameFld;

    @AppFindBy(accessibilityId = "middleName", metaName = "Поле 'Отчество'")
    EditBox middleNameFld;

    @AppFindBy(accessibilityId = "lastName", metaName = "Поле 'Фамилия'")
    EditBox lastNameFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='Button'][android.widget.TextView[@text='Мужской']]")
    GenderRadioButton maleOptionBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='Button'][android.widget.TextView[@text='Женский']]")
    GenderRadioButton femaleOptionBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'Телефон')]]//android.widget.EditText")
    ElementList<Element> phoneFields;

    @AppFindBy(text = "Показать все поля")
    MagMobButton showAllFieldsBtn;

    @AppFindBy(text = "Скрыть дополнительные поля")
    MagMobButton hideAdditionalFieldsBtn;

    // Action

    @Step("Выбрать пол клиента")
    public T selectGender(Gender gender, boolean isVerify) throws Exception {
        switch (gender) {
            case MALE:
                maleOptionBtn.click();
                break;
            case FEMALE:
                femaleOptionBtn.click();
                break;
            default:
                throw new IllegalArgumentException("Выбран неверный пол");
        }
        if (isVerify)
            shouldGenderIsSelected(gender);
        return (T) this;
    }

    @Step("Заполнить / Изменить поле с именем клиента")
    public T editCustomerFirstName(String firstName, boolean isVerify) throws Exception {
        firstNameFld.clearAndFill(firstName);
        E(By.xpath(String.format(TICK_XPATH, FIRST_NAME_ID))).click();
        if (isVerify)
            return shouldFirstNameFieldIs(firstName);
        return (T) this;
    }

    @Step("Заполнить / Изменить поле с фамилией клиента")
    public T editCustomerLastName(String lastName) throws Exception {
        lastNameFld.clearFillAndSubmit(lastName);
        return (T) this;
    }

    @Step("Заполнить / Изменить поле с номером телефона")
    public T editPhoneNumber(int index, PhoneUiData phoneData, boolean isVerify) throws Exception {
        index--;
        Element phoneFld = phoneFields.get(index);
        phoneFld.click();
        new EditPhoneModalPage()
                .verifyRequiredElements()
                .editPhoneNumber(phoneData);
        T page = newThisPage();
        if (isVerify)
            page.shouldPhoneFieldIs(index + 1, phoneData.getPhoneNumber());
        return page;
    }

    @Step("Нажать на 'Показать все поля")
    public T clickShowAllFieldsButton() {
        showAllFieldsBtn.click();
        anAssert.isElementVisible(middleNameFld, short_timeout);
        return (T) this;
    }

    @Step("Нажать на 'Скрыть все поля")
    public T clickHideAdditionalFieldsButton() throws Exception {
        mainScrollView.scrollDownToElement(hideAdditionalFieldsBtn);
        hideAdditionalFieldsBtn.click();
        middleNameFld.waitForInvisibility();
        anAssert.isElementNotVisible(middleNameFld);
        return (T) this;
    }

    // Verifications
    @Step("Проверить, что страница 'Создание / Редактирования клиента' отображается корректно")
    public T verifyRequiredElements(Boolean withAdditionalFields) {
        String ps = getPageSource();
        softAssert.areElementsVisible(ps, headerLbl, backBtn, firstNameFld,
                maleOptionBtn, femaleOptionBtn);
        if (!withAdditionalFields) {
            softAssert.areElementsNotVisible(ps, middleNameFld, lastNameFld, hideAdditionalFieldsBtn);
            softAssert.isElementVisible(showAllFieldsBtn, ps);
        }
        if (withAdditionalFields)
            softAssert.areElementsVisible(ps, middleNameFld, lastNameFld);
        softAssert.verifyAll();
        return (T) this;
    }

    @Step("Поле с номером телефона должно содержать значение {value}")
    public T shouldPhoneFieldIs(int index, String value) throws Exception {
        index--;
        Element phoneFld = phoneFields.get(index);
        anAssert.isEquals(ParserUtil.standardPhoneFmt(phoneFld.getText()), value,
                "Ожидался другой номер телефона");
        return (T) this;
    }

    @Step("Поле с именем клиента должно содержать значение {value}")
    public T shouldFirstNameFieldIs(String value) {
        anAssert.isElementTextEqual(firstNameFld, value);
        return (T) this;
    }

    @Step("Должен быть выбран пол = {value}")
    public T shouldGenderIsSelected(Gender value) throws Exception {
        if (value.equals(Gender.MALE))
            anAssert.isTrue(maleOptionBtn.isChecked(), "Должен быть выбран мужской пол");
        else if (value.equals(Gender.FEMALE))
            anAssert.isTrue(femaleOptionBtn.isChecked(), "Должен быть выбран женский пол");
        else throw new IllegalArgumentException("Illegal argument  " + value + " for method shouldGenderIsSelected()");
        return (T) this;
    }

    // Specific custom page elements

    private static class GenderRadioButton extends MagMobRadioButton {

        public GenderRadioButton(WebDriver driver, CustomLocator locator) {
            super(driver, locator);
        }

        @Override
        public Color getPointColor() throws Exception {
            return getPointColor(0, 5 - (getHeight() / 2));
        }
    }

}
