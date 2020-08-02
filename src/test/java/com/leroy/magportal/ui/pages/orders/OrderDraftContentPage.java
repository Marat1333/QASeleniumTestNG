package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.orders.widget.OrderDraftProductCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.Collections;

public class OrderDraftContentPage extends OrderDraftPage {

    @WebFindBy(xpath = "//div[contains(@class, 'CreationProductCard')]",
            clazz = OrderDraftProductCardWidget.class)
    CardWebWidgetList<OrderDraftProductCardWidget, ProductOrderCardWebData> productCards;

    // Grab data

    @Step("Получить информацию о заказе с вкладки 'Содержание'")
    public SalesDocWebData getOrderData() throws Exception {
        SalesDocWebData salesDocWebData = new SalesDocWebData();
        salesDocWebData.setCreationDate(creationDate.getText());
        salesDocWebData.setAuthorName(author.getText());
        salesDocWebData.setNumber(ParserUtil.strWithOnlyDigits(orderNumber.getText()));
        salesDocWebData.setStatus(orderStatus.getText());
        OrderWebData orderWebData = new OrderWebData();
        orderWebData.setTotalPrice(ParserUtil.strToDouble(orderTotalPrice.getText()));
        orderWebData.setProductCount(getProductCount());
        orderWebData.setTotalWeight(getTotalWeight());
        orderWebData.setProductCardDataList(productCards.getDataList());
        salesDocWebData.setOrders(Collections.singletonList(orderWebData));
        return salesDocWebData;
    }

    // Verifications
    @Step("Проверить, что данные заказа соответствуют ожидаемому")
    public OrderDraftContentPage shouldOrderContentDataIs(SalesDocWebData expectedOrderData) throws Exception {
        SalesDocWebData actualData = getOrderData();
        expectedOrderData.setPinCode(null);
        expectedOrderData.setCreationDate(null); // TODO Баг с Invalid date
        expectedOrderData.getOrders().get(0).getProductCardDataList().forEach(p -> p.setBarCode(null));
        // Не понятно как проверять вес, когда он в корзине отображается суммарный, а в заказе за штуку:
        expectedOrderData.getOrders().get(0).getProductCardDataList().forEach(p -> p.setWeight(null));
        actualData.assertEqualsNotNullExpectedFields(expectedOrderData);
        return this;
    }

}
