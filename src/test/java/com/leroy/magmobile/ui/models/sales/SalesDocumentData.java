package com.leroy.magmobile.ui.models.sales;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.utils.ParserUtil;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class SalesDocumentData {
    private String title;
    private String number;
    private LocalDateTime date;
    private String status;
    private String creator;

    private List<OrderAppData> orderAppDataList;

    public void setFieldsFrom(ShortSalesDocumentData shortSalesDocumentData) {
        title = shortSalesDocumentData.getTitle();
        number = shortSalesDocumentData.getNumber();
        date = shortSalesDocumentData.getDate();
        status = shortSalesDocumentData.getDocumentState();
    }

    public void removeOrder(int index) {
        orderAppDataList.remove(index);
        if (orderAppDataList.size() == 1) {
            OrderAppData orderAppData = orderAppDataList.get(0);
            orderAppData.setOrderIndex(null);
            orderAppData.setOrderMaxCount(null);
            orderAppData.setProductCountTotal(null);
        }
    }

    public void consolidateOrders() {
        OrderAppData newOrder = new OrderAppData();
        newOrder.setDate(LocalDate.now().plusDays(14));
        double totalWeight = 0.0;
        double totalPrice = 0.0;
        List<ProductOrderCardAppData> products = new ArrayList<>();
        for (OrderAppData orderAppData : orderAppDataList) {
            totalWeight = ParserUtil.plus(totalWeight, orderAppData.getTotalWeight(), 2);
            totalPrice = ParserUtil.plus(totalPrice, orderAppData.getTotalPrice(), 2);
            products.addAll(orderAppData.getProductCardDataList());
        }
        newOrder.setTotalWeight(totalWeight);
        newOrder.setTotalPrice(totalPrice);
        newOrder.setProductCardDataList(products);
        newOrder.setProductCount(products.size());
        orderAppDataList = Collections.singletonList(newOrder);
    }

    public void assertEqualsNotNullExpectedFields(SalesDocumentData expectedSalesDocumentData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedSalesDocumentData.getTitle() != null) {
            softAssert.isEquals(title, expectedSalesDocumentData.getTitle(),
                    "Неверное название документа");
        }
        if (expectedSalesDocumentData.getNumber() != null) {
            softAssert.isEquals(number, expectedSalesDocumentData.getNumber(),
                    "Неверный номер документа");
        }
        if (expectedSalesDocumentData.getDate() != null) {
            softAssert.isEquals(date, expectedSalesDocumentData.getDate(),
                    "Неверная дата создания документа");
        }
        if (expectedSalesDocumentData.getStatus() != null) {
            softAssert.isEquals(status, expectedSalesDocumentData.getStatus(),
                    "Неверный статус документа");
        }
        if (expectedSalesDocumentData.getCreator() != null) {
            softAssert.isEquals(creator, expectedSalesDocumentData.getCreator(),
                    "Неверный автор документа");
        }
        softAssert.verifyAll();
        softAssert.isEquals(orderAppDataList.size(), expectedSalesDocumentData.getOrderAppDataList().size(),
                "Неверное кол-во заказов в документе");
        softAssert.verifyAll();
        for (int i = 0; i < expectedSalesDocumentData.getOrderAppDataList().size(); i++) {
            orderAppDataList.get(i).assertEqualsNotNullExpectedFields(
                    expectedSalesDocumentData.getOrderAppDataList().get(i));
        }

        softAssert.verifyAll();
    }
}
