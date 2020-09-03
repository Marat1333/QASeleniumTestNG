package com.leroy.magportal.api.constants;

import lombok.Data;
import lombok.experimental.Accessors;

public class CardConst {

    @Data
    @Accessors(chain = true)
    public static class CardData {

        public String pan;
        public String month;
        public String year;
        public String cvc;
        public String text;
        public String password;
    }

    public static final CardData VISA_1111 = new CardData()
            .setPan("4111111111111111")
            .setMonth("12")
            .setYear("2024")
            .setCvc("123")
            .setText("MOSKAL")
            .setPassword("12345678");
}
