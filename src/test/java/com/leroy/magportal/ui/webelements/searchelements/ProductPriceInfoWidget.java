package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.search.PriceContainerData;
import com.leroy.magportal.ui.webelements.commonelements.PriceContainer;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class ProductPriceInfoWidget extends BaseWidget {

    public ProductPriceInfoWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//span[contains(text(),'Закупочная')]/ancestor::div[2]/div[contains(@class, 'textAlign')]//div[contains(@class,'ProductCard')][1]",
            refreshEveryTime = true)
    PriceContainer hiddenPurchasingPrice;

    @WebFindBy(xpath = ".//span[contains(text(),'Рекомендованная')]/ancestor::div[2]/div[contains(@class, 'textAlign')]//div[contains(@class,'ProductCard')][1]",
            refreshEveryTime = true)
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

    public boolean isMismatchPriceThanRecommendedTooltipVisible() {
        return recommendedPriceNotMatchesLbl.isVisible();
    }

    public String getLastPriceChangeDateLbl() {
        return lastPriceChangeDateLbl.getText();
    }

    public PriceContainerData getHiddenRecommendedPriceContainerData() throws Exception {
        hiddenRecommendedPrice.findChildElement("./..").click();
        return new PriceContainerData(
                ParserUtil.strToDouble(hiddenRecommendedPrice.getDecimalPrice()),
                hiddenRecommendedPrice.getPriceCurrency(), hiddenRecommendedPrice.getUnit());
    }

    public PriceContainerData getHiddenPurchasePriceContainerData() throws Exception {
        hiddenPurchasingPrice.findChildElement("./..").click();
        return new PriceContainerData(
                ParserUtil.strToDouble(hiddenPurchasingPrice.getDecimalPrice()),
                hiddenPurchasingPrice.getPriceCurrency(), hiddenPurchasingPrice.getUnit());
    }

    public PriceContainerData getPriceContainerData() {
        return new PriceContainerData(ParserUtil.strToDouble(productPriceLbl.getDecimalPrice()),
                productPriceLbl.getPriceCurrency(), productPriceLbl.getUnit());
    }

    public PriceContainerData getPricePerUnitContainerData() {
        return new PriceContainerData(ParserUtil.strToDouble(pricePerUnit.getDecimalPrice()),
                pricePerUnit.getPriceCurrency(), pricePerUnit.getUnit());
    }


}
