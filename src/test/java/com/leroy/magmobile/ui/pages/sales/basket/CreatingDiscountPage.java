package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class CreatingDiscountPage extends CommonMagMobilePage {

    @AppFindBy(text = "Создание скидки", metaName = "Заголовок")
    Element headerLbl;

    // Карточка товара
    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='lmCode']", metaName = "ЛМ код товара")
    Element lmCode;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='barCode']", metaName = "Бар код товара")
    Element barCode;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='barCode']/following-sibling::android.widget.TextView[1]",
            metaName = "Название товара")
    Element title;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='barCode']/following-sibling::android.widget.TextView[2]",
            metaName = "Стоимость товара")
    Element totalPrice;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='presenceValue']",
            metaName = "Выбранное кол-во товара")
    Element selectedQuantity;

    // Основная область (параметры скидки)

    @AppFindBy(accessibilityId = "discountReason", metaName = "Поле 'Причина скидки'")
    EditBox discountReasonFld;

    @AppFindBy(accessibilityId = "discountPercent", metaName = "Поле 'Скидка (%)'")
    EditBox discountPercentFld;

    @AppFindBy(accessibilityId = "discountAmount", metaName = "Поле 'Разовая скидка'")
    EditBox discountAmountFld;

    @AppFindBy(accessibilityId = "discountNewPrice", metaName = "Поле 'Новая цена'")
    EditBox discountNewPriceFld;


    // Нижняя область

    @AppFindBy(containsText = "Итого со скидкой")
    Element totalDiscountPercentLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого со скидкой')]/following-sibling::android.widget.TextView",
            metaName = "Итоговая стоимость с учетом скидки")
    Element totalDiscountAmountLbl;

    @AppFindBy(text = "ПРИМЕНИТЬ", metaName = "Стоимость товара")
    MagMobButton confirmBtn;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitForVisibility();
        discountAmountFld.waitForVisibility();
    }

    // ACTIONS

    @Step("Нажать на поле 'Причина скидки'")
    public DiscountReasonModal clickDiscountReasonFld() {
        discountReasonFld.click();
        return new DiscountReasonModal();
    }

    @Step("Ввести значение {value} в поле 'Скидка'")
    public CreatingDiscountPage enterDiscountPercent(double value) {
        discountPercentFld.clearFillAndSubmit(String.valueOf(value));
        return this;
    }

    @Step("Ввести значение {value} в поле 'Разовая скидка'")
    public CreatingDiscountPage enterDiscountAmount(double value) {
        discountAmountFld.clearFillAndSubmit(String.valueOf(value));
        return this;
    }

    @Step("Нажать кнопку ПРИМЕНИТЬ")
    public Basket35Page clickConfirmButton() {
        confirmBtn.click();
        return new Basket35Page();
    }

    // VERIFICATIONS

    @Step("Проверить, что страница 'Создание скидки' отображается корректно")
    public CreatingDiscountPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, discountReasonFld,
                discountPercentFld, discountAmountFld, confirmBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что причина скидки: {value}")
    public CreatingDiscountPage shouldDiscountReasonIs(double value) {
        anAssert.isEquals(discountReasonFld.getText(),
                value, "Неверная причина скидки");
        return this;
    }

    @Step("Проверить, что первоначальная сумма товара равна: {value}")
    public CreatingDiscountPage shouldProductTotalPriceIs(double value) {
        anAssert.isEquals(ParserUtil.strToDouble(totalPrice.getText()),
                value, "Неверная суммма товара");
        return this;
    }

    @Step("Проверить, что причина скидки выбрана: {value}")
    public CreatingDiscountPage shouldDiscountReasonIs(String value) {
        anAssert.isElementTextEqual(discountReasonFld, value);
        return this;
    }

    @Step("Проверить, что 'Скидка (%)' = {value}")
    public CreatingDiscountPage shouldDiscountPercentIs(double value) {
        anAssert.isEquals(ParserUtil.strToDouble(discountPercentFld.getText()),
                value, "Неверная Скидка % в соответствующем поле");
        anAssert.isEquals(ParserUtil.strToDouble(totalDiscountPercentLbl.getText()),
                value, "Неверная Скидка % на нижней панели");
        return this;
    }

    @Step("Проверить, что разовая скидка = {value}")
    public CreatingDiscountPage shouldDiscountAmountIs(double value) {
        anAssert.isEquals(ParserUtil.strToDouble(discountAmountFld.getText()),
                value, "Неверная разовая скидка");
        return this;
    }

    @Step("Проверить, что Новая цена = {value}")
    public CreatingDiscountPage shouldDiscountNewPriceIs(double value) {
        anAssert.isEquals(ParserUtil.strToDouble(discountNewPriceFld.getText()),
                value, "Неверная новая цена");
        return this;
    }

    @Step("Проверить, что скидка расчитана правильно на основе введенных данных и стоимости товара")
    public CreatingDiscountPage shouldDiscountCalculatedCorrectly() {
        double totalPriceVal = ParserUtil.strToDouble(totalPrice.getText());
        double newTotalPriceVal = ParserUtil.strToDouble(totalDiscountAmountLbl.getText());
        shouldDiscountPercentIs(ParserUtil.minus(
                100, newTotalPriceVal / totalPriceVal * 100, 2));
        shouldDiscountAmountIs(ParserUtil.minus(totalPriceVal, newTotalPriceVal, 2));
        shouldDiscountNewPriceIs(newTotalPriceVal);
        return this;
    }

}
