package com.leroy.magmobile.ui.pages.sales.estimate;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketProductWidget;
import com.leroy.magmobile.ui.pages.search.CustomerData;
import com.leroy.magmobile.ui.pages.search.SearchCustomerPage;
import com.leroy.magmobile.ui.pages.search.SearchCustomerWidget;
import com.leroy.models.EstimateData;
import com.leroy.models.ProductCardData;
import com.leroy.utils.Converter;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EstimatePage extends CommonMagMobilePage {

    public EstimatePage(TestContext context) {
        super(context);
    }

    public static class PageState {
        boolean customerIsSelected;
        boolean productIsAdded;

        public boolean isCustomerIsSelected() {
            return customerIsSelected;
        }

        public PageState setCustomerIsSelected(boolean customerIsSelected) {
            this.customerIsSelected = customerIsSelected;
            return this;
        }

        public boolean isProductIsAdded() {
            return productIsAdded;
        }

        public PageState setProductIsAdded(boolean productIsAdded) {
            this.productIsAdded = productIsAdded;
            return this;
        }
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

    @AppFindBy(xpath = "//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]",
            metaName = "Карточка товара")
    BasketProductWidget productCardWidget;

    @AppFindBy(text = "ТОВАРЫ И УСЛУГИ", metaName = "Кнопка 'Товары и Услуги'")
    MagMobWhiteSubmitButton productAndServiceBtn;

    // Кнопка видна, когда Смета уже создана
    @AppFindBy(text = "ДЕЙСТВИЯ СО СМЕТОЙ")
    private MagMobGreenSubmitButton actionsWithEstimateBtn;

    // Bottom Area (It is visible when document is created)
    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/preceding-sibling::android.widget.TextView[2]",
            metaName = "Текст с кол-вом товара")
    Element countProductLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/preceding-sibling::android.widget.TextView[1]",
            metaName = "Текст с весом товара")
    Element weightProductLbl;

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

    // ------ Grab info from Page methods -----------//
    @Step("Забираем со страницы информацию о смете")
    public EstimateData getEstimateDataFromPage() throws Exception {
        EstimateData estimateData = new EstimateData();
        List<ProductCardData> productCardDataList = new ArrayList<>();
        productCardDataList.add(getProductCardDataFromPage(1));
        estimateData.setProductCardDataList(productCardDataList);

        String ps = getPageSource();
        estimateData.setTotalPrice(Converter.strToDouble(totalPriceVal.getText(ps)));
        estimateData.setCountOfProducts(Converter.strToInt(countProductLbl.getText(ps)));
        estimateData.setWeight(Converter.strToDouble(weightProductLbl.getText(ps)));
        return estimateData;
    }

    @Step("Забираем со страницу информацию о {index}-ом товаре")
    public ProductCardData getProductCardDataFromPage(int index) {
        index--;
        ProductCardData cardData = new ProductCardData();
        String ps = getPageSource();
        cardData.setLmCode(productCardWidget.getLmCode(true, ps));
        cardData.setName(productCardWidget.getName(ps));
        cardData.setSelectedQuantity(productCardWidget.getProductCount(true, ps));
        cardData.setPrice(productCardWidget.getPrice(true, ps));
        cardData.setAvailableQuantity(productCardWidget.getAvailableTodayProductCountLbl(true, ps));
        return cardData;
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

    @Step("Нажмите кнопку 'Действия со сметой'")
    public ActionsWithEstimateModalPage clickActionsWithEstimateButton() {
        actionsWithEstimateBtn.click();
        return new ActionsWithEstimateModalPage(context);
    }

    // VERIFICATIONS

    @Step("Проверить, что страница 'Смета' отображается корректно")
    public EstimatePage verifyRequiredElements(PageState state) {
        List<Element> expectedElements = new ArrayList<>(Arrays.asList(
                backBtn, headerLbl));
        if (!state.isCustomerIsSelected())
            expectedElements.add(selectCustomerBtn);
        if (state.isProductIsAdded()) {
            expectedElements.add(countProductLbl);
            expectedElements.add(weightProductLbl);
            expectedElements.add(totalPriceLbl);
            expectedElements.add(totalPriceVal);
            expectedElements.add(addProductBtn);
            expectedElements.add(createBtn);
            expectedElements.add(productCardWidget);
        } else {
            expectedElements.add(productAndServiceBtn);
        }
        softAssert.areElementsVisible(expectedElements.toArray(new Element[0]));
        if (!state.isProductIsAdded()) {
            if (!state.isCustomerIsSelected())
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