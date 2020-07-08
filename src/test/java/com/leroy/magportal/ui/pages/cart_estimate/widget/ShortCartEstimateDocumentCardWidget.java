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

    @WebFindBy(xpath = ".//div[contains(@class, '-price')]")
    Element price;

    @WebFindBy(xpath = ".//span[contains(@class, '-status-label')]")
    Element status;

    @WebFindBy(xpath = ".//div[contains(@class, 'Documents-ListItemCard__footer-row')]/div/div//span")
    Element creationDate;

    @WebFindBy(xpath = ".//div[contains(@class, 'Documents-ListItemCard__footer__name')]")
    Element author;

    public String getNumber() {
        return ParserUtil.strWithOnlyDigits(number.getText());
    }

    @Override
    public ShortSalesDocWebData collectDataFromPage() {
        ShortSalesDocWebData salesDocData = new ShortSalesDocWebData();
        salesDocData.setNumber(getNumber());
        salesDocData.setTotalPrice(ParserUtil.strToDouble(price.getText()));
        salesDocData.setStatus(status.getTextIfPresent());
        salesDocData.setCreationDate(creationDate.getText());
        salesDocData.setAuthor(author.getText());
        return salesDocData;
    }

}
