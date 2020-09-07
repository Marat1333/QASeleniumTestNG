package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.pages.orders.widget.OrderProductCardWidget;
import com.leroy.magportal.ui.pages.orders.widget.PickingWidget;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import com.leroy.magportal.ui.pages.picking.data.PickingData;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

/**
 * Описывает вкладку "Сборки"
 */
public class AssemblyOrderPage extends OrderCreatedPage{
    @WebFindBy(xpath = "//div[contains(@class, 'lm-puz2-Order-OrderPickingTaskListItem')]", metaName = "Карточка сборки",
    clazz = PickingWidget.class)
    CardWebWidgetList<PickingWidget, PickingData> orderPickingTaskListItem;

    @Step ("Кликнуть на Сборку")
public PickingContentPage clickToPickingTask(int index) throws Exception {
    index--;
    orderPickingTaskListItem.get(index).click();
    return new PickingContentPage();

}
    
}
