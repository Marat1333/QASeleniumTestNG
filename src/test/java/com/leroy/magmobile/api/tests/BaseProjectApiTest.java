package com.leroy.magmobile.api.tests;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.UserSessionData;
import com.leroy.core.api.Module;
import com.leroy.core.configuration.BaseTest;
import com.leroy.magportal.api.helpers.ShopsHelper;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.testng.annotations.Guice;
import ru.leroymerlin.qa.core.clients.base.Response;

@Guice(modules = {Module.class})
public class BaseProjectApiTest extends BaseTest {

    @Inject
    private AuthClient authClient;
    @Inject
    private ShopsHelper shopsHelper;

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData userSessionData = new UserSessionData();
        userSessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        userSessionData.setUserShopId(EnvConstants.SHOP_WITH_NEW_INTERFACE);
        userSessionData.setRegionId(shopsHelper.getRegionIdByShopId(Integer.parseInt(EnvConstants.BASIC_USER_SHOP_ID)).toString());
        userSessionData.setUserDepartmentId("1");
        if (isNeedAccessToken()) {
            userSessionData.setAccessToken(getAccessToken());
        }
        return userSessionData;
    }

    protected String getAccessToken() {
        return authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
    }

    protected boolean isNeedAccessToken() {
        return true;
    }

    protected void isResponseOk(Response<?> response) {
        assertThat(response, successful());
    }

}
