package com.leroy.magportal.ui.pages.products.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.search.ShopCardData;
import com.leroy.magportal.ui.webelements.commonelements.PriceContainer;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class ShopCardWidget extends BaseWidget {
    public ShopCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = "./div[1]/span")
    Element shopId;

    @WebFindBy(xpath = "./div[2]/span")
    Element shopName;

    @WebFindBy(xpath = "./div[2]/p[1]")
    Element shopAddress;

    @WebFindBy(xpath = "./div[2]/p[2]")
    Element distance;

    @WebFindBy(xpath = "./div[3]/div[1]")
    PriceContainer price;

    @WebFindBy(xpath = "./div[3]/div[2]")
    Element availableStock;

    public ShopCardData grabDataFromWidget() {
        ShopCardData data = new ShopCardData();
        data.setId(Integer.valueOf(shopId.getText()));
        data.setName(shopName.getText());
        data.setAddress(shopAddress.getText());
        data.setDistance(Double.parseDouble(distance.getText().split(" ")[0]));
        data.setPrice(price.getDecimalPrice());
        data.setQuantity(ParserUtil.strToDouble(availableStock.getText()));
        return data;
    }
}
