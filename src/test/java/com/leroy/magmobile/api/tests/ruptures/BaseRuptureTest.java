package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.RuptureProductDataList;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import ru.leroymerlin.qa.core.clients.base.Response;

public abstract class BaseRuptureTest extends BaseProjectApiTest {

    protected RupturesClient rupturesClient() {
        return apiClientProvider.getRupturesClient();
    }

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    protected Integer sessionId;
    protected RuptureProductDataList ruptureProductDataListBody;

    private void deleteSessionAfter() {
        if (sessionId != null) {
            RupturesClient rupturesClient = rupturesClient();
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

}
