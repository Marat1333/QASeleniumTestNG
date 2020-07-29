package com.leroy.magportal.ui.models.salesdoc;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShortOrderDocWebData implements IDataWithNumberAndStatus<ShortOrderDocWebData> {
    private String number;
    private String status;
    private String customer;
    private LocalDateTime creationDate;
    private Double totalPrice;
    private String deliveryType;

    @Override
    public void assertEqualsNotNullExpectedFields(ShortOrderDocWebData expectedData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(this, expectedData, "Неверная информация в документе");
        softAssert.verifyAll();
    }
}
