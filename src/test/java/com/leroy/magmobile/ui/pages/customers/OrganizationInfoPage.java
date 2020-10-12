package com.leroy.magmobile.ui.pages.customers;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class OrganizationInfoPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Загаловок 'Личные данные'")
    Element title;

    @AppFindBy(accessibilityId = "BackButton", metaName = "Кнопка вернуться назад")
    Element backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'ИНН')]]/android.widget.TextView",
            metaName = "Название организации")
    Element orgName;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'карта')]]/android.widget.TextView",
            metaName = "Корпоративная карта")
    Element orgCard;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[contains(@text, 'Адрес')]]/android.widget.TextView",
            metaName = "Адрес организации")
    Element orgAddress;


    // Verifications

    @Step("Проверить, что название организации = {value}")
    public OrganizationInfoPage shouldOrgNameIs(String value) {
        anAssert.isEquals(orgName.getText(), value, "Ожидалось другое название организации");
        return this;
    }

    @Step("Проверить, что корпоративная карта организации = {value}")
    public OrganizationInfoPage shouldOrgCardIs(String value) {
        anAssert.isEquals(ParserUtil.strWithOnlyDigits(orgCard.getText()), value,
                "Ожидался другой номер корпоративной карты");
        return this;
    }

    @Step("Проверить, что Адрес организации = {value}")
    public OrganizationInfoPage shouldOrgAddressIs(String value) {
        anAssert.isEquals(orgAddress.getText(), value, "Ожидался другой адрес организации");
        return this;
    }
}
