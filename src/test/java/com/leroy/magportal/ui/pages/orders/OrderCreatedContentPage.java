package com.leroy.magportal.ui.pages.orders;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.annotations.Form;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.OrderWebData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.customers.form.CustomerSearchForm;
import com.leroy.magportal.ui.pages.orders.widget.OrderProductCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.Collections;

public class OrderCreatedContentPage extends OrderCreatedPage {

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewHeader__orderId')]//span", metaName = "Номер заказа")
    Element orderNumber;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewHeader__mainInfo')]//div[contains(@class, 'Order-OrderStatus')]//span",
            metaName = "Статус заказа")
    Element orderStatus;

    private final static String ORDER_SUB_HEADER_XPATH = "//div[contains(@class, 'OrderViewHeader')][div[contains(@class, 'OrderViewHeader__mainInfo')]]/div[2]";

    @WebFindBy(xpath = ORDER_SUB_HEADER_XPATH + "/span[1]",
            metaName = "Дата создания")
    Element creationDate;

    @WebFindBy(xpath = ORDER_SUB_HEADER_XPATH + "/span[7]",
            metaName = "Способ получения")
    Element deliveryType;

    @Form
    CustomerSearchForm customerSearchForm;

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - string-length('order-ProductCard') +1) = 'order-ProductCard']",
            clazz = OrderProductCardWidget.class)
    CardWebWidgetList<OrderProductCardWidget, ProductOrderCardWebData> productCards;

    // Total information

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__labeledText')][1]/span[2]", metaName = "Вес заказа")
    Element weight;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__labeledText')][2]/span[2]", metaName = "Габариты заказа")
    Element maxSize;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__labeledText')][3]/span[2]", metaName = "Габариты заказа")
    Element departments;

    @WebFindBy(xpath = "//div[contains(@class, 'OrderViewFooter__totalPrice')]/span[2]", metaName = "Итого стоиммость заказа")
    Element totalPrice;

    // Grab data
    @Step("Получить информацию о заказе с вкладки 'Содержание'")
    public SalesDocWebData getOrderData() throws Exception {
        SalesDocWebData salesDocWebData = new SalesDocWebData();
        salesDocWebData.setNumber(ParserUtil.strWithOnlyDigits(orderNumber.getText()));
        salesDocWebData.setCreationDate(creationDate.getText());
        salesDocWebData.setStatus(orderStatus.getText());
        salesDocWebData.setClient(customerSearchForm.getCustomerData());
        salesDocWebData.setDeliveryType(deliveryType.getText().toLowerCase().equals("самовывоз") ?
                SalesDocumentsConst.GiveAwayPoints.PICKUP : SalesDocumentsConst.GiveAwayPoints.DELIVERY);
        OrderWebData orderWebData = new OrderWebData();
        orderWebData.setTotalPrice(ParserUtil.strToDouble(totalPrice.getText()));
        orderWebData.setTotalWeight(ParserUtil.strToDouble(weight.getText()));
        orderWebData.setProductCardDataList(productCards.getDataList());
        salesDocWebData.setOrders(Collections.singletonList(orderWebData));
        return salesDocWebData;
    }

    // Verifications

    @Step("Проверить, что данные заказа соответствуют ожидаемому")
    public OrderCreatedContentPage shouldOrderContentDataIs(SalesDocWebData expectedOrderData) throws Exception {
        SalesDocWebData actualData = getOrderData();
        expectedOrderData.setAuthorName(null);
        expectedOrderData.setPinCode(null);
        expectedOrderData.setCreationDate(null); // TODO Надо приводить к LocalDate и проверять
        expectedOrderData.getOrders().get(0).getProductCardDataList().forEach(p -> p.setAvailableTodayQuantity(null));
        actualData.getOrders().get(0).setProductCount(actualData.getOrders().get(0).getProductCardDataList().size());
        actualData.assertEqualsNotNullExpectedFields(expectedOrderData);
        return this;
    }

}
