package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.sales.OrderAppData;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;

/**
 * Верхняя информация о заказе (Заказ # из #), Дата исполнения заказа и кол-во товаров в заказе
 */
public class HeaderOrderInfoWidget extends CardWidget<OrderAppData> {

    public HeaderOrderInfoWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(containsText = "Заказ")
    Element orderIndexTotalLbl;

    @AppFindBy(containsText = "Товаров")
    Element productCount;

    @AppFindBy(accessibilityId = "Badge-Text", metaName = "Дата исполнения заказа")
    Element date;

    /**
     * Получить индекс текущего заказа (Заказ # из ... )
     */
    public int getOrderIndex(String ps) {
        String str = orderIndexTotalLbl.getText(ps);
        return ParserUtil.strToInt(StringUtils.substringBetween(str, "Заказ", "из"));
    }

    /**
     * Получить общее кол-во заказов (Заказ ... из # )
     */
    public int getOrderTotalCount(String ps) {
        String str = orderIndexTotalLbl.getText(ps);
        return ParserUtil.strToInt(StringUtils.substringAfter(str, "из"));
    }

    /**
     * Получить дату исполнения заказа
     */
    public LocalDate getDate(String ps) {
        return DateTimeUtil.strToLocalDate(date.getText(ps), "dd MMM");
    }

    /**
     * Получить кол-во товаров в теущем заказе
     */
    public int getProductCount(String ps) {
        String str = productCount.getText(ps);
        return ParserUtil.strToInt(StringUtils.substringBetween(str, "Товаров:", "из"));
    }

    /**
     * Получить общее кол-во товаров в документе (корзине)
     */
    public int getProductCountTotal(String ps) {
        String str = productCount.getText(ps);
        return ParserUtil.strToInt(StringUtils.substringAfter(str, "из"));
    }

    @Override
    public OrderAppData collectDataFromPage(String ps) {
        OrderAppData orderAppData = new OrderAppData();
        orderAppData.setDate(getDate(ps));
        orderAppData.setOrderIndex(getOrderIndex(ps));
        orderAppData.setOrderMaxCount(getOrderTotalCount(ps));
        orderAppData.setProductCount(getProductCount(ps));
        orderAppData.setProductCountTotal(getProductCountTotal(ps));
        return orderAppData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return orderIndexTotalLbl.isVisible(pageSource) && productCount.isVisible(pageSource);
    }

}
