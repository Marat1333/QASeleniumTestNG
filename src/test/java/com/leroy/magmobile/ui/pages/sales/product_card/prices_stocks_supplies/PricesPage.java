package com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.catalog.product.PriceInfo;
import com.leroy.utils.DateTimeUtil;

public class PricesPage extends ProductPricesQuantitySupplyPage{
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

    @Override
    public void waitForPageIsLoaded() {
        priceLbl.waitForVisibility();
        reasonOfChangeLbl.waitForVisibility();
    }

    public PricesPage shouldDataIsCorrect(CatalogProductData data){
        String uiDateFormat = "d.MM.yy";
        String apiDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        PriceInfo priceInfo = data.getSalesPrice();
        softAssert.isElementTextContains(priceLbl, String.valueOf(priceInfo.getPrice()).replaceAll("\\.", ","));
        softAssert.isElementTextContains(purchasePriceLbl, String.valueOf(data.getPurchasePrice()).replaceAll("\\.", ","));
        softAssert.isElementTextContains(recommendedPriceLbl, String.valueOf(priceInfo.getRecommendedPrice()).replaceAll("\\.", ","));
        softAssert.isEquals(DateTimeUtil.strToLocalDateTime(priceChangeDateLbl.getText().replaceAll("c ", ""), uiDateFormat),
                DateTimeUtil.strToLocalDateTime(priceInfo.getDateOfChange(), apiDateFormat).plusHours(3), "date of price change");
        softAssert.isElementTextEqual(reasonOfChangeLbl, priceInfo.getReasonOfChange());
        if (priceInfo.getPrice()-priceInfo.getRecommendedPrice()!=0.0){
            softAssert.isElementVisible(recommendedPriceMismatchLbl);
        }
        softAssert.verifyAll();
        return this;
    }
}
