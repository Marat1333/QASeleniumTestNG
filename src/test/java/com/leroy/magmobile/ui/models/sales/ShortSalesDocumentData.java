package com.leroy.magmobile.ui.models.sales;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortSalesDocumentData {

    private String title;
    private Double documentTotalPrice;
    private String number;
    private String pin;
    private LocalDateTime date;
    private String documentState;
    private String customerName;

    public void assertEqualsNotNullExpectedFields(ShortSalesDocumentData expectedData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedData.getTitle() != null) {
            softAssert.isEquals(title, expectedData.getTitle(),
                    "Неверное название документа");
        }
        if (expectedData.getDocumentTotalPrice() != null) {
            softAssert.isEquals(documentTotalPrice, expectedData.getDocumentTotalPrice(),
                    "Неверная сумма документа");
        }
        if (expectedData.getNumber() != null) {
            softAssert.isEquals(number, expectedData.getNumber(),
                    "Неверный номер документа");
        }
        if (expectedData.getPin() != null) {
            softAssert.isEquals(pin, expectedData.getPin(),
                    "Неверный пин код");
        }
        if (expectedData.getDate() != null) {
            softAssert.isEquals(date, expectedData.getDate(),
                    "Неверная дата создания документа");
        }
        if (expectedData.getDocumentState() != null) {
            softAssert.isEquals(documentState, expectedData.getDocumentState(),
                    "Неверное статус документа");
        }
        if (expectedData.getCustomerName() != null) {
            softAssert.isEquals(customerName, expectedData.getCustomerName(),
                    "Неверное имя клиента");
        }
        softAssert.verifyAll();
    }

}
