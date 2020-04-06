package com.leroy.magmobile.api.tests.print_price;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.CatalogProductClient;
import com.leroy.magmobile.api.clients.PrintPriceClient;
import com.leroy.magmobile.api.data.catalog.product.ProductCardData;
import com.leroy.magmobile.api.data.print.EmptyResponse;
import com.leroy.magmobile.api.data.print.PrintDepartmentList;
import com.leroy.magmobile.api.data.print.PrintDepartments;
import com.leroy.magmobile.api.requests.catalog_product.GetCatalogProduct;
import com.leroy.magmobile.api.requests.print.GetPrintersList;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class PrintTagPriceTest extends BaseProjectApiTest {
    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    @Inject
    private PrintPriceClient printPriceClient;

    @Inject
    private CatalogProductClient catalogProductClient;

    private Response<PrintDepartmentList> printersList;

    private List<Response<ProductCardData>> responseProductList = new ArrayList<>();

    @BeforeClass
    private void getProductData() {
        getProductData("14238108", "14250959");
    }

    private void getProductData(String... lmCode) {
        for (String eachLm : lmCode) {
            GetCatalogProduct params = new GetCatalogProduct()
                    .setLmCode(eachLm)
                    .setShopId("20");
            responseProductList.add(catalogProductClient.searchProductByLmCode(params));
        }
    }

    @Test(description = "C23190526 get printers list", priority = 1)
    public void testGetPrintersList() {
        GetPrintersList printersListParams = new GetPrintersList().setShopId("20");

        printersList = printPriceClient.getPrinterList(printersListParams);

        assertThat(printersList, successful());
        List<PrintDepartments> departmentsList = printersList.asJson().getDepartments();
        assertThat("Response is empty", departmentsList, notNullValue());
    }

    @Test(description = "C23190528 post print task (few products)", priority = 2)
    public void testSendPrintTaskFewProduct() {
        Response<EmptyResponse> response = printPriceClient.sendPrintTask(printersList.asJson()
                        .getDepartments().get(0).getDept5().get((int) (Math.random() *
                                (printersList.asJson().getDepartments().get(0).getDept5().size()))), 3,
                responseProductList);
        assertThat(response, successful());
        for (int i=responseProductList.size()-1;i>0;i--){
            responseProductList.remove(i);
        }
    }

    @Test(description = "C23190527 post print task (1 product)", priority = 3)
    public void testSendPrintTaskOneProduct() {
        Response<EmptyResponse> response = printPriceClient.sendPrintTask(printersList.asJson()
                        .getDepartments().get(0).getDept5().get((int) (Math.random() *
                                (printersList.asJson().getDepartments().get(0).getDept5().size()))), 3,
                responseProductList);
        assertThat(response, successful());
    }
}
