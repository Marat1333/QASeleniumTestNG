package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.catalog.product.ProductCardData;
import com.leroy.magmobile.api.data.print.*;
import com.leroy.magmobile.api.requests.print.GetPrintersList;
import com.leroy.magmobile.api.requests.print.PostPrintTask;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.List;

public class PrintPriceClient extends MagMobileClient {
    public Response<PrintDepartmentList> getPrinterList(GetPrintersList params) {
        return execute(params, PrintDepartmentList.class);
    }

    public Response<EmptyResponse> sendPrintTask(PrintPrinterData printerData, int priceTagQuantity, List<Response<ProductCardData>> productData) {
        List<PrintTaskProductData> printTaskProductDataList = new ArrayList<>();
        PrintTaskProductData printTaskProductData = new PrintTaskProductData();

        for (int i = 0; i < productData.size(); i++) {
            printTaskProductData.setLmCode(productData.get(i).asJson().getLmCode());
            printTaskProductData.setBarCode(productData.get(i).asJson().getBarCode());
            printTaskProductData.setTitle(productData.get(i).asJson().getTitle());
            printTaskProductData.setPrice(productData.get(i).asJson().getPrice());
            printTaskProductData.setPriceCurrency(productData.get(i).asJson().getPurchasePriceCurrency());
            printTaskProductData.setRecommendedPrice(productData.get(i).asJson().getSalesPrice().getRecommendedPrice());
            printTaskProductData.setSalesPrice(productData.get(i).asJson().getSalesPrice().getPrice());
            printTaskProductData.setPriceReasonOfChange(productData.get(i).asJson().getSalesPrice().getReasonOfChange());
            printTaskProductData.setFuturePriceFromDate(productData.get(i).asJson().getSalesPrice().getDateOfChange());
            printTaskProductData.setPriceUnit(productData.get(i).asJson().getPriceUnit());
            printTaskProductData.setQuantity(priceTagQuantity);
            printTaskProductData.setSize("pricetag-small-50x40mm");

            printTaskProductDataList.add(new PrintTaskProductData(printTaskProductData));
        }
        PrintTaskProductsList printTaskProductsList = new PrintTaskProductsList();
        printTaskProductsList.setData(printTaskProductDataList);
        PostPrintTask printTask = new PostPrintTask()
                .setPrinterName(printerData)
                .setShopId("20")
                .jsonBody(printTaskProductsList);

        return execute(printTask, EmptyResponse.class);
    }
}
