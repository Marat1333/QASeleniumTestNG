package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.pages.customers.data.PhoneUiData;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class PersonalInfoPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Загаловок 'Личные данные'")
    Element title;

    @AppFindBy(accessibilityId = "BackButton", metaName = "Кнопка вернуться назад")
    Element backBtn;

    @AppFindBy(xpath = "//*[@content-desc='ScreenHeader']//*[@content-desc='Button']", metaName = "Кнопка редактирования")
    Element editBtn;

    AndroidScrollView<PhoneUiData> phonesScrollView = new AndroidScrollView<>(
            driver, AndroidScrollView.TYPICAL_LOCATOR,
            "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'Телефон')]]",
            PhoneWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        title.waitForVisibility();
        editBtn.waitForVisibility();
    }

    // Actions

    @Step("Нажать кнопку редактирования (карандаш) клиента")
    public EditCustomerInfoPage clickEditButton() {
        editBtn.click();
        return new EditCustomerInfoPage();
    }


    // Verifications

    @Step("Проверить, что страница 'Личные данные' отображается корректно")
    public PersonalInfoPage verifyRequiredElements() {
        softAssert.areElementsVisible(title, backBtn, editBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что у клиента следующие телефоны: {phoneNumbers}")
    public PersonalInfoPage shouldPhoneNumbersAre(List<String> phoneNumbers) throws Exception {
        List<PhoneUiData> actualData = phonesScrollView.getFullDataList();
        List<String> actualPhones = new ArrayList<>();
        for (PhoneUiData phoneUiData : actualData) {
            actualPhones.add(ParserUtil.standardPhoneFmt(phoneUiData.getPhoneNumber()));
        }
        anAssert.isEquals(actualPhones, phoneNumbers,
                "Ожидались другие номера телефонов");
        return this;
    }

    @Step("Проверить, что у клиента следующие типы телефона: {phoneTypes}")
    public PersonalInfoPage shouldPhoneTypesAre(List<PhoneUiData.Type> phoneTypes) throws Exception {
        List<PhoneUiData> actualData = phonesScrollView.getFullDataList();
        List<PhoneUiData.Type> actualPhoneTypes = new ArrayList<>();
        for (PhoneUiData phoneUiData : actualData) {
            if (phoneUiData.getIsMain() == true)
                actualPhoneTypes.add(PhoneUiData.Type.MAIN);
            else
                actualPhoneTypes.add(phoneUiData.getType());
        }
        anAssert.isEquals(actualPhoneTypes, phoneTypes,
                "Ожидались другие типы телефонов");
        return this;
    }

    // Widgets

    public static class PhoneWidget extends CardWidget<PhoneUiData> {

        @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, '+7')]", metaName = "Номер телефона")
        Element phoneNumber;

        @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, 'Телефон')]", metaName = "Тип телефона")
        Element phoneType;

        public PhoneWidget(WebDriver driver, CustomLocator locator) {
            super(driver, locator);
        }

        @Override
        public PhoneUiData collectDataFromPage(String pageSource) {
            PhoneUiData phoneUiData = new PhoneUiData();
            phoneUiData.setPhoneNumber(phoneNumber.getText(pageSource));
            String phoneTypeText = phoneType.getText(pageSource);
            if (phoneTypeText.toLowerCase().contains("основной"))
                phoneUiData.setIsMain(true);
            else
                phoneUiData.setIsMain(false);
            if (phoneTypeText.toLowerCase().contains("личный"))
                phoneUiData.setType(PhoneUiData.Type.PERSONAL);
            if (phoneTypeText.toLowerCase().contains("рабочий"))
                phoneUiData.setType(PhoneUiData.Type.WORK);
            return phoneUiData;
        }

        @Override
        public boolean isFullyVisible(String pageSource) {
            return phoneNumber.isVisible(pageSource) && phoneType.isVisible(pageSource);
        }
    }

}
