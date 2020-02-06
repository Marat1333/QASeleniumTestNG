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

    @AppFindBy(containsText = "Сегодня доступно")
    Element availableTodayProductCount;

    public String getLmCode(boolean onlyDigits) {
        if (onlyDigits)
            return lmCode.getText().replaceAll("\\D+", "");
        else
            return lmCode.getText();
    }

    public String getName() {
        return name.getText();
    }

    public String getProductCount(boolean onlyDigits) {
        if (onlyDigits)
            return productCount.getText().replaceAll("\\D+", "");
        else
            return productCount.getText();
    }

    public String getPrice(boolean onlyDigits) {
        if (onlyDigits)
            return price.getText().replaceAll("\\D+", "");
        else
            return price.getText();
    }

    public String getTotalPrice(boolean onlyDigits) {
        if (onlyDigits)
            return totalPrice.getText().replaceAll("\\D+", "");
        else
            return totalPrice.getText();
    }

    public String getAvailableTodayProductCountLbl(boolean onlyDigits) {
        if (onlyDigits)
            return availableTodayProductCount.getText().replaceAll("\\D+", "");
        else
            return availableTodayProductCount.getText();
    }
}