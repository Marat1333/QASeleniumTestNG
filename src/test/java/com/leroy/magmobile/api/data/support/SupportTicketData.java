package com.leroy.magmobile.api.data.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

@Data
public class SupportTicketData {
    private String ticketNumber;
    private String description;
    private String ldap;
    private String techDescription;
    private String email;
    private String title;
    private String phone;

    @JsonIgnore
    public void generateRequiredReqData() {
        this.title = RandomStringUtils.randomAlphanumeric(12);
        this.description = RandomStringUtils.randomAlphanumeric(12);
    }
}
