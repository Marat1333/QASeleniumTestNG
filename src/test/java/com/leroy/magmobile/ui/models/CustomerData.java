package com.leroy.magmobile.ui.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class CustomerData extends CardWidgetData {
    private String name;
    private String phone;
    private String cardNumber;
    private String cardType;
    private String email;
}
