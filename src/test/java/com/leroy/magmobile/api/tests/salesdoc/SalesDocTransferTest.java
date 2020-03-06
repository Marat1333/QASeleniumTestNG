package com.leroy.magmobile.api.tests.salesdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.constants.StatusCodes;
import com.leroy.magmobile.api.SessionData;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferSalesDocData;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.leroy.magmobile.api.tests.matchers.IsApproximatelyEqual.approximatelyEqual;
import static com.leroy.magmobile.api.tests.matchers.IsSuccessful.successful;
import static com.leroy.magmobile.api.tests.matchers.IsValid.valid;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SalesDocTransferTest extends BaseProjectTest {

    @Inject
    private Provider<MagMobileClient> magMobileClient;

    @Inject
    private AuthClient authClient;

    private TransferSalesDocData transferSalesDocData;

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setUserDepartmentId("1");
        //sessionData.setAccessToken(authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
        //        EnvConstants.BASIC_USER_PASS));
    }

    @Test
    public void tt() {
        step("Step 1");
        method1();
        step("Step 2");
        method2();
        step("Step 3");
        method3();
    }

    @Step("Method1")
    private void method1() {

    }

    @Step("Method2")
    private void method2() {

    }

    @Step("Method3")
    private void method3() {

    }

    @Test(description = "C3248457 SalesDoc transfer create POST")
    public void testSalesDocTransferCreatePOST() {
        // Prepare Test Data
        String productLmCode = "11297305";/*FindTestDataHelper.getProductLmCodes(magMobileClient.get(),
                sessionData.getUserShopId(), 1).get(0);*/

        TransferProductOrderData productOrderData = new TransferProductOrderData();
        productOrderData.setLmCode(productLmCode);
        productOrderData.setOrderedQuantity(1);

        TransferSalesDocData postSalesDocData = new TransferSalesDocData();
        postSalesDocData.setProducts(Collections.singletonList(productOrderData));
        postSalesDocData.setShopId(Integer.valueOf(sessionData.getUserShopId()));
        postSalesDocData.setDepartmentId(sessionData.getUserDepartmentId());
        postSalesDocData.setDateOfGiveAway(ZonedDateTime.now().plusDays(5).withFixedOffsetZone());
        postSalesDocData.setPointOfGiveAway(SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR.getApiVal());

        // Send request
        Response<TransferSalesDocData> resp = magMobileClient.get().createSalesDocTransfer(
                sessionData, postSalesDocData);

        // Verifications
        assertThat(resp, is(successful()));
        transferSalesDocData = resp.asJson();
        assertThat("taskId", transferSalesDocData.getTaskId(), not(isEmptyOrNullString()));
        assertThat("status", transferSalesDocData.getStatus(), is(SalesDocumentsConst.States.NEW.getApiVal()));
        assertThat("createdBy", transferSalesDocData.getCreatedBy(), is(sessionData.getUserLdap()));
        assertThat("createdDate", transferSalesDocData.getCreatedDate(),
                approximatelyEqual(ZonedDateTime.now()));
        assertThat("pointOfGiveAway", transferSalesDocData.getPointOfGiveAway(),
                is(postSalesDocData.getPointOfGiveAway()));
        assertThat("dateOfGiveAway", transferSalesDocData.getDateOfGiveAway(),
                is(postSalesDocData.getDateOfGiveAway()));
        assertThat("departmentId", transferSalesDocData.getDepartmentId(),
                is(postSalesDocData.getDepartmentId()));
        assertThat("products size", transferSalesDocData.getProducts(), hasSize(1));
        // Product
        TransferProductOrderData resultProductData = transferSalesDocData.getProducts().get(0);
        assertThat("Product lineId", resultProductData.getLineId(), is("1"));
        assertThat("Product lmCode", resultProductData.getLmCode(),
                is(productOrderData.getLmCode()));
        assertThat("Product status", resultProductData.getStatus(),
                is(transferSalesDocData.getStatus()));
        assertThat("Product orderedQuantity", resultProductData.getOrderedQuantity(),
                is(productOrderData.getOrderedQuantity()));
        // TODO Need to check assignedQuantity? and how?
        //assertThat("Product assignedQuantity", resultProductData.getOrderedQuantity(),
        //        is(productOrderData.getOrderedQuantity()));
        productOrderData.setAssignedQuantity(resultProductData.getAssignedQuantity());
    }

    @Test(description = "C3248458 SalesDoc transfer add PUT")
    public void testSalesDocTransferAddPUT() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        TransferProductOrderData productOrderData = new TransferProductOrderData();
        productOrderData.setLmCode("82234002");
        productOrderData.setOrderedQuantity(4);

        transferSalesDocData.addProduct(productOrderData);
        Response<TransferSalesDocData> resp = magMobileClient.get()
                .addProductsIntoSalesDocTransfer(sessionData, transferSalesDocData.getTaskId(), productOrderData);
        assertThat(resp, is(successful()));
        TransferSalesDocData resultTransferDocData = resp.asJson();
        assertThat("taskId", resultTransferDocData.getTaskId(), is(transferSalesDocData.getTaskId()));
        assertThat("status", resultTransferDocData.getStatus(), is(transferSalesDocData.getStatus()));
        // Product
        TransferProductOrderData resultProductData = resultTransferDocData.getProducts().get(0);
        productOrderData.setLineId("2");
        assertThat("Product lineId", resultProductData.getLineId(), is(productOrderData.getLineId()));
        assertThat("Product lmCode", resultProductData.getLmCode(), is(productOrderData.getLmCode()));
        productOrderData.setStatus(transferSalesDocData.getStatus());
        assertThat("Product status", resultProductData.getStatus(), is(productOrderData.getStatus()));
        assertThat("Product orderedQuantity", resultProductData.getOrderedQuantity(),
                is(productOrderData.getOrderedQuantity()));
        //assertThat("Product assignedQuantity", resultProductData.assignedQuantity(), is(transferSalesDocData.assignedQuantity()));
        productOrderData.setAssignedQuantity(resultProductData.getAssignedQuantity());
    }

    @Test(description = "Validate Json Schema")
    public void testValidateJsonSchema() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        Response<TransferSalesDocData> resp = magMobileClient.get()
                .getTransferSalesDoc(sessionData, transferSalesDocData.getTaskId());
        assertThat(resp, is(successful()));
        assertThat(resp, is(valid(TransferSalesDocData.class)));
    }

    @Test(description = "GET")
    public void testGET() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        Response<TransferSalesDocData> resp = magMobileClient.get()
                .getTransferSalesDoc(sessionData, transferSalesDocData.getTaskId());
        assertThat(resp, is(successful()));
        assertThatTransferSalesDocMatches(resp.asJson());
    }

    @Test(description = "DELETE")
    public void testDelete() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        step("Delete SalesDocTransfer task");
        Response<JsonNode> resp = magMobileClient.get()
                .deleteTransferSalesDoc(sessionData, transferSalesDocData.getTaskId());
        assertThat(resp, is(successful()));
        step("GET SalesDocTransfer for confirming that task is removed");
        Response<TransferSalesDocData> resp2 = magMobileClient.get()
                .getTransferSalesDoc(sessionData, transferSalesDocData.getTaskId());
        assertThat("Status code", resp2.getStatusCode(), is(StatusCodes.ST_404_NOT_FOUND));
    }

    private void assertThatTransferSalesDocMatches(TransferSalesDocData data) {
        assertThat("Task Id", data.getTaskId(), is(transferSalesDocData.getTaskId()));
        assertThat("status", data.getStatus(), is(transferSalesDocData.getStatus()));
        assertThat("shopId", data.getShopId(), is(transferSalesDocData.getShopId()));
        assertThat("createdBy", data.getCreatedBy(), is(transferSalesDocData.getCreatedBy()));
        assertThat("createdDate", data.getCreatedDate(), is(transferSalesDocData.getCreatedDate()));
        assertThat("pointOfGiveAway", data.getPointOfGiveAway(),
                is(transferSalesDocData.getPointOfGiveAway()));
        assertThat("dateOfGiveAway", data.getDateOfGiveAway(),
                is(transferSalesDocData.getDateOfGiveAway()));

        List<TransferProductOrderData> actualProductOrderDataList = data.getProducts();
        List<TransferProductOrderData> expectedProductOrderDataList = transferSalesDocData.getProducts();
        assertThat("Product size", actualProductOrderDataList, hasSize(expectedProductOrderDataList.size()));
        for (int i = 0; i < actualProductOrderDataList.size(); i++) {
            assertThat("LineId of Product #" + i, actualProductOrderDataList.get(i).getLineId(),
                    equalTo(expectedProductOrderDataList.get(i).getLineId()));
            assertThat("lmCode of Product #" + i, actualProductOrderDataList.get(i).getLmCode(),
                    equalTo(expectedProductOrderDataList.get(i).getLmCode()));
            assertThat("status of Product #" + i, actualProductOrderDataList.get(i).getStatus(),
                    equalTo(expectedProductOrderDataList.get(i).getStatus()));
            assertThat("orderedQuantity of Product #" + i, actualProductOrderDataList.get(i).getOrderedQuantity(),
                    equalTo(expectedProductOrderDataList.get(i).getOrderedQuantity()));
            assertThat("assignedQuantity of Product #" + i, actualProductOrderDataList.get(i).getAssignedQuantity(),
                    equalTo(expectedProductOrderDataList.get(i).getAssignedQuantity()));
        }
    }

}
