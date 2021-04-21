package com.leroy.magmobile.api.clients;

import com.leroy.constants.EnvConstants;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.requests.salesdoc.search.SalesDocSearchV3Get;
import io.qameta.allure.Step;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SalesDocSearchClient extends BaseMashupClient {

    @Data
    @Accessors(chain = true)
    public static class Filters {
        private String docType;
        private String docId;
        private String shopId;
        private String customerNumber;
    }

    @Override
    protected void init() {
        gatewayUrl = EnvConstants.PAO_API_HOST;
        jaegerHost = EnvConstants.PAO_JAEGER_HOST;
        jaegerService = EnvConstants.PAO_JAEGER_SERVICE;
    }

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

    @Step("Search for documents with filters")
    public Response<SalesDocumentListResponse> searchForDocuments(Filters filters, Integer startFrom, Integer pageSize) {
        SalesDocSearchV3Get req = new SalesDocSearchV3Get();
        req.setDocId(filters.getDocId());
        req.setShopId(filters.getShopId());
        req.setDocType(filters.getDocType());
        req.setCustomerNumber(filters.getCustomerNumber());
        req.setStartFrom(startFrom);
        req.setPageSize(pageSize);
        return execute(req, SalesDocumentListResponse.class);
    }

    public Response<SalesDocumentListResponse> searchForDocuments(Filters filters) {
        return searchForDocuments(filters, null, null);
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
