package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.magmobile.api.clients.MagMobileClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.requests.salesdoc.search.SalesDocSearchV3Get;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SalesDocSearchTest extends BaseProjectApiTest {

    @Inject
    private Provider<MagMobileClient> magMobileClient;

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    private final int PAGE_SIZE = 4;
    private final int MAX_COUNT_BACKEND = 4;

    private SalesDocSearchV3Get buildDefaultSalesDocSearchV3Params() {
        return new SalesDocSearchV3Get()
                .setPageSize(PAGE_SIZE)
                .setStartFrom(1);
    }

    @Test(description = "C3164797 Search by shopId")
    public void testSearchByShopId() {
        String testShop = "5";
        SalesDocSearchV3Get params = buildDefaultSalesDocSearchV3Params()
                .setShopId(testShop);
        Response<SalesDocumentListResponse> resp = magMobileClient.get().searchForSalesDocumentBy(params);
        assertThat(resp, successful());
        List<SalesDocumentResponseData> salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(),
                allOf(greaterThanOrEqualTo(PAGE_SIZE), lessThanOrEqualTo(PAGE_SIZE * MAX_COUNT_BACKEND)));
        assertThatSalesDocumentsMatch(salesDocList, null, testShop, null);
    }

    @Test(description = "C3164798 Search by docType")
    public void testSearchByDocType() {
        String testDocType1 = SalesDocumentsConst.Types.CART.getApiVal();
        String testDocType2 = SalesDocumentsConst.Types.QUOTATION.getApiVal();
        String testDocType3 = SalesDocumentsConst.Types.ORDER.getApiVal();
        String testDocType4 = SalesDocumentsConst.Types.SALE.getApiVal();

        // Step 1
        step("Search for documents with type = CART");
        Response<SalesDocumentListResponse> resp = magMobileClient.get().searchForSalesDocumentBy(
                buildDefaultSalesDocSearchV3Params().setDocType(testDocType1)
        );
        assertThat(resp, successful());
        List<SalesDocumentResponseData> salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), is(PAGE_SIZE));
        assertThatSalesDocumentsMatch(salesDocList, testDocType1, null, null);

        // Step 2
        step("Search for documents with type = QUOTATION");
        resp = magMobileClient.get().searchForSalesDocumentBy(
                buildDefaultSalesDocSearchV3Params().setDocType(testDocType2)
        );
        assertThat(resp, successful());
        salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), is(PAGE_SIZE));
        assertThatSalesDocumentsMatch(salesDocList, testDocType2, null, null);

        // Step 3
        step("Search for documents with type = ORDER");
        resp = magMobileClient.get().searchForSalesDocumentBy(
                buildDefaultSalesDocSearchV3Params().setDocType(testDocType3)
        );
        assertThat(resp, successful());
        salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), is(PAGE_SIZE));
        assertThatSalesDocumentsMatch(salesDocList, testDocType3, null, null);

        // Step 4
        step("Search for documents with type = SALE");
        resp = magMobileClient.get().searchForSalesDocumentBy(
                buildDefaultSalesDocSearchV3Params().setDocType(testDocType4)
        );
        assertThat(resp, successful());
        salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), is(PAGE_SIZE));
        assertThatSalesDocumentsMatch(salesDocList, testDocType4, null, null);
    }

    @Test(description = "C3164799 Search by docId")
    public void testSearchByDocId() {
        String testDocId = "1524";
        SalesDocSearchV3Get params = buildDefaultSalesDocSearchV3Params()
                .setDocId(testDocId);
        Response<SalesDocumentListResponse> resp = magMobileClient.get().searchForSalesDocumentBy(params);
        assertThat(resp, successful());
        List<SalesDocumentResponseData> salesDocList = resp.asJson().getSalesDocuments();
        assertThat("Count of the documents", salesDocList.size(), greaterThan(0));
        assertThatSalesDocumentsMatch(salesDocList, null, null, testDocId);
    }

    private void assertThatSalesDocumentsMatch(List<SalesDocumentResponseData> salesDocList,
                                               String docType, String shopId, String docId) {
        for (SalesDocumentResponseData salesDoc : salesDocList) {
            assertThat("docId", salesDoc.getDocId(), not(isEmptyOrNullString()));
            assertThat("fullDocId", salesDoc.getFullDocId(), is(endsWith(salesDoc.getDocId())));
            if (docType != null)
                assertThat("docType", salesDoc.getDocType(), is(docType));
            if (shopId != null)
                assertThat("shopId", salesDoc.getShopId(), is(shopId));
            if (docId != null)
                assertThat("docId", salesDoc.getDocId(), is(containsString(docId)));
        }

    }

}
