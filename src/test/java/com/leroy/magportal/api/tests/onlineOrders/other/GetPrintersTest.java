package com.leroy.magportal.api.tests.onlineOrders.other;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.data.printer.PrinterData;
import com.leroy.magportal.api.data.printer.PrinterData.Departments;
import com.leroy.magportal.api.data.printer.PrinterData.Printer;
import com.leroy.magportal.api.helpers.ShopsHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import java.util.List;

import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class GetPrintersTest extends BaseMagPortalApiTest {

    @Inject
    private OrderClient orderClient;
    @Inject
    private ShopsHelper shopsHelper;

    private Integer shopId;

    @Test(description = "C23749381 Get Printers for Default Shop")
    @TmsLink("1957")
    public void testGetPrintersDefaultShop() {
        shopId = Integer.parseInt(getUserSessionData().getUserShopId());
        Response<PrinterData> response = orderClient.getPrinters(shopId);
        assertCheckPrintersResult(response);
    }

    @Test(description = "C23749382 Get Printers for Random Shop")
    @TmsLink("1958")
    public void testGetPrintersRandomShop() {
        shopId = shopsHelper.getRandomShopId();
        Response<PrinterData> response = orderClient.getPrinters(shopId);
        assertCheckPrintersResult(response);
    }

    @Test(description = "C23749383 Get Printers No Shop")
    @TmsLink("1959")
    public void testGetPrintersNoShop() {
        shopId = 0;
        Response<PrinterData> response = orderClient.getPrinters(shopId);
        assertCheckPrintersResult(response);
    }

    //Verification
    @Step("Check that check Quantity response is OK. Response body matches expected data")
    public void assertCheckPrintersResult(Response<PrinterData> response) {
        assertThat("Get Printers request has Failed.", response, successful());
        String desc = String.format("ShopId: %s: NO printers for Department ", shopId);
        Departments data = response.asJson().getDepartments();
        if (shopId > 0) {
            softAssert().isTrue(verifyDep(data.getDep1()), desc + 1);
            softAssert().isTrue(verifyDep(data.getDep2()), desc + 2);
            softAssert().isTrue(verifyDep(data.getDep3()), desc + 3);
            softAssert().isTrue(verifyDep(data.getDep4()), desc + 4);
            softAssert().isTrue(verifyDep(data.getDep5()), desc + 5);
            softAssert().isTrue(verifyDep(data.getDep6()), desc + 6);
            softAssert().isTrue(verifyDep(data.getDep7()), desc + 7);
            softAssert().isTrue(verifyDep(data.getDep8()), desc + 8);
            softAssert().isTrue(verifyDep(data.getDep9()), desc + 9);
            softAssert().isTrue(verifyDep(data.getDep10()), desc + 10);
            softAssert().isTrue(verifyDep(data.getDep11()), desc + 11);
            softAssert().isTrue(verifyDep(data.getDep12()), desc + 12);
            softAssert().isTrue(verifyDep(data.getDep13()), desc + 13);
            softAssert().isTrue(verifyDep(data.getDep14()), desc + 14);
            softAssert().isTrue(verifyDep(data.getDep15()), desc + 15);
        }
        softAssert().isTrue(data.getOthers().size() > 0, desc + "others");
        softAssert().verifyAll();
    }

    private Boolean verifyDep (List<Printer> printers) {
        return printers != null && printers.size() > 0;
    }
}