package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RuptureSessionSearchTest extends BaseProjectApiTest {

    // Test constants
    private static final String ACTIVE_STATUS = "active";
    private static final String FINISHED_STATUS = "finished";

    private LinkedHashMap<Integer, String> ruptureStatuses = new LinkedHashMap<>();

    private RupturesClient rupturesClient() {
        return apiClientProvider.getRupturesClient();
    }

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    @BeforeClass
    public void setUp() {
        RupturesClient rupturesClient = rupturesClient();

        // Clear department sessions
        int j = 0;
        while (j < 100) {
            Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(100);
            if (resp.asJson().getTotalCount() == 0) {
                break;
            }
            for (ResRuptureSessionData session : resp.asJson().getItems()) {
                rupturesClient.deleteSession(session.getSessionId());
            }
            j++;
        }

        // Generate test data (8 finished session and 3 active sessions)
        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(getUserSessionData().getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()));

        for (int i = 0; i < 11; i++) {
            Response<JsonNode> resp = rupturesClient.createProduct(rupturePostData);
            Integer sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
            ruptureStatuses.put(sessionId, ACTIVE_STATUS);
            if (i < 3) {
                continue;
            }
            resp = rupturesClient.finishSession(sessionId);
            rupturesClient.assertThatIsUpdatedOrDeleted(resp);
            ruptureStatuses.put(sessionId, FINISHED_STATUS);
        }
    }

    @Test(description = "C3285388 GET ruptures sessions status active")
    public void testSearchForActiveRuptureSessions() {
        RupturesClient rupturesClient = rupturesClient();
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(ACTIVE_STATUS, 10);
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(3)));
        for (ResRuptureSessionData item : items) {
            assertThat("SessionId: " + item.getSessionId() + "; status", item.getStatus(),
                    equalTo(ACTIVE_STATUS));
        }
    }

    @Test(description = "C3285383 GET ruptures sessions status finished")
    public void testSearchForFinishedRuptureSessions() {
        RupturesClient rupturesClient = rupturesClient();

        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(FINISHED_STATUS, 10);
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(8)));
        for (ResRuptureSessionData item : items) {
            assertThat("SessionId: " + item.getSessionId() + "; status",
                    item.getStatus(), equalTo(FINISHED_STATUS));
        }
    }

    @Test(description = "C3285389 GET ruptures sessions pagination first page")
    public void testSearchForRuptureSessionsPaginationFirstPage() {
        RupturesClient rupturesClient = rupturesClient();
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(1, 4);
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(4)));
        int i = 0;
        for (ResRuptureSessionData item : items) {
            if (i < 4)
                assertThat("SessionId: " + item.getSessionId() + "; status",
                        item.getStatus(), equalTo(ACTIVE_STATUS));
            else
                assertThat("SessionId: " + item.getSessionId() + "; status",
                        item.getStatus(), equalTo(FINISHED_STATUS));
            i++;
        }
    }

    @Test(description = "C3285384 GET ruptures sessions pagination second page")
    public void testSearchForRuptureSessionsPaginationSecondPage() {
        RupturesClient rupturesClient = rupturesClient();
        int startFrom = 5;
        int pageSize = 4;

        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(startFrom, pageSize);
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(4)));

        List<Integer> expectedSessionIDs = new ArrayList<>(
                ruptureStatuses.keySet()).subList(startFrom + 1, startFrom + 1 + pageSize);
        Collections.reverse(expectedSessionIDs);
        List<Integer> actualSessionIDs = items.stream().map(ResRuptureSessionData::getSessionId)
                .collect(Collectors.toList());
        assertThat("SessionIds:", actualSessionIDs, equalTo(expectedSessionIDs));
    }

    @Test(description = "C3233580 GET ruptures sessions only shop and department")
    public void testSearchForRuptureSessionsWithoutSpecificFilters() {
        RupturesClient rupturesClient = rupturesClient();
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions();
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(10)));
        for (ResRuptureSessionData item : items) {
            String descPrefix = "SessionId: " + item.getSessionId() + "; ";
            assertThat(descPrefix + "storeId", item.getStoreId(), is(Integer.parseInt(getUserSessionData().getUserShopId())));
            if (item.getStatus().equals(FINISHED_STATUS))
                assertThat(descPrefix + "finishedOn", item.getFinishedOn(), notNullValue());
            else
                assertThat(descPrefix + "finishedOn", item.getFinishedOn(), nullValue());
            assertThat(descPrefix + "createdOn", item.getCreatedOn(), notNullValue());
            assertThat(descPrefix + "createdByLdap", item.getCreatedByLdap(), equalTo(getUserSessionData().getUserLdap()));
            assertThat(descPrefix + "completedProductCount", item.getCompletedProductCount(), notNullValue());
            assertThat(descPrefix + "totalProductCount", item.getTotalProductCount(), notNullValue());
            assertThat(descPrefix + "userFullName", item.getUserFullName(), not(emptyOrNullString()));
            assertThat(descPrefix + "sessionId", item.getSessionId(), in(ruptureStatuses.keySet()));
            assertThat(descPrefix + "status", item.getStatus(), oneOf(FINISHED_STATUS, ACTIVE_STATUS));
        }
    }

}
