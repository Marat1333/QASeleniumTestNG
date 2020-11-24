package com.leroy.common_mashups.customer_accounts.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CustomerSearchFilters {

    public enum CustomerType {
        NATURAL, LEGAL
    }

    public enum DiscriminantType {
        PHONENUMBER, LOYALTY_CARD_NUMBER
    }

    private CustomerType customerType;
    private DiscriminantType discriminantType;
    private String discriminantValue;
}
