package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ShortSalesDocWebData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class ShortCartEstimateDocumentCardWidget extends CardWebWidget<ShortSalesDocWebData> {

    public ShortCartEstimateDocumentCardWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'Documents-ListItemCard__heading-text')]//span")
    Element number;

    public String getNumber() {
        return ParserUtil.strWithOnlyDigits(number.getText());
    }

    @Override
    public ShortSalesDocWebData collectDataFromPage() {
        ShortSalesDocWebData salesDocData = new ShortSalesDocWebData();
        salesDocData.setNumber(getNumber());
        return salesDocData;
    }

}
