package com.leroy.magmobile.api.enums;

public enum CorrectionAccessLevels {
    ALL_DEPARTMENTS("60069805", 35, "{\"departmentList\":[\"1\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\"]}"),
    ONE_DEPARTMENT("60069801", 35, "{\"departmentList\":[\"15\"]}"),
    NO_ONE_DEPARTMENT("60069806", 35, "{\"departmentList\":[]}"),
    UNKNOWN_USER("60069999", 35, "{\"error\":\"USER_NOT_FOUND\"}");


    private String ldap;
    private int shopId;
    private String response;

    CorrectionAccessLevels(String ldap, int shopId, String response) {
        this.ldap = ldap;
        this.shopId = shopId;
        this.response = response;
    }

    public String getLdap() {
        return ldap;
    }

    public int getShopId() {
        return shopId;
    }

    public String getResponse() {
        return response;
    }
}
