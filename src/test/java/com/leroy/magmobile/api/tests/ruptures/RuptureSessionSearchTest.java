package com.leroy.magmobile.api.tests.ruptures;

import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RuptureSessionSearchTest extends BaseProjectApiTest {

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @Test(description = "Search for Rupture session without specific filters")
    public void testSearchForRuptureSessionWithoutSpecificFilters() {
        RupturesClient rupturesClient = apiClientProvider.getRupturesClient();
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions();
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(greaterThan(0)));
        for (ResRuptureSessionData item : items) {
            assertThat("storeId", item.getStoreId(), is(Integer.parseInt(sessionData.getUserShopId())));
            if (item.getStatus().equals("finished"))
                assertThat("finishedOn", item.getFinishedOn(), notNullValue());
            else
                assertThat("finishedOn", item.getFinishedOn(), nullValue());
            assertThat("createdOn", item.getCreatedOn(), notNullValue());
            assertThat("createdByLdap", item.getCreatedByLdap(), not(emptyOrNullString()));
            assertThat("completedProductCount", item.getCompletedProductCount(), notNullValue());
            assertThat("totalProductCount", item.getTotalProductCount(), notNullValue());
            assertThat("userFullName", item.getUserFullName(), not(emptyOrNullString()));
            assertThat("sessionId", item.getSessionId(), greaterThan(0));
            assertThat("status", item.getStatus(), oneOf("finished", "active"));
        }
    }

}
