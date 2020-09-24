package com.leroy.common_mashups.data.customer;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

@Data
public class Communication {

    private Long id;
    private String type;
    private String value;
    private Boolean isMain;
    private Integer communicationOrder;
    private String phoneType;
    private String emailType;
    private Boolean goneAway;

    public void generateRandomPhoneNumber() {
        this.phoneType = "1";
        this.type = "PHONENUMBER";
        this.value = "+7" + RandomStringUtils.randomNumeric(10);
        this.isMain = true;
    }

    public void generateRandomEmail() {
        this.emailType = "1";
        this.type = "EMAIL";
        this.value = RandomStringUtils.randomAlphanumeric(5) + "@autotest.com";
        this.isMain = true;
    }

}
