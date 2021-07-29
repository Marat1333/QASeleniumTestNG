package com.leroy.magportal.api.tests.onlineOrders.other;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.magmobile.api.data.sales.orders.ResOrderCheckQuantityData;
import com.leroy.magmobile.api.data.sales.orders.ResOrderProductCheckQuantityData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PostCheckQuantityTest extends BaseMagPortalApiTest {

    @Inject
    private OrderClient orderClient;
    @Inject
    private SearchProductHelper searchProductHelper;

    private List<String> lmCodes;
    private LocalDateTime gateAwayDate;

    @Test(description = "C23440815 Check Quantity One LmCode a few hours forward", priority = 1)
    public void testCheckQuantityOneLmCode() {
        lmCodes = searchProductHelper.getProductLmCodes(1);
        gateAwayDate = LocalDateTime.now().plusHours(12);
        Response<ResOrderCheckQuantityData> response = orderClient
                .checkQuantity(lmCodes, 3.0, gateAwayDate);
        assertCheckQuantityResult(response);
    }

    @Test(description = "C23440816 Check Quantity Several LmCodes Today dimensional count", priority = 2)
    public void testCheckQuantitySeveralLmCodesToday() {
        gateAwayDate = LocalDateTime.now();
        lmCodes = searchProductHelper.getProductLmCodes(10);
        Response<ResOrderCheckQuantityData> response = orderClient
                .checkQuantity(lmCodes, 3.5, gateAwayDate);
        assertCheckQuantityResult(response);
    }

    @Test(description = "C23440817 Check Quantity Week Forward", priority = 3)
    public void testCheckQuantityWeekForward() {
        gateAwayDate = LocalDateTime.now().plusDays(7);
        Response<ResOrderCheckQuantityData> response = orderClient
                .checkQuantity(lmCodes, 1.0, gateAwayDate);
        assertCheckQuantityResult(response);
    }


    //Verification
    @Step("Check that check Quantity response is OK. Response body matches expected data")
    public void assertCheckQuantityResult(Response<ResOrderCheckQuantityData> response) {
        assertThat("Check Quantity has Failed.", response, successful());
        ResOrderCheckQuantityData actualData = response.asJson();
        List<String> actualLmCodes = new ArrayList<>();
        if (gateAwayDate.isBefore(LocalDateTime.now().plusDays(1).plusSeconds(1))) {
            softAssert().isTrue(actualData.getGroupingId().equalsIgnoreCase("AVAILABLE_NOW"),
                    actualData.getGroupingId() + ": Unexpected GroupingId");
        } else {
            softAssert().isTrue(actualData.getGroupingId().equalsIgnoreCase("ON_ORDER"),
                    actualData.getGroupingId() + ": Unexpected GroupingId");
        }

        softAssert().isTrue(actualData.getResult().equalsIgnoreCase("OK"),
                "Unexpected Result: " + actualData.getResult());
        assertThat("Wrong product size", actualData.getProducts(),
                hasSize(lmCodes.size()));
        for (ResOrderProductCheckQuantityData productData : actualData.getProducts()) {
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(productData.getLmCode()),
                    "LmCode is NOT defined");
            actualLmCodes.add(productData.getLmCode());
            softAssert().isNotNull(productData.getAvailableStock(),
                    productData.getLmCode() + ": AvailableStock is null");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(productData.getLineId()),
                    productData.getLmCode() + ": LineId is NOT defined");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(productData.getTitle()),
                    productData.getLmCode() + ": Title is NOT defined");
        }
        assertThat("List of LmCodes is Invalid", actualLmCodes, equalTo(lmCodes));
        softAssert().verifyAll();
    }
}