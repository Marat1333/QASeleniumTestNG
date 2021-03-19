package com.leroy.magportal.api.requests.printer;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "GET", path = "/v1/printers")
public class PrintersGetRequest extends CommonLegoRequest<PrintersGetRequest> {

    public PrintersGetRequest setStoreId(String value) {
        return queryParam("storeId", value);
    }

}
