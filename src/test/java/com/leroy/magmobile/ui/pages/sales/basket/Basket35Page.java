package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.models.ProductCardData;
import io.qameta.allure.Step;

public class Basket35Page extends CommonMagMobilePage {

    public Basket35Page(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "BackButton",
            metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='DefaultScreenHeader']/android.widget.TextView[1]")
    protected Element screenTitle;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.view.ViewGroup[android.view.ViewGroup[android.widget.ImageView]]]")
    private BasketProductWidget productCard;

    // Bottom Area
    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/preceding-sibling::android.widget.TextView",
            metaName = "Текст с количеством и весом товара")
    Element countAndWeightProductLbl;

    @AppFindBy(text = "Итого: ")
    Element totalPriceLbl;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/following-sibling::android.widget.TextView")
    Element totalPriceVal;

    @AppFindBy(text = "ТОВАР")
    private MagMobWhiteSubmitButton addProductBtn;

    @AppFindBy(text = "ОФОРМИТЬ")
    private MagMobGreenSubmitButton submitBtn;

    @Override
    public void waitForPageIsLoaded() {
        submitBtn.waitForVisibility();
    }

    // -------------- ACTIONS ---------------------------//

    @Step("Нажмите ОФОРМИТЬ")
    public ProcessOrder35Page clickSubmitButton() {
        submitBtn.click();
        return new ProcessOrder35Page(context);
    }

    // ------------- Verifications ----------------------//

    @Step("Проверить, что страница 'Корзина' отображается корректно")
    public Basket35Page verifyRequiredElements() {
        softAssert.areElementsVisible(backBtn, screenTitle, productCard, totalPriceLbl, totalPriceVal,
                countAndWeightProductLbl, addProductBtn, submitBtn);
        softAssert.verifyAll();
        return this;
    }

    public Basket35Page shouldProductCardDataIs(ProductCardData expectedProductCardData) {
        softAssert.isEquals(productCard.getLmCode(true), expectedProductCardData.getLmCode(),
                "Неверный лм код продукта");
        softAssert.isEquals(productCard.getName(), expectedProductCardData.getName(),
                "Неверное название продукта");
        softAssert.isEquals(productCard.getProductCount(true), expectedProductCardData.getSelectedQuantity(),
                "Неверное кол-во продукта");
        softAssert.isEquals(productCard.getPrice(true), expectedProductCardData.getPrice(),
                "Неверная цена товара");
        softAssert.isEquals(productCard.getTotalPrice(true), String.valueOf(
                Math.round(Double.parseDouble(expectedProductCardData.getPrice()) *
                        Double.parseDouble(expectedProductCardData.getSelectedQuantity()))),
                "Неверная общая стоимость товара");

        softAssert.verifyAll();
        return this;
    }

}
