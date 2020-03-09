package com.leroy.magmobile.api.builders;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.constants.StatusCodes;
import com.leroy.magmobile.api.helpers.FindTestDataHelper;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferSalesDocData;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.DeleteSalesDocTransferRequest;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.GetSalesDocTransfer;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.PostSalesDocTransfer;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.transfer.PutSalesDocTransferAdd;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.leroy.magmobile.api.matchers.IsApproximatelyEqual.approximatelyEqual;
import static com.leroy.magmobile.api.matchers.ProjectMatchers.valid;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SalesDocTransferBuilder extends BaseApiBuilder {

    private Response<TransferSalesDocData> response;
    private Response<JsonNode> simpleResponse;

    public TransferSalesDocData getResponseData() {
        assertThatResponseIsOk(response);
        return response.asJson();
    }

    /**
     * ---------- Executable Requests -------------
     **/

    // Lego SalesDoc Transfer
    public SalesDocTransferBuilder sendRequestCreate(
            TransferSalesDocData transferSalesDocData) {
        PostSalesDocTransfer params = new PostSalesDocTransfer();
        params.setLdap(sessionData.getUserLdap());
        params.jsonBody(transferSalesDocData);
        response = apiClient.execute(params, TransferSalesDocData.class);
        return this;
    }

    public SalesDocTransferBuilder sendRequestAddProducts(
            String taskId, List<TransferProductOrderData> productDataList) {
        PutSalesDocTransferAdd params = new PutSalesDocTransferAdd();
        params.setLdap(sessionData.getUserLdap());
        params.setTaskId(taskId);
        params.setShopId(sessionData.getUserShopId());

        TransferSalesDocData transferSalesDocData = new TransferSalesDocData();
        transferSalesDocData.setProducts(productDataList);
        params.jsonBody(transferSalesDocData);
        response = apiClient.execute(params, TransferSalesDocData.class);
        return this;
    }

    public SalesDocTransferBuilder sendRequestAddProducts(
            String taskId, TransferProductOrderData productData) {
        return sendRequestAddProducts(taskId, Collections.singletonList(productData));
    }

    public SalesDocTransferBuilder sendRequestGet(String taskId) {
        GetSalesDocTransfer request = new GetSalesDocTransfer();
        request.setTaskId(taskId);
        request.setLdap(sessionData.getUserLdap());
        response = apiClient.execute(request, TransferSalesDocData.class);
        return this;
    }

    public SalesDocTransferBuilder sendRequestDelete(String taskId) {
        DeleteSalesDocTransferRequest request = new DeleteSalesDocTransferRequest();
        request.setTaskId(taskId);
        simpleResponse = apiClient.execute(request, JsonNode.class);
        return this;
    }

    /**
     * ---------- Verifications ------------
     */

    public SalesDocTransferBuilder assertThatIsCreated(TransferSalesDocData postSalesDocData) {
        assertThatResponseIsOk(response);
        TransferSalesDocData data = response.asJson();
        assertThat("taskId", data.getTaskId(), not(isEmptyOrNullString()));
        assertThat("status", data.getStatus(), is(SalesDocumentsConst.States.NEW.getApiVal()));
        assertThat("createdBy", data.getCreatedBy(), is(sessionData.getUserLdap()));
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
        return this;
    }

    public SalesDocTransferBuilder assertThatIsProductAdded(
            TransferSalesDocData putSalesDocData, int expectedNewLineId) {
        assertThatResponseIsOk(response);
        TransferSalesDocData data = response.asJson();
        assertThat("taskId", data.getTaskId(), not(isEmptyOrNullString()));
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
        return this;
    }

    public SalesDocTransferBuilder assertThatGetResponseMatches(TransferSalesDocData expectedData) {
        assertThatResponseIsOk(response);
        TransferSalesDocData actualData = response.asJson();
        assertThat("Task Id", actualData.getTaskId(), is(expectedData.getTaskId()));
        assertThat("status", actualData.getStatus(), is(expectedData.getStatus()));
        assertThat("shopId", actualData.getShopId(), is(expectedData.getShopId()));
        assertThat("createdBy", actualData.getCreatedBy(), is(expectedData.getCreatedBy()));
        assertThat("createdDate", actualData.getCreatedDate(), is(expectedData.getCreatedDate()));
        assertThat("pointOfGiveAway", actualData.getPointOfGiveAway(),
                is(expectedData.getPointOfGiveAway()));
        assertThat("dateOfGiveAway", actualData.getDateOfGiveAway(),
                is(expectedData.getDateOfGiveAway()));

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
        return this;
    }

    public SalesDocTransferBuilder assertThatIsDeleted() {
        assertThatResponseIsOk(simpleResponse);
        JsonNode respData = simpleResponse.asJson();
        assertThat("success", respData.get("success").booleanValue());
        return this;
    }

    public SalesDocTransferBuilder assertThatDocumentIsNotExist() {
        assertThat("Status code", response.getStatusCode(), is(StatusCodes.ST_404_NOT_FOUND));
        return this;
    }

    public SalesDocTransferBuilder assertThatResponseIsValid() {
        assertThatResponseIsOk(response);
        assertThat(response, valid(TransferSalesDocData.class));
        return this;
    }


    /**
     * ------------  Help Methods -----------------
     **/

    public List<String> findProductLmCodes(int count) {
        return FindTestDataHelper.getProductLmCodes(apiClient, sessionData.getUserShopId(), count);
    }

}