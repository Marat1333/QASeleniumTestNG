package com.leroy.magmobile.api.tests.print_price;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.clients.CatalogProductClient;
import com.leroy.magmobile.api.clients.PrintPriceClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.print.PrintDepartmentList;
import com.leroy.magmobile.api.data.print.PrintDepartments;
import com.leroy.magmobile.api.data.print.PrintPrinterData;
import com.leroy.magmobile.api.data.print.PrintTaskProductData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static com.leroy.core.matchers.Matchers.valid;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class PrintTagPriceTest extends BaseProjectApiTest {

    private PrintPriceClient printPriceClient;

    private CatalogProductClient catalogProductClient;

    private PrintDepartments printDepartmentsList;

    private List<PrintTaskProductData> printTaskProductDataList;

    @BeforeClass
    private void beforeSetUp() {
        printPriceClient = apiClientProvider.getPrintPriceClient();
        catalogProductClient = apiClientProvider.getCatalogProductClient();
        String[] lmCodes = apiClientProvider.getProducts(2).stream()
                .map(ProductItemData::getLmCode).toArray(String[]::new);
        initCatalogProductDataList(lmCodes);
    }

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("20");
        return sessionData;
    }

    private void initCatalogProductDataList(String... lmCode) {
        List<CatalogProductData> catalogProductList = new ArrayList<>();
        for (String eachLm : lmCode) {
            CatalogProductClient.Extend extendOptions = CatalogProductClient.Extend.builder()
                    .inventory(true).logistic(true).rating(true).build();
            Response<CatalogProductData> resp = catalogProductClient.getProduct(
                    eachLm, SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR, extendOptions);
            assertThat(resp, successful());
            catalogProductList.add(resp.asJson());
        }
        printTaskProductDataList = new ArrayList<>();
        for (CatalogProductData catalogProductData : catalogProductList) {
            PrintTaskProductData printTaskProductData = new PrintTaskProductData();
            printTaskProductData.setLmCode(catalogProductData.getLmCode());
            printTaskProductData.setBarCode(catalogProductData.getBarCode());
            printTaskProductData.setTitle(catalogProductData.getTitle());
            printTaskProductData.setPrice(catalogProductData.getPrice());
            printTaskProductData.setPriceCurrency(catalogProductData.getPurchasePriceCurrency());
            printTaskProductData.setRecommendedPrice(catalogProductData.getSalesPrice().getRecommendedPrice());
            printTaskProductData.setSalesPrice(catalogProductData.getSalesPrice().getPrice());
            printTaskProductData.setPriceReasonOfChange(catalogProductData.getSalesPrice().getReasonOfChange());
            printTaskProductData.setFuturePriceFromDate(catalogProductData.getSalesPrice().getDateOfChange());
            printTaskProductData.setPriceUnit(catalogProductData.getPriceUnit());
            printTaskProductData.setQuantity(3);
            printTaskProductData.setSize("pricetag-small-50x40mm");
            printTaskProductDataList.add(printTaskProductData);
        }
    }

    @Test(description = "C23190526 get printers list", priority = 1)
    public void testGetPrintersList() {
        Response<PrintDepartmentList> resp = printPriceClient.getDepartmentPrinterList();
        assertThat(resp, successful());
        printDepartmentsList = resp.asJson().getDepartments().get(0);
        assertThat(resp, valid(PrintDepartmentList.class));
    }

    @Test(description = "C23190528 post print task (few products)", priority = 2)
    public void testSendPrintTaskFewProduct() {
        List<PrintPrinterData> dept5 = printDepartmentsList.getDept5();
        Response<JsonNode> response = printPriceClient.sendPrintTask(
                dept5.get((int) (Math.random() * dept5.size())).getName(), printTaskProductDataList);
        printPriceClient.assertThatSendPrintTaskIsSuccessful(response);
    }

    @Test(description = "C23190527 post print task (1 product)", priority = 3)
    public void testSendPrintTaskOneProduct() {
        List<PrintPrinterData> dept5 = printDepartmentsList.getDept5();
        Response<JsonNode> response = printPriceClient.sendPrintTask(
                dept5.get((int) (Math.random() * dept5.size())).getName(),
                printTaskProductDataList.get(0));
        printPriceClient.assertThatSendPrintTaskIsSuccessful(response);
    }
}
