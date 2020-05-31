package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.Cart35Page;
import com.leroy.magmobile.ui.pages.sales.orders.estimate.EstimatePage;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class AddProduct35Page extends CommonMagMobilePage {

    protected String SCREEN_TITLE_VALUE() {
        return "Добавление товара";
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

    // White Bottom Area

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.widget.EditText",
            metaName = "Поле для редактирования кол-ва")
    private EditBox editQuantityFld;

    @AppFindBy(xpath = "//android.widget.EditText/following-sibling::android.widget.TextView[2]",
            metaName = "Сумма")
    private Element totalPrice;

    public enum SubmitBtnCaptions {
        ADD_TO_BASKET("ДОБАВИТЬ В КОРЗИНУ"),
        ADD_TO_ESTIMATE("ДОБАВИТЬ В СМЕТУ"),
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
    public String getPrice() {
        return price.getText();
    }

    @Step("Получить значение кол-ва товара в торговом зале")
    public int getAvailableQuantityInShoppingRoom() {
        return ParserUtil.strToInt(shoppingRoomAvailableQuantity.getText());
    }

    @Step("Получить значение кол-ва товара на складе")
    public int getAvailableQuantityInStock() {
        return ParserUtil.strToInt(inStockAvailableQuantity.getText());
    }

    @Step("Получить информацию со страницы о товаре/услуги/выбранном кол-ве и т.п.")
    public ProductOrderCardAppData getProductOrderDataFromPage() {
        String ps = getPageSource();

        // Карточка товара
        ProductOrderCardAppData productData = new ProductOrderCardAppData();
        //productData.setAvailableTodayQuantity(
        //        ParserUtil.strToInt(shoppingRoomAvailableQuantity.getText(ps)));
        productData.setPrice(ParserUtil.strToDouble(price.getText(ps)));
        productData.setTitle(title.getText(ps));
        productData.setLmCode(ParserUtil.strWithOnlyDigits(lmCode.getText(ps)));
        productData.setBarCode(ParserUtil.strWithOnlyDigits(barCode.getText(ps)));
        productData.setPriceUnit(shoppingRoomAvailablePriceUnit.getText(ps));

        // Детали выбора товара (Строка заказа)
        productData.setSelectedQuantity(ParserUtil.strToDouble(editQuantityFld.getText(ps)));
        productData.setTotalPrice(ParserUtil.strToDouble(totalPrice.getText(ps)));
        productData.setAvailableTodayQuantity(getAvailableQuantityInShoppingRoom() +
                getAvailableQuantityInStock());
        return productData;
    }

    // ---------------- Action Steps -------------------------//

    @Step("Нажмите на поле количества")
    public AddProduct35Page clickEditQuantityField() {
        editQuantityFld.click();
        return this;
    }

    @Step("Введите {text} количества товара")
    public AddProduct35Page enterQuantityOfProduct(String text) {
        editQuantityFld.clearFillAndSubmit(text);
        return this;
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
    public AddProduct35Page verifyRequiredElements(SubmitBtnCaptions caption) {
        String ps = getPageSource();
        softAssert.isElementTextEqual(screenTitle, SCREEN_TITLE_VALUE(), ps);
        softAssert.isElementVisible(backBtn, ps);
        softAssert.isElementVisible(priceLbl, ps);
        softAssert.isElementVisible(shoppingRoomLbl, ps);
        softAssert.isElementVisible(editQuantityFld, ps);
        anAssert.isElementTextEqual(submitBtn, caption.value(), ps);
        softAssert.isTrue(submitBtn.isEnabled(),
                "Кнопка 'Добавить' должна быть активна");
        softAssert.verifyAll();
        return this;
    }

    @Step("Убедиться, что поле для редактирования кол-ва = {text}")
    public AddProduct35Page shouldEditQuantityFieldIs(String text) {
        if (!text.contains(","))
            text = text + ",00";
        else if (text.length() - text.indexOf(",") == 2)
            text = text + "0";
        anAssert.isElementTextEqual(editQuantityFld, text);
        return this;
    }

    @Step("Убедиться, что итоговая сумма рассчитана корректно на основе цены и введенного кол-ва")
    public AddProduct35Page shouldTotalPriceCalculateCorrectly() {
        double _price = ParserUtil.strToDouble(getPrice());
        double _quantity = ParserUtil.strToDouble(editQuantityFld.getText());
        String expectedTotalPrice = ParserUtil.prettyDoubleFmt(_price * _quantity);
        anAssert.isEquals(ParserUtil.strWithOnlyDigits(totalPrice.getText()), expectedTotalPrice,
                "Сумма итого (как цена * кол-во) рассчитана не верно");
        return this;
    }

}
