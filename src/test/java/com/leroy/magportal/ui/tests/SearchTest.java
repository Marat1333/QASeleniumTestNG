package com.leroy.magportal.ui.tests;

import com.leroy.constants.EnvConstants;
import com.leroy.core.api.Module;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.enums.CatalogSearchFields;
import com.leroy.magmobile.api.enums.SortingOrder;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.models.search.FiltersData;
import com.leroy.magportal.ui.pages.products.ParamNames;
import com.leroy.magportal.ui.pages.products.ProductCardPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import com.mongodb.client.model.Filters;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.HashMap;

@Guice(modules = {Module.class})
public class SearchTest extends WebBaseSteps {

    private CatalogSearchClient apiClient;

    @BeforeClass
    private void setUpSearchTests() {
        apiClient = apiClientProvider.getCatalogSearchClient();
    }

    private final int defaultPageSize = 12;
    private final String allDepartments = "Каталог товаров";

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
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.HAS_AVAILABLE_STOCK);
        searchProductPage.shouldNotFoundMsgIsDisplayed(true, SEARCH_PHRASE);
    }

    @Test(description = "C22782951 Pagination")
    public void testPagination() throws Exception {
        final String DEPT_ID = "007";
        final String SUB_DEPT_ID = "730";
        final String CLASS_ID = "20";

        GetCatalogSearch filterParams = new GetCatalogSearch()
                .setDepartmentId(DEPT_ID.substring(2))
                .setSubDepartmentId(SUB_DEPT_ID)
                .setClassId(CLASS_ID)
                .setTopEM(true)
                .setPageSize(12)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.ASC);

        GetCatalogSearch paginationParams = new GetCatalogSearch()
                .setPageSize(24)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.ASC);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads = sendRequestsSearchProductsBy(filterParams, paginationParams);

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("осуществить поиск по фильтру номенклатуры так, чтобы результат поиска содержал менее 12 артикулов");
        searchProductPage.choseNomenclature(DEPT_ID, SUB_DEPT_ID, CLASS_ID, null);
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.TOP_EM);
        searchProductPage.choseSortType(SearchProductPage.SortType.LM_CODE_ASC);
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

    @Test(description = "C23384708 search by criterions")
    public void testSearchByCriterions() throws Exception {
        String lmCode = "10008698";
        String searchContext = "штора";
        String shortSearchPhrase = "12";
        String barCode = "5902120110575";
        String shortLmCode = "1234";
        String shortBarCode = "590212011";

        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.navigateToPreviousNomenclatureElement("Каталог товаров");

        searchProductPage.searchByPhrase(lmCode);
        ProductCardPage productCardPage = new ProductCardPage(getContext());
        productCardPage.shouldProductCardContainsText(lmCode);
        productCardPage.verifyUrlContainsString(lmCode);
        productCardPage.navigateBack();

        //Могут вернуться результаты, которые не содержат в названии поискового критерия
        /*searchProductPage.searchByPhrase(searchContext);
        searchProductPage.shouldProductCardContainsText(searchContext);
        searchProductPage.clearSearchInputByClearBtn();*/

        searchProductPage.searchByPhrase(shortSearchPhrase);
        searchProductPage.verifyUrlContainsString(ParamNames.byNameLikeParamName + shortSearchPhrase);
        searchProductPage.shouldProductCardContainsText(shortSearchPhrase);
        searchProductPage.clearSearchInputByClearBtn();

        searchProductPage.searchByPhrase(barCode);
        productCardPage.shouldProductCardContainsText(barCode);
        productCardPage.navigateBack();

        searchProductPage.searchByPhrase(shortLmCode);
        searchProductPage.verifyUrlContainsString(ParamNames.bylmCodeParamName + shortLmCode);
        searchProductPage.shouldProductCardContainsText(shortLmCode);
        searchProductPage.clearSearchInputByClearBtn();

        searchProductPage.searchByPhrase(shortBarCode);
        searchProductPage.verifyUrlContainsString(ParamNames.byBarCodeParamName + shortBarCode);
        searchProductPage.shouldProductCardContainsText(shortBarCode);
    }

    @Test(description = "C22782935 Clear field 'x'")
    public void testClearTextInputByClearBtn() throws Exception {
        String searchPhrase = "123";

        GetCatalogSearch params = new GetCatalogSearch()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> results = sendRequestsSearchProductsBy(params);

        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.searchByPhrase(searchPhrase);

        searchProductPage.clearSearchInputByClearBtn();
        ProductItemDataList responseData = results.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(responseData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.shouldSearchInputBeEmpty();
    }

    @Test(description = "C23384732 nomenclature filters and navigation")
    public void testNomenclature() throws Exception {
        String dept = "005";
        String subDept = "510";
        String classId = "010";
        String subClassId = "010";

        GetCatalogSearch allDepartmentsParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        GetCatalogSearch departmentParams = new GetCatalogSearch()
                .setDepartmentId(dept.substring(2))
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        GetCatalogSearch subDepartmentParams = new GetCatalogSearch()
                .setDepartmentId(dept.substring(2))
                .setSubDepartmentId(subDept)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        GetCatalogSearch classParams = new GetCatalogSearch()
                .setDepartmentId(dept.substring(2))
                .setSubDepartmentId(subDept)
                .setClassId(classId.substring(1))
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        GetCatalogSearch subClassParams = new GetCatalogSearch()
                .setDepartmentId(dept.substring(2))
                .setSubDepartmentId(subDept)
                .setClassId(classId.substring(1))
                .setSubclassId(subClassId.substring(1))
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultsMap =
                sendRequestsSearchProductsBy(allDepartmentsParams, departmentParams, subDepartmentParams, classParams,
                        subClassParams);

        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(allDepartments);
        ProductItemDataList allDepartmentsData = resultsMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(allDepartmentsData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        searchProductPage.choseNomenclature(dept, null, null, null);
        searchProductPage.verifyUrlContainsString(ParamNames.departmentId + dept.substring(2));
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(dept);
        searchProductPage.shouldBreadCrumbsContainsPreviousNomenclatureName(allDepartments);
        ProductItemDataList departmentData = resultsMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(departmentData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        searchProductPage.choseNomenclature(dept, subDept, null, null);
        searchProductPage.verifyUrlContainsString(ParamNames.subdepartmentId + subDept);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(subDept);
        searchProductPage.shouldBreadCrumbsContainsPreviousNomenclatureName(dept);
        ProductItemDataList subDepartmentData = resultsMap.get(2).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(subDepartmentData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        searchProductPage.choseNomenclature(dept, subDept, classId, null);
        searchProductPage.verifyUrlContainsString(ParamNames.classId + classId.substring(1));
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(classId);
        searchProductPage.shouldBreadCrumbsContainsPreviousNomenclatureName(subDept);
        ProductItemDataList classData = resultsMap.get(3).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(classData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        searchProductPage.choseNomenclature(dept, subDept, classId, subClassId);
        searchProductPage.verifyUrlContainsString(ParamNames.subclassId + subClassId.substring(1));
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(subClassId);
        searchProductPage.shouldBreadCrumbsContainsPreviousNomenclatureName(classId);
        ProductItemDataList subClassData = resultsMap.get(4).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(subClassData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
    }

    @Test(description = "C23384733 sorting")
    public void testSorting() throws Exception {
        GetCatalogSearch defaultSort = new GetCatalogSearch()
                .setDepartmentId("5")
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        Response<ProductItemDataList> response = apiClient.searchProductsBy(defaultSort);
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.choseNomenclature("005", null, null, null);

        searchProductPage.choseSortType(SearchProductPage.SortType.LM_CODE_DESC);
        searchProductPage.verifyUrlContainsString(ParamNames.sortBy + "lmCode%7CDESC");
        searchProductPage.shouldProductsAreSorted(SearchProductPage.SortType.LM_CODE_DESC);

        searchProductPage.choseSortType(SearchProductPage.SortType.LM_CODE_ASC);
        searchProductPage.verifyUrlContainsString(ParamNames.sortBy + "lmCode%7CASC");
        searchProductPage.shouldProductsAreSorted(SearchProductPage.SortType.LM_CODE_ASC);

        searchProductPage.choseSortType(SearchProductPage.SortType.NAME_DESC);
        searchProductPage.verifyUrlContainsString(ParamNames.sortBy + "name%7CDESC");
        searchProductPage.shouldProductsAreSorted(SearchProductPage.SortType.NAME_DESC);

        searchProductPage.choseSortType(SearchProductPage.SortType.NAME_ASC);
        searchProductPage.verifyUrlContainsString(ParamNames.sortBy + "name%7CASC");
        searchProductPage.shouldProductsAreSorted(SearchProductPage.SortType.NAME_ASC);

        searchProductPage.choseSortType(SearchProductPage.SortType.DEFAULT);
        searchProductPage.shouldResponseEntityEqualsToViewEntity(response.asJson(), SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
    }

    @Test(description = "C23384739 searchHistory")
    public void testSearchHistory() throws Exception {
        String searchCriterion = "qqqq";
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.shouldSearchHistoryContainsEachElement(searchProductPage.createSearchHistory(11))
                .enterStringInSearchInput(searchCriterion)
                .shouldSearchHistoryElementsContainsSearchCriterion(searchCriterion);
    }

    @Test(description = "C22782963 Supplier")
    public void testSupplierFilter() throws Exception {
        final String FIRST_SUPPLIER_CODE = "1001123001";
        final String FIRST_SUPPLIER_NAME = "ООО Бард-Спб";
        final String SECOND_SUPPLIER_CODE = "12301";

        GetCatalogSearch fewSuppliersParams = new GetCatalogSearch()
                .setSupId(FIRST_SUPPLIER_CODE + "," + SECOND_SUPPLIER_CODE)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultMap =
                sendRequestsSearchProductsBy(fewSuppliersParams);

        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments)
                .choseSupplier(false, FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE)
                .shouldSupplierComboBoxContainsCorrectText(FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE)
                .applyFilters()
                .verifyUrlContainsString(ParamNames.supplierId + FIRST_SUPPLIER_CODE + "%2C" + SECOND_SUPPLIER_CODE);

        ProductItemDataList fewSuppliersData = resultMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(fewSuppliersData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        searchProductPage.deleteAllChosenSuppliers()
                .choseSupplier(false, FIRST_SUPPLIER_CODE)
                .shouldChosenSupplierCheckboxHasCorrectCondition(true, FIRST_SUPPLIER_NAME)
                .shouldSupplierComboBoxContainsCorrectText(FIRST_SUPPLIER_NAME)
                .deleteChosenSuppliers(false, FIRST_SUPPLIER_NAME)
                .shouldChosenSupplierCheckboxHasCorrectCondition(false, FIRST_SUPPLIER_NAME)
                .shouldSupplierComboBoxContainsCorrectText(null);
    }

    //bug
    @Test(description = "C22782965 AVS")
    public void testAvsFilter() throws Exception {
        LocalDate avsDate = LocalDate.of(2020, 3, 3);

        GetCatalogSearch avsParam = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize)
                .setAvsDate(String.format("between%%7C%s-0%s-0%sT00:00:00.000Z%%7C%s-0%s-0%sT00:00:00.000Z",
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(),
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1))
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);

        GetCatalogSearch avsNeqNullParam = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize)
                .setAvsDate("neq|null")
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultsMap =
                sendRequestsSearchProductsBy(avsParam, avsNeqNullParam);

        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.choseCheckboxFilter(false, SearchProductPage.Filters.AVS)
                .shouldCheckboxFilterHasCorrectCondition(true, SearchProductPage.Filters.AVS)
                .choseAvsDate(avsDate)
                .shouldAvsContainerContainsCorrectText(avsDate)
                .applyFilters();

        ProductItemDataList avsDateResponse = resultsMap.get(0).getData();
        ProductItemDataList avsNeqNullResponse = resultsMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(avsDateResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED)
                .clearAllFilters()
                .choseCheckboxFilter(true, SearchProductPage.Filters.AVS)
                .shouldAvsContainerContainsCorrectText(null)
                .shouldResponseEntityEqualsToViewEntity(avsNeqNullResponse, SearchProductPage.FilterFrame.MY_SHOP,
                        SearchProductPage.ViewMode.EXTENDED);

        searchProductPage.enterAvsDateManually(avsDate)
                .applyFilters()
                .shouldResponseEntityEqualsToViewEntity(avsDateResponse, SearchProductPage.FilterFrame.MY_SHOP,
                        SearchProductPage.ViewMode.EXTENDED)
                //bug of manually import
                .verifyUrlContainsString(ParamNames.avsDate + String.format(
                        "between%%7C%s-0%s-0%sT00%3A00%3A00.000Z%%7C%s-0%s-0%sT00%3A00%3A00.000Z", avsDate.getYear(),
                        avsDate.getMonthValue(), avsDate.getDayOfMonth(),
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1));
    }

    @Test(description = "C23384959 search by my shop filters group")
    public void testMyShopFiltersGroupSearch() throws Exception {

        GetCatalogSearch myShopFiltersParam = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize)
                .setGamma("A,S")
                .setTop("1,2")
                .setHasAvailableStock(true)
                .setTopEM(true)
                .setCtm(true);

        GetCatalogSearch bestPriceParams = new GetCatalogSearch()
                .setTop1000(true)
                .setBestPrice(true)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        GetCatalogSearch limitedOfferParams = new GetCatalogSearch()
                .setLimitedOffer(true)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        GetCatalogSearch orderTypeParams = new GetCatalogSearch()
                .setOrderType("MBO")
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultMap =
                sendRequestsSearchProductsBy(myShopFiltersParam,bestPriceParams, limitedOfferParams, orderTypeParams);

        FiltersData filtersData = new FiltersData();
        filtersData.setGammaFilters(new String[]{"Гамма А", "Гамма S"});
        filtersData.setTopFilters(new String[]{"Топ 1", "Топ 2"});
        filtersData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.HAS_AVAILABLE_STOCK,
                SearchProductPage.Filters.TOP_EM, SearchProductPage.Filters.CTM});

        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);
        searchProductPage.choseSeveralFilters(filtersData, true);
        ProductItemDataList myShopResponse = resultMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(myShopResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.gamma+"A%2CS", ParamNames.top+"1%2C2",
                ParamNames.hasAvailableStock+"true", ParamNames.topEM+"true", ParamNames.ctm+"true");

        filtersData.clearFilterData();
        searchProductPage.clearAllFilters();

        filtersData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.TOP_1000,
        SearchProductPage.Filters.BEST_PRICE});
        searchProductPage.choseSeveralFilters(filtersData, true);

        ProductItemDataList bestPrice = resultMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(bestPrice, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.bestPrice+"true", ParamNames.top1000+"true",
                ParamNames.shopId+EnvConstants.BASIC_USER_SHOP_ID);

        searchProductPage.clearAllFilters();
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.LIMITED_OFFER);
        ProductItemDataList limitedOffer = resultMap.get(2).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(limitedOffer, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.limitedOffer+"true");

        searchProductPage.clearAllFilters();
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.ORDERED);
        ProductItemDataList orderType = resultMap.get(3).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(orderType, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.orderType+"MBO");
    }

    @Test(description = "C23384960 search by all gamma filters group")
    public void testAllGammaFiltersGroupSearch() throws Exception {

        GetCatalogSearch ctmParam = new GetCatalogSearch()
                .setPageSize(defaultPageSize)
                .setGamma("A,S")
                .setCtm(true);

        GetCatalogSearch bestPriceParams = new GetCatalogSearch()
                .setTop1000(true)
                .setBestPrice(true)
                .setPageSize(defaultPageSize);

        GetCatalogSearch limitedOfferParams = new GetCatalogSearch()
                .setLimitedOffer(true)
                .setPageSize(defaultPageSize);

        GetCatalogSearch orderTypeParams = new GetCatalogSearch()
                .setOrderType("MBO")
                .setPageSize(defaultPageSize);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultMap =
                sendRequestsSearchProductsBy(ctmParam,bestPriceParams, limitedOfferParams, orderTypeParams);

        FiltersData filtersData = new FiltersData();
        filtersData.setGammaFilters(new String[]{"Гамма А", "Гамма S"});
        filtersData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.CTM});

        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.switchFiltersFrame(SearchProductPage.FilterFrame.ALL_GAMMA_LM);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);
        searchProductPage.choseSeveralFilters(filtersData, true);
        ProductItemDataList ctmResponse = resultMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(ctmResponse, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.gamma+"A%2CS", ParamNames.ctm+"true");
        searchProductPage.verifyUrlContainsStringNot(ParamNames.shopId);

        filtersData.clearFilterData();
        searchProductPage.clearAllFilters();

        filtersData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.TOP_1000,
                SearchProductPage.Filters.BEST_PRICE});
        searchProductPage.choseSeveralFilters(filtersData, true);

        ProductItemDataList bestPrice = resultMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(bestPrice, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.bestPrice+"true", ParamNames.top1000+"true");
        searchProductPage.verifyUrlContainsStringNot(ParamNames.shopId);

        searchProductPage.clearAllFilters();
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.LIMITED_OFFER);
        ProductItemDataList limitedOffer = resultMap.get(2).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(limitedOffer, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.limitedOffer+"true");
        searchProductPage.verifyUrlContainsStringNot(ParamNames.shopId);

        searchProductPage.clearAllFilters();
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.ORDERED);
        ProductItemDataList orderType = resultMap.get(3).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(orderType, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.orderType+"MBO");
        searchProductPage.verifyUrlContainsStringNot(ParamNames.shopId);
    }
}
