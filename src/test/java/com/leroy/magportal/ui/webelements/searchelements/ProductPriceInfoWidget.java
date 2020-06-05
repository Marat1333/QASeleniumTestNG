package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.webelements.commonelements.PriceContainer;
import org.openqa.selenium.WebDriver;

public class ProductPriceInfoWidget extends BaseWidget {
    public ProductPriceInfoWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//span[contains(text(),'Закупочная')]/ancestor::div[2]/div[contains(@class, 'textAlign')]//div[contains(@class,'ProductCard')][1]",
            refreshEveryTime = true)
    PriceContainer hiddenPurchasingPrice;

    @WebFindBy(xpath = ".//span[contains(text(),'Рекомендованная')]/ancestor::div[2]/div[contains(@class, 'textAlign')]" +
            "//div[contains(@class,'ProductCard')][1]", refreshEveryTime = true)
    PriceContainer hiddenRecommendedPrice;

    @WebFindBy(xpath = ".//span[contains(text(),'Цена')]/ancestor::div[1]/following-sibling::div[1]/span")
    private Element lastPriceChangeDateLbl;

    @WebFindBy(xpath = ".//span[contains(text(),'За шт.')]/../following-sibling::div")
    PriceContainer pricePerUnit;

    @WebFindBy(xpath = ".//span[contains(text(),'Цена')]/ancestor::div[2]/div[3]")
    PriceContainer productPriceLbl;

    @WebFindBy(xpath = "./div[2]//span")
    Element reasonOfChangeLbl;

    @WebFindBy(xpath = ".//span[contains(text(),'Не соответствует')]")
    private Element recommendedPriceNotMatchesLbl;

    public String getReasonOfChange() {
        return reasonOfChangeLbl.isVisible() ? reasonOfChangeLbl.getText() : null;
    }

    public Element getRecommendedPriceNotMatchesLbl() {
        return recommendedPriceNotMatchesLbl;
    }

    public String getLastPriceChangeDateLbl() {
        return lastPriceChangeDateLbl.getText();
    }

    public boolean isPriceMismatchRecommendedPrice(String recommendedPrice) {
        String salePrice = getSalesPrice().getDecimalPrice();
        return !recommendedPrice.equals(salePrice);
    }

    public PriceContainer getSalesPrice() {
        return productPriceLbl;
    }

    public PriceContainer getPricePerUnit() {
        return pricePerUnit.isVisible() ? pricePerUnit : null;
    }

    public PriceContainer getHiddenRecommendedPrice() throws Exception {
        hiddenRecommendedPrice.findChildElement("./..").click();
        return hiddenRecommendedPrice;
    }

    public PriceContainer getHiddenPurchasePrice() throws Exception {
        hiddenPurchasingPrice.findChildElement("./..").click();
        return hiddenPurchasingPrice;
    }
}
