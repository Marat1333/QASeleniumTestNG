package com.leroy.magmobile.ui.models.sales;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

import java.util.List;

@Data
public class SalesDocumentData {
    private String title;
    private String number;
    private String date;
    private String documentState;

    private List<OrderAppData> orderAppDataList;

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
        if (expectedSalesDocumentData.getDocumentState() != null) {
            softAssert.isEquals(documentState, expectedSalesDocumentData.getDocumentState(),
                    "Неверный статус документа");
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
