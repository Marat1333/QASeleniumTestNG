package com.leroy.common_mashups.data.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public class CustomerData {
    private String gender;
    private String firstName;
    private String lastName;
    private String customerType;
    private String customerNumber;
    private List<Communication> communications;

    public void generateRandomValidRequiredData(boolean hasPhoneCommunication) {
        generateRandomValidRequiredData(hasPhoneCommunication, false);
    }

    public void generateRandomValidRequiredData(boolean hasPhoneCommunication, boolean hasEmailCommunication) {
        this.firstName = "Auto" + RandomStringUtils.randomAlphabetic(5);
        this.lastName = "Auto" + RandomStringUtils.randomAlphabetic(6);
        this.gender = new Random().nextInt(2) == 0 ? "male" : "female";

        List<Communication> tmpCommunicationList = new ArrayList<>();
        if (hasPhoneCommunication) {
            Communication phoneCommunication = new Communication();
            phoneCommunication.generateRandomPhoneNumber();
            tmpCommunicationList.add(phoneCommunication);
        }
        if (hasEmailCommunication) {
            Communication emailCommunication = new Communication();
            emailCommunication.generateRandomEmail();
            tmpCommunicationList.add(emailCommunication);
        }
        if (tmpCommunicationList.size() > 0)
            this.communications = tmpCommunicationList;
    }

    @JsonIgnore
    public String getMainPhoneFromCommunication() {
        for (Communication communication : communications) {
            if (true == communication.getIsMain() && communication.getType().equals("PHONENUMBER"))
                return communication.getValue();
        }
        return null;
    }

    @JsonIgnore
    public String getMainEmailFromCommunication() {
        for (Communication communication : communications) {
            if (true == communication.getIsMain() && communication.getType().equals("EMAIL"))
                return communication.getValue();
        }
        return null;
    }
}
