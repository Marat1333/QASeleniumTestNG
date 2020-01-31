package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.widgets.CardWidget;
import com.leroy.models.ProductCardData;
import org.apache.kafka.common.protocol.types.Field;
import org.openqa.selenium.WebDriver;

public class SearchProductCardWidget extends CardWidget<ProductCardData> {

    public SearchProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./android.widget.TextView[1]")
    private Element lmCodeObj;

    @AppFindBy(xpath = "./android.widget.TextView[2]")
    private Element barCodeObj;

    @AppFindBy(xpath = "./android.widget.TextView[3]")
    private Element nameObj;

    @AppFindBy(xpath = "./android.widget.TextView[4]")
    private Element priceObj;

    @AppFindBy(xpath = "./android.widget.TextView[5]")
    private Element priceLbl;

    @AppFindBy(xpath = "./android.widget.TextView[6]")
    private Element quantityObj;

    @AppFindBy(xpath = "./android.widget.TextView[7]")
    private Element quantityType;

    @AppFindBy(xpath = "./android.widget.TextView[8]")
    private Element quantityLbl;

    public String getLmCode(boolean onlyDigits) {
        if (onlyDigits)
            return lmCodeObj.getText().replaceAll("\\D", "");
        else
            return lmCodeObj.getText();
    }

    public String getBarCode(boolean onlyDigits) {
        if (onlyDigits)
            return barCodeObj.getText().replaceAll(" ", "");
        else
            return barCodeObj.getText();
    }

    public String getName() {
        return nameObj.getText();
    }

    public String getPrice() {
        return priceObj.getText();
    }

    public String getPriceLbl() {
        return priceLbl.getText();
    }

    public String getQuantity(boolean onlyDigits) {
        if (onlyDigits)
            return quantityObj.getText().replaceAll(" ", "");
        else
            return quantityObj.getText();
    }

    public String getQuantityType() {
        return quantityType.getText();
    }

    public String getQuantityLbl() {
        return quantityLbl.getText();
    }

    @Override
    public ProductCardData collectDataFromPage() {
        ProductCardData productCardData = new ProductCardData();
        productCardData.setLmCode(getLmCode(true));
        productCardData.setBarCode(getBarCode(true));
        productCardData.setName(getName());
        productCardData.setPrice(getPrice());
        productCardData.setAvailableQuantity(getQuantity(true));
        return productCardData;
    }

    @Override
    public boolean isFullyVisible() {
        if (lmCodeObj.isVisible()) {
            String check = lmCodeObj.getText().substring(3);
            if (check.matches("\\d+") && quantityLbl.isVisible()) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    @Override
    public boolean isServiceCard(){
        if (lmCodeObj.isVisible()) {
            String check = lmCodeObj.getText().substring(3);
            if (check.matches("\\d+") && check.startsWith("49")) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }
    }
}
