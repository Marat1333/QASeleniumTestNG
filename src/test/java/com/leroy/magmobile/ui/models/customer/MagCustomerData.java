package com.leroy.magmobile.ui.models.customer;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MagCustomerData {
    private String name;
    private String phone;
    private String cardNumber;
    private String cardType;
    private String email;

    public MagCustomerData clone() {
        MagCustomerData clone = new MagCustomerData().toBuilder()
                .name(name)
                .cardNumber(cardNumber)
                .cardType(cardType)
                .phone(phone)
                .email(email)
                .build();
        return clone;
    }

    private boolean existedClient; // Ранее существующий в системе клиент, который может быть найден через поиск (а не ново созданный через оформление заказа)
    public void assertEqualsNotNullExpectedFields(MagCustomerData expectedCustomerData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedCustomerData.getName() != null) {
            softAssert.isEquals(name, expectedCustomerData.getName(),
                    "Неверное имя клиента");
        }
        if (expectedCustomerData.getPhone() != null) {
            softAssert.isEquals(phone, expectedCustomerData.getPhone(),
                    "Неверный телефон клиента");
        }
        if (expectedCustomerData.getCardNumber() != null) {
            softAssert.isEquals(cardNumber, expectedCustomerData.getCardNumber(),
                    "Неверный номер карты клиента");
        }
        if (expectedCustomerData.getCardType() != null) {
            softAssert.isEquals(cardType, expectedCustomerData.getCardType(),
                    "Неверный тип карты клиента");
        }
        if (expectedCustomerData.getEmail() != null) {
            softAssert.isEquals(email, expectedCustomerData.getEmail(),
                    "Неверный email клиента");
        }
        softAssert.verifyAll();
    }
}
