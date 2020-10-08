package com.leroy.magmobile.api.data.sales.orders;

import com.leroy.common_mashups.data.customer.PhoneData;
import lombok.Data;

import java.util.List;

@Data
public class OrderCustomerData {
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String primaryPhone;
    private PhoneData phone;
    private String email;
    private String role;
    private List<String> roles;
    private String type;
    private String serviceCard;
}
