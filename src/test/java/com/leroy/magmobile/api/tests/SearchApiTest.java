package com.leroy.magmobile.api.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.magmobile.ui.pages.common.FilterPage;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;
import com.leroy.umbrella_extension.magmobile.data.ResponseItem;
import com.leroy.umbrella_extension.magmobile.data.ResponseList;
import com.leroy.umbrella_extension.magmobile.enums.CatalogSearchFields;
import com.leroy.umbrella_extension.magmobile.enums.SortingOrder;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.TestCase;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class SearchApiTest extends BaseProjectTest {

    @Inject
    private MagMobileClient magMobileClient;

    private GetCatalogSearch buildDefaultCatalogSearchParams() {
        return new GetCatalogSearch()
                .setPageSize(10)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1);
    }

    @TestCase(111)
    @Test(description = "C3161100 search by lmCode")
    public void testSearchByLmCode() {
        final String lmCode = "142007600";

        GetCatalogSearch byLmCodeParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByLmCode(lmCode);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byLmCodeParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(isResponseEmpty(response), );
    }

    /**
     * VERIFICATIONS
     **/

    public boolean isResponseEmpty( responseData) {
        boolean result;
        result = ()response.asJson().getItems().isEmpty();
        return result;
    }

    public boolean isResponseContainsCorrectData(List<? extends ResponseItem> responseItemsList, String criterion) {
        List<? extends ResponseList> productData = responseItemsList.asJson().getItems();
        if (criterion.startsWith(FilterPage.GAMMA)) {
            String productGamma;
            criterion = criterion.substring(7);
            for ( eachProduct : productData) {
                productGamma = eachProduct.getGamma();
                anAssert.isEquals(criterion, productGamma, "\"v3 catalog search\" return wrong data by gamma criterion");
            }
        }

        if (criterion.startsWith(MyShopFilterPage.TOP)) {
            String productTop;
            criterion = criterion.substring(5);
            for (ProductItemResponse eachProduct : productData) {
                productTop = String.valueOf(eachProduct.getTop());
                anAssert.isEquals(criterion, productTop, "\"v3 catalog search\" return wrong data by gamma criterion");
            }
        }

        switch (criterion) {
            case MyShopFilterPage.TOP_EM:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getTopEM(), "\"v3 catalog search\" return wrong data by topEm criterion");
                }
                break;
            case MyShopFilterPage.HAS_AVAILABLE_STOCK:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getAvailableStock() > 0, "\"v3 catalog search\" return wrong data by availableStock criterion");
                }
                break;
            case FilterPage.BEST_PRICE:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getPriceCategory().equals("BPR"), "\"v3 catalog search\" return wrong data by bestPrice criterion");
                }
                break;
            case FilterPage.CTM:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getCtm(), "\"v3 catalog search\" return wrong data by ctm criterion");
                }
                break;
            case FilterPage.TOP_1000:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getTop1000(), "\"v3 catalog search\" return wrong data by top1000 criterion");
                }
                break;
        }
        return this;
    }
}
