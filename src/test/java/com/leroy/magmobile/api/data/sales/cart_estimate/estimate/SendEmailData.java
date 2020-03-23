package com.leroy.magmobile.api.data.sales.cart_estimate.estimate;

import lombok.Data;

import java.util.List;

@Data
public class SendEmailData {
    private String shopName;
    private String shopAddress;
    List<String> emails;
}
