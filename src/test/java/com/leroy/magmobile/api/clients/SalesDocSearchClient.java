package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.requests.salesdoc.search.SalesDocSearchV3Get;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SalesDocSearchClient extends MagMobileClient {

    /**
     * ---------- Requests -------------
     **/

    @Step("Get sales documents by Pin code or DocId = {pinCodeOrDocId}")
    public Response<SalesDocumentListResponse> getSalesDocumentsByPinCodeOrDocId(String pinCodeOrDocId) {
        return execute(new SalesDocSearchV3Get()
                .queryParam("pinCodeOrDocId", pinCodeOrDocId), SalesDocumentListResponse.class);
    }

    @Step("Search for documents by docId={docId}")
    public Response<SalesDocumentListResponse> searchForDocumentsByDocId(String docId) {
        return execute(new SalesDocSearchV3Get()
                .queryParam("docId", docId), SalesDocumentListResponse.class);
    }

}
