package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
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

    @AppFindBy(containsText = "оступно",
            metaName = "Элемент с информацией о доступном кол-ве")
    Element availableTodayProductCount;

    public String getLmCode(boolean onlyDigits, String ps) {
        if (onlyDigits)
            return lmCode.getText(ps).replaceAll("\\D+", "");
        else
            return lmCode.getText(ps);
    }

    public String getName(String ps) {
        return name.getText(ps);
    }

    public String getProductCount(boolean onlyDigits, String ps) {
        if (onlyDigits)
            return productCount.getText(ps).replaceAll("\\D+", "");
        else
            return productCount.getText(ps);
    }

    public String getPrice(boolean onlyDigits, String ps) {
        if (onlyDigits)
            return price.getText(ps).replaceAll("\\D+", "");
        else
            return price.getText(ps);
    }

    public String getTotalPrice(boolean onlyDigits, String ps) {
        if (onlyDigits)
            return totalPrice.getText(ps).replaceAll("\\D+", "");
        else
            return totalPrice.getText(ps);
    }

    public String getAvailableTodayProductCountLbl(boolean onlyDigits, String ps) {
        if (onlyDigits)
            return availableTodayProductCount.getText(ps).replaceAll("\\D+", "");
        else
            return availableTodayProductCount.getText(ps);
    }
}