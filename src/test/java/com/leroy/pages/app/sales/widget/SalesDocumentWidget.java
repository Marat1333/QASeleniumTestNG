package com.leroy.pages.app.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.models.SalesDocumentData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SalesDocumentWidget extends Element {

    public SalesDocumentWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public SalesDocumentWidget(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public SalesDocumentWidget(WebDriver driver, WebElement we, CustomLocator locator) {
        super(driver, we, locator);
    }

    @AppFindBy(xpath = ".//android.widget.TextView[1]")
    private Element whereFrom;

    @AppFindBy(xpath = ".//android.widget.TextView[2]")
    private Element price;

    @AppFindBy(xpath = ".//android.widget.TextView[3]")
    private Element number;

    @AppFindBy(xpath = ".//android.widget.TextView[4]")
    private Element pin;

    @AppFindBy(xpath = ".//android.view.ViewGroup[3]/android.widget.TextView[1]")
    private Element date;

    @AppFindBy(xpath = ".//android.view.ViewGroup[1]/android.widget.TextView")
    private Element documentType;

    public SalesDocumentData getSalesDocumentData() {
        SalesDocumentData document = new SalesDocumentData();
        document.setWhereFrom(whereFrom.getText());
        document.setPrice(Double.valueOf(price.getText().replaceAll("₽|\\s", "")));
        document.setNumber(Long.valueOf(number.getText().replaceAll("№|\\s", "")));
        document.setPin(Integer.valueOf(pin.getText().replaceAll("PIN ", "")));
        document.setDate(date.getText());
        document.setDocumentType(documentType.getText());
        return document;
    }

}
