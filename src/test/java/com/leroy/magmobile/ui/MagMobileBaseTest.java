package com.leroy.magmobile.ui;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.UserSessionData;
import com.leroy.core.api.Module;
import com.leroy.core.configuration.BaseUiTest;
import com.leroy.umbrella_extension.authorization.AuthClient;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

@Guice(modules = {Module.class})
public class MagMobileBaseTest extends BaseUiTest {

    @Inject
    private AuthClient authClient;

    private String accessToken;

    @Override
    public UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData userSessionData = new UserSessionData();
        userSessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        userSessionData.setUserShopId(EnvConstants.SHOP_WITH_NEW_INTERFACE);
        userSessionData.setUserDepartmentId("1");
        return userSessionData;
    }

    protected boolean isNeedAccessToken() {
        return true;
    }

    protected String getAccessToken() {
        return authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS);
    }

    // Groups

    protected final String OLD_SHOP_GROUP = "old_shop";
    protected final String NEED_ACCESS_TOKEN_GROUP = "need_access_token";

    @BeforeMethod
    protected void setUserSessionDataByGroup(Method method) {
        List<String> groups = Arrays.asList(method.getAnnotation(Test.class).groups());
        UserSessionData userSessionData = getUserSessionData();
        if (groups.contains(NEED_ACCESS_TOKEN_GROUP) || isNeedAccessToken()) {
            userSessionData.setAccessToken(getAccessToken());
        }
        if (groups.contains(OLD_SHOP_GROUP)) {
            userSessionData.setUserShopId(EnvConstants.SHOP_WITH_OLD_INTERFACE);
        }
    }

}
