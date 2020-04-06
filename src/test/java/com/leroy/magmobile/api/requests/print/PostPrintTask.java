package com.leroy.magmobile.api.requests.print;

import com.leroy.magmobile.api.data.print.PrintPrinterData;
import com.leroy.magmobile.api.requests.CommonSearchRequestBuilder;
import ru.leroymerlin.qa.core.clients.base.Method;

@Method(value = "POST", path = "/printing/printTagTask")
public class PostPrintTask extends CommonSearchRequestBuilder<PostPrintTask> {
    public PostPrintTask setPrinterName(PrintPrinterData data) {
        return queryParam("printerName", data.getName());
    }
}
