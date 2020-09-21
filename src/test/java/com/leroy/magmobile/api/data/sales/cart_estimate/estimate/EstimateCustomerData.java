package com.leroy.magmobile.api.data.sales.cart_estimate.estimate;

import com.leroy.common_mashups.data.customer.PhoneData;
import lombok.Data;

import java.util.List;

@Data
public class EstimateCustomerData {
    private String customerNumber;
    private String firstName;
    private String lastName;
    private String type;
    private List<String> roles;
    private PhoneData phone;
    private String email;
}
