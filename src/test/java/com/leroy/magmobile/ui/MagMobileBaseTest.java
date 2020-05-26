package com.leroy.magmobile.ui;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.UserSessionData;
import com.leroy.core.configuration.BaseUiTest;
import com.leroy.magmobile.api.ApiClientProvider;
import com.leroy.umbrella_extension.authorization.AuthClient;

public class MagMobileBaseTest extends BaseUiTest {

    @Inject
    protected ApiClientProvider apiClientProvider;
    @Inject
    private AuthClient authClient;

    @Override
    public UserSessionData initUserSessionData() {
        UserSessionData userSessionData = new UserSessionData();
        userSessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        userSessionData.setUserShopId(EnvConstants.SHOP_WITH_NEW_INTERFACE);
        userSessionData.setUserDepartmentId("1");
        if (isNeedAccessToken()) {
            userSessionData.setAccessToken(getAccessToken());
        }
        return userSessionData;
    }

    protected boolean isNeedAccessToken() {
        return false;
    }

    protected String getAccessToken() {
        return authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS);
    }

}
