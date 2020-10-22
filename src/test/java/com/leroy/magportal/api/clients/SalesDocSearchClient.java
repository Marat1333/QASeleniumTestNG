package com.leroy.magportal.api.clients;

import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SalesDocSearchClient extends com.leroy.magmobile.api.clients.SalesDocSearchClient {

    /**
     * ---------- Requests -------------
     **/

    private Response<SalesDocumentListResponse> searchForDocuments(
            String docId, String shopId, String docType, Integer startFrom, Integer pageSize) {
        return null;
    }

    @Override
    public Response<SalesDocumentListResponse> getSalesDocumentsByPinCodeOrDocId(String pinCodeOrDocId) {
        return null; // TODO
    }

    @Override
    public Response<SalesDocumentListResponse> searchForDocumentsByDocId(String docId) {
        return searchForDocuments(docId, null, null, null, null);
    }

    @Override
    public Response<SalesDocumentListResponse> searchForDocumentsByShopId(
            String shopId, Integer startFrom, Integer pageSize) {
        return searchForDocuments(null, shopId, null, startFrom, pageSize);
    }

    @Override
    public Response<SalesDocumentListResponse> searchForDocumentsByShopId(String shopId) {
        return searchForDocumentsByShopId(shopId, null, null);
    }

    @Override
    public Response<SalesDocumentListResponse> searchForDocumentsByDocType(String docType, Integer startFrom, Integer pageSize) {
        return searchForDocuments(null, null, docType, startFrom, pageSize);
    }

    @Override
    public Response<SalesDocumentListResponse> searchForDocumentsByDocType(String docType) {
        return searchForDocumentsByDocType(docType, null, null);
    }

}
