package com.leroy.magmobile.api.data.customer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
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
