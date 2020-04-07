package com.leroy.magmobile.api.requests.print;

import com.leroy.magmobile.api.requests.CommonLegoRequest;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/printing/printTagTask")
public class PrintPriceTaskRequest extends CommonLegoRequest<PrintPriceTaskRequest> {

    public PrintPriceTaskRequest setPrinterName(String printerName) {
        return queryParam("printerName", printerName);
    }

}
