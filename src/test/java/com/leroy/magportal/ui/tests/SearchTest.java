package com.leroy.magportal.ui.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import com.leroy.umbrella_extension.ThreadApiClient;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.enums.CatalogSearchFields;
import com.leroy.umbrella_extension.magmobile.enums.SortingOrder;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.BaseModule;

import java.util.HashMap;

@Guice(modules = {BaseModule.class})
public class SearchTest extends WebBaseSteps {

    @Inject
    private MagMobileClient apiClient;

    private HashMap<Integer, ThreadApiClient<ProductItemListResponse, MagMobileClient>> sendRequestsSearchProductsBy(
            GetCatalogSearch... paramsArray) {
        HashMap<Integer, ThreadApiClient<ProductItemListResponse, MagMobileClient>> resultMap = new HashMap<>();
        int i = 0;
        for (GetCatalogSearch param : paramsArray) {
            ThreadApiClient<ProductItemListResponse, MagMobileClient> myThread = new ThreadApiClient<>(
                    apiClient);
            myThread.sendRequest(client -> client.searchProductsBy(param));
            resultMap.put(i, myThread);
            i++;
        }
        return resultMap;
    }

    @Test(description = "C22782949 No results msg")
    public void testNotFoundResults() throws Exception {
        final String SEARCH_PHRASE = "asdf123";

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        searchProductPage.searchByPhrase(SEARCH_PHRASE);
        searchProductPage.shouldNotFoundMsgIsDisplayed(false, SEARCH_PHRASE);

        //Step 2
        searchProductPage.choseCheckboxFilter(SearchProductPage.Filters.HAS_AVAILABLE_STOCK, true);
        searchProductPage.shouldNotFoundMsgIsDisplayed(true, SEARCH_PHRASE);
    }

    @Test(description = "C22782951 Pagination")
    public void testPagination() throws Exception {
        final String DEPT_ID = "007";
        final String SUB_DEPT_ID = "730";
        final String CLASS_ID = "20";

        GetCatalogSearch paginationParams = new GetCatalogSearch()
                .setDepartmentId(DEPT_ID)
                .setSubDepartmentId(SUB_DEPT_ID)
                .setClassId(CLASS_ID)
                .setTopEM(true)
                .setPageSize(12)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC);

        HashMap<Integer, ThreadApiClient<ProductItemListResponse, MagMobileClient>> apiThreads = sendRequestsSearchProductsBy(paginationParams);

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        searchProductPage.choseNomenclature(DEPT_ID, SUB_DEPT_ID, CLASS_ID, null);
        ProductItemListResponse productItemListResponse = apiThreads.get(0).getData();
        searchProductPage.shouldResponseEntityCountEqualsToViewEntityCount(productItemListResponse);
    }
}
