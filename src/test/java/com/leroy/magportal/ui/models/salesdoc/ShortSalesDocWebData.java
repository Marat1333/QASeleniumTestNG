package com.leroy.magportal.ui.models.salesdoc;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

@Data
public class ShortSalesDocWebData implements IDataWithNumberAndStatus<ShortSalesDocWebData> {

    private String number;
    private String status;
    private String author;
    private String creationDate;
    private Double totalPrice;

    public void assertEqualsNotNullExpectedFields(ShortSalesDocWebData expectedDocumentData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedDocumentData.getNumber() != null) {
            softAssert.isEquals(number, expectedDocumentData.getNumber(),
                    "Неверный номер документа");
        }
        if (expectedDocumentData.getStatus() != null) {
            softAssert.isEquals(status, expectedDocumentData.getStatus(),
                    "Неверный статус документа");
        }
        if (expectedDocumentData.getAuthor() != null) {
            softAssert.isEquals(author, expectedDocumentData.getAuthor(),
                    "Неверная автор документа");
        }
        if (expectedDocumentData.getCreationDate() != null) {
            softAssert.isEquals(creationDate, expectedDocumentData.getCreationDate(),
                    "Неверная дата создания документа");
        }
        if (expectedDocumentData.getTotalPrice() != null) {
            softAssert.isEquals(totalPrice, expectedDocumentData.getTotalPrice(),
                    "Неверная итого стоимость документа");
        }
        softAssert.verifyAll();
    }
}
