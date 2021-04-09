package com.leroy.magportal.ui.models.customers;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
    private String firstName;
    private String lastName;
    private String cardNumber;
    private String phoneNumber;
    private String email;
    private String type;

    public SimpleCustomerData clone() {
        SimpleCustomerData clone = new SimpleCustomerData().toBuilder()
                .id(id)
                .name(name)
                .firstName(firstName)
                .lastName(lastName)
                .cardNumber(cardNumber)
                .phoneNumber(phoneNumber)
                .email(email)
                .type(type)
                .build();
        return clone;
    }

    public void generateRandomData() {
        this.firstName = RandomStringUtils.randomAlphanumeric(5);
        this.lastName = RandomStringUtils.randomAlphanumeric(5);
        this.name = this.firstName + " " + this.lastName;
        this.phoneNumber = "+7" + RandomStringUtils.randomNumeric(10);
        this.email = RandomStringUtils.randomAlphanumeric(5) + "@autotest.com";
    }

    public void setName(String val) {
        this.name = val;
        fillFirstLastNames();
    }

    public void fillFirstLastNames() {
        List<String> names = Arrays.stream(this.name.split(" ")).collect(Collectors.toList());
        this.firstName = names.get(0);
        this.lastName = names.stream().filter(x -> !x.equals(this.firstName)).findFirst()
                .orElse(null);
    }

    public void setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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
        if (expectedData.getName() != null) {
            softAssert.isEquals(this.getName(), expectedData.getName(),
                    "Неверное имя клиента");
        }
        if (expectedData.getEmail() != null) {
            softAssert.isEquals(this.getEmail(), expectedData.getEmail(),
                    "Неверный email клиента");
        }
        if (expectedData.getCardNumber() != null) {
            softAssert.isEquals(this.getCardNumber(), expectedData.getCardNumber(),
                    "Неверный номер карточки клиента");
        }
        if (expectedData.getPhoneNumber() != null) {
            softAssert.isEquals(this.getPhoneNumber(), expectedData.getPhoneNumber(),
                    "Неверный номер телефона клиента");
        }
        softAssert.verifyAll();
    }
}
