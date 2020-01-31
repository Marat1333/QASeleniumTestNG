package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.search.CustomerData;
import com.leroy.magmobile.ui.pages.search.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.search.SearchCustomerWidget;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EstimatePage extends CommonMagMobilePage {

    public EstimatePage(TestContext context) {
        super(context);
    }

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
    }

    @AppFindBy(accessibilityId = "BackCloseMaster", metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(text = "Смета", metaName = "Заголовок экрана")
    Element headerLbl;

    @AppFindBy(text = "Клиент", metaName = "Поле 'Клиент' (добавить)")
    Element selectCustomerBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup[android.widget.TextView[@index='2']]",
            metaName = "Поле 'Клиент' (клиент выбран)")
    SearchCustomerWidget customerWidget;

    @AppFindBy(text = "ТОВАРЫ И УСЛУГИ", metaName = "Кнопка 'Товары и Услуги'")
    MagMobSubmitButton submitBtn;

    // ACTIONS

    @Step("Нажмите на поле 'Клиенты'")
    public SearchCustomerPage clickCustomerField() {
        selectCustomerBtn.click();
        return new SearchCustomerPage(context);
    }

    @Step("Нажмите кнопку 'Товары и Услуги'")
    public EstimatePage clickSubmitButton() {
        submitBtn.click();
        return this;
    }

    // VERIFICATIONS

    @Step("Проверить, что страница 'Смета' отображается корректно")
    public EstimatePage verifyRequiredElements(boolean customerIsSelected) {
        List<Element> expectedElements = new ArrayList<>(Arrays.asList(
                backBtn, headerLbl, submitBtn));
        if (!customerIsSelected)
            expectedElements.add(selectCustomerBtn);
        softAssert.areElementsVisible(expectedElements.toArray(new Element[0]));
        if (!customerIsSelected)
            softAssert.isFalse(submitBtn.isEnabled(), "Кнопка '+ Товары и Услуги' активна");
        else
            softAssert.isTrue(submitBtn.isEnabled(), "Кнопка '+ Товары и Услуги' неактивна");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что выбранный клиент содержит следующие данные: {expectedCustomerData}")
    public EstimatePage shouldSelectedCustomerIs(CustomerData expectedCustomerData) {
        String ps = getPageSource();
        CustomerData actualCustomerData = customerWidget.collectDataFromPage(ps);
        softAssert.isEquals(actualCustomerData.getName(), expectedCustomerData.getName(),
                "Имя выбранного клиента неверно");
        if (expectedCustomerData.getCardNumber() != null)
            softAssert.isEquals(actualCustomerData.getCardNumber(), expectedCustomerData.getCardNumber(),
                    "Номер карты выбранного клиента неверен");
        if (expectedCustomerData.getCardType() != null)
            softAssert.isEquals(actualCustomerData.getCardType(), expectedCustomerData.getCardType(),
                    "Тип карты выбранного клиента неверен");
        if (expectedCustomerData.getPhone() != null)
            softAssert.isEquals(actualCustomerData.getPhone(), expectedCustomerData.getPhone(),
                    "Телефон выбранного клиента неверен");
        if (expectedCustomerData.getEmail() != null)
            softAssert.isEquals(actualCustomerData.getEmail(), expectedCustomerData.getEmail(),
                    "Email выбранного клиента неверен");
        softAssert.verifyAll();
        return this;
    }

}
