package com.leroy.magportal.api.tests.onlineOrders.other;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.data.onlineOrders.OrderProductsInfoData;
import com.leroy.magportal.api.data.picking.PickingTaskProductsInfoData;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class GetAdditionalProductsInfoTest extends BaseMagPortalApiTest {

    @Inject
    private OrderClient orderClient;
    @Inject
    private SearchProductHelper searchProductHelper;

    private List<String> lmCodes;
    private List<OrderProductsInfoData> defaultData;
    private final String shopId = "13";

    @Test(description = "C23440819 Get Products Additional Info", priority = 1)
    @AllureId("16124")
    public void testGetProductsAdditionalInfo() {
        lmCodes = searchProductHelper.getProductLmCodes(1);
        Response<?> response = orderClient.getProductsAdditionalInfo(lmCodes);
        assertStorageResult(response);
    }

    @Test(description = "C23440820 Get Products Additional Info Several LmCodes", priority = 2)
    @AllureId("16125")
    public void testGetProductsAdditionalInfoSeveralLmCodes() {
        lmCodes = searchProductHelper.getProductLmCodes(10);
        Response<?> response = orderClient.getProductsAdditionalInfo(lmCodes);
        assertStorageResult(response);
        defaultData = response.asJsonList(OrderProductsInfoData.class);
    }

    @Test(description = "C23440821 Get Products Additional Info for non-default Shop", dependsOnMethods = {
            "testGetProductsAdditionalInfoSeveralLmCodes"})
    @AllureId("16126")
    public void testGetProductsAdditionalInfoDiffShop() {
        Response<?> response = orderClient.getProductsAdditionalInfo(lmCodes, shopId);
        assertStorageResult(response);
        defaultData = null;
    }

    //Verification
    @Step("GET Additional Products Info results verification")
    public void assertStorageResult(Response<?> response) {
        assertThat("Request to Additional Products Info has Failed.", response, successful());
        List<String> actualLmCodes = new ArrayList<>();
        List<PickingTaskProductsInfoData> responseData = response
                .asJsonList(PickingTaskProductsInfoData.class);
        assertThat("List of LmCodes is Invalid", responseData.size(), equalTo(lmCodes.size()));
        for (PickingTaskProductsInfoData pickingTaskProductInfoData : responseData) {
            softAssert()
                    .isTrue(Strings.isNotNullAndNotEmpty(pickingTaskProductInfoData.getLmCode()),
                            "LmCode is NOT defined");

            actualLmCodes.add(pickingTaskProductInfoData.getLmCode());
        }
        assertThat("List of LmCodes is Invalid", actualLmCodes, equalTo(lmCodes));

        if (defaultData != null) {
            assertThat("Stock Data is the same for different shop with ID: "
                    + shopId, defaultData, not(responseData));
        }
        softAssert().verifyAll();
    }
}