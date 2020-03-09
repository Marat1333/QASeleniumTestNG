package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.SessionData;
import com.leroy.magmobile.api.builders.SalesDocTransferBuilder;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.sales.transfer.TransferSalesDocData;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;
import java.util.Collections;

public class SalesDocTransferTest extends BaseProjectTest {

    @Inject
    private Provider<SalesDocTransferBuilder> builderProvider;

    private SalesDocTransferBuilder builder;

    private TransferSalesDocData transferSalesDocData;

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setUserDepartmentId("1");
        builder = builderProvider.get();
        builder.setSessionData(sessionData);
    }

    @Test(description = "C3248457 SalesDoc transfer create POST")
    public void testSalesDocTransferCreatePOST() {
        // Prepare Test Data
        String productLmCode = /*"11297305";*/
                builder.findProductLmCodes(1).get(0);

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
        transferSalesDocData = builder.sendRequestCreate(postSalesDocData)
                .assertThatIsCreated(postSalesDocData)
                .getResponseData();

        // Get?
    }

    @Test(description = "C3248458 SalesDoc transfer add PUT")
    public void testSalesDocTransferAddPUT() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        TransferProductOrderData productOrderData = new TransferProductOrderData();
        productOrderData.setLmCode("82234002");
        productOrderData.setOrderedQuantity(4);

        TransferSalesDocData expectedDocument = new TransferSalesDocData();
        expectedDocument.setTaskId(transferSalesDocData.getTaskId());
        expectedDocument.setStatus(transferSalesDocData.getStatus());
        expectedDocument.addProduct(productOrderData);

        builder.sendRequestAddProducts(transferSalesDocData.getTaskId(), productOrderData)
                .assertThatIsProductAdded(expectedDocument, 2);

        productOrderData.setLineId("2");
        transferSalesDocData.addProduct(productOrderData);
    }

    @Test(description = "Validate Json Schema", enabled = false)
    public void testValidateJsonSchema() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        builder.sendRequestGet(transferSalesDocData.getTaskId())
                .assertThatResponseIsValid();
    }

    @Test(description = "GET")
    public void testGET() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        builder.sendRequestGet(transferSalesDocData.getTaskId())
                .assertThatGetResponseMatches(transferSalesDocData);
    }

    @Test(description = "DELETE")
    public void testDelete() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        step("Delete SalesDocTransfer task");
        builder.sendRequestDelete(transferSalesDocData.getTaskId())
                .assertThatIsDeleted();
        step("GET SalesDocTransfer for confirming that task is removed");
        builder.sendRequestGet(transferSalesDocData.getTaskId())
                .assertThatDocumentIsNotExist();
    }

}
