package com.leroy.models;

import com.leroy.constants.Gender;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class CustomerData {

    private Gender gender;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private boolean isPersonalPhone;
    private boolean isWorkPhone;
    private String email;
    private boolean isPersonalEmail;
    private boolean isWorkEmail;

    private String addressName;
    private String region;
    private String city;
    private String street;
    private String house;
    private String build;
    private String flat;
    private String entrance;
    private String floor;
    private String intercom;

    public CustomerData setRandomRequiredData() {
        int _gender = new Random().nextInt(2);
        if (_gender == 0)
            setGender(Gender.MALE);
        else
            setGender(Gender.FEMALE);
        setFirstName(getRandomCyrillicCharacters(8));
        int _phone = new Random().nextInt(2);
        if (_phone == 0) {
            setPersonalPhone(true);
        } else {
            setWorkPhone(true);
        }
        setPhoneNumber(RandomStringUtils.randomNumeric(10));
        return this;
    }

    // Default Getter and setters
    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isPersonalPhone() {
        return isPersonalPhone;
    }

    public void setPersonalPhone(boolean personalPhone) {
        isPersonalPhone = personalPhone;
    }

    public boolean isWorkPhone() {
        return isWorkPhone;
    }

    public void setWorkPhone(boolean workPhone) {
        isWorkPhone = workPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPersonalEmail() {
        return isPersonalEmail;
    }

    public void setPersonalEmail(boolean personalEmail) {
        isPersonalEmail = personalEmail;
    }

    public boolean isWorkEmail() {
        return isWorkEmail;
    }

    public void setWorkEmail(boolean workEmail) {
        isWorkEmail = workEmail;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getIntercom() {
        return intercom;
    }

    public void setIntercom(String intercom) {
        this.intercom = intercom;
    }

    // Private methods
    public String getRandomCyrillicCharacters(int count) {
        String cyrillicCharacters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        StringBuffer result = new StringBuffer();

        for (int i = 0; i < count; i++) {
            result.append(cyrillicCharacters.charAt(
                    new Random().nextInt(cyrillicCharacters.length())));
        }
        return result.toString();
    }
}
