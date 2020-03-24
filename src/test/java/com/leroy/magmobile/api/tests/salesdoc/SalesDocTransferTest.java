package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.clients.SalesDocTransferClient;
import com.leroy.magmobile.api.data.sales.transfer.TransferProductOrderData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSalesDocData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.ZonedDateTime;
import java.util.Collections;

public class SalesDocTransferTest extends BaseProjectApiTest {

    @Inject
    private SalesDocTransferClient transferClient;

    private CatalogSearchClient searchBuilder;

    private TransferSalesDocData transferSalesDocData;

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    @BeforeClass
    private void setUp() {
        transferClient.setSessionData(sessionData);
        searchBuilder = getCatalogSearchClient();
    }

    @Test(description = "C3248457 SalesDoc transfer create POST")
    public void testSalesDocTransferCreatePOST() {
        // Prepare Test Data
        String productLmCode =
                searchBuilder.getProductLmCodes(1).get(0);

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
        transferSalesDocData = transferClient.sendRequestCreate(postSalesDocData)
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

        transferClient.sendRequestAddProducts(transferSalesDocData.getTaskId(), productOrderData)
                .assertThatIsProductAdded(expectedDocument, 2);

        productOrderData.setLineId("2");
        transferSalesDocData.addProduct(productOrderData);
    }

    @Test(description = "Validate Json Schema", enabled = false)
    public void testValidateJsonSchema() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        transferClient.sendRequestGet(transferSalesDocData.getTaskId())
                .assertThatResponseIsValid();
    }

    @Test(description = "GET")
    public void testGET() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        transferClient.sendRequestGet(transferSalesDocData.getTaskId())
                .assertThatGetResponseMatches(transferSalesDocData);
    }

    @Test(description = "DELETE")
    public void testDelete() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        step("Delete SalesDocTransfer task");
        transferClient.sendRequestDelete(transferSalesDocData.getTaskId())
                .assertThatIsDeleted();
        step("GET SalesDocTransfer for confirming that task is removed");
        transferClient.sendRequestGet(transferSalesDocData.getTaskId())
                .assertThatDocumentIsNotExist();
    }

}
