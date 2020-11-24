package com.leroy.common_mashups.customer_accounts.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhoneData {
    private String primary;
    private String secondary;

    public PhoneData(String primary) {
        this.primary = primary;
    }
}
