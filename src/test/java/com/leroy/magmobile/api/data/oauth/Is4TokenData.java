package com.leroy.magmobile.api.data.oauth;

import lombok.Data;

@Data
public class Is4TokenData {
    private String refreshToken;
    private String accessToken;
    private Integer expiresIn;
    private String ldap;
    private String name;
    private String surname;
    private String title;
    private String userEmail;
    private String titleEmail;
    private Integer shopId;
    private String shopName;
    private Integer departmentId;
    private String departmentName;
    private String storeCurrency;
    private Object grants;
    private Object userRoles;
    private Integer shopRegion;
}
