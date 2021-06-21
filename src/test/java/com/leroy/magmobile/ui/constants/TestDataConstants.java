package com.leroy.magmobile.ui.constants;

import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.ui.models.customer.MagCustomerData;
import com.leroy.magmobile.ui.models.customer.MagLegalCustomerData;

public class TestDataConstants extends EnvConstants {

    public static final MagCustomerData CUSTOMER_DATA_1 = new MagCustomerData().toBuilder()
            .phone(getProperty("lego.customer.simple1.phone"))
            .name(getProperty("lego.customer.simple1.name"))
            .email(getProperty("lego.customer.simple1.email"))
            .build();

    public static final MagCustomerData CUSTOMER_DATA_2 = new MagCustomerData().toBuilder()
            .phone(getProperty("lego.customer.simple2.phone"))
            .name(getProperty("lego.customer.simple2.name"))
            .email(getProperty("lego.customer.simple2.email"))
            .build();

    public static final MagLegalCustomerData LEGAL_ENTITY_1 = new MagLegalCustomerData().toBuilder()
            .orgName("\"БАНК ЗАРЕЧЬЕ\" (АО)")
            .orgPhone("+78009996655")
            .contractNumber("000012947")
            .chargePerson(new MagCustomerData().toBuilder()
                    .name("Коротких, Александр Абрамович")
                    .phone("+79653776820")
                    .build())
            .build();

    public static final MagLegalCustomerData LEGAL_ENTITY_2 = new MagLegalCustomerData().toBuilder()
            .orgName("Общество с ограниченной ответственностью \"Энерго-Комплект\"")
            .orgCard(getProperty("lego.customer.cardNumber.corporate"))
            .build();

    public static final MagCustomerData CUSTOMER_WITH_PROFESSIONAL_CARD = new MagCustomerData().toBuilder()
            .phone("+79160343344")
            .name("Ольга Дядина")
            .email("u910or@gmail.com")
            .cardNumber(getProperty("lego.customer.cardNumber.professional"))
            .build();

    public static final MagCustomerData CUSTOMER_WITH_SERVICE_CARD = new MagCustomerData().toBuilder()
            .phone("+79265342960")
            .name("Дарья Колтакова")
            .email("darya.koltakova@leroymerlin.ru")
            .cardNumber(getProperty("lego.customer.cardNumber.service"))
            .build();
}
