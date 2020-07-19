package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.EstimatePrintProductData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import org.apache.commons.lang3.StringUtils;
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
        estimatePrintProductData.setLmCode(ParserUtil.strWithOnlyDigits(lmCode.getText()));
        estimatePrintProductData.setQuantity(ParserUtil.strToDouble(StringUtils.substringAfter(quantity.getText(), "):")));
        estimatePrintProductData.setPercentNDS(ParserUtil.strToDouble(nds.getText()));
        estimatePrintProductData.setPrice(ParserUtil.strToDouble(price.getText()));
        estimatePrintProductData.setTotalPriceWithNDS(ParserUtil.strToDouble(totalPriceWithNDS.getText()));
        return estimatePrintProductData;
    }
}
