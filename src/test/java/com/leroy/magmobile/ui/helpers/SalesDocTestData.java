package com.leroy.magmobile.ui.helpers;

import com.leroy.core.configuration.Log;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentListResponse;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentResponse;
import org.apache.commons.lang.RandomStringUtils;

import java.util.List;

public class SalesDocTestData {

    /**
     * Подбирает PIN код, который не был использован в других документах
     * @param client - API клиент
     * @return (String) PIN код
     */
    public static String getAvailablePinCode(MagMobileClient client) {
        int tryCount = 10;
        for (int i=0; i < tryCount; i++) {
            String generatedPinCode;
            do {
                generatedPinCode = RandomStringUtils.randomNumeric(5);
            } while (generatedPinCode.startsWith("9"));
            SalesDocumentListResponse salesDocumentsResponse = client.getSalesDocumentsByPinCodeOrDocId(generatedPinCode)
                    .asJson();
            if (salesDocumentsResponse.getTotalCount() == 0) {
                Log.info("API: Не найдено ни одного документа с PIN кодом: " + generatedPinCode);
                return generatedPinCode;
            }
            List<SalesDocumentResponse> salesDocs = salesDocumentsResponse.getSalesDocuments();
            if (!generatedPinCode.equals(salesDocs.get(0).getPinCode())) {
                return generatedPinCode;
            }
        }
        throw new RuntimeException("Мы не смогли за "+tryCount+" попыток подобрать неиспользованный PIN код");
    }

}
