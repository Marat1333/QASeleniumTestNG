package com.leroy.magmobile.ui.pages.customers.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PhoneUiData {

    public PhoneUiData(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String phoneNumber;
    private Boolean isMain;
    private Type type;

    public enum Type {
        MAIN, PERSONAL, WORK;
    }

}
