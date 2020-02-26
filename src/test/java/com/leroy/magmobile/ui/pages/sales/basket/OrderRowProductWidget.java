package com.leroy.magmobile.ui.pages.sales.basket;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.magmobile.models.sales.SalesOrderCardData;
import com.leroy.magmobile.models.search.ProductCardData;
import com.leroy.utils.Converter;
import org.openqa.selenium.WebDriver;

public class OrderRowProductWidget extends CardWidget<SalesOrderCardData> {

    public OrderRowProductWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.widget.TextView[1]", metaName = "ЛМ код")
    Element lmCode;

    @AppFindBy(xpath = ".//android.widget.TextView[2]", metaName = "Название товара")
    Element name;

    @AppFindBy(xpath = ".//android.widget.TextView[3]", metaName = "Кол-во товара")
    Element selectedProductQuantity;

    @AppFindBy(xpath = ".//android.widget.TextView[4]", metaName = "Цена товара")
    Element price;

    @AppFindBy(xpath = ".//android.widget.TextView[5]", metaName = "Итого стоимость")
    Element totalPrice;

    @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, 'оступно')]",
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

    public String getSelectedProductQuantity(String ps) {
        return selectedProductQuantity.getText(ps).replaceAll("[^\\d+\\,]", "");
    }

    public String getPrice(String ps) {
        return price.getText(ps);
    }

    public String getPriceUnit(String ps) {
        return selectedProductQuantity.getText(ps).replaceAll("\\d+| ", "");
    }


    public String getTotalPrice(String ps) {
        return totalPrice.getText(ps);
    }

    public String getAvailableTodayProductQuantity(String ps) {
        return availableTodayProductCount.getTextIfPresent(ps);
    }

    @Override
    public SalesOrderCardData collectDataFromPage(String ps) {
        ProductCardData cardData = new ProductCardData();
        cardData.setLmCode(getLmCode(true, ps));
        cardData.setName(getName(ps));
        cardData.setPrice(Converter.strToDouble(getPrice(ps)));
        cardData.setPriceUnit(getPriceUnit(ps));

        SalesOrderCardData orderCardData = new SalesOrderCardData();
        orderCardData.setProductCardData(cardData);
        orderCardData.setSelectedQuantity(Converter.strToDouble(getSelectedProductQuantity(ps)));
        orderCardData.setTotalPrice(Converter.strToDouble(getTotalPrice(ps)));

        // Доступное кол-во отображается не всегда, и находится внизу карточки.
        // Нужно придумывать способ как реализовывать метод isFullyVisible или искать workaround
        //orderCardData.setAvailableTodayQuantity(Converter.strToDouble(getAvailableTodayProductQuantity(ps)));
        return orderCardData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCode.isVisible(pageSource) && totalPrice.isVisible(pageSource);
    }
}