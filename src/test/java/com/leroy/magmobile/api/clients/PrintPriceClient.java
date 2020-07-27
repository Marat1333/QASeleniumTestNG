package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.data.print.PrintDepartmentList;
import com.leroy.magmobile.api.data.print.PrintTaskProductData;
import com.leroy.magmobile.api.data.print.PrintTaskProductsList;
import com.leroy.magmobile.api.requests.print.PrintDocumentsPrintersRequest;
import com.leroy.magmobile.api.requests.print.PrintPriceTaskRequest;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;

public class PrintPriceClient extends BaseMashupClient {

    public Response<PrintDepartmentList> getDepartmentPrinterList() {
        return getDepartmentPrinterList(userSessionData.getUserShopId());
    }

    @Step("Get department printers info")
    public Response<PrintDepartmentList> getDepartmentPrinterList(String shopId) {
        PrintDocumentsPrintersRequest req = new PrintDocumentsPrintersRequest();
        req.setShopId(shopId);
        return execute(req, PrintDepartmentList.class);
    }

    public Response<JsonNode> sendPrintTask(
            String printerName, PrintTaskProductData printProductData) {
        return sendPrintTask(printerName, Collections.singletonList(printProductData));
    }

    @Step("Send printer task on the printer = {printerName}")
    public Response<JsonNode> sendPrintTask(
            String printerName, List<PrintTaskProductData> printProductDataList) {
        PrintTaskProductsList printTaskProductsList = new PrintTaskProductsList();
        printTaskProductsList.setData(printProductDataList);
        PrintPriceTaskRequest printTaskReq = new PrintPriceTaskRequest()
                .setPrinterName(printerName)
                .setShopId(userSessionData.getUserShopId())
                .jsonBody(printTaskProductsList);

        return execute(printTaskReq, JsonNode.class);
    }

    // Verifications

    @Step("Check that print task is sent successfully")
    public void assertThatSendPrintTaskIsSuccessful(Response<JsonNode> response) {
        assertThatResponseIsOk(response);
        assertThat("Response body", response.asString(), emptyString());
    }
}
