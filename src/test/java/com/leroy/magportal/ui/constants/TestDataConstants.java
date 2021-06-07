package com.leroy.magportal.ui.constants;

import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.customer.MagLegalCustomerData;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;

public class TestDataConstants extends EnvConstants {

    public static final SimpleCustomerData SIMPLE_CUSTOMER_DATA_1 = new SimpleCustomerData().toBuilder()
            .phoneNumber(getProperty("data.customer.simple1.phone"))
            .name(getProperty("data.customer.simple1.name"))
            .email(getProperty("data.customer.simple1.email"))
            .build();

    public static final SimpleCustomerData SIMPLE_CUSTOMER_DATA_2 = new SimpleCustomerData().toBuilder()
            .phoneNumber(getProperty("data.customer.simple2.phone"))
            .name(getProperty("data.customer.simple2.name"))
            .email(getProperty("data.customer.simple2.email"))
            .build();

    public static final SimpleCustomerData CUSTOMER_WITH_PROFESSIONAL_CARD = new SimpleCustomerData().toBuilder()
            .phoneNumber("+79160343344")
            .name("Ольга Дядина")
            .email("u910or@gmail.com")
            .cardNumber(getProperty("data.customer.cardNumber.professional"))
            .build();

    public static final SimpleCustomerData CUSTOMER_WITH_SERVICE_CARD = new SimpleCustomerData().toBuilder()
            .phoneNumber("+79265342960")
            .name("Дарья Колтакова")
            .email("darya.koltakova@leroymerlin.ru")
            .cardNumber(getProperty("data.customer.cardNumber.service"))
            .build();

    public static final SimpleCustomerData CORPORATE_CUSTOMER = new SimpleCustomerData().toBuilder()
            .phoneNumber("+78009996655")
            .name("БАНК ЗАРЕЧЬЕ")
            .email("zarechye@bank.ru")
            .cardNumber(getProperty("data.customer.cardNumber.corporate"))
            .type("B2B")
            .build();
}
