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
    private PayType payType;

    public enum PayType {
        ONLINE("Онлайн"), OFFLINE("Оффлайн");

        private String title;

        PayType(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    @Override
    public void assertEqualsNotNullExpectedFields(ShortOrderDocWebData expectedData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedData.getNumber() != null) {
            softAssert.isEquals(number, expectedData.getNumber(),
                    "Неверный номер документа");
        }
        if (expectedData.getStatus() != null) {
            softAssert.isEquals(status.toLowerCase(), expectedData.getStatus().toLowerCase(),
                    "Неверный статус документа");
        }
        if (expectedData.getCustomer() != null) {
            softAssert.isEquals(customer, expectedData.getCustomer(),
                    "Неверный клиент у документа");
        }
        if (expectedData.getCreationDate() != null) {
            softAssert.isEquals(creationDate, expectedData.getCreationDate(),
                    "Неверная дата создания документа");
        }
        if (expectedData.getTotalPrice() != null) {
            softAssert.isEquals(totalPrice, expectedData.getTotalPrice(),
                    "Неверная стоимость документа");
        }
        if (expectedData.getDeliveryType() != null) {
            softAssert.isEquals(deliveryType, expectedData.getDeliveryType(),
                    "Неверный тип доставки документа");
        }
        if (expectedData.getPayType() != null) {
            softAssert.isEquals(payType, expectedData.getPayType(),
                    "Неверный тип оплаты документа");
        }
        softAssert.verifyAll();
    }
}
