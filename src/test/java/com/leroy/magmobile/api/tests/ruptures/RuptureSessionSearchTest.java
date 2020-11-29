package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.ErrorTextConst;
import com.leroy.constants.api.StatusCodes;
import com.leroy.magmobile.api.clients.RupturesClient;
import com.leroy.magmobile.api.data.CommonErrorResponseData;
import com.leroy.magmobile.api.data.ruptures.ReqRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionData;
import com.leroy.magmobile.api.data.ruptures.ResRuptureSessionDataList;
import com.leroy.magmobile.api.data.ruptures.RuptureProductData;
import com.leroy.magmobile.api.requests.ruptures.RupturesSessionsRequest;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.assertj.core.util.Arrays;
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
            Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
            Integer sessionId = rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
            ruptureStatuses.put(sessionId, ACTIVE_STATUS);
            if (i < 3) {
                continue;
            }
            resp = rupturesClient.finishSession(sessionId);
            rupturesClient.assertThatIsUpdatedOrDeleted(resp);
            ruptureStatuses.put(sessionId, FINISHED_STATUS);
        }

        // Create session in another department
        rupturePostData.setDepartmentId(Integer.parseInt(getUserSessionData().getUserDepartmentId()) + 1);
        Response<JsonNode> resp = rupturesClient.createSession(rupturePostData);
        rupturesClient.assertThatSessionIsCreatedAndGetId(resp);
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
            if (i < 3)
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
        int expectedTotalCount = 11;
        RupturesClient rupturesClient = rupturesClient();
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(20);
        isResponseOk(resp);
        List<ResRuptureSessionData> items = resp.asJson().getItems();
        assertThat("items count", items, hasSize(equalTo(expectedTotalCount)));
        assertThat("total count", resp.asJson().getTotalCount(), equalTo(expectedTotalCount));
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

    @Test(description = "C3285386 GET ruptures sessions without shopid header (check definition validation)")
    public void testGetRupturesSessionsWithoutShopidHeaderCheckValidation() {
        RupturesClient rupturesClient = rupturesClient();
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(
                new RupturesSessionsRequest()
                        .setAppVersion("1.6.4")
                        .setDepartmentId(getUserSessionData().getUserDepartmentId()));
        assertThat("Response Code", resp.getStatusCode(), equalTo(StatusCodes.ST_400_BAD_REQ));
        CommonErrorResponseData errorResp = resp.asJson(CommonErrorResponseData.class);
        assertThat("error text", errorResp.getError(),
                equalTo(ErrorTextConst.WRONG_HEADERS));
        assertThat("validation shopId", errorResp.getValidation().getShopid(),
                equalTo(ErrorTextConst.REQUIRED));
    }

    @Test(description = "C23440582 GET ruptures sessions without departmentId param (check definition validation)")
    public void testGetRupturesSessionsWithoutParamsCheckValidation() {
        RupturesClient rupturesClient = rupturesClient();
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(
                new RupturesSessionsRequest()
                        .setAppVersion("1.6.4")
                        .setShopIdHeader(getUserSessionData().getUserShopId()));
        assertThat("Response Code", resp.getStatusCode(), equalTo(StatusCodes.ST_400_BAD_REQ));
        CommonErrorResponseData errorResp = resp.asJson(CommonErrorResponseData.class);
        assertThat("error text", errorResp.getError(),
                equalTo(ErrorTextConst.WRONG_QUERY_DATA));
        assertThat("validation departmentId", errorResp.getValidation().getDepartmentId(),
                equalTo(ErrorTextConst.REQUIRED));
    }

    @Test(description = "C3285387 GET ruptures sessions wrong shop and wrong department")
    public void testGetRupturesSessionsWrongShopAndWrongDepartment() {
        RupturesClient rupturesClient = rupturesClient();
        Response<ResRuptureSessionDataList> resp = rupturesClient.getSessions(
                new RupturesSessionsRequest()
                        .setAppVersion("1.6.4")
                        .setDepartmentId(50)
                        .setShopIdHeader(500));
        isResponseOk(resp);
        ResRuptureSessionDataList body = resp.asJson();
        assertThat("items count", body.getItems(), hasSize(0));
        assertThat("total count", body.getTotalCount(), equalTo(0));
    }

}
