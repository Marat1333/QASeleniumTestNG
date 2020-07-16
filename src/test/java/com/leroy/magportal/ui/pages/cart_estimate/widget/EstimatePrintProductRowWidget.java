package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.EstimatePrintProductData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import org.openqa.selenium.WebDriver;

public class EstimatePrintProductRowWidget extends CardWebWidget<EstimatePrintProductData> {

    public EstimatePrintProductRowWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'EstimatePrint__product__col')][1]")
    Element title;

    @WebFindBy(xpath = ".//p[contains(text(), 'Артикул:')]")
    Element lmCode;

    private final static String COLUMN_2_XPATH = ".//div[contains(@class, 'EstimatePrint__product__col')][2]";
    @WebFindBy(xpath = COLUMN_2_XPATH + "/p[1]")
    Element quantity;

    @WebFindBy(xpath = COLUMN_2_XPATH + "/p[2]")
    Element nds;

    @WebFindBy(xpath = COLUMN_2_XPATH + "/p[3]")
    Element price;

    @WebFindBy(xpath = COLUMN_2_XPATH + "/p[4]")
    Element totalPriceWithNDS;

    @Override
    public EstimatePrintProductData collectDataFromPage() {
        EstimatePrintProductData estimatePrintProductData = new EstimatePrintProductData();
        estimatePrintProductData.setTitle(title.getText());
        estimatePrintProductData.setLmCode(lmCode.getText());
        estimatePrintProductData.setQuantity(quantity.getText());
        estimatePrintProductData.setPercentNDS(nds.getText());
        estimatePrintProductData.setPrice(price.getText());
        estimatePrintProductData.setTotalPriceWithNDS(totalPriceWithNDS.getText());
        return estimatePrintProductData;
    }
}
