package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.widgets.CardWidget;
import com.leroy.models.ProductCardData;
import org.openqa.selenium.WebDriver;

public class SearchProductCardWidget extends CardWidget<ProductCardData> {

    public SearchProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    private Element lmCodeObj;

    @AppFindBy(xpath = "./android.widget.TextView[2]")
    private Element barCodeObj;

    @AppFindBy(xpath = "./android.widget.TextView[3]")
    private Element nameObj;

    @AppFindBy(xpath = "./android.widget.TextView[4]")
    private Element priceObj;

    @AppFindBy(xpath = "./android.widget.TextView[5]")
    private Element priceLbl;

    @AppFindBy(xpath = "./android.widget.TextView[6]")
    private Element quantityObj;

    @AppFindBy(xpath = "./android.widget.TextView[7]")
    private Element quantityType;

    @AppFindBy(xpath = "./android.widget.TextView[8]")
    private Element quantityLbl;

    public String getLmCode(boolean onlyDigits, String pageSource) {
        if (onlyDigits)
            return lmCodeObj.getText(pageSource).replaceAll("\\D", "");
        else
            return lmCodeObj.getText(pageSource);
    }

    public String getLmCode(boolean onlyDigits) {
        return getLmCode(onlyDigits, null);
    }

    public String getBarCode(boolean onlyDigits, String pageSource) {
        if (onlyDigits)
            return barCodeObj.getText(pageSource).replaceAll(" ", "");
        else
            return barCodeObj.getText(pageSource);
    }

    public String getBarCode(boolean onlyDigits) {
        return getBarCode(onlyDigits, null);
    }

    public String getName(String pageSource) {
        return nameObj.getText(pageSource);
    }

    public String getName() {
        return getName(null);
    }

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

    public String getQuantity(boolean onlyDigits, String pageSource) {
        if (onlyDigits)
            return quantityObj.getText(pageSource).replaceAll(" ", "");
        else
            return quantityObj.getText(pageSource);
    }

    public String getQuantity(boolean onlyDigits) {
        return getQuantity(onlyDigits, null);
    }

    public String getQuantityType(String pageSource) {
        return quantityType.getText(pageSource);
    }

    public String getQuantityType() {
        return getQuantityType(null);
    }

    public String getQuantityLbl(String pageSource) {
        return quantityLbl.getText(pageSource);
    }

    public String getQuantityLbl() {
        return getQuantityLbl(null);
    }

    @Override
    public ProductCardData collectDataFromPage(String pageSource) {
        String ps = pageSource == null? driver.getPageSource() : pageSource;
        ProductCardData productCardData = new ProductCardData();
        productCardData.setLmCode(getLmCode(true, ps));
        productCardData.setBarCode(getBarCode(true, ps));
        productCardData.setName(getName(ps));
        productCardData.setPrice(getPrice(ps));
        productCardData.setAvailableQuantity(getQuantity(true, ps));
        return productCardData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCodeObj.isVisible(pageSource) && quantityLbl.isVisible(pageSource);
    }
}
