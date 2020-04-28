package com.leroy.magmobile.api.data.customer;

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
