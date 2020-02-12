package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.models.ProductCardData;
import com.leroy.utils.Converter;
import org.openqa.selenium.WebDriver;

public class SearchProductCardWidget extends SearchProductAllGammaCardWidget {

    public SearchProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./android.view.ViewGroup[android.widget.TextView[@content-desc='name']]/following-sibling::android.widget.TextView")
    private Element priceObj;

    @AppFindBy(xpath = "./android.widget.TextView[5]")
    private Element priceLbl;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc=\"presenceValue\"]")
    private Element quantityObj;

    @AppFindBy(xpath = "./android.widget.TextView[7]")
    private Element priceUnit;

    @AppFindBy(xpath = "./android.widget.TextView[6]")
    private Element quantityLbl;

    public String getPrice(String pageSource) {
        return priceObj.getText(pageSource);
    }

    public String getPrice() {
        return getPrice(null);
    }

    public String getPriceLbl(String pageSource) {
        return priceLbl.getText(pageSource);
    }

    public String getPriceLbl() {
        return getPriceLbl(null);
    }

    public String getQuantity(String pageSource) {
        return quantityObj.getText(pageSource);
    }

    public String getPriceUnit(String pageSource) {
        return priceUnit.getText(pageSource);
    }

    public String getPriceUnit() {
        return getPriceUnit(null);
    }

    public String getQuantityLbl(String pageSource) {
        return quantityLbl.getText(pageSource);
    }

    public String getQuantityLbl() {
        return getQuantityLbl(null);
    }

    @Override
    public ProductCardData collectDataFromPage(String pageSource) {
        String ps = pageSource == null ? driver.getPageSource() : pageSource;
        ProductCardData productCardData = new ProductCardData();
        productCardData.setLmCode(getLmCode(true, ps));
        productCardData.setBarCode(getBarCode(true, ps));
        productCardData.setName(getName(ps));
        productCardData.setPrice(Converter.strToDouble(getPrice(ps)));
        productCardData.setAvailableQuantity(Converter.strToDouble(getQuantity(ps)));
        return productCardData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return super.isFullyVisible(pageSource) && quantityLbl.isVisible(pageSource);
    }
}
