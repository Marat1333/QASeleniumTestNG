package com.leroy.magportal.ui.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.api.Module;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.enums.CatalogSearchFields;
import com.leroy.magmobile.api.enums.SortingOrder;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.HashMap;

@Guice(modules = {Module.class})
public class SearchTest extends WebBaseSteps {

    @Inject
    private CatalogSearchClient apiClient;

    private HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> sendRequestsSearchProductsBy(
            GetCatalogSearch... paramsArray) {
        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultMap = new HashMap<>();
        int i = 0;
        for (GetCatalogSearch param : paramsArray) {
            ThreadApiClient<ProductItemDataList, CatalogSearchClient> myThread = new ThreadApiClient<>(
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
        log.step("ввести в поисковую строку нерелевантный поисковой запрос");
        searchProductPage.searchByPhrase(SEARCH_PHRASE);
        searchProductPage.shouldNotFoundMsgIsDisplayed(false, SEARCH_PHRASE);

        //Step 2
        log.step("выбрать любой фильтр и применить его");
        searchProductPage.choseCheckboxFilter(SearchProductPage.Filters.HAS_AVAILABLE_STOCK, true);
        searchProductPage.shouldNotFoundMsgIsDisplayed(true, SEARCH_PHRASE);
    }

    @Test(description = "C22782951 Pagination")
    public void testPagination() throws Exception {
        final String DEPT_ID = "007";
        final String SUB_DEPT_ID = "730";
        final String CLASS_ID = "20";

        GetCatalogSearch filterParams = new GetCatalogSearch()
                .setDepartmentId(DEPT_ID)
                .setSubDepartmentId(SUB_DEPT_ID)
                .setClassId(CLASS_ID)
                .setTopEM(true)
                .setPageSize(12)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC);

        GetCatalogSearch paginationParams = new GetCatalogSearch()
                .setPageSize(24)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads = sendRequestsSearchProductsBy(filterParams, paginationParams);

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("осуществить поиск по фильтру номенклатуры так, чтобы результат поиска содержал менее 12 артикулов");
        searchProductPage.choseNomenclature(DEPT_ID, SUB_DEPT_ID, CLASS_ID, null);
        searchProductPage.choseCheckboxFilter(SearchProductPage.Filters.TOP_EM, true);
        ProductItemDataList productItemListResponse = apiThreads.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(productItemListResponse,
                SearchProductPage.FilterFrame.MY_SHOP, SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.shouldNoMoreResultsBeVisible();

        //Step 2
        log.step("осуществить поиск по фильтрам так, чтобы результат поиска содержал более 12 артикулов");
        searchProductPage.navigateToPreviousNomenclatureElement("Каталог товаров");
        searchProductPage.shouldShowMoreBtnBeVisible();

        //Step 3
        log.step("Нажать на кнопку \"Показать еще\"");
        searchProductPage.showMoreResults();
        ProductItemDataList productItemListResponse1 = apiThreads.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(productItemListResponse1,
                SearchProductPage.FilterFrame.MY_SHOP, SearchProductPage.ViewMode.EXTENDED);
    }
}
