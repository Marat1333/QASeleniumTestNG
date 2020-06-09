package com.leroy.magmobile.ui.constants;

import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.ui.models.MagCustomerData;

public class TestDataConstants extends EnvConstants {

    public static final MagCustomerData CUSTOMER_DATA_1 = new MagCustomerData().toBuilder()
            .phone(getProperty("data.customer.simple1.phone"))
            .name(getProperty("data.customer.simple1.name"))
            .email(getProperty("data.customer.simple1.email"))
            .build();

    public static final MagCustomerData CUSTOMER_DATA_2 = new MagCustomerData().toBuilder()
            .phone(getProperty("data.customer.simple2.phone"))
            .name(getProperty("data.customer.simple2.name"))
            .email(getProperty("data.customer.simple2.email"))
            .build();

    public static final MagCustomerData CUSTOMER_WITH_PROFESSIONAL_CARD = new MagCustomerData().toBuilder()
            .phone("+79160343344")
            .name("Ольга Дядина")
            .email("u910or@gmail.com")
            .cardNumber(getProperty("data.customer.cardNumber.professional"))
            .build();

    public static final MagCustomerData CUSTOMER_WITH_SERVICE_CARD = new MagCustomerData().toBuilder()
            .phone("+79265342960")
            .name("Дарья Колтакова")
            .email("darya.koltakova@leroymerlin.ru")
            .cardNumber(getProperty("data.customer.cardNumber.service"))
            .build();
}
