package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.basket.Basket35Page;
import com.leroy.magmobile.ui.pages.sales.estimate.EstimatePage;
import com.leroy.models.SalesOrderCardData;
import com.leroy.models.ProductCardData;
import com.leroy.utils.Converter;
import io.qameta.allure.Step;

public class AddProduct35Page extends CommonMagMobilePage {

    private static final String SCREEN_TITLE = "Добавление товара";

    public AddProduct35Page(TestContext context) {
        super(context);
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
    private Element name;

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

    // White Bottom Area

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.widget.EditText",
            metaName = "Поле для редактирования кол-ва")
    private EditBox editQuantityFld;

    @AppFindBy(xpath = "//android.widget.EditText/following-sibling::android.widget.TextView[2]",
            metaName = "Сумма")
    private Element totalPrice;

    public enum SubmitBtnCaptions {
        ADD_TO_BASKET("ДОБАВИТЬ В КОРЗИНУ"),
        ADD_TO_ESTIMATE("ДОБАВИТЬ В СМЕТУ");

        String value;

        SubmitBtnCaptions(String val) {
            this.value = val;
        }

        public String value() {
            return value;
        }
    }

    @AppFindBy(accessibilityId = "Button-container")
    private MagMobGreenSubmitButton addBtn;


    @Override
    public void waitForPageIsLoaded() {
        screenTitle.waitUntilTextIsEqualTo(SCREEN_TITLE);
        waitUntilProgressBarIsInvisible();
    }

    /**
     * Получить Цену продукта за единицу товара
     */
    public String getPrice() {
        String _priceValue = price.getText().replaceAll("₽/м²|₽/шт.", "").trim();
        try {
            Double.parseDouble(_priceValue);
            return _priceValue;
        } catch (NumberFormatException err) {
            anAssert.isTrue(false, "Цена имеет не правильный формат: " + _priceValue);
            throw err;
        }
    }

    // ----- Grab Data from Page ----------//

    @Step("Получить информацию со страницы о товаре/услуги/выбранном кол-ве и т.п.")
    public SalesOrderCardData getOrderRowDataFromPage() {
        String ps = getPageSource();

        // Карточка товара
        ProductCardData cardData = new ProductCardData();
        cardData.setAvailableQuantity(Converter.strToDouble(shoppingRoomAvailableQuantity.getText(ps)));
        cardData.setPrice(Converter.strToDouble(price.getText(ps)));
        cardData.setName(name.getText(ps));
        cardData.setLmCode(Converter.strToStrWithoutDigits(lmCode.getText(ps)));
        cardData.setBarCode(Converter.strToStrWithoutDigits(barCode.getText(ps)));
        cardData.setPriceUnit(shoppingRoomAvailablePriceUnit.getText(ps));

        // Детали выбора товара (Строка заказа)
        SalesOrderCardData orderCardData = new SalesOrderCardData();
        orderCardData.setSelectedQuantity(Converter.strToDouble(editQuantityFld.getText(ps)));
        orderCardData.setTotalPrice(Converter.strToDouble(totalPrice.getText(ps)));
        orderCardData.setProductCardData(cardData);
        return orderCardData;
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
    public Basket35Page clickAddIntoBasketButton() {
        addBtn.click();
        return new Basket35Page(context);
    }

    @Step("Нажмите кнопку Добавить в смету")
    public EstimatePage clickAddIntoEstimateButton() {
        addBtn.click();
        return new EstimatePage(context);
    }

    // ---------------- Verifications ----------------------- //

    @Step("Проверить, что страница 'Добавление товара' отображается корректно")
    public AddProduct35Page verifyRequiredElements(SubmitBtnCaptions caption) {
        String ps = getPageSource();
        softAssert.isElementTextEqual(screenTitle, SCREEN_TITLE, ps);
        softAssert.isElementVisible(backBtn, ps);
        softAssert.isElementVisible(priceLbl, ps);
        softAssert.isElementVisible(shoppingRoomLbl, ps);
        softAssert.isElementVisible(editQuantityFld, ps);
        anAssert.isElementTextEqual(addBtn, caption.value(), ps);
        softAssert.isTrue(addBtn.isEnabled(),
                "Кнопка 'Добавить' должна быть активна");
        softAssert.verifyAll();
        return this;
    }

}
