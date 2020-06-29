package com.leroy.magmobile.ui.pages.sales.orders.cart;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.modal.ConfirmRemovingProductModal;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class DiscountPage extends CommonMagMobilePage {

    @AppFindBy(containsText = "скидки", metaName = "Заголовок")
    Element headerLbl;

    @AppFindBy(xpath = "//*[@content-desc='Button']", metaName = "Иконка корзины")
    Element trashBtn;

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
        headerLbl.waitForVisibility(short_timeout);
        discountAmountFld.waitForVisibility(short_timeout);
    }

    // ACTIONS

    @Step("Нажать на поле 'Причина скидки'")
    public DiscountReasonModal clickDiscountReasonFld() {
        discountReasonFld.click();
        return new DiscountReasonModal();
    }

    @Step("Ввести значение {value} в поле 'Скидка'")
    public DiscountPage enterDiscountPercent(double value) {
        discountPercentFld.clearFillAndSubmit(String.valueOf(value));
        return this;
    }

    @Step("Ввести значение {value} в поле 'Разовая скидка'")
    public DiscountPage enterDiscountAmount(double value) {
        discountAmountFld.clearFillAndSubmit(String.valueOf(value));
        return this;
    }

    @Step("Нажать кнопку ПРИМЕНИТЬ")
    public Cart35Page clickConfirmButton() {
        confirmBtn.click();
        return new Cart35Page();
    }

    @Step("Нажать кнопку удаления скидки")
    public Cart35Page clickRemoveDiscount() {
        trashBtn.click();
        new ConfirmRemovingProductModal().clickConfirmButton();
        return new Cart35Page();
    }

    // VERIFICATIONS

    @Step("Проверить, что страница 'Создание скидки' отображается корректно")
    public DiscountPage verifyRequiredElements() {
        softAssert.areElementsVisible(headerLbl, discountReasonFld,
                discountPercentFld, discountAmountFld, confirmBtn);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что причина скидки: {value}")
    public DiscountPage shouldDiscountReasonIs(double value) {
        anAssert.isEquals(discountReasonFld.getText(),
                value, "Неверная причина скидки");
        return this;
    }

    @Step("Проверить, что сумма товара до добавления/изменения скидки равна: {value}")
    public DiscountPage shouldProductTotalPriceBeforeIs(double value) {
        anAssert.isEquals(ParserUtil.strToDouble(totalPrice.getText()),
                value, "Неверная суммма товара");
        return this;
    }

    @Step("Проверить, что причина скидки выбрана: {value}")
    public DiscountPage shouldDiscountReasonIs(String value) {
        anAssert.isElementTextEqual(discountReasonFld, value);
        return this;
    }

    @Step("Проверить, что 'Скидка (%)' = {value}")
    public DiscountPage shouldDiscountPercentIs(double value) {
        anAssert.isEquals(ParserUtil.strToDouble(discountPercentFld.getText()),
                value, "Неверная Скидка % в соответствующем поле");
        anAssert.isEquals(ParserUtil.strToDouble(totalDiscountPercentLbl.getText()),
                value, "Неверная Скидка % на нижней панели");
        return this;
    }

    @Step("Проверить, что разовая скидка = {value}")
    public DiscountPage shouldDiscountAmountIs(double value) {
        double actualDiscountAmount = ParserUtil.strToDouble(discountAmountFld.getText());
        anAssert.isTrue(Math.abs(value - actualDiscountAmount) < 0.011,
                "Неверная разовая скидка. Actual: " + actualDiscountAmount + " \n Expected:" + value);
        return this;
    }

    @Step("Проверить, что Новая цена = {value}")
    public DiscountPage shouldDiscountNewPriceIs(double value) {
        double actualDiscountNewPrice = ParserUtil.strToDouble(discountNewPriceFld.getText());
        anAssert.isTrue(Math.abs(value - actualDiscountNewPrice) <= 0.02,
                "Неверная новая цена. Actual: " + actualDiscountNewPrice + " \n Expected:" + value);
        return this;
    }

    @Step("Проверить, что скидка расчитана правильно на основе введенных данных и стоимости товара")
    public DiscountPage shouldDiscountCalculatedCorrectly(Double totalPriceWithoutDiscount) {
        double newTotalPriceVal = ParserUtil.strToDouble(totalDiscountAmountLbl.getText());
        shouldDiscountPercentIs(ParserUtil.minus(
                100, newTotalPriceVal / totalPriceWithoutDiscount * 100, 2));
        shouldDiscountAmountIs(ParserUtil.minus(totalPriceWithoutDiscount, newTotalPriceVal, 2));
        shouldDiscountNewPriceIs(newTotalPriceVal);
        return this;
    }

}
