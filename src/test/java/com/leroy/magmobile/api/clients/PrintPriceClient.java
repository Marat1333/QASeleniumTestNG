package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.data.print.*;
import com.leroy.magmobile.api.requests.print.PrintDocumentsPrintersRequest;
import com.leroy.magmobile.api.requests.print.PrintPriceTaskRequest;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.notNullValue;

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

    public String getRandomPrinterName() throws IllegalAccessException {
        List<String> printersName = getPrinterNamesList();
        int randomPrinterName = (int) (Math.random() * printersName.size());
        return printersName.get(randomPrinterName);
    }

    public List<String> getPrinterNamesList() throws IllegalAccessException {
        List<String> printersName = new ArrayList<>();
        PrintDepartments departmentsList = getDepartmentPrinterList().asJson().getDepartments().get(0);
        Field[] fields = departmentsList.getClass().getDeclaredFields();
        for (Field eachField : fields) {
            eachField.setAccessible(true);
            try {
                List<PrintPrinterData> eachDeptPrinters = (List<PrintPrinterData>) eachField.get(departmentsList);
                List<String> tmpPrintersNames = eachDeptPrinters.stream().map(PrintPrinterData::getName).collect(Collectors.toList());
                printersName.addAll(tmpPrintersNames);
            } catch (NullPointerException e) {
                Log.warn("There is no printers for department " + eachField.getName());
            }
        }
        return printersName;
    }

    public Response<PrintTaskResponseData> sendPrintTask(
            String printerName, PrintTaskProductData printProductData) {
        return sendPrintTask(printerName, Collections.singletonList(printProductData));
    }

    @Step("Send printer task on the printer = {printerName}")
    public Response<PrintTaskResponseData> sendPrintTask(
            String printerName, List<PrintTaskProductData> printProductDataList) {
        PrintTaskProductsList printTaskProductsList = new PrintTaskProductsList();
        printTaskProductsList.setData(printProductDataList);
        PrintPriceTaskRequest printTaskReq = new PrintPriceTaskRequest()
                .setPrinterName(printerName)
                .setShopId(userSessionData.getUserShopId())
                .jsonBody(printTaskProductsList);

        return execute(printTaskReq, PrintTaskResponseData.class);
    }

    // Verifications

    @Step("Check that print task is sent successfully")
    public void assertThatSendPrintTaskIsSuccessful(Response<PrintTaskResponseData> response) {
        assertThatResponseIsOk(response);
        assertThat("Response body", response.asJson().getTemplateVersion(), notNullValue());
    }
}
