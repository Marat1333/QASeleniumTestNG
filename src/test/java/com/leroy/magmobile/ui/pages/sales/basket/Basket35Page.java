package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.elements.MagMobWhiteSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.common.SearchProductPage;
import com.leroy.models.EstimateData;
import com.leroy.models.ProductCardData;
import com.leroy.utils.Converter;
import io.qameta.allure.Step;

import java.util.List;

public class Basket35Page extends CommonMagMobilePage {

    public final static String SCREEN_TITLE = "Корзина";

    public Basket35Page(TestContext context) {
        super(context);
    }

    public static class PageState {
        boolean productIsAdded;

        public boolean isProductIsAdded() {
            return productIsAdded;
        }

        public PageState setProductIsAdded(boolean productIsAdded) {
            this.productIsAdded = productIsAdded;
            return this;
        }
    }

    @AppFindBy(accessibilityId = "BackButton",
            metaName = "Кнопка назад")
    Element backBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='DefaultScreenHeader']/android.widget.TextView[1]")
    protected Element screenTitle;

    @AppFindBy(text = "Пока пусто")
    Element emptyInfoMessageLbl;

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
    private MagMobGreenSubmitButton makeSalesBtn;

    @AppFindBy(text = "ТОВАРЫ И УСЛУГИ", metaName = "Кнопка 'Товары и Услуги'")
    MagMobWhiteSubmitButton productAndServiceBtn;

    @Override
    public void waitForPageIsLoaded() {
        screenTitle.waitUntilTextIsEqualTo(SCREEN_TITLE);
        waitUntilProgressBarIsInvisible();
    }


    // -------------- ACTIONS ---------------------------//

    @Step("Нажмите ОФОРМИТЬ")
    public ProcessOrder35Page clickMakeSalesButton() {
        makeSalesBtn.click();
        return new ProcessOrder35Page(context);
    }

    @Step("Нажмите ТОВАРЫ И УСЛУГИ")
    public SearchProductPage clickProductAndServiceSubmitButton() {
        productAndServiceBtn.click();
        return new SearchProductPage(context);
    }

    // ------------- Verifications ----------------------//

    @Step("Проверить, что страница 'Корзина' отображается корректно")
    public Basket35Page verifyRequiredElements(PageState state) {
        String ps = getPageSource();
        // Всегда есть эти элементы:
        softAssert.areElementsVisible(ps, backBtn, screenTitle);
        // Разные состояния:
        if (state.isProductIsAdded())
            softAssert.areElementsVisible(ps, productCard, totalPriceLbl, totalPriceVal,
                    countAndWeightProductLbl, addProductBtn, makeSalesBtn);
        else
            softAssert.areElementsVisible(ps, productAndServiceBtn, emptyInfoMessageLbl);
        softAssert.isElementTextEqual(screenTitle, SCREEN_TITLE, ps);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что {index}-ый продукт содержит следующие данные: {expectedProductCardData}")
    public Basket35Page shouldProductCardDataIs(int index, ProductCardData expectedProductCardData) {
        index--;
        String ps = getPageSource();
        softAssert.isEquals(productCard.getLmCode(true, ps), expectedProductCardData.getLmCode(),
                "Неверный лм код продукта");
        softAssert.isEquals(productCard.getName(ps), expectedProductCardData.getName(),
                "Неверное название продукта");
        softAssert.isEquals(productCard.getProductCount(true, ps), expectedProductCardData.getSelectedQuantity(),
                "Неверное кол-во продукта");
        softAssert.isEquals(productCard.getPrice(true, ps), expectedProductCardData.getPrice(),
                "Неверная цена товара");
        softAssert.isEquals(productCard.getTotalPrice(true, ps), String.valueOf(
                Math.round(Double.parseDouble(expectedProductCardData.getPrice()) *
                        Double.parseDouble(expectedProductCardData.getSelectedQuantity()))),
                "Неверная общая стоимость товара");
        softAssert.isEquals(productCard.getAvailableTodayProductCountLbl(true, ps),
                expectedProductCardData.getAvailableQuantity(),
                "Неверное доступное кол-во");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что информация по всем товарам в корзине следующая:" +
            " Кол-во товаров = {productCount}; Вес = {weight}; Итого = {totalPrice}")
    public Basket35Page shouldTotalBasketInfoIs(int expectedProductCount,
                                                Double expectedWeight,
                                                Double expectedTotalPrice) throws Exception {
        String pageSource = getPageSource();
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText(pageSource).split("•");
        anAssert.isTrue(actualCountProductAndWeight.length == 2,
                "Что-то не так с меткой содержащей информацию о кол-ве и весе товара");
        String actualTotalPrice = totalPriceVal.getText(pageSource).replaceAll("\\D+", "");
        softAssert.isEquals(Converter.strToInt(actualCountProductAndWeight[0]),
                expectedProductCount, "Неверное кол-во товара");
        softAssert.isEquals(Converter.strToDouble(actualCountProductAndWeight[1]),
                expectedWeight, "Неверное кол-во товара");
        softAssert.isEquals(Converter.strToDouble(actualTotalPrice), expectedTotalPrice,
                "Неверное сумма итого");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что информация о товарах в корзине должна быть следующая: {estimateData}")
    public Basket35Page shouldBasketInformationIs(EstimateData estimateData) throws Exception {
        List<ProductCardData> productCardDataList = estimateData.getProductCardDataList();
        for (int i = 0; i < productCardDataList.size(); i++) {
            shouldProductCardDataIs(i + 1, productCardDataList.get(i));
        }
        shouldTotalBasketInfoIs(estimateData.getCountOfProducts(), estimateData.getWeight(),
                estimateData.getTotalPrice());
        return this;
    }

}
