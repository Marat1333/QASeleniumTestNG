package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import io.qameta.allure.Step;

public class AddProductPage extends BaseAppPage {

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
    private MagMobSubmitButton addBtn;


    @Override
    public void waitForPageIsLoaded() {
        screenTitle.waitUntilTextIsEqualTo(SCREEN_TITLE);
        waitForProgressBarIsInvisible();
    }

    public Double getPrice() {
        try {
            return Double.valueOf(price.getText().replaceAll(" ₽/м²", ""));
        } catch (NumberFormatException err) {
            anAssert.isTrue(false, "Цена имеет не правильный формат: " + price.getText());
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


    @Override
    public AddProductPage verifyRequiredElements() {
        softAssert.isElementTextEqual(screenTitle, SCREEN_TITLE);
        softAssert.isElementVisible(backBtn);
        softAssert.isElementVisible(priceLbl);
        softAssert.isElementVisible(availableForSaleLbl);
        softAssert.isElementVisible(shoppingRoomLbl);
        softAssert.isElementVisible(stockRMLbl);
        softAssert.isElementVisible(stockEMLbl);
        softAssert.isElementVisible(stockOutLbl);
        softAssert.isElementVisible(editQuantityFld);
        softAssert.isElementVisible(addBtn);
        softAssert.isTrue(addBtn.isEnabled(),
                "Кнопка 'Добавить' должна быть активна");
        softAssert.verifyAll();
        return this;
    }

    public AddProductPage shouldEditQuantityFieldIs(String text) {
        anAssert.isElementTextEqual(editQuantityFld, text);
        return this;
    }

    public AddProductPage shouldTotalPriceIs(Double number) {
        String sTotalPrice = totalPrice.getText().replaceAll(" ₽", "")
                .replaceAll(",", ".");
        Double total = Double.valueOf(sTotalPrice);
        anAssert.isEquals(total, number, "Сумма должна быть равна %s");
        return this;
    }
}
