package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardPuzData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.Converter;
import org.openqa.selenium.WebDriver;

public class ProductOrderCardPuzWidget extends CardWebWidget<ProductOrderCardPuzData> {

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

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__price')]//span")
    Element totalPrice;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__qty__input-counter')]//input")
    EditBox quantityFld;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__available')]//span")
    Element availableStock;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__buttons')]/div[1]//button")
    Element copyBtn;

    @WebFindBy(xpath = ".//div[contains(@class, 'SalesDocProduct__content__buttons')]/div[2]//button")
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
    public ProductOrderCardPuzData collectDataFromPage() {
        ProductOrderCardPuzData productOrderCardWebData = new ProductOrderCardPuzData();
        productOrderCardWebData.setLmCode(getLmCode());
        productOrderCardWebData.setBarCode(Converter.strToStrWithoutDigits(getBarCode()));
        productOrderCardWebData.setTitle(getTitle());
        productOrderCardWebData.setWeight(Converter.strToDouble(getWeight()));
        productOrderCardWebData.setTotalPrice(Converter.strToDouble(getTotalPrice()));
        productOrderCardWebData.setSelectedQuantity(Converter.strToDouble(getQuantity()));
        productOrderCardWebData.setAvailableTodayQuantity(Converter.strToDouble(getAvailableStock()));
        return productOrderCardWebData;
    }

    public void clickCopy() {
        copyBtn.click();
    }

    public void clickDelete() {
        deleteBtn.click();
    }
}
