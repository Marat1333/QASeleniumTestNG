package com.leroy.magmobile.ui.pages.sales.product_card.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.product_card.ShopCardData;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import org.openqa.selenium.WebDriver;

public class ShopStockInfoWidget extends CardWidget<ShopCardData> {

    @AppFindBy(xpath = "./*[1]")
    Element id;

    @AppFindBy(xpath = "./*[@content-desc='presenceValue']")
    Element stock;

    public ShopStockInfoWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public ShopCardData collectDataFromPage(String pageSource) {
        ShopCardData data = new ShopCardData();
        data.setId(id.getText());
        data.setStock(stock.getText());
        return data;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return id.isVisible() && stock.isVisible();
    }
}
