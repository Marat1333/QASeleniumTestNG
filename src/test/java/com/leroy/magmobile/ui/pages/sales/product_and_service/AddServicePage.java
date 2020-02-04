package com.leroy.magmobile.ui.pages.sales.product_and_service;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep1Page;
import io.qameta.allure.Step;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class AddServicePage extends CommonMagMobilePage {

    public static class Constants {
        private static final String SCREEN_TITLE = "Добавление услуги";
        public static final String DEFAULT_QUANTITY_VALUE = "1,00";
        public static final String EMPTY_TOTAL_PRICE_VALUE = "— ₽";
    }

    public AddServicePage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Загаловок страницы")
    Element screenTitle;

    @AppFindBy(accessibilityId = "BackCloseModal", metaName = "Кнопка 'Назад'")
    Element backBtn;

    @AppFindBy(accessibilityId = "price", metaName = "Поле для редактирования цены за единицу услуги")
    EditBox editPriceServiceFld;

    @AppFindBy(accessibilityId = "quantity", metaName = "Поле для редактирования кол-ва для продажи")
    EditBox editQuantityServiceFld;

    private static final String totalText = "Итого: ";

    @AppFindBy(xpath = "//android.widget.TextView[@text='" + totalText + "']", metaName = "'Итого:' метка")
    Element totalPriceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='" + totalText + "']/following-sibling::android.widget.TextView",
            metaName = "Сумма Итого")
    Element totalPriceVal;

    @AppFindBy(text = "ДОБАВИТЬ В ДОКУМЕНТ ПРОДАЖИ", metaName = "Кнопка для добавления документа в продажи")
    MagMobGreenSubmitButton submitBtn;

    // Actions
    @Step("Ввести значение цены {value} в поле 'Цена за единицу услуги'")
    public AddServicePage enterValueInPriceServiceField(String value) {
        editPriceServiceFld.clearFillAndSubmit(value);
        return this;
    }

    @Step("Ввести значение кол-ва {value} в поле 'Количество для продажи'")
    public AddServicePage enterValueInQuantityServiceField(String value) {
        editQuantityServiceFld.clearFillAndSubmit(value);
        return this;
    }

    @Step("Нажмите на кнопку Добавить в документ продажи")
    public BasketStep1Page clickAddIntoDocumentSalesButton() {
        submitBtn.click();
        return new BasketStep1Page(context);
    }

    // Verifications

    @Step("Проверить, что страница 'Добавления услуги' отображается корректно")
    public AddServicePage verifyRequiredElements() {
        softAssert.isElementTextEqual(screenTitle, Constants.SCREEN_TITLE);
        softAssert.areElementsVisible(
                backBtn, editPriceServiceFld, editPriceServiceFld);
        softAssert.isFalse(submitBtn.isEnabled(), "Кнопка 'Добавить в документ продажи' активна");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что поле цена = {price}, кол-во для продажи = {quantity}, Итого = {total}")
    public AddServicePage shouldFieldsAre(String price, String quantity, String total) {
        softAssert.isElementTextEqual(editPriceServiceFld, price);
        softAssert.isElementTextEqual(editQuantityServiceFld, quantity);
        try {
            total = (new DecimalFormat("###,###.###", new DecimalFormatSymbols(Locale.FRANCE))
                    .format(Integer.parseInt(total)) + " ₽")
                    .replaceAll(" ", " "); // different space symbols
        } catch (NumberFormatException err) {
            // nothing to do, because expected the 'total' already is the same as actual result
        }
        softAssert.isElementTextEqual(totalPriceVal, total);
        softAssert.verifyAll();
        return this;
    }
}
