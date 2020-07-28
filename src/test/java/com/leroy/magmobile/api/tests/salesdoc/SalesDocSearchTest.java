package com.leroy.magmobile.api.tests.salesdoc;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.SalesDocSearchClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SalesDocSearchTest extends BaseProjectApiTest {

    private SalesDocSearchClient client() {
        return apiClientProvider.getSalesDocSearchClient();
    }

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    private final int PAGE_SIZE = 4;
    private final int MAX_COUNT_BACKEND = 4;

    @Test(description = "C3164797 Search by shopId")
    public void testSearchByShopId() {
        String testShop = "5";
        Response<SalesDocumentListResponse> resp = client().searchForDocumentsByShopId(testShop, 1, PAGE_SIZE);
        assertThat(resp, successful());
        List<SalesDocumentResponseData> salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(),
                allOf(greaterThanOrEqualTo(PAGE_SIZE), lessThanOrEqualTo(PAGE_SIZE * MAX_COUNT_BACKEND)));
        assertThatSalesDocumentsMatch(salesDocList, null, testShop, null);
    }

    @Test(description = "C3164798 Search by docType")
    public void testSearchByDocType() {
        int startFrom = 1;

        String testDocType1 = SalesDocumentsConst.Types.CART.getApiVal();
        String testDocType2 = SalesDocumentsConst.Types.ESTIMATE.getApiVal();
        String testDocType3 = SalesDocumentsConst.Types.ORDER.getApiVal();
        String testDocType4 = SalesDocumentsConst.Types.SALE.getApiVal();
        SalesDocSearchClient client = client();

        // Step 1
        step("Search for documents with type = CART");
        Response<SalesDocumentListResponse> resp = client.searchForDocumentsByDocType(testDocType1, startFrom, PAGE_SIZE);
        assertThat(resp, successful());
        List<SalesDocumentResponseData> salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), is(PAGE_SIZE));
        assertThatSalesDocumentsMatch(salesDocList, testDocType1, null, null);

        // Step 2
        step("Search for documents with type = QUOTATION");
        resp = client.searchForDocumentsByDocType(testDocType2, startFrom, PAGE_SIZE);
        assertThat(resp, successful());
        salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), is(PAGE_SIZE));
        assertThatSalesDocumentsMatch(salesDocList, testDocType2, null, null);

        // Step 3
        step("Search for documents with type = ORDER");
        resp = client.searchForDocumentsByDocType(testDocType3, startFrom, PAGE_SIZE);
        assertThat(resp, successful());
        salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), is(PAGE_SIZE));
        assertThatSalesDocumentsMatch(salesDocList, testDocType3, null, null);

        // Step 4
        step("Search for documents with type = SALE");
        resp = client.searchForDocumentsByDocType(testDocType4, startFrom, PAGE_SIZE);
        assertThat(resp, successful());
        salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), is(PAGE_SIZE));
        assertThatSalesDocumentsMatch(salesDocList, testDocType4, null, null);
    }

    @Test(description = "C3164799 Search by docId")
    public void testSearchByDocId() {
        String testDocId = "1524";
        Response<SalesDocumentListResponse> resp = client().searchForDocumentsByDocId(testDocId);
        assertThat(resp, successful());
        List<SalesDocumentResponseData> salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), greaterThan(0));
        assertThatSalesDocumentsMatch(salesDocList, null, null, testDocId);
    }

    private void assertThatSalesDocumentsMatch(List<SalesDocumentResponseData> salesDocList,
                                               String docType, String shopId, String docId) {
        String shopId2 = shopId != null && shopId.length() == 1 ? "00" + shopId : shopId;
        for (SalesDocumentResponseData salesDoc : salesDocList) {
            assertThat("docId", salesDoc.getDocId(), not(isEmptyOrNullString()));
            assertThat("fullDocId", salesDoc.getFullDocId(), is(endsWith(salesDoc.getDocId())));
            if (docType != null)
                assertThat("docType", salesDoc.getDocType(), is(docType));
            if (shopId != null)
                assertThat("shopId", salesDoc.getShopId(), oneOf(shopId, shopId2));
            if (docId != null)
                assertThat("docId", salesDoc.getDocId(), is(containsString(docId)));
        }

    }

}
