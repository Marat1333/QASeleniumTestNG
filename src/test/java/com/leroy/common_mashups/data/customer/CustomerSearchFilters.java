package com.leroy.common_mashups.data.customer;

import lombok.Data;

@Data
public class CustomerSearchFilters {

    public enum CustomerType {
        NATURAL;
    }

    public enum DiscriminantType {
        PHONENUMBER;
    }

    private CustomerType customerType;
    private DiscriminantType discriminantType;
    private String discriminantValue;
}
