package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.widget.CalculatorWidget;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.EstimatePage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class AddProduct35Page<T> extends CommonMagMobilePage {

    private Class<T> parentPage;

    public AddProduct35Page(Class<T> type) {
        super();
        parentPage = type;
    }

    protected String SCREEN_TITLE_VALUE() {
        return "Добавление товара";
    }

    protected T newParentPage() throws Exception {
        return parentPage.getConstructor().newInstance();
    }

    @AppFindBy(accessibilityId = "ScreenTitle")
    private Element screenTitle;

    @AppFindBy(xpath = "(//android.view.ViewGroup[@content-desc='Button'])[1]", metaName = "Кнопка назад")
    private Element backBtn;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='lmCode']", metaName = "ЛМ код")
    private Element lmCode;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='barCode']", metaName = "бар код")
    private Element barCode;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='barCode']/following::android.widget.TextView")
    private Element title;

    @AppFindBy(text = "Цена")
    private Element priceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Цена']/following-sibling::android.widget.TextView[1]")
    private Element price;

    @AppFindBy(text = "Торговый зал")
    private Element shoppingRoomLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Торговый зал']/following-sibling::android.widget.TextView[@content-desc='presenceValue']")
    private Element shoppingRoomAvailableQuantity;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Торговый зал']/following-sibling::android.widget.TextView[@content-desc='priceUnit']")
    private Element shoppingRoomAvailablePriceUnit;

    @AppFindBy(text = "На складе")
    private Element inStockLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='На складе']/following-sibling::android.widget.TextView[@content-desc='presenceValue']")
    private Element inStockAvailableQuantity;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Поштучно']/following-sibling::android.widget.TextView[@content-desc='presenceValue']")
    private Element byPeaceQuantity;

    @AppFindBy(xpath = "//android.widget.TextView[@text='На моно-палете']/following-sibling::android.widget.TextView[1][not(@content-desc='presenceValue')]",
            metaName = "Количество моно палет")
    private Element monoPalletQuantity;

    @AppFindBy(xpath = "//android.widget.TextView[@text='На моно-палете']/following-sibling::android.widget.TextView[@content-desc='presenceValue']",
            metaName = "Кол-во товаров на одном моно-палете")
    private Element byOneMonoPalletQuantity;

    @AppFindBy(xpath = "//android.widget.TextView[@text='На микс-палете']/following-sibling::android.widget.TextView[@content-desc='presenceValue']",
            metaName = "Общее кол-во товаров на моно-палете")
    private Element byMixPalletQuantity;

    // White Bottom Area

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.widget.EditText",
            metaName = "Поле для редактирования кол-ва")
    private EditBox editQuantityFld;

    @AppFindBy(xpath = "//android.widget.EditText/following-sibling::android.widget.TextView[2]",
            metaName = "Сумма")
    private Element totalPrice;

    @AppFindBy(xpath = "//android.widget.EditText/following-sibling::android.widget.TextView[contains(@text, 'Доступно для продажи')]",
            metaName = "Предупреждающее красное сообщение о доступном кол-ве товара")
    Element availableStockAlertMsgLbl;

    public enum SubmitBtnCaptions {
        ADD_TO_BASKET("ДОБАВИТЬ В КОРЗИНУ"),
        ADD_TO_ESTIMATE("ДОБАВИТЬ В СМЕТУ"),
        ADD_TO_ORDER("ДОБАВИТЬ В ЗАКАЗ"),
        ADD_TO_TASK("ДОБАВИТЬ В ЗАЯВКУ"),
        SAVE("СОХРАНИТЬ");

        String value;

        SubmitBtnCaptions(String val) {
            this.value = val;
        }

        public String value() {
            return value;
        }
    }

    @AppFindBy(accessibilityId = "Button-container")
    MagMobGreenSubmitButton submitBtn;


    @Override
    public void waitForPageIsLoaded() {
        screenTitle.waitUntilTextIsEqualTo(SCREEN_TITLE_VALUE());
        waitUntilProgressBarIsInvisible();
    }

    // ----- Grab Data from Page ----------//

    @Step("Получить Цену продукта за единицу товара")
    public Double getPrice() {
        return ParserUtil.strToDouble(price.getText());
    }

    @Step("Получить значение кол-ва товара в торговом зале")
    public int getAvailableQuantityInShoppingRoom() {
        return ParserUtil.strToInt(shoppingRoomAvailableQuantity.getText());
    }

    @Step("Получить значение кол-ва товара на складе")
    public int getAvailableQuantityInStock() {
        return ParserUtil.strToInt(inStockAvailableQuantity.getText());
    }

    @Step("Получить значение кол-ва одного моно-палета")
    public int getByOneMonoPalletQuantity() {
        return ParserUtil.strToInt(byOneMonoPalletQuantity.getText());
    }

    @Step("Получить информацию со страницы о товаре/услуги/выбранном кол-ве и т.п.")
    public ProductOrderCardAppData getProductOrderDataFromPage() {
        String ps = getPageSource();

        // Карточка товара
        ProductOrderCardAppData productData = new ProductOrderCardAppData();
        productData.setPrice(ParserUtil.strToDouble(price.getTextIfPresent(ps)));
        productData.setTitle(title.getText(ps));
        productData.setLmCode(ParserUtil.strWithOnlyDigits(lmCode.getText(ps)));
        productData.setBarCode(ParserUtil.strWithOnlyDigits(barCode.getText(ps)));
        productData.setPriceUnit(shoppingRoomAvailablePriceUnit.getText(ps));

        // Запасы
        productData.setAvailableTodayQuantity(getAvailableQuantityInShoppingRoom() +
                getAvailableQuantityInStock());
        productData.setTotalStock(ParserUtil.strToInt(inStockAvailableQuantity.getText(ps)));
        productData.setPieceQuantityInStock(ParserUtil.strToInt(byPeaceQuantity.getTextIfPresent(ps)));
        Integer oneMonoPalletQuantity = ParserUtil.strToInt(byOneMonoPalletQuantity.getTextIfPresent(ps));
        if (oneMonoPalletQuantity != null) {
            productData.setMonoPalletQuantityInStock(oneMonoPalletQuantity == 0 ? 0 :
                    oneMonoPalletQuantity * ParserUtil.strToInt(monoPalletQuantity.getTextIfPresent(ps)));
        }
        productData.setMixPalletQuantityInStock(ParserUtil.strToInt(byMixPalletQuantity.getTextIfPresent(ps)));

        // Детали выбора товара (Строка заказа)
        productData.setSelectedQuantity(ParserUtil.strToDouble(editQuantityFld.getText(ps)));
        productData.setTotalPrice(ParserUtil.strToDouble(totalPrice.getTextIfPresent(ps)));
        return productData;
    }

    // ---------------- Action Steps -------------------------//

    @Step("Нажмите на поле количества")
    public AddProduct35Page<T> clickEditQuantityField() {
        editQuantityFld.click();
        return this;
    }

    @Step("Введите {text} количества товара")
    public AddProduct35Page<T> enterQuantityOfProduct(String text, boolean actionVerification) {
        editQuantityFld.clearFillAndSubmit(text);
        if (actionVerification) {
            shouldEditQuantityFieldIs(text);
            shouldTotalPriceCalculateCorrectly();
        }
        return this;
    }

    public AddProduct35Page<T> enterQuantityOfProduct(int value, boolean actionVerification) {
        return enterQuantityOfProduct(String.valueOf(value), actionVerification);
    }

    @Step("Нажмите кнопку для подтверждения изменений (Добавить / Сохранить)")
    public T clickSubmitButton() throws Exception {
        submitBtn.click();
        return newParentPage();
    }

    @Step("Нажмите кнопку Добавить в корзину")
    public Cart35Page clickAddIntoBasketButton() {
        submitBtn.click();
        return new Cart35Page();
    }

    @Step("Нажмите кнопку Добавить в смету")
    public EstimatePage clickAddIntoEstimateButton() {
        submitBtn.click();
        return new EstimatePage();
    }

    // ---------------- Verifications ----------------------- //

    @Step("Проверить, что страница 'Добавление товара' отображается корректно")
    public AddProduct35Page<T> verifyRequiredElements(SubmitBtnCaptions caption, boolean priceShouldBePresent) {
        String ps = getPageSource();
        softAssert.isElementTextEqual(screenTitle, SCREEN_TITLE_VALUE(), ps);
        softAssert.isElementVisible(backBtn, ps);
        if (priceShouldBePresent)
            softAssert.isElementVisible(priceLbl, ps);
        else
            softAssert.isElementNotVisible(priceLbl, ps);
        softAssert.isElementVisible(shoppingRoomLbl, ps);
        softAssert.isElementVisible(editQuantityFld, ps);
        anAssert.isElementTextEqual(submitBtn, caption.value(), ps);
        softAssert.isTrue(submitBtn.isEnabled(),
                "Кнопка 'Добавить' должна быть активна");
        softAssert.verifyAll();
        return this;
    }

    public AddProduct35Page<T> verifyRequiredElements(SubmitBtnCaptions caption) {
        return verifyRequiredElements(caption, true);
    }

    @Step("Убедиться, что поле для редактирования кол-ва = {text}")
    public AddProduct35Page<T> shouldEditQuantityFieldIs(String text) {
        anAssert.isElementTextEqual(editQuantityFld, text);
        return this;
    }

    @Step("Убедиться, что итоговая сумма рассчитана корректно на основе цены и введенного кол-ва")
    public AddProduct35Page<T> shouldTotalPriceCalculateCorrectly() {
        double _price = getPrice();
        double _quantity = ParserUtil.strToDouble(editQuantityFld.getText());
        String expectedTotalPrice = ParserUtil.prettyDoubleFmt(_price * _quantity);
        anAssert.isEquals(ParserUtil.strWithOnlyDigits(totalPrice.getText()), expectedTotalPrice,
                "Сумма итого (как цена * кол-во) рассчитана не верно");
        return this;
    }

    @Step("Проерить, что предупреждающее сообщение о доступном кол-ве товара отображается")
    public AddProduct35Page<T> shouldAvailableStockAlertMessageIsVisible() {
        anAssert.isElementVisible(availableStockAlertMsgLbl);
        return this;
    }

    @Step("Проерить, что предупреждающее сообщение о доступном кол-ве товара НЕ отображается")
    public AddProduct35Page<T> shouldAvailableStockAlertMessageIsNotVisible() {
        anAssert.isElementNotVisible(availableStockAlertMsgLbl);
        return this;
    }

    private int calculate(int operand1, int operand2, String operation) {
        switch (operation) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                return operand1 / operand2;
            default:
                throw new IllegalArgumentException("Недопустимая операция:" + operation + " Допустимые значения: +, -, *, /");
        }
    }

    /**
     * Вводим выражение в калькулятор и проверяем правильность расчета
     *
     * @param expression - выражение. Возможные операции:
     *                   + - сложение
     *                   - - вычитаение
     *                   * - умножение
     *                   / - деление
     */
    @Step("Проверить корректность расчета калькулятора")
    public AddProduct35Page<T> verifyCalculator(String expression) {
        String[] digits = expression.replaceAll("\\D+", " ").trim().split(" ");
        String[] operations = expression.replaceAll("\\d+", " ").trim().split(" ");
        int expectedResult = 0;
        if (digits.length != operations.length + 1)
            throw new IllegalArgumentException("Некорректное выражение:" + expression);
        editQuantityFld.click();
        editQuantityFld.clear();
        CalculatorWidget calculatorWidget = new CalculatorWidget();
        for (int i = 0; i < digits.length; i++) {
            expectedResult = i == 0 ? ParserUtil.strToInt(digits[i]) :
                    calculate(expectedResult, ParserUtil.strToInt(digits[i]), operations[i - 1]);
            editQuantityFld.fill(digits[i]);
            if (i < digits.length - 1)
                calculatorWidget.clickOperation(operations[i]);
        }
        String fieldValueBefore = editQuantityFld.getText();
        calculatorWidget.clickOperation("=");
        editQuantityFld.waitUntilTextIsChanged(fieldValueBefore);
        anAssert.isEquals(editQuantityFld.getText(), String.valueOf(expectedResult),
                "Выражение '" + expression + "' неверно посчитано (до нажатия 'ok')");
        editQuantityFld.submit();
        anAssert.isFalse(isKeyboardVisible(), "Клавиатура для ввода должна быть не видна");
        anAssert.isEquals(editQuantityFld.getText(), String.valueOf(expectedResult),
                "Выражение '" + expression + "' неверно посчитано (после нажатия 'ok')");
        return this;
    }

}
