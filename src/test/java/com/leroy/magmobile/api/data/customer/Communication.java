package com.leroy.magmobile.api.data.customer;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

@Data
public class Communication {

    private Long id;
    private String type;
    private String value;
    private Boolean isMain;
    private Integer communicationOrder;
    private Integer phoneType;
    private Boolean goneAway;

    public void generateRandomPhoneNumber() {
        this.type = "PHONENUMBER";
        this.value = "+7" + RandomStringUtils.randomNumeric(10);
        this.isMain = true;
    }

}
