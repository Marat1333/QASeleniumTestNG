package com.leroy.magmobile.ui.pages.sales;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.basket.Basket35Page;
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

    @AppFindBy(text = "Цена")
    private Element priceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[@text='Цена']/following-sibling::android.widget.TextView[1]")
    private Element price;

    @AppFindBy(text = "Торговый зал")
    private Element shoppingRoomLbl;

    // White Bottom Area

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.widget.EditText",
            metaName = "Поле для редактирования кол-ва")
    private EditBox editQuantityFld;

    @AppFindBy(xpath = "//android.widget.EditText/following-sibling::android.widget.TextView[2]",
            metaName = "Сумма")
    private Element totalPrice;

    @AppFindBy(text = "ДОБАВИТЬ В КОРЗИНУ")
    private MagMobSubmitButton addBtn;


    @Override
    public void waitForPageIsLoaded() {
        screenTitle.waitUntilTextIsEqualTo(SCREEN_TITLE);
        waitForProgressBarIsInvisible();
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

    @Step("Нажмите кнопку Добавить")
    public Basket35Page clickAddButton() {
        addBtn.click();
        return new Basket35Page(context);
    }

    // ---------------- Verifications ----------------------- //

    @Step("Проверить, что страница 'Добавление товара' отображается корректно")
    public AddProduct35Page verifyRequiredElements() {
        String ps = getPageSource();
        softAssert.isElementTextEqual(screenTitle, SCREEN_TITLE, ps);
        softAssert.isElementVisible(backBtn, ps);
        softAssert.isElementVisible(priceLbl, ps);
        softAssert.isElementVisible(shoppingRoomLbl, ps);
        softAssert.isElementVisible(editQuantityFld, ps);
        softAssert.isElementVisible(addBtn, ps);
        softAssert.isTrue(addBtn.isEnabled(),
                "Кнопка 'Добавить' должна быть активна");
        softAssert.verifyAll();
        return this;
    }

}
