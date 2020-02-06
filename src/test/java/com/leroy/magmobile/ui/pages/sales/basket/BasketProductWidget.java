package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class BasketProductWidget extends Element {

    public BasketProductWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.widget.TextView[1]", metaName = "ЛМ код")
    Element lmCode;

    @AppFindBy(xpath = ".//android.widget.TextView[2]", metaName = "Название товара")
    Element name;

    @AppFindBy(xpath = ".//android.widget.TextView[3]", metaName = "Кол-во товара")
    Element productCount;

    @AppFindBy(xpath = ".//android.widget.TextView[4]", metaName = "Цена товара")
    Element price;

    @AppFindBy(xpath = ".//android.widget.TextView[5]", metaName = "Итого стоимость")
    Element totalPrice;

    public String getLmCode(boolean onlyDigits) {
        if (onlyDigits)
            return lmCode.getText().replaceAll("\\D+", "");
        else
            return lmCode.getText();
    }

    public String getName() {
        return name.getText();
    }

    public String getQuantity(boolean onlyDigits) {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView[3]")).getText();
    }

    public String getQuantityType() {
        return new Element(driver, By.xpath(getXpath() + "/android.widget.TextView[4]")).getText();
    }

}