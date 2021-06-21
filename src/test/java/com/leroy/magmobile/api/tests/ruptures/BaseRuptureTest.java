package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.RuptureProductDataList;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import ru.leroymerlin.qa.core.clients.base.Response;

public abstract class BaseRuptureTest extends BaseProjectApiTest {

    @Inject
    protected RupturesClient rupturesClient;
    @Inject
    protected AuthClient authClient;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    protected Integer sessionId;
    protected RuptureProductDataList ruptureProductDataListBody;

    private void deleteSessionAfter() {
        if (sessionId != null) {
            Response<JsonNode> r = rupturesClient.deleteSession(sessionId);
            rupturesClient.assertThatIsUpdatedOrDeleted(r);
        }
    }

    @AfterMethod
    protected void deleteSessionAfterMethod() {
        if (isDeleteSessionAfterEveryMethod()) {
            deleteSessionAfter();
        }
    }

    @AfterClass
    protected void deleteSessionAfterClass() {
        deleteSessionAfter();
    }

    protected boolean isDeleteSessionAfterEveryMethod() {
        return true;
    }

    //PP Ruptures targeted to TEST auth
    protected String getAccessToken() {
        authClient.authClientId = "mag-mobile-test";
        return authClient.getAccessToken(
                EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS,
                "check-token-test",
                "secret",
                "https://t-legoauth-as02.hq.ru.corp.leroymerlin.com"
        );
    }
}
