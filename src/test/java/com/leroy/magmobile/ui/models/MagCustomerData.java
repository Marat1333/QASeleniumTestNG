package com.leroy.magmobile.ui.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MagCustomerData {
    private String name;
    private String phone;
    private String cardNumber;
    private String cardType;
    private String email;
}
