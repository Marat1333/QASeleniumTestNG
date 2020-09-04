package com.leroy.magportal.api.constants;

public enum PaymentTypeEnum {
    CASH("Cash", "POSTPAYMENT"),
    CASH_OFFLINE("Cash", "CASHLINE_DEPOSIT"),
    SBERBANK("Sberbank", "PREPAYMENT"),
    BILL("Bill", "POD_AGENT");

    private final String name;
    private final String mashName;

    PaymentTypeEnum(String name, String mashName) {

        this.name = name;
        this.mashName = mashName;
    }

    public String getName() {
        return name;
    }

    public String getMashName() {
        return mashName;
    }
}
