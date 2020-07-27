package com.leroy.magmobile.api.clients;

import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.requests.salesdoc.search.SalesDocSearchV3Get;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SalesDocSearchClient extends BaseMashupClient {

    /**
     * ---------- Requests -------------
     **/

    private Response<SalesDocumentListResponse> searchForDocuments(
            String docId, String shopId, String docType, Integer startFrom, Integer pageSize) {
        SalesDocSearchV3Get req = new SalesDocSearchV3Get();
        req.setDocId(docId);
        req.setShopId(shopId);
        req.setDocType(docType);
        req.setStartFrom(startFrom);
        req.setPageSize(pageSize);
        return execute(req, SalesDocumentListResponse.class);
    }

    @Step("Get sales documents by Pin code or DocId = {pinCodeOrDocId}")
    public Response<SalesDocumentListResponse> getSalesDocumentsByPinCodeOrDocId(String pinCodeOrDocId) {
        return execute(new SalesDocSearchV3Get()
                .queryParam("pinCodeOrDocId", pinCodeOrDocId), SalesDocumentListResponse.class);
    }

    @Step("Search for documents by docId={docId}")
    public Response<SalesDocumentListResponse> searchForDocumentsByDocId(String docId) {
        return searchForDocuments(docId, null, null, null, null);
    }

    @Step("Search for documents by shopId={shopId}")
    public Response<SalesDocumentListResponse> searchForDocumentsByShopId(
            String shopId, Integer startFrom, Integer pageSize) {
        return searchForDocuments(null, shopId, null, startFrom, pageSize);
    }

    public Response<SalesDocumentListResponse> searchForDocumentsByShopId(String shopId) {
        return searchForDocumentsByShopId(shopId, null, null);
    }

    @Step("Search for documents by docType={docType}")
    public Response<SalesDocumentListResponse> searchForDocumentsByDocType(String docType, Integer startFrom, Integer pageSize) {
        return searchForDocuments(null, null, docType, startFrom, pageSize);
    }

    public Response<SalesDocumentListResponse> searchForDocumentsByDocType(String docType) {
        return searchForDocumentsByDocType(docType, null, null);
    }

}
