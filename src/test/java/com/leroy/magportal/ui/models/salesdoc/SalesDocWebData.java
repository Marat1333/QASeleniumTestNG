package com.leroy.magportal.ui.models.salesdoc;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.magportal.ui.constants.OrderConst;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.utils.DateTimeUtil;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class SalesDocWebData {
    private String number;
    private String status;
    private String creationDate;
    private String authorName;
    private SimpleCustomerData client;
    private List<OrderWebData> orders;

    // Order Details
    private String pinCode;
    private LocalDate deliveryDate;
    private SalesDocumentsConst.GiveAwayPoints deliveryType;
    private SimpleCustomerData recipient;
    private String comment;

    public SalesDocWebData clone() {
        SalesDocWebData salesDocWebData = new SalesDocWebData();
        salesDocWebData.setNumber(number);
        salesDocWebData.setStatus(status);
        salesDocWebData.setCreationDate(creationDate);
        salesDocWebData.setAuthorName(authorName);
        if (client != null)
            salesDocWebData.setClient(client.clone());
        if (orders != null) {
            List<OrderWebData> cloneOrders = new ArrayList<>();
            for (OrderWebData oneOrderData : orders) {
                cloneOrders.add(oneOrderData.clone());
            }
            salesDocWebData.setOrders(cloneOrders);
        }
        salesDocWebData.setPinCode(pinCode);
        salesDocWebData.setDeliveryDate(deliveryDate);
        salesDocWebData.setDeliveryType(deliveryType);
        if (recipient != null)
            salesDocWebData.setRecipient(recipient.clone());
        salesDocWebData.setComment(comment);
        return salesDocWebData;
    }

    public boolean isDocumentContainsProduct(String lmCode) {
        for (OrderWebData orderWebData : orders) {
            for (ProductOrderCardWebData productOrderCardWebData : orderWebData.getProductCardDataList()) {
                if (productOrderCardWebData.getLmCode().equals(lmCode))
                    return true;
            }
        }
        return false;
    }

    public ShortOrderDocWebData getShortOrderData() {
        ShortOrderDocWebData shortOrderDocWebData = new ShortOrderDocWebData();
        shortOrderDocWebData.setNumber(number);
        shortOrderDocWebData.setStatus(status);
        if (creationDate != null)
            shortOrderDocWebData.setCreationDate(DateTimeUtil.strToLocalDateTime(creationDate, "dd MMM, HH:mm"));
        shortOrderDocWebData.setDeliveryType(deliveryType.equals(SalesDocumentsConst.GiveAwayPoints.PICKUP) ?
                OrderConst.DeliveryType.PICKUP : OrderConst.DeliveryType.DELIVERY_TK); // todo
        shortOrderDocWebData.setTotalPrice(orders.get(0).getTotalPrice());
        if (client != null)
            shortOrderDocWebData.setCustomer(client.getName());
        else
            shortOrderDocWebData.setCustomer("");
        return shortOrderDocWebData;
    }

    public void assertEqualsNotNullExpectedFields(SalesDocWebData expectedData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedData.getNumber() != null)
            softAssert.isEquals(this.getNumber(), expectedData.getNumber(),
                    "Неверный номер документа");
        if (expectedData.getAuthorName() != null)
            softAssert.isEquals(this.getAuthorName(), expectedData.getAuthorName(),
                    "Неверный автора документа");
        if (expectedData.getStatus() != null)
            softAssert.isEquals(this.getStatus().toLowerCase(), expectedData.getStatus().toLowerCase(),
                    "Неверный статус документа");
        if (expectedData.getCreationDate() != null)
            softAssert.isEquals(this.getCreationDate(), expectedData.getCreationDate(),
                    "Неверная дата создания документа");
        if (expectedData.getPinCode() != null)
            softAssert.isEquals(this.getPinCode(), expectedData.getPinCode(),
                    "Неверный PIN код документа");
        if (expectedData.getDeliveryDate() != null)
            softAssert.isEquals(this.getDeliveryDate(), expectedData.getDeliveryDate(),
                    "Неверная дата получения заказа");
        if (expectedData.getDeliveryType() != null)
            softAssert.isEquals(this.getDeliveryType(), expectedData.getDeliveryType(),
                    "Неверный способ получения документа");
        if (expectedData.getClient() != null)
            this.getClient().assertEqualsNotNullExpectedFields(expectedData.getClient());
        if (expectedData.getOrders() != null) {
            softAssert.isEquals(this.getOrders().size(), expectedData.getOrders().size(),
                    "Неверное кол-во заказов в документе");
            softAssert.verifyAll();
            for (int i = 0; i < expectedData.getOrders().size(); i++) {
                this.getOrders().get(i).assertEqualsNotNullExpectedFields(expectedData.getOrders().get(i), i);
            }
        }

        softAssert.verifyAll();
    }

}
