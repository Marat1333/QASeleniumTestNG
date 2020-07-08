package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.ElementClickInterceptedException;
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

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__discount-percent')]/span")
    Element discountPercent;

    @WebFindBy(xpath = ".//span[contains(@class, 'SalesDocProduct__content__discount-price')]")
    Element totalPriceWithoutDiscount;

    @WebFindBy(xpath = ".//div[contains(@class, 'Estimate-price')]")
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

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__buttons')]/div[2]//button")
    Element discountBtn;

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
        return price.isVisible() ? price.getText() : totalPriceWithoutDiscount.isVisible() ? getTotalPriceWithoutDiscount() : getTotalPrice();
    }

    public String getTotalPrice() {
        return totalPrice.getText();
    }

    public String getDiscountPercent() {
        return discountPercent.getTextIfPresent();
    }

    public String getTotalPriceWithoutDiscount() {
        return totalPriceWithoutDiscount.getTextIfPresent();
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
        if (totalPriceWithoutDiscount.isVisible()) {
            productOrderCardWebData.setTotalPrice(ParserUtil.strToDouble(getTotalPriceWithoutDiscount()));
            productOrderCardWebData.setTotalPriceWithDiscount(ParserUtil.strToDouble(getTotalPrice()));
        } else
            productOrderCardWebData.setTotalPrice(ParserUtil.strToDouble(getTotalPrice()));
        productOrderCardWebData.setDiscountPercent(ParserUtil.strToDouble(getDiscountPercent()));

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

    public void clickCreateDiscount() {
        discountBtn.click();
    }

    public Color getColorOfAvailableStockLbl() {
        return availableStock.getColor();
    }

    public void clickDelete() {
        try {
            deleteBtn.click();
        } catch (ElementClickInterceptedException err) {
            Log.error(err.getMessage());
            deleteBtn.scrollTo();
            deleteBtn.click();
        }
    }
}
