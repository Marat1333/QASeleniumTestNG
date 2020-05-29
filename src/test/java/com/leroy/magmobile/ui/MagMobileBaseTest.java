package com.leroy.magmobile.ui;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.UserSessionData;
import com.leroy.core.configuration.BaseUiTest;
import com.leroy.magmobile.api.ApiClientProvider;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MagMobileBaseTest extends BaseUiTest {

    @Inject
    protected ApiClientProvider apiClientProvider;
    @Inject
    private AuthClient authClient;

    private String accessToken;

    @Override
    public UserSessionData initTestClassUserSessionDataTemplate() {
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

    // Groups

    protected final String OLD_SHOP_GROUP = "old_shop";
    protected final String NEED_ACCESS_TOKEN_GROUP = "need_access_token";

    @BeforeGroups(NEED_ACCESS_TOKEN_GROUP)
    protected void setAccessTokenForSessionData() {
        if (!isNeedAccessToken())
            accessToken = getAccessToken();
    }

    @BeforeMethod
    protected void setUserSessionDataByGroup(Method method) {
        List<String> groups = Arrays.asList(method.getAnnotation(Test.class).groups());
        UserSessionData userSessionData = getUserSessionData();
        if (groups.contains(NEED_ACCESS_TOKEN_GROUP)) {
            userSessionData.setAccessToken(accessToken);
        }
        if (groups.contains(OLD_SHOP_GROUP)) {
            userSessionData.setUserShopId(EnvConstants.SHOP_WITH_OLD_INTERFACE);
        }
    }

}
