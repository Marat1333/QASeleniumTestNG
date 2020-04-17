package com.leroy.magmobile.api.data.notification;

import lombok.Data;

@Data
public class NotificationCustomerData {
    private String customerNumber;
    private String name;
    private String surname;
    private String email;
    private String primaryPhone;
}
