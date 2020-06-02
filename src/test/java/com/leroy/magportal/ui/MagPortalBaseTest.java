package com.leroy.magportal.ui;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.UserSessionData;
import com.leroy.core.api.Module;
import com.leroy.core.configuration.BaseUiTest;
import com.leroy.magportal.api.ApiClientProvider;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.testng.annotations.Guice;

@Guice(modules = {Module.class})
public class MagPortalBaseTest extends BaseUiTest {

    @Inject
    protected ApiClientProvider apiClientProvider;
    @Inject
    private AuthClient authClient;

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData userSessionData = new UserSessionData();
        userSessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        userSessionData.setUserShopId(EnvConstants.BASIC_USER_SHOP_ID);
        userSessionData.setUserDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);
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
