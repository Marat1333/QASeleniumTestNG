package com.leroy.magmobile.ui.pages.sales.product_card.widgets;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class SalesHistoryWidget extends BaseWidget {
    public SalesHistoryWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @AppFindBy(xpath = "./*")
    ElementList<Element> salesPerMonthList;

    public List<Double> grabDataFromWidget() throws Exception{
        List<Double> salesResultList = new ArrayList<>();
        //current year
        for (int i=0; i<salesPerMonthList.getCount();i=i+2) {
            salesResultList.add(ParserUtil.strToDouble(salesPerMonthList.get(i).findChildElement("./android.widget.TextView[2]").getText()));
        }
        //previous year
        for (int i=1;i<salesPerMonthList.getCount();i=i+2){
            salesResultList.add(ParserUtil.strToDouble(salesPerMonthList.get(i).findChildElement("./android.widget.TextView[2]").getText()));
        }
        return salesResultList;
    }

}
