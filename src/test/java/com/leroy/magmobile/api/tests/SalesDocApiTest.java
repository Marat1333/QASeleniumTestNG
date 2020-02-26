package com.leroy.magmobile.api.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.StatusCodes;
import com.leroy.magmobile.api.SessionData;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderData;
import com.leroy.umbrella_extension.magmobile.data.estimate.ProductOrderDataList;
import com.leroy.umbrella_extension.magmobile.data.sales.SalesDocumentResponse;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.TestCase;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class SalesDocApiTest extends BaseProjectTest {

    @Inject
    private MagMobileClient magMobileClient;

    @Inject
    private AuthClient authClient;

    private String productLmCode;

    // Получить ЛМ код для обычного продукта без специфичных опций
    protected List<String> getAnyLmCodesProductWithoutSpecificOptions(SessionData sessionData,
                                                                      int necessaryCount, Boolean hasAvailableStock) {
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(sessionData.getUserShopId())
                .setTopEM(false)
                .setHasAvailableStock(hasAvailableStock);
        List<ProductItemResponse> items = magMobileClient.searchProductsBy(params).asJson().getItems();
        List<String> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemResponse item : items) {
            if (item.getAvsDate() == null && !Arrays.asList(badLmCodes).contains(item.getLmCode())) {
                if (necessaryCount > i)
                    resultList.add(item.getLmCode());
                else
                    break;
                i++;
            }
        }
        return resultList;
    }

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserLdap(EnvConstants.BASIC_USER_LDAP);
        sessionData.setUserShopId("35");
        sessionData.setAccessToken(authClient.getAccessToken(EnvConstants.BASIC_USER_LDAP,
                EnvConstants.BASIC_USER_PASS));
        productLmCode = getAnyLmCodesProductWithoutSpecificOptions(
                sessionData, 1, false).get(0);
    }

    @TestCase(111)
    @Test(description = "")
    public void test() {
        log.step("Step 1");
        ProductOrderData productOrderData = new ProductOrderData();
        productOrderData.setLmCode(productLmCode);
        productOrderData.setQuantity((double) new Random().nextInt(6) + 1);
        Response<SalesDocumentResponse> resp = magMobileClient.createSalesDocProducts(sessionData,
                new ProductOrderDataList(Collections.singletonList(productOrderData)));
        assertThat(resp.getStatusCode(), is(StatusCodes.ST_500_ERROR));
        String s = "";
    }

    @AfterMethod
    private void af() {
        String s = "";
    }

}
