package com.leroy.magportal.ui.pages.cart_estimate.modal;

import com.leroy.constants.sales.DiscountConst;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.webelements.commonelements.PuzComboBox;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class DiscountModal extends MagPortalBasePage {

    private final static String MODAL_DIV_XPATH =
            "//div[contains(@class, 'Common-ConfirmModal__modal__container')]";

    @Override
    protected void waitForPageIsLoaded() {
        anAssert.isTrue(waitForAnyOneOfElementsIsVisible(discountReasonCmbBox, percentageDiscountFld,
                discountAmountFld, discountNewPriceFld, cancelBtn, confirmBtn),
                "Модальное окно создания скидки не загрузилось");
    }

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Estimate-price')]")
    Element totalPriceWithoutDiscount;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Select__container-overflowMode-ellipsis')]",
            metaName = "Выпадающий список 'Причина скидки'")
    PuzComboBox discountReasonCmbBox;

    @WebFindBy(id = "percentageInput", metaName = "Поле % скидка")
    EditBox percentageDiscountFld;

    @WebFindBy(id = "discountAmountInput", metaName = "Поле разовая скидка")
    EditBox discountAmountFld;

    @WebFindBy(id = "priceAfterDiscountInput", metaName = "Поле новая цена")
    EditBox discountNewPriceFld;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//button[descendant::*[text()='Удалить скидку']]", metaName = "Кнопка Удалить скидку")
    Button removeDiscountBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//button[descendant::*[text()='Отмена']]", metaName = "Кнопка Отмена")
    Button cancelBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//button[contains(@class, 'Common-ConfirmModal__modal__okButton')]",
            metaName = "Кнопка Применить (Сохранить)")
    Button confirmBtn;

    // Actions

    @Step("Выбрать причину скидки {value}")
    public DiscountModal selectReasonDiscount(String value) throws Exception {
        discountReasonCmbBox.selectOption(value);
        return this;
    }

    @Step("Ввести значение {value} в поле 'Скидка'")
    public DiscountModal enterDiscountPercent(double value) {
        percentageDiscountFld.clearFillAndSubmit(String.valueOf(value));
        return this;
    }

    @Step("Ввести значение {value} в поле 'Разовая скидка'")
    public DiscountModal enterDiscountAmount(double value) {
        discountAmountFld.clearFillAndSubmit(String.valueOf(value));
        return this;
    }

    @Step("Нажать кнопку ПРИМЕНИТЬ")
    public CartPage clickConfirmButton() {
        confirmBtn.click();
        return new CartPage();
    }

    @Step("Нажать кнопку 'Удалить скидку'")
    public CartPage clickRemoveDiscount() {
        removeDiscountBtn.click();
        removeDiscountBtn.waitForInvisibility();
        return new CartPage();
    }

    // Verifications

    @Step("Проверить, что доступны все необходимые опции в выпадающем списке 'Причина скидки'")
    public DiscountModal verifyAvailableDiscountReasonOptions() throws Exception {
        List<String> expectedOptions = Arrays.asList(DiscountConst.Reasons.AFTER_REPAIR.getName(),
                DiscountConst.Reasons.PRODUCT_SAMPLE.getName(), DiscountConst.Reasons.BALANCE_MEASURED_PRODUCT.getName(),
                DiscountConst.Reasons.INCOMPLETE_KIT.getName(), DiscountConst.Reasons.DEFECT.getName(),
                DiscountConst.Reasons.B2B_PRICE_ADJUSTMENT.getName());
        anAssert.isEquals(new HashSet<>(discountReasonCmbBox.getOptionList()), new HashSet<>(expectedOptions),
                "Ожидались другие причины скидки");
        return this;
    }

    @Step("Проверить, что выбрана {value} причина скидки")
    public DiscountModal shouldDiscountReasonIs(String value) {
        anAssert.isEquals(discountReasonCmbBox.getSelectedOptionText(), value,
                "Ожидалось, что выбрана другая причина скидки");
        return this;
    }

    @Step("Проверить, что сумма товара до добавления/изменения скидки равна: {value}")
    public DiscountModal shouldProductTotalPriceWithoutDiscount(double value) {
        anAssert.isEquals(ParserUtil.strToDouble(totalPriceWithoutDiscount.getText()),
                value, "Неверная суммма товара (без скидки)");
        return this;
    }

    @Step("Проверить, что 'Скидка (%)' = {value}")
    public DiscountModal shouldDiscountPercentIs(double value) {
        anAssert.isEquals(ParserUtil.strToDouble(percentageDiscountFld.getText()),
                value, "Неверная Скидка % в соответствующем поле");
        return this;
    }

    @Step("Проверить, что разовая скидка = {value}")
    public DiscountModal shouldDiscountAmountIs(double value) {
        double actualDiscountAmount = ParserUtil.strToDouble(discountAmountFld.getText());
        anAssert.isTrue(Math.abs(value - actualDiscountAmount) < 0.011,
                "Неверная разовая скидка. Actual: " + actualDiscountAmount + " \n Expected:" + value);
        return this;
    }

    @Step("Проверить, что Новая цена = {value}")
    public DiscountModal shouldDiscountNewPriceIs(double value) {
        double actualDiscountNewPrice = ParserUtil.strToDouble(discountNewPriceFld.getText());
        anAssert.isTrue(Math.abs(value - actualDiscountNewPrice) <= 0.02,
                "Неверная новая цена. Actual: " + actualDiscountNewPrice + " \n Expected:" + value);
        return this;
    }

    @Step("Проверить, что скидка расчитана правильно на основе введенных данных и стоимости товара")
    public DiscountModal shouldDiscountCalculatedCorrectly(Double totalPriceWithoutDiscount) {
        double newTotalPriceVal = ParserUtil.strToDouble(discountNewPriceFld.getText());
        shouldDiscountPercentIs(ParserUtil.minus(
                100, newTotalPriceVal / totalPriceWithoutDiscount * 100, 2));
        shouldDiscountAmountIs(ParserUtil.minus(totalPriceWithoutDiscount, newTotalPriceVal, 2));
        shouldDiscountNewPriceIs(newTotalPriceVal);
        return this;
    }

}
