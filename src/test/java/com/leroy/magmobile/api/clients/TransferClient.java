package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.api.StatusCodes;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.sales.transfer.*;
import com.leroy.magmobile.api.requests.salesdoc.transfer.*;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.leroy.core.matchers.IsApproximatelyEqual.approximatelyEqual;
import static com.leroy.core.matchers.Matchers.valid;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TransferClient extends BaseMashupClient {

    /**
     * ---------- Executable Requests -------------
     **/

    // Lego SalesDoc Transfer
    @Step("Create Transfer task")
    public Response<TransferSalesDocData> sendRequestCreate(
            TransferSalesDocData transferSalesDocData) {
        PostSalesDocTransfer params = new PostSalesDocTransfer();
        params.setLdap(userSessionData.getUserLdap());
        params.jsonBody(transferSalesDocData);
        return execute(params, TransferSalesDocData.class);
    }

    @Step("Add products for task with id = {taskId}")
    public Response<TransferSalesDocData> sendRequestAddProducts(
            String taskId, List<TransferProductOrderData> productDataList) {
        PutSalesDocTransferAdd params = new PutSalesDocTransferAdd();
        params.setLdap(userSessionData.getUserLdap());
        params.setTaskId(taskId);
        params.setShopId(userSessionData.getUserShopId());

        TransferSalesDocData transferSalesDocData = new TransferSalesDocData();
        transferSalesDocData.setProducts(productDataList);
        params.jsonBody(transferSalesDocData);
        return execute(params, TransferSalesDocData.class);
    }

    public Response<TransferSalesDocData> sendRequestAddProducts(
            String taskId, TransferProductOrderData productData) {
        return sendRequestAddProducts(taskId, Collections.singletonList(productData));
    }

    @Step("Get information about transfer task with id = {taskId}")
    public Response<TransferSalesDocData> sendRequestGet(String taskId) {
        GetSalesDocTransfer request = new GetSalesDocTransfer();
        request.setTaskId(taskId);
        request.setLdap(userSessionData.getUserLdap());
        return execute(request, TransferSalesDocData.class);
    }

    public Response<TransferRunRespData> run(TransferSalesDocData transferSalesDocData) {
        return run(transferSalesDocData.getTaskId(), transferSalesDocData.getPointOfGiveAway(),
                transferSalesDocData.getDateOfGiveAway());
    }

    @Step("Run transfer task with id = {taskId}")
    public Response<TransferRunRespData> run(String taskId, String pointOfGiveAway, ZonedDateTime dateOfGiveAway) {
        TransferRunRequest req = new TransferRunRequest();
        req.setTaskId(taskId);
        TransferSalesDocData putData = new TransferSalesDocData();
        putData.setPointOfGiveAway(pointOfGiveAway);
        putData.setDateOfGiveAway(dateOfGiveAway);
        req.jsonBody(putData);
        return execute(req, TransferRunRespData.class);
    }

    @Step("Update task with id = {taskId}")
    public Response<TransferSalesDocData> update(String taskId, TransferProductOrderData productData) {
        TransferUpdateRequest req = new TransferUpdateRequest();
        req.setTaskId(taskId);
        req.setLdap(userSessionData.getUserLdap());
        TransferSalesDocData putData = new TransferSalesDocData();
        putData.setProducts(Collections.singletonList(productData));
        req.jsonBody(putData);
        return execute(req, TransferSalesDocData.class);
    }

    @Step("Get status transfer task with id = {taskId}")
    public Response<TransferStatusRespData> getStatus(String taskId) {
        TransferStatusRequest req = new TransferStatusRequest();
        req.setTaskId(taskId);
        return execute(req, TransferStatusRespData.class);
    }

    /**
     * Wait until task status is success
     *
     * @param taskId - task Id
     */
    @Step("Wait until transfer task with id={taskId} status is SUCCESS")
    public void waitUntilIsSuccess(String taskId) throws Exception {
        String successStatus = "SUCCESS";
        int maxTimeoutInSeconds = 60;
        long currentTimeMillis = System.currentTimeMillis();
        Response<TransferStatusRespData> r = null;
        while (System.currentTimeMillis() - currentTimeMillis < maxTimeoutInSeconds * 1000) {
            r = getStatus(taskId);
            if (r.isSuccessful() && r.asJson().getStatus().equals(successStatus)) {
                Log.info("waitUntilIsSuccess() has executed for " +
                        (System.currentTimeMillis() - currentTimeMillis) / 1000 + " seconds");
                return;
            }
            Thread.sleep(1000);
        }
        assertThat("Could not wait for the task to be SUCCESS. Timeout=" + maxTimeoutInSeconds + ". " +
                        "Response error:" + r.toString(),
                r.isSuccessful());
        assertThat("Could not wait for the task to be SUCCESS. Timeout=" + maxTimeoutInSeconds + ". " +
                "Status:", r.asJson().getStatus(), is(successStatus));
    }

    @Step("Delete transfer task with id = {taskId}")
    public Response<JsonNode> sendRequestDelete(String taskId) {
        DeleteSalesDocTransferRequest request = new DeleteSalesDocTransferRequest();
        request.setTaskId(taskId);
        return execute(request, JsonNode.class);
    }

    // Search
    @Step("Search for transfer tasks")
    public Response<TransferDataList> searchForTasks(TransferSearchFilters filters) {
        TransferSearchRequest req = new TransferSearchRequest();
        req.setShopId(userSessionData.getUserShopId());
        if (filters.getStatus() != null)
            req.setStatus(filters.getStatus());
        if (filters.getCreatedBy() != null)
            req.setCreatedBy(filters.getCreatedBy());
        return execute(req, TransferDataList.class);
    }

    @Step("Search for transfer products")
    public Response<TransferSearchProductDataList> searchForTransferProducts(
            SalesDocumentsConst.GiveAwayPoints pointOfGiveAway) {
        TransferProductSearchRequest req = new TransferProductSearchRequest();
        req.setShopId(userSessionData.getUserShopId());
        req.setDepartmentId(userSessionData.getUserDepartmentId());
        req.setPointOfGiveAway(pointOfGiveAway.getApiVal());
        return execute(req, TransferSearchProductDataList.class);
    }

    /**
     * ---------- Verifications ------------
     */

    @Step("Check that transfer task is created")
    public TransferSalesDocData assertThatIsCreatedAndGetData(
            Response<TransferSalesDocData> response, TransferSalesDocData postSalesDocData) {
        assertThatResponseIsOk(response);
        TransferSalesDocData data = response.asJson();
        assertThat("taskId", data.getTaskId(), not(emptyOrNullString()));
        assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.NEW.getApiVal()));
        assertThat("createdBy", data.getCreatedBy(), is(userSessionData.getUserLdap()));
        assertThat("createdDate", data.getCreatedDate(),
                approximatelyEqual(ZonedDateTime.now()));
        assertThat("pointOfGiveAway", data.getPointOfGiveAway(),
                is(postSalesDocData.getPointOfGiveAway()));
        assertThat("dateOfGiveAway", data.getDateOfGiveAway(),
                is(postSalesDocData.getDateOfGiveAway()));
        assertThat("departmentId", data.getDepartmentId(),
                is(postSalesDocData.getDepartmentId()));
        assertThat("products size", data.getProducts(), hasSize(postSalesDocData.getProducts().size()));

        // Product
        for (int i = 0; i < data.getProducts().size(); i++) {
            TransferProductOrderData actualProductData = data.getProducts().get(i);
            assertThat("Product lineId", actualProductData.getLineId(), is(String.valueOf(i + 1)));
            assertThat("Product lmCode", actualProductData.getLmCode(),
                    is(postSalesDocData.getProducts().get(i).getLmCode()));
            assertThat("Product status", actualProductData.getStatus(),
                    is(SalesDocumentsConst.States.NEW.getApiVal()));
            assertThat("Product orderedQuantity", actualProductData.getOrderedQuantity(),
                    is(postSalesDocData.getProducts().get(i).getOrderedQuantity()));
            // TODO Need to check assignedQuantity? and how?
            //assertThat("Product assignedQuantity", actualProductData.getOrderedQuantity(),
            //        is(postSalesDocData.getProducts().get(i).getOrderedQuantity()));
        }
        return data;
    }

    @Step("Check that products are added")
    public void assertThatIsProductAdded(Response<TransferSalesDocData> response,
                                         TransferSalesDocData putSalesDocData, int expectedNewLineId) {
        assertThatResponseIsOk(response);
        TransferSalesDocData data = response.asJson();
        assertThat("taskId", data.getTaskId(), not(emptyOrNullString()));
        assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.NEW.getApiVal()));

        assertThat("products size", data.getProducts(), hasSize(putSalesDocData.getProducts().size()));

        // Product
        for (int i = 0; i < data.getProducts().size(); i++) {
            TransferProductOrderData actualProductData = data.getProducts().get(i);
            assertThat("Product lineId", actualProductData.getLineId(),
                    is(String.valueOf(expectedNewLineId + i)));
            assertThat("Product lmCode", actualProductData.getLmCode(),
                    is(putSalesDocData.getProducts().get(i).getLmCode()));
            assertThat("Product status", actualProductData.getStatus(),
                    is(SalesDocumentsConst.States.NEW.getApiVal()));
            assertThat("Product orderedQuantity", actualProductData.getOrderedQuantity(),
                    is(putSalesDocData.getProducts().get(i).getOrderedQuantity()));
            // TODO Need to check assignedQuantity? and how?
            //assertThat("Product assignedQuantity", actualProductData.getOrderedQuantity(),
            //        is(postSalesDocData.getProducts().get(i).getOrderedQuantity()));
        }
    }

    @Step("Check that task is run")
    public void assertThatIsRun(Response<TransferRunRespData> resp, TransferSalesDocData expectedData) {
        assertThatResponseIsOk(resp);
        TransferRunRespData actualData = resp.asJson();
        assertThat("Task Id", actualData.getTaskId(),
                is(expectedData.getTaskId()));
        assertThat("Status", actualData.getStatus(), is("IN_PROGRESS"));
        assertThat("code", actualData.getCode(), is(1));
    }

    public void assertThatResponseMatches(Response<TransferSalesDocData> resp, TransferSalesDocData expectedData) {
        assertThatResponseMatches(resp, expectedData, ResponseType.GET);
    }

    @Step("Check that response body data matches expected data")
    public void assertThatResponseMatches(Response<TransferSalesDocData> resp, TransferSalesDocData expectedData,
                                          ResponseType responseType) {
        assertThatResponseIsOk(resp);
        TransferSalesDocData actualData = resp.asJson();
        assertThat("Task Id", actualData.getTaskId(), is(expectedData.getTaskId()));
        assertThat("status", actualData.getStatus(), is(expectedData.getStatus()));
        if (!responseType.equals(ResponseType.PUT)) {
            assertThat("shopId", actualData.getShopId(), is(expectedData.getShopId()));
            assertThat("createdBy", actualData.getCreatedBy(), is(expectedData.getCreatedBy()));
            assertThat("createdDate", actualData.getCreatedDate(), is(expectedData.getCreatedDate()));
            assertThat("pointOfGiveAway", actualData.getPointOfGiveAway(),
                    is(expectedData.getPointOfGiveAway()));
            assertThat("dateOfGiveAway", actualData.getDateOfGiveAway(),
                    is(expectedData.getDateOfGiveAway()));
        }
        List<TransferProductOrderData> actualProductOrderDataList = actualData.getProducts();
        List<TransferProductOrderData> expectedProductOrderDataList = expectedData.getProducts();
        assertThat("Product size", actualProductOrderDataList, hasSize(expectedProductOrderDataList.size()));
        for (int i = 0; i < actualProductOrderDataList.size(); i++) {
            assertThat("LineId of Product #" + i, actualProductOrderDataList.get(i).getLineId(),
                    equalTo(expectedProductOrderDataList.get(i).getLineId()));
            assertThat("lmCode of Product #" + i, actualProductOrderDataList.get(i).getLmCode(),
                    equalTo(expectedProductOrderDataList.get(i).getLmCode()));
            String expectedProductStatus = expectedProductOrderDataList.get(i).getStatus() == null ?
                    SalesDocumentsConst.States.NEW.getApiVal() : expectedProductOrderDataList.get(i).getStatus();
            assertThat("status of Product #" + i, actualProductOrderDataList.get(i).getStatus(),
                    equalTo(expectedProductStatus));
            assertThat("orderedQuantity of Product #" + i, actualProductOrderDataList.get(i).getOrderedQuantity(),
                    equalTo(expectedProductOrderDataList.get(i).getOrderedQuantity()));
            /*assertThat("assignedQuantity of Product #" + i, actualProductOrderDataList.get(i).getAssignedQuantity(),
                    equalTo(expectedProductOrderDataList.get(i).getAssignedQuantity()));*/
        }
    }

    @Step("Check that response has body with success=true")
    public void assertThatIsDeleted(Response<JsonNode> resp) {
        assertThatResponseIsOk(resp);
        JsonNode respData = resp.asJson();
        assertThat("success", respData.get("success").booleanValue());
    }

    @Step("Check that document shouldn't exist")
    public void assertThatDocumentIsNotExist(Response<TransferSalesDocData> resp) {
        assertThat("Status code", resp.getStatusCode(), is(StatusCodes.ST_404_NOT_FOUND));
    }

    @Step("Check response by json schema")
    public void assertThatResponseIsValid(Response<TransferSalesDocData> resp) {
        assertThatResponseIsOk(resp);
        assertThat(resp, valid(TransferSalesDocData.class));
    }

}