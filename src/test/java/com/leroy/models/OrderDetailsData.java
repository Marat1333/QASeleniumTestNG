package com.leroy.models;

import com.leroy.core.BaseModel;
import org.apache.commons.lang3.RandomStringUtils;

public class OrderDetailsData extends BaseModel {

    public enum DeliveryType {
        PICKUP("Самовывоз"), DELIVERY("Доставка");

        String value;

        DeliveryType(String val) {
            this.value = val;
        }

        public String getValue() {
            return value;
        }
    }

    private DeliveryType deliveryType;
    private String fullName;
    private String phone;
    private String email;
    private String pinCode;
    private String comment;

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone(boolean isFormatted) {
        if (!isFormatted)
            return phone;
        else {
            if (phone.length() != 10)
                throw new IllegalArgumentException("Неправильное кол-во цифр. Ожидалось 10 цифр");
            return String.format("+7 %s %s-%s-%s",
                    phone.substring(0, 3), phone.substring(3,6),
                    phone.substring(6, 8), phone.substring(8, 10));
        }
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public OrderDetailsData setRequiredRandomData() {
        this.fullName = String.format("%s %s", RandomStringUtils.randomAlphanumeric(6),
                RandomStringUtils.randomAlphabetic(6));
        this.phone = RandomStringUtils.randomNumeric(10);
        String generatedPinCode;
        do {
            generatedPinCode = RandomStringUtils.randomNumeric(5);
        } while (generatedPinCode.startsWith("9"));
        this.pinCode = generatedPinCode;
        return this;
    }
}
