package com.leroy.magmobile.api.tests.salesdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.clients.TransferClient;
import com.leroy.magmobile.api.data.sales.transfer.TransferProductOrderData;
import com.leroy.magmobile.api.data.sales.transfer.TransferRunRespData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSalesDocData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

public class SalesDocTransferTest extends BaseProjectApiTest {

    @Inject
    SearchProductHelper searchProductHelper;

    private TransferClient transferClient;

    private TransferSalesDocData transferSalesDocData;

    private List<String> productLmCodes;

    @BeforeClass
    private void initClients() {
        transferClient = apiClientProvider.getTransferClient();
    }

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    @BeforeClass
    private void findProducts() {
        productLmCodes = searchProductHelper.getProductLmCodes(2);
    }

    @Test(description = "C3248457 SalesDoc transfer create POST")
    public void testSalesDocTransferCreatePOST() {
        // Prepare Test Data
        String productLmCode = productLmCodes.get(0);

        TransferProductOrderData productOrderData = new TransferProductOrderData();
        productOrderData.setLmCode(productLmCode);
        productOrderData.setOrderedQuantity(1);

        TransferSalesDocData postSalesDocData = new TransferSalesDocData();
        postSalesDocData.setProducts(Collections.singletonList(productOrderData));
        postSalesDocData.setShopId(Integer.valueOf(getUserSessionData().getUserShopId()));
        postSalesDocData.setDepartmentId(getUserSessionData().getUserDepartmentId());
        postSalesDocData.setDateOfGiveAway(ZonedDateTime.now().plusDays(5).withFixedOffsetZone());
        postSalesDocData.setPointOfGiveAway(SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR.getApiVal());

        // Send request
        Response<TransferSalesDocData> resp = transferClient.sendRequestCreate(postSalesDocData);
        transferSalesDocData = transferClient.assertThatIsCreatedAndGetData(resp, postSalesDocData);
    }

    @Test(description = "C3248482 SalesDoc transfer update PUT - change quantity")
    public void testUpdateTransfer() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        TransferProductOrderData productOrderData = transferSalesDocData.getProducts().get(0);
        productOrderData.setOrderedQuantity(productOrderData.getOrderedQuantity() + 1);
        Response<TransferSalesDocData> resp = transferClient.update(transferSalesDocData.getTaskId(), productOrderData);
        transferClient.assertThatResponseMatches(resp, transferSalesDocData, BaseMashupClient.ResponseType.PUT);
    }

    @Test(description = "C3248458 SalesDoc transfer add Product")
    public void testSalesDocTransferAddProduct() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        TransferProductOrderData productOrderData = new TransferProductOrderData();
        productOrderData.setLmCode(productLmCodes.get(1));
        productOrderData.setOrderedQuantity(4);

        TransferSalesDocData expectedDocument = new TransferSalesDocData();
        expectedDocument.setTaskId(transferSalesDocData.getTaskId());
        expectedDocument.setStatus(transferSalesDocData.getStatus());
        expectedDocument.addProduct(productOrderData);

        Response<TransferSalesDocData> resp = transferClient.sendRequestAddProducts(transferSalesDocData.getTaskId(), productOrderData);
        transferClient.assertThatIsProductAdded(resp, expectedDocument, 2);

        productOrderData.setLineId("2");
        transferSalesDocData.addProduct(productOrderData);
    }

    @Test(description = "Validate Json Schema", enabled = false)
    public void testValidateJsonSchema() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        Response<TransferSalesDocData> resp = transferClient.sendRequestGet(transferSalesDocData.getTaskId());
        transferClient.assertThatResponseIsValid(resp);
    }

    @Test(description = "C3248455 SalesDoc transfers GET")
    public void testGET() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        Response<TransferSalesDocData> resp = transferClient.sendRequestGet(transferSalesDocData.getTaskId());
        transferClient.assertThatResponseMatches(resp, transferSalesDocData);
    }

    @Test(description = "C3248469 SalesDoc transfer DELETE")
    public void testDelete() {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        step("Delete SalesDocTransfer task");
        Response<JsonNode> resp = transferClient.sendRequestDelete(transferSalesDocData.getTaskId());
        transferClient.assertThatIsDeleted(resp);
        step("GET SalesDocTransfer for confirming that task is removed");
        Response<TransferSalesDocData> getResp = transferClient.sendRequestGet(transferSalesDocData.getTaskId());
        transferClient.assertThatDocumentIsNotExist(getResp);
    }

    @Test(description = "C3248459 SalesDoc transfer run PUT")
    public void testTransferRun() {
        testSalesDocTransferCreatePOST();
        Response<TransferRunRespData> resp = transferClient.run(transferSalesDocData);
        transferClient.assertThatIsRun(resp, transferSalesDocData);
    }

    @Test(description = "C3248460 SalesDoc transfer status GET")
    public void testTransferStatus() throws Exception {
        if (transferSalesDocData == null) {
            throw new IllegalArgumentException("Transfer SalesDoc hasn't been created");
        }
        transferClient.waitUntilIsSuccess(transferSalesDocData.getTaskId());
    }

}
