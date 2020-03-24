package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.requests.salesdoc.search.SalesDocSearchV3Get;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SalesDocSearchClient extends MagMobileClient {

    /**
     * ---------- Requests -------------
     **/

    public Response<SalesDocumentListResponse> getSalesDocumentsByPinCodeOrDocId(String pinCodeOrDocId) {
        return execute(new SalesDocSearchV3Get()
                .queryParam("pinCodeOrDocId", pinCodeOrDocId), SalesDocumentListResponse.class);
    }

}
