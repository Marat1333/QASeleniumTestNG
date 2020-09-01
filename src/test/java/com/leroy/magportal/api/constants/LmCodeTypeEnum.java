package com.leroy.magportal.api.constants;

public enum LmCodeTypeEnum {
  KK("11795347"),
  PVZ("80120442");

  private String value;

  LmCodeTypeEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
