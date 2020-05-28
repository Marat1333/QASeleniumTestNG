package com.leroy.magportal.ui.webelements.searchelements;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.webelements.commonelements.PriceContainer;
import org.openqa.selenium.WebDriver;

public class ProductPriceInfoWidget extends BaseWidget {
    public ProductPriceInfoWidget(WebDriver driver) {
        super(driver);
    }

    @WebFindBy(xpath = ".//span[contains(text(),'Закупочная')]/ancestor::div[2]/div[contains(@class, 'textAlign')]//div[contains(@class,'ProductCard')][1]",
            refreshEveryTime = true)
    PriceContainer hiddenPurchasingPrice;

    @WebFindBy(xpath = ".//span[contains(text(),'Рекомендованная')]/ancestor::div[2]/div[contains(@class, 'textAlign')]" +
            "//div[contains(@class,'ProductCard')][1]", refreshEveryTime = true)
    PriceContainer hiddenRecommendedPrice;

    @WebFindBy(xpath = ".//span[contains(text(),'Цена')]/ancestor::div[1]/following-sibling::div[1]/span")
    Element lastPriceChangeDateLbl;

    @WebFindBy(xpath = ".//span[contains(text(),'Цена')]/ancestor::div[2]/following-sibling::div[2]" +
            "/div[contains(@class, 'textAlign')]")
    PriceContainer pricePerUnit;

    @WebFindBy(xpath = ".//span[contains(text(),'Цена')]/ancestor::div[2]/div[3]")
    PriceContainer productPriceLbl;

    @WebFindBy(xpath = "./div[2]//span")
    Element reasonOfChangeLbl;

    @WebFindBy(xpath = ".//span[contains(text(),'Не соответствует')]")
    Element recommendedPriceNotMatchesLbl;



    /*private PriceContainer getHiddenRecommendedPrice() {
        hiddenRecommendedPrice.click();
        initElements(new CustomLocator(By.xpath(hiddenRecommendedPrice.getXpath())));
        return hiddenRecommendedPrice;
    }

    private PriceContainer getHiddenPurchasePrice() {
        hiddenPurchasingPrice.click();
        return hiddenPurchasingPrice;
    }*/
}
