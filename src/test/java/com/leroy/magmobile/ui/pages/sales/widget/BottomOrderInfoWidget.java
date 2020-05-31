package com.leroy.magmobile.ui.pages.sales.widget;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.models.sales.OrderAppData;
import com.leroy.magmobile.ui.pages.common.widget.CardWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

/**
 * Нижняя информация о заказе: Вес, кол-во товара, итого стоимость
 */
public class BottomOrderInfoWidget extends CardWidget<OrderAppData> {

    public BottomOrderInfoWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = ".//android.widget.TextView[contains(@text, 'Итого:')]/following-sibling::android.widget.TextView")
    Element totalPriceVal;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text, 'Итого:')]/preceding-sibling::android.widget.TextView",
            metaName = "Текст с количеством и весом товара")
    Element countAndWeightProductLbl;

    public int getProductCount(String ps) {
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText(ps).split("•");
        return ParserUtil.strToInt(actualCountProductAndWeight[0]);
    }

    public Double getTotalWeight(String ps) {
        String[] actualCountProductAndWeight = countAndWeightProductLbl.getText(ps).split("•");
        return ParserUtil.strToDouble(actualCountProductAndWeight[1]);
    }

    public Double getTotalPrice(String ps) {
        return ParserUtil.strToDouble(totalPriceVal.getText(ps));
    }

    @Override
    public OrderAppData collectDataFromPage(String ps) {
        OrderAppData orderAppData = new OrderAppData();
        orderAppData.setProductCount(getProductCount(ps));
        orderAppData.setTotalWeight(getTotalWeight(ps));
        orderAppData.setTotalPrice(getTotalPrice(ps));
        return orderAppData;
    }

    @Override
    public boolean isFullyVisible(String pageSource) {
        return totalPriceVal.isVisible(pageSource);
    }
}
