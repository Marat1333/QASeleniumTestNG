package com.leroy.magmobile.api.clients;

import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.sales.SalesDocumentListResponse;
import com.leroy.magmobile.api.data.sales.SalesDocumentResponseData;
import com.leroy.magmobile.api.requests.salesdoc.search.SalesDocSearchV3Get;
import org.apache.commons.lang.RandomStringUtils;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

public class SalesDocSearchClient extends MagMobileClient {

    /**
     * ---------- Requests -------------
     **/

    public Response<SalesDocumentListResponse> getSalesDocumentsByPinCodeOrDocId(String pinCodeOrDocId) {
        return execute(new SalesDocSearchV3Get()
                .queryParam("pinCodeOrDocId", pinCodeOrDocId), SalesDocumentListResponse.class);
    }


    // ----- HELP METHODS ------ //

    public String getValidPinCode() {
        int tryCount = 10;
        for (int i = 0; i < tryCount; i++) {
            String generatedPinCode;
            do {
                generatedPinCode = RandomStringUtils.randomNumeric(5);
            } while (generatedPinCode.startsWith("9"));
            SalesDocumentListResponse salesDocumentsResponse = getSalesDocumentsByPinCodeOrDocId(generatedPinCode)
                    .asJson();
            if (salesDocumentsResponse.getTotalCount() == 0) {
                Log.info("API: None documents found with PIN: " + generatedPinCode);
                return generatedPinCode;
            }
            List<SalesDocumentResponseData> salesDocs = salesDocumentsResponse.getSalesDocuments();
            if (!generatedPinCode.equals(salesDocs.get(0).getPinCode())) {
                return generatedPinCode;
            }
        }
        throw new RuntimeException("Couldn't find valid pin code for " + tryCount + " trying");
    }

}
