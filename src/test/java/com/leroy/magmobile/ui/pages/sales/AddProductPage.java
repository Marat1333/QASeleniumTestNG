package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.orders.cart.CartStep1Page;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

public class AddProductPage extends CommonMagMobilePage {

    private static final String SCREEN_TITLE = "Добавление товара";

    @AppFindBy(accessibilityId = "ScreenTitle")
    private Element screenTitle;

    @AppFindBy(xpath = "(//android.view.ViewGroup[@content-desc='Button'])[1]", metaName = "Кнопка назад")
    private Element backBtn;

    @AppFindBy(text = "Цена")
    private Element priceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Цена']/following-sibling::android.widget.TextView[1]")
    private Element price;

    @AppFindBy(text = "Доступно для продажи")
    private Element availableForSaleLbl;

    @AppFindBy(text = "Торговый зал")
    private Element shoppingRoomLbl;

    @AppFindBy(text = "Склад RM")
    private Element stockRMLbl;

    @AppFindBy(text = "Склад EM")
    private Element stockEMLbl;

    @AppFindBy(text = "Склад на улице")
    private Element stockOutLbl;

    @AppFindBy(text = "Удаленный склад RD")
    private Element remoteStockRDLbl;

    // Yellow Bottom Area

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.widget.EditText",
            metaName = "Поле для редактирования кол-ва")
    private EditBox editQuantityFld;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']/android.view.ViewGroup/android.widget.TextView[2]",
            metaName = "Сумма")
    private Element totalPrice;

    @AppFindBy(text = "ДОБАВИТЬ")
    private MagMobGreenSubmitButton addBtn;


    @Override
    public void waitForPageIsLoaded() {
        screenTitle.waitUntilTextIsEqualTo(SCREEN_TITLE);
        waitUntilProgressBarIsInvisible();
    }

    public Double getPrice() {
        return ParserUtil.strToDouble(price.getText());
    }

    // ---------------- Action Steps -------------------------//

    @Step("Нажмите на поле количества")
    public AddProductPage clickEditQuantityField() {
        editQuantityFld.click();
        return this;
    }

    @Step("Введите {text} количества товара")
    public AddProductPage enterQuantityOfProduct(String text) {
        editQuantityFld.clearFillAndSubmit(text);
        return this;
    }

    @Step("Нажмите кнопку Добавить")
    public CartStep1Page clickAddButton() {
        addBtn.click();
        return new CartStep1Page();
    }

    // ---------------- Verifications ----------------------- //

    public AddProductPage verifyRequiredElements() {
        String ps = getPageSource();
        softAssert.isElementTextEqual(screenTitle, SCREEN_TITLE, ps);
        softAssert.isElementVisible(backBtn, ps);
        softAssert.isElementVisible(priceLbl, ps);
        softAssert.isElementVisible(availableForSaleLbl, ps);
        softAssert.isElementVisible(shoppingRoomLbl, ps);
        softAssert.isElementVisible(stockRMLbl, ps);
        softAssert.isElementVisible(stockEMLbl, ps);
        softAssert.isElementVisible(stockOutLbl, ps);
        softAssert.isElementVisible(editQuantityFld, ps);
        softAssert.isElementVisible(addBtn, ps);
        softAssert.isTrue(addBtn.isEnabled(),
                "Кнопка 'Добавить' должна быть активна");
        softAssert.verifyAll();
        return this;
    }

    public AddProductPage shouldEditQuantityFieldIs(String text) {
        anAssert.isElementTextEqual(editQuantityFld, text);
        return this;
    }

    @Step("Проверить, что итогова сумма равна {number}")
    public AddProductPage shouldTotalPriceIs(Double expectedTotalPrice) {
        Double actualTotalPrice = ParserUtil.strToDouble(totalPrice.getText());
        anAssert.isEquals(actualTotalPrice, expectedTotalPrice, "Неверная итого стоимость");
        return this;
    }
}
