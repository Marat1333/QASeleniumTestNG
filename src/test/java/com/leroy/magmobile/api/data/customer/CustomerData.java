package com.leroy.magmobile.api.data.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;

@Data
public class CustomerData {
    private String gender;
    private String firstName;
    private String lastName;
    private String customerType;
    private String customerNumber;
    private List<Communication> communications;

    public void generateRandomValidRequiredData(boolean hasCommunication) {
        this.firstName = RandomStringUtils.randomAlphabetic(5);
        this.lastName = RandomStringUtils.randomAlphabetic(6);
        this.gender = new Random().nextInt(2) == 0 ? "male" : "female";
        if (hasCommunication) {
            Communication communication = new Communication();
            communication.generateRandomPhoneNumber();
            this.communications = new ArrayList<>(Collections.singletonList(communication));
        }
    }

    @JsonIgnore
    public String getMainPhoneFromCommunication() {
        for (Communication communication : communications) {
            if (true == communication.getIsMain() && communication.getType().equals( "PHONENUMBER"))
                return communication.getValue();
        }
        return null;
    }
}
