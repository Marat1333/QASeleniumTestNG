package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.sales.ProductOrderCardAppData;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

/**
 * Карточка товара внутри заказа
 * Экраны: Корзина, Смета и т.д.
 */
public class ProductOrderCardAppWidget extends CardWidget<ProductOrderCardAppData> {

    public ProductOrderCardAppWidget(WebDriver driver, CustomLocator locator) {
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

    @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, 'Скидка')]", metaName = "Скидка %")
    Element discountPercent;

    @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, 'Скидка')]/following-sibling::android.widget.TextView",
            metaName = "Итого стоимость с учетом скидки")
    Element totalPriceWithDiscount;

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

    public String getTotalPriceWithDiscount(String ps) {
        return totalPriceWithDiscount.getTextIfPresent(ps);
    }

    public String getDiscountPercent(String ps) {
        return discountPercent.getTextIfPresent(ps);
    }

    public Integer getAvailableTodayProductQuantity(String ps) {
        String val = availableTodayProductCount.getTextIfPresent(ps);
        if (val == null)
            return null;
        else
            return ParserUtil.strToInt(val);
    }

    @Override
    public ProductOrderCardAppData collectDataFromPage(String ps) {
        ProductOrderCardAppData cardData = new ProductOrderCardAppData();
        cardData.setLmCode(getLmCode(true, ps));
        cardData.setTitle(getName(ps));
        cardData.setPrice(ParserUtil.strToDouble(getPrice(ps)));
        cardData.setPriceUnit(getPriceUnit(ps));
        cardData.setSelectedQuantity(ParserUtil.strToDouble(getSelectedProductQuantity(ps)));
        cardData.setTotalPrice(ParserUtil.strToDouble(getTotalPrice(ps)));
        cardData.setDiscountPercent(ParserUtil.strToDouble(getDiscountPercent(ps)));
        cardData.setTotalPriceWithDiscount(ParserUtil.strToDouble(getTotalPriceWithDiscount(ps)));

        // Доступное кол-во отображается не всегда, и находится внизу карточки.
        // Нужно придумывать способ как реализовывать метод isFullyVisible или искать workaround
        cardData.setAvailableTodayQuantity(getAvailableTodayProductQuantity(ps));
        return cardData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return lmCode.isVisible(pageSource) && totalPrice.isVisible(pageSource);
    }
}