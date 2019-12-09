package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import org.openqa.selenium.WebDriver;

public class StockProductsPage extends BaseAppPage {

    public StockProductsPage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup//android.widget.ImageView")
    private ElementList<Element> productImages;

    @AppFindBy(xpath = "//android.widget.ScrollView//android.view.ViewGroup[@index='0' and android.widget.TextView[contains(@text, 'шт.')]]")
    private ElementList<Element> pieceProductAreas;

    public boolean isAnyProductAvailableOnPage() {
        return productImages.getCount() > 0;
    }

    public ProductCardPage clickFirstPieceProduct() throws Exception {
        pieceProductAreas.get(0).click();
        return new ProductCardPage(driver);
    }

}
