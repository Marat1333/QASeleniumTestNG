package com.leroy.core;

import lombok.Data;

@Data
public class UserSessionData {
    private String accessToken;
    private String userLdap;
    private String userShopId;
    private String regionId;
    private String userDepartmentId;

    public UserSessionData copy() {
        UserSessionData userSessionData = new UserSessionData();
        userSessionData.setAccessToken(accessToken);
        userSessionData.setUserLdap(userLdap);
        userSessionData.setUserShopId(userShopId);
        userSessionData.setRegionId(regionId);
        userSessionData.setUserDepartmentId(userDepartmentId);
        return userSessionData;
    }
}
