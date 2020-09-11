package com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies;

import com.leroy.constants.TimeZone;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.catalog.product.PriceInfo;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.ui.models.product_card.ShopCardData;
import com.leroy.magmobile.ui.pages.sales.product_card.widgets.ShopPriceInfoWidget;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.List;

public class PricesPage extends ProductPricesQuantitySupplyPage {
    @AppFindBy(xpath = "//*[@text='Цена']/following-sibling::*")
    Element priceLbl;

    @AppFindBy(xpath = "//*[@text='Цена']/ancestor::*[1]/following-sibling::*[1]/*")
    Element priceChangeDateLbl;

    @AppFindBy(xpath = "//*[@text='Цена']/ancestor::*[1]/following-sibling::android.widget.TextView[1]")
    Element reasonOfChangeLbl;

    @AppFindBy(text = "Не соответствует рекомендованной цене")
    Element recommendedPriceMismatchLbl;

    @AppFindBy(xpath = "//*[@text='Закупочная цена']/following-sibling::*[1]")
    Element purchasePriceLbl;

    @AppFindBy(xpath = "//*[@text='Рекомендованная цена']/following-sibling::*[1]")
    Element recommendedPriceLbl;

    @AppFindBy(containsText = "Все (1")
    Button shopListNavBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    private AndroidScrollView<ShopCardData> shopCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//*[contains(@text,'км')]/../*[1]", ShopPriceInfoWidget.class);

    @Step("Перейти на страницу со списком магазинов")
    public ShopPricesPage goToShopListPage() {
        mainScrollView.scrollUpToElement(shopListNavBtn);
        shopListNavBtn.click();
        return new ShopPricesPage();
    }

    @Override
    public void waitForPageIsLoaded() {
        priceLbl.waitForVisibility();
        reasonOfChangeLbl.waitForVisibility();
    }

    @Step("Проверить корректность данных")
    public PricesPage shouldDataIsCorrect(CatalogProductData data) {
        String uiDateFormat = "d.MM.yy";
        String apiDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        PriceInfo priceInfo = data.getSalesPrice();
        softAssert.isElementTextContains(priceLbl, String.valueOf(priceInfo.getPrice()).replaceAll("\\.", ","));
        softAssert.isElementTextContains(purchasePriceLbl, String.valueOf(data.getPurchasePrice()).replaceAll("\\.", ","));
        softAssert.isElementTextContains(recommendedPriceLbl, String.valueOf(priceInfo.getRecommendedPrice()).replaceAll("\\.", ","));
        softAssert.isEquals(DateTimeUtil.strToLocalDateTime(priceChangeDateLbl.getText().replaceAll("c ", ""), uiDateFormat),
                DateTimeUtil.strToLocalDateTime(priceInfo.getDateOfChange(), apiDateFormat).plusHours(DriverFactory.isGridProfile() ? TimeZone.UTC : TimeZone.MSC), "date of price change");
        softAssert.isElementTextEqual(reasonOfChangeLbl, priceInfo.getReasonOfChange());
        if (priceInfo.getPrice() - priceInfo.getRecommendedPrice() != 0.0) {
            softAssert.isElementVisible(recommendedPriceMismatchLbl);
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить данные о ценах")
    public PricesPage shouldShopPricesAreCorrect(List<ShopData> data) throws Exception {
        mainScrollView.scrollDownToElement(shopListNavBtn);
        List<ShopCardData> shopData = shopCardsScrollView.getFullDataList();
        for (int i = 0; i < shopData.size(); i++) {
            ShopCardData uiData = shopData.get(i);
            ShopData apiData = data.get(i);
            softAssert.isEquals(uiData.getId(), apiData.getId() + " " + apiData.getName(), "id and name");
            softAssert.isContainsIgnoringCase(uiData.getPrice().replaceAll(",", "."), ParserUtil.prettyDoubleFmt(apiData.getPriceAndStock().getPrice()), "price");
        }
        softAssert.verifyAll();
        return this;
    }
}
