package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.ruptures.*;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionsRequest;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RuptureSessionSearchTest extends BaseProjectApiTest {

    private RupturesClient rupturesClient() {
        return apiClientProvider.getRupturesClient();
    }

    private UserSessionData defaultUserSessionData;

    @Override
    protected boolean isNeedAccessToken() {
        return true;
    }

    private Integer sessionId;
    private HashMap<Integer, String> ruptureStatuses = new HashMap<>();

    @BeforeClass
    public void setUp() {
        RupturesClient rupturesClient = rupturesClient();
        defaultUserSessionData = getUserSessionData();
        RupturesSessionsRequest req = new RupturesSessionsRequest();
        req.setShopId(defaultUserSessionData.getUserShopId());
        req.setDepartmentId(defaultUserSessionData.getUserDepartmentId());
        req.setPageSize(100);

        // Clear department sessions
        while (true) {
            Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(req);
            if (resp.asJson().getTotalCount() == 0) {
                break;
            }
            for (ResRuptureSessionData session : resp.asJson().getItems()) {
                rupturesClient.deleteSession(session.getSessionId());
            }
        }

        // Generate test data (8 finished session and 3 active sessions)
        RuptureProductData productData = new RuptureProductData();
        productData.generateRandomData();

        ReqRuptureSessionData rupturePostData = new ReqRuptureSessionData();
        rupturePostData.setProduct(productData);
        rupturePostData.setShopId(Integer.parseInt(defaultUserSessionData.getUserShopId()));
        rupturePostData.setStoreId(Integer.parseInt(defaultUserSessionData.getUserShopId()));
        rupturePostData.setDepartmentId(Integer.parseInt(defaultUserSessionData.getUserDepartmentId()));

        for (int i = 0; i < 11; i++) {
            Response<JsonNode> resp = rupturesClient.createProduct(rupturePostData);
            sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
            ruptureStatuses.put(sessionId, "active");
            if (i < 3) {
                continue;
            }
            resp = rupturesClient.finishSession(sessionId);
            rupturesClient.assertThatIsUpdatedOrDeleted(resp);
            ruptureStatuses.put(sessionId, "finished");
        }
    }

    @Test(description = "C3285388 GET ruptures sessions status active")
    public void testSearchForActiveRuptureSessions() {
        RupturesClient rupturesClient = rupturesClient();
        RupturesSessionsRequest req = new RupturesSessionsRequest();
        req.setShopId(defaultUserSessionData.getUserShopId());
        req.setDepartmentId(defaultUserSessionData.getUserDepartmentId());
        req.setStatus("active");
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(req);
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(3)));
        for (ResRuptureSessionData item : items) {
            assertThat("status", item.getStatus(), equalTo(ruptureStatuses.get(item.getSessionId())));
        }
    }

    @Test(description = "C3285383 GET ruptures sessions status finished")
    public void testSearchForFinishedRuptureSessions() {
        RupturesClient rupturesClient = rupturesClient();
        RupturesSessionsRequest req = new RupturesSessionsRequest();
        req.setShopId(defaultUserSessionData.getUserShopId());
        req.setDepartmentId(defaultUserSessionData.getUserDepartmentId());
        req.setStatus("finished");
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(req);
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(8)));
        for (ResRuptureSessionData item : items) {
            assertThat("status", item.getStatus(), equalTo(ruptureStatuses.get(item.getSessionId())));
        }
    }

    @Test(description = "C3285389 GET ruptures sessions pagination first page")
    public void testSearchForRuptureSessionsPaginationFirstPage() {
        RupturesClient rupturesClient = rupturesClient();
        RupturesSessionsRequest req = new RupturesSessionsRequest();
        req.setShopId(defaultUserSessionData.getUserShopId());
        req.setDepartmentId(defaultUserSessionData.getUserDepartmentId());
        req.setPageSize(4);
        req.setPageNumber(1);
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(req);
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(4)));
        for (ResRuptureSessionData item : items) {
            assertThat("status", item.getStatus(), equalTo(ruptureStatuses.get(item.getSessionId())));
        }
    }

    @Test(description = "C3233580 GET ruptures sessions only shop and department")
    public void testSearchForRuptureSessionsWithoutSpecificFilters() {
        RupturesClient rupturesClient = rupturesClient();
        RupturesSessionsRequest req = new RupturesSessionsRequest();
        req.setShopId(defaultUserSessionData.getUserShopId());
        req.setDepartmentId(defaultUserSessionData.getUserDepartmentId());
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(req);
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(10)));
        for (ResRuptureSessionData item : items) {
            assertThat("storeId", item.getStoreId(), is(Integer.parseInt(defaultUserSessionData.getUserShopId())));
            if (item.getStatus().equals("finished"))
                assertThat("finishedOn", item.getFinishedOn(), notNullValue());
            else
                assertThat("finishedOn", item.getFinishedOn(), nullValue());
            assertThat("createdOn", item.getCreatedOn(), notNullValue());
            assertThat("createdByLdap", item.getCreatedByLdap(), equalTo(defaultUserSessionData.getUserLdap()));
            assertThat("completedProductCount", item.getCompletedProductCount(), notNullValue());
            assertThat("totalProductCount", item.getTotalProductCount(), notNullValue());
            assertThat("userFullName", item.getUserFullName(), not(emptyOrNullString()));
            assertThat("sessionId", item.getSessionId(), in(ruptureStatuses.keySet()));
            assertThat("status", item.getStatus(), oneOf("finished", "active"));
        }
    }

}
