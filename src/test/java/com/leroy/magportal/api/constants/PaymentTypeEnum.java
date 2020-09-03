package com.leroy.magportal.api.constants;

public enum PaymentTypeEnum {
  CASH("Cash"),
  SBERBANK("Sberbank"),
  BILL("Bill");

  private final String name;

  PaymentTypeEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
