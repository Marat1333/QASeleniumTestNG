package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.Color;

public class ProductOrderCardPuzWidget extends CardWebWidget<ProductOrderCardWebData> {

    public ProductOrderCardPuzWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    @WebFindBy(xpath = ".//div[span[normalize-space(.)='ЛМ']]/div/span[not(.='')]")
    Element lmCode;

    @WebFindBy(xpath = ".//div[@id='barCodeButton']//span")
    Element barCode;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__title')]//p")
    Element title;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__header')]/div[contains(@class, 'lmui-View')][3]/span[2]")
    Element weight;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__price')]/span")
    Element price;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__price')]//span")
    Element totalPrice;

    @WebFindBy(xpath = ".//span[@id='inputCounterDecrementButton']/div")
    EditBox minusQuantityBtn;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__qty__input-counter')]//input")
    EditBox quantityFld;

    @WebFindBy(xpath = ".//span[@id='inputCounterIncrementButton']/div")
    EditBox plusQuantityBtn;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__available')]//span")
    Element availableStock;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__buttons')]/div[1]//button")
    Element copyBtn;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__buttons')]/div[last()]//button")
    Element deleteBtn;

    public String getLmCode() {
        return lmCode.getText();
    }

    public String getBarCode() {
        return barCode.getText();
    }

    public String getTitle() {
        return title.getText();
    }

    public String getWeight() {
        return weight.getText();
    }

    public String getPrice() {
        return price.isVisible() ? price.getText() : getTotalPrice();
    }

    public String getTotalPrice() {
        return totalPrice.getText();
    }

    public String getQuantity() {
        return quantityFld.getText();
    }

    public String getAvailableStock() {
        return availableStock.getText();
    }

    @Override
    public ProductOrderCardWebData collectDataFromPage() {
        ProductOrderCardWebData productOrderCardWebData = new ProductOrderCardWebData();
        productOrderCardWebData.setLmCode(getLmCode());
        productOrderCardWebData.setBarCode(ParserUtil.strWithOnlyDigits(getBarCode()));
        productOrderCardWebData.setTitle(getTitle());
        productOrderCardWebData.setWeight(ParserUtil.strToDouble(getWeight()));
        productOrderCardWebData.setPrice(ParserUtil.strToDouble(getPrice()));
        productOrderCardWebData.setTotalPrice(ParserUtil.strToDouble(getTotalPrice()));
        productOrderCardWebData.setSelectedQuantity(ParserUtil.strToDouble(getQuantity()));
        productOrderCardWebData.setAvailableTodayQuantity(ParserUtil.strToDouble(getAvailableStock()));
        return productOrderCardWebData;
    }

    public void editQuantity(Number value) {
        quantityFld.clear(true);
        quantityFld.fill(String.valueOf(value.intValue()));
        quantityFld.sendBlurEvent();
    }

    public void clickPlusQuantity() {
        plusQuantityBtn.click();
    }

    public void clickMinusQuantity() {
        minusQuantityBtn.click();
    }

    public void clickCopy() {
        copyBtn.click();
    }

    public Color getColorOfAvailableStockLbl() {
        return availableStock.getColor();
    }

    public void clickDelete() {
        deleteBtn.click();
    }
}
