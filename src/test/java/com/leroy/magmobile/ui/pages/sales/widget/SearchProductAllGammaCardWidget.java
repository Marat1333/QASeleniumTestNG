package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.ui.models.search.ProductCardData;
import org.openqa.selenium.WebDriver;

public class SearchProductAllGammaCardWidget extends CardWidget<ProductCardData> {

    public SearchProductAllGammaCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='lmCode']")
    private Element lmCodeObj;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='barCode']")
    private Element barCodeObj;

    @AppFindBy(xpath = ".//android.widget.TextView[@content-desc='name']")
    private Element nameObj;

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

    @Override
    public ProductCardData collectDataFromPage(String pageSource) {
        ProductCardData productCardData = new ProductCardData();
        productCardData.setLmCode(getLmCode(true));
        productCardData.setBarCode(getBarCode(true));
        productCardData.setName(getName());
        return productCardData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCodeObj.isVisible(pageSource) && barCodeObj.isVisible(pageSource) && nameObj.isVisible(pageSource);
    }
}
