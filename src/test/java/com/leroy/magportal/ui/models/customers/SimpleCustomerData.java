package com.leroy.magportal.ui.models.customers;

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
    private String name;
    private String cardNumber;
    private String phoneNumber;
    private String email;

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
}
