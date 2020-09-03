package com.leroy.magportal.ui.webelements;

import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.ElementList;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class CardWebWidgetList<E extends CardWebWidget<D>, D> extends ElementList<E> {

    // Constructors

    public CardWebWidgetList(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public CardWebWidgetList(WebDriver driver, CustomLocator locator, Class<? extends BaseWidget> elementClass) {
        super(driver, locator, elementClass);
    }

    public List<D> getDataList(int limit) throws Exception {
        List<D> dataList = new ArrayList<>();
        for (CardWebWidget<D> cardWidget : this) {
            if (limit <= 0)
                break;
            dataList.add(cardWidget.collectDataFromPage());
            limit--;
        }
        return dataList;
    }

    public List<D> getDataList() throws Exception {
        return getDataList(Integer.MAX_VALUE);
    }


}
