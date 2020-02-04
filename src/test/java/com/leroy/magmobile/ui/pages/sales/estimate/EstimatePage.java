package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
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
        E("$EstimateDocumentScreenId", "EstimateDocumentScreen").waitForVisibility();
    }

    @AppFindBy(accessibilityId = "BackCloseMaster", metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(text = "Смета", metaName = "Заголовок экрана")
    Element headerLbl;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='EstimateDocumentScreenId']//android.widget.TextView[contains(@text, 'Документ №')]",
            metaName = "Номер документа")
    Element documentNumber;

    @AppFindBy(text = "Клиент", metaName = "Поле 'Клиент' (добавить)")
    Element selectCustomerBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup[android.widget.TextView[@index='2']]",
            metaName = "Поле 'Клиент' (клиент выбран)")
    SearchCustomerWidget customerWidget;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.ImageView]",
            metaName = "Карточка товара")
    Element productCardWidget;

    @AppFindBy(text = "ТОВАРЫ И УСЛУГИ", metaName = "Кнопка 'Товары и Услуги'")
    MagMobWhiteSubmitButton productAndServiceBtn;

    // Bottom Area (It is visible when document is created)
    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/preceding-sibling::android.widget.TextView",
            metaName = "Текст с количеством и весом товара")
    Element countAndWeightProductLbl;

    @AppFindBy(text = "Итого: ")
    Element totalPriceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/following-sibling::android.widget.TextView")
    Element totalPriceVal;

    @AppFindBy(text = "ТОВАР")
    private MagMobWhiteSubmitButton addProductBtn;

    @AppFindBy(text = "СОЗДАТЬ")
    private MagMobGreenSubmitButton createBtn;

    /**
     * Получить Итоговую стоимость
     */
    public String getTotalPrice() {
        String _priceValue = totalPriceVal.getText().replaceAll("₽", "").trim();
        try {
            Double.parseDouble(_priceValue);
            return _priceValue;
        } catch (NumberFormatException err) {
            anAssert.isTrue(false, "Итого цена имеет не правильный формат: " + _priceValue);
            throw err;
        }
    }

    /**
     * Получить номер документа
     * @param onlyDigits true - получить только номер из цифр. false - получить всю строку как есть
     * @return document number
     */
    public String getDocumentNumber(boolean onlyDigits) {
        if (onlyDigits)
            return documentNumber.getText().replaceAll("\\D+", "");
        else
            return documentNumber.getText();
    }

    // ACTIONS

    @Step("Нажмите на поле 'Клиенты'")
    public SearchCustomerPage clickCustomerField() {
        selectCustomerBtn.click();
        return new SearchCustomerPage(context);
    }

    @Step("Нажмите кнопку 'Товары и Услуги'")
    public SearchProductPage clickProductAndServiceButton() {
        productAndServiceBtn.click();
        return new SearchProductPage(context);
    }

    @Step("Нажмите кнопку 'Создать'")
    public EstimateSubmittedPage clickCreateButton() {
        createBtn.click();
        return new EstimateSubmittedPage(context);
    }

    // VERIFICATIONS

    @Step("Проверить, что страница 'Смета' отображается корректно")
    public EstimatePage verifyRequiredElements(boolean customerIsSelected, boolean productIsAdded) {
        List<Element> expectedElements = new ArrayList<>(Arrays.asList(
                backBtn, headerLbl));
        if (!customerIsSelected)
            expectedElements.add(selectCustomerBtn);
        if (productIsAdded) {
            expectedElements.add(countAndWeightProductLbl);
            expectedElements.add(totalPriceLbl);
            expectedElements.add(totalPriceVal);
            expectedElements.add(addProductBtn);
            expectedElements.add(createBtn);
            expectedElements.add(productCardWidget);
        } else {
            expectedElements.add(productAndServiceBtn);
        }
        softAssert.areElementsVisible(expectedElements.toArray(new Element[0]));
        if (!productIsAdded) {
            if (!customerIsSelected)
                softAssert.isFalse(productAndServiceBtn.isEnabled(),
                        "Кнопка '+ Товары и Услуги' активна");
            else
                softAssert.isTrue(productAndServiceBtn.isEnabled(),
                        "Кнопка '+ Товары и Услуги' неактивна");
        }
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
