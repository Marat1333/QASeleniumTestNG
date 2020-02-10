package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.basket.BasketStep1Page;
import io.qameta.allure.Step;

public class AddProductPage extends CommonMagMobilePage {

    private static final String SCREEN_TITLE = "Добавление товара";

    public AddProductPage(TestContext context) {
        super(context);
    }

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

    public String getPrice() {
        String _priceValue = price.getText().replaceAll(" ₽/м²", "").trim();
        try {
            Double.parseDouble(_priceValue);
            return _priceValue;
        } catch (NumberFormatException err) {
            anAssert.isTrue(false, "Цена имеет не правильный формат: " + _priceValue);
            throw err;
        }
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
    public BasketStep1Page clickAddButton() {
        addBtn.click();
        return new BasketStep1Page(context);
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
    public AddProductPage shouldTotalPriceIs(String number) {
        String sTotalPrice = totalPrice.getText().replaceAll(" ₽", "")
                .trim();
        anAssert.isEquals(sTotalPrice, number, "Сумма должна быть равна %s");
        return this;
    }
}
