package com.leroy.magportal.ui.pages.cart_estimate.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.sales.SalesOrderCardData;
import com.leroy.magmobile.ui.models.search.ProductCardData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.Converter;
import org.openqa.selenium.WebDriver;

public class PuzProductOrderCardWidget extends CardWebWidget<SalesOrderCardData> {

    public PuzProductOrderCardWidget(WebDriver driver, CustomLocator customLocator) {
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
    public SalesOrderCardData collectDataFromPage() {
        SalesOrderCardData salesOrderCardData = new SalesOrderCardData();
        ProductCardData productCardData = new ProductCardData();
        productCardData.setLmCode(getLmCode());
        productCardData.setBarCode(Converter.strToStrWithoutDigits(getBarCode()));
        productCardData.setName(getTitle());
        salesOrderCardData.setProductCardData(productCardData);
        salesOrderCardData.setWeight(Converter.strToDouble(getWeight()));
        salesOrderCardData.setTotalPrice(Converter.strToDouble(getTotalPrice()));
        salesOrderCardData.setSelectedQuantity(Converter.strToDouble(getQuantity()));
        salesOrderCardData.setAvailableTodayQuantity(Converter.strToDouble(getAvailableStock()));
        return salesOrderCardData;
    }

    public void clickCopy() {
        copyBtn.click();
    }

    public void clickDelete() {
        deleteBtn.click();
    }
}
