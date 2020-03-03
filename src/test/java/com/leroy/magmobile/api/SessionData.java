package com.leroy.magmobile.api;

import lombok.Data;

@Data
public class SessionData {
    private String accessToken;
    private String userLdap;
    private String userShopId;
    private String regionId;
    private String userDepartmentId;
}
