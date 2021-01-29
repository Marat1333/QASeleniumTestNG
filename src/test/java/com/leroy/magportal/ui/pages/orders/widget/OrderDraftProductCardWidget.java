package com.leroy.magportal.ui.pages.orders.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class OrderDraftProductCardWidget extends OrderProductCardWidget {

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__smallSide')]//p[2]", metaName = "Габариты")
    Element dimension;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__bigSide')]//p[2]", metaName = "Вес")
    Element weight;

    public OrderDraftProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @Override
    public String getWeight() {
        return weight.getText();
    }

    @Override
    public ProductOrderCardWebData collectDataFromPage() {
        ProductOrderCardWebData productOrderCardWebData = new ProductOrderCardWebData();
        productOrderCardWebData.setBarCode(ParserUtil.strWithOnlyDigits(barCode.getTextIfPresent()));
        productOrderCardWebData.setLmCode(lmCode.getText());
        productOrderCardWebData.setTitle(title.getText());
        productOrderCardWebData.setAvailableTodayQuantity(ParserUtil.strToDouble(availableQuantity.getText()));
        productOrderCardWebData.setSelectedQuantity(ParserUtil.strToDouble(orderedQuantityFld.getText()));
        productOrderCardWebData.setWeight(ParserUtil.strToDouble(getWeight(), "."));
        if (!discountPercent.isVisible())
            productOrderCardWebData.setTotalPrice(ParserUtil.strToDouble(price.getText()));
        else {
            productOrderCardWebData.setDiscountPercent(ParserUtil.strToDouble(discountPercent.getText()));
            productOrderCardWebData.setTotalPriceWithDiscount(ParserUtil.strToDouble(price.getText()));
            productOrderCardWebData.setTotalPrice(ParserUtil.plus(productOrderCardWebData.getTotalPriceWithDiscount() /
                    (1 - productOrderCardWebData.getDiscountPercent() / 100.0), 0, 2));
        }
        productOrderCardWebData.setPrice(ParserUtil.plus(productOrderCardWebData.getTotalPrice() / productOrderCardWebData.getSelectedQuantity(), 0, 2));
        return productOrderCardWebData;
    }
}
