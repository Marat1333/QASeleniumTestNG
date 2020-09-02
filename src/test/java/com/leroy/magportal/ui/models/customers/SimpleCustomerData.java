package com.leroy.magportal.ui.models.customers;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCustomerData {
    private String id;
    private String name;
    private String cardNumber;
    private String phoneNumber;
    private String email;

    public SimpleCustomerData clone() {
        SimpleCustomerData clone = new SimpleCustomerData().toBuilder()
                .name(name)
                .cardNumber(cardNumber)
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
        return clone;
    }

    public void generateRandomData() {
        this.name = RandomStringUtils.randomAlphanumeric(5) + " " +
                RandomStringUtils.randomAlphanumeric(5);
        this.phoneNumber = "+7" + RandomStringUtils.randomNumeric(10);
        this.email = RandomStringUtils.randomAlphanumeric(5) + "@autotest.com";
    }

    public void setName(String val) {
        this.name = val;
    }

    public void setName(String firstName, String lastName) {
        this.name = firstName + " " + lastName;
    }

    public String getFirstPartCardNumber() {
        return cardNumber.substring(0, 7);
    }

    public String getSecondPartCardNumber() {
        return cardNumber.substring(7);
    }

    public void assertEqualsNotNullExpectedFields(SimpleCustomerData expectedData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedData.getName() != null)
            softAssert.isEquals(this.getName(), expectedData.getName(),
                    "Неверное имя клиента");
        if (expectedData.getEmail() != null)
            softAssert.isEquals(this.getEmail(), expectedData.getEmail(),
                    "Неверный email клиента");
        if (expectedData.getCardNumber() != null)
            softAssert.isEquals(this.getCardNumber(), expectedData.getCardNumber(),
                    "Неверный номер карточки клиента");
        if (expectedData.getPhoneNumber() != null)
            softAssert.isEquals(this.getPhoneNumber(), expectedData.getPhoneNumber(),
                    "Неверный номер телефона клиента");
        softAssert.verifyAll();
    }
}
