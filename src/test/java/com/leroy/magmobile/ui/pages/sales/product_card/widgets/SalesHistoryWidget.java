package com.leroy.magmobile.ui.pages.sales.product_card.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

public class SalesHistoryWidget extends BaseWidget {
    public SalesHistoryWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./*")
    ElementList<Element> salesPerMonthList;

    public List<Double> grabDataFromWidget() throws Exception {
        String dateFormat = "MMM yy";
        TreeMap<LocalDate, Double> dateQuantityMap = new TreeMap<>(Collections.reverseOrder());
        //current year
        for (int i = 0; i < salesPerMonthList.getCount(); i = i + 2) {
            Element current = salesPerMonthList.get(i);
            dateQuantityMap.put(DateTimeUtil.strToLocalDate(current.findChildElement("./android.widget.TextView[@index='0']").getText(), dateFormat),
                    ParserUtil.strToDouble(current.findChildElement("./android.widget.TextView[@index='1']").getText()));
        }
        //previous year
        for (int i = 1; i < salesPerMonthList.getCount(); i = i + 2) {
            Element current = salesPerMonthList.get(i);
            dateQuantityMap.put(DateTimeUtil.strToLocalDate(current.findChildElement("./android.widget.TextView[@index='0']").getText(), dateFormat),
                    ParserUtil.strToDouble(current.findChildElement("./android.widget.TextView[@index='1']").getText()));
        }
        return new ArrayList<>(dateQuantityMap.values());
    }

}
