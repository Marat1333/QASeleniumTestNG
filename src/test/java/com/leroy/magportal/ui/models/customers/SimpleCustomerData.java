package com.leroy.magportal.ui.models.customers;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

@Data
public class SimpleCustomerData {
    private String name;
    private String phoneNumber;
    private String email;

    public void generateRandomData() {
        this.name = RandomStringUtils.randomAlphanumeric(5) + " " +
                RandomStringUtils.randomAlphanumeric(5);
        this.phoneNumber = "+7" + RandomStringUtils.randomNumeric(10);
        this.email = RandomStringUtils.randomAlphanumeric(5) + "@autotest.com";
    }
}
