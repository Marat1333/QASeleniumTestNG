package com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.ui.models.product_card.ShopCardData;
import com.leroy.magmobile.ui.pages.sales.product_card.widgets.ShopPriceInfoWidget;
import com.leroy.magmobile.ui.pages.sales.product_card.widgets.ShopStockInfoWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.List;

public class ShopsStocksPage extends ShopPricesPage {

    @AppFindBy(text = "Запас в магазинах")
    private Element title;

    private AndroidScrollView<ShopCardData> shopCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//*[contains(@text,'км')]/../*[1]", ShopStockInfoWidget.class);

    @Override
    protected AndroidScrollView<ShopCardData> getShopCardsScrollView() {
        return shopCardsScrollView;
    }

    @Override
    protected void waitForPageIsLoaded() {
        title.waitForVisibility();
    }

    @Step("Проверить доступный остаток в магазинах")
    public ShopsStocksPage shouldShopStocksAreCorrect(List<ShopData> data){
        List<ShopCardData> shopData = shopCardsScrollView.getFullDataList();
        for (int i=0;i<shopData.size();i++){
            ShopCardData uiData = shopData.get(i);
            ShopData apiData = data.get(i);
            softAssert.isEquals(uiData.getId(), apiData.getId()+" "+apiData.getName(), "id and name");
            softAssert.isEquals(uiData.getStock(), ParserUtil.prettyDoubleFmt(apiData.getPriceAndStock().getStock()), "stock");
        }
        softAssert.verifyAll();
        return this;
    }
}
