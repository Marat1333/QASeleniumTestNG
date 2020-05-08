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
import com.leroy.magportal.ui.pages.products.ExtendedProductCardPage;
import com.leroy.magportal.ui.pages.products.ParamNames;
import com.leroy.magportal.ui.pages.products.ProductCardPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import io.qameta.allure.Issue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
                .setGamma("A")
                .setDepartmentId(DEPT_ID.substring(2))
                .setSubDepartmentId(SUB_DEPT_ID)
                .setClassId(CLASS_ID)
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
        searchProductPage.choseSortType(SearchProductPage.SortType.LM_CODE_ASC);
        searchProductPage.choseGammaFilter("Гамма А");
        searchProductPage.applyFilters();
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

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.navigateToPreviousNomenclatureElement("Каталог товаров");

        //Step 1
        log.step("Выполнить поиск по полному лм коду " + lmCode);
        ProductCardPage productCardPage = searchProductPage.searchByPhrase(lmCode);
        productCardPage.shouldProductCardContainsText(lmCode);
        productCardPage.verifyUrlContainsString(lmCode);
        searchProductPage.switchToWindow();

        //Могут вернуться результаты, которые не содержат в названии поискового критерия
        /*searchProductPage.searchByPhrase(searchContext);
        searchProductPage.shouldProductCardContainsText(searchContext);
        searchProductPage.clearSearchInputByClearBtn();*/

        //Step 2
        log.step("Выполнить поиск по имени, длинной менее 4 символов " + shortSearchPhrase);
        searchProductPage.clearSearchInputByClearBtn();
        searchProductPage.searchByPhrase(shortSearchPhrase);
        searchProductPage.verifyUrlContainsString(ParamNames.byNameLikeParamName + shortSearchPhrase);
        searchProductPage.shouldProductCardContainsText(shortSearchPhrase);
        searchProductPage.clearSearchInputByClearBtn();

        //Step 3
        log.step("Выполнить поиск по штрихкоду " + barCode);
        searchProductPage.searchByPhrase(barCode);
        productCardPage.shouldProductCardContainsText(barCode);
        searchProductPage.switchToWindow();

        //Step 4
        log.step("Выполнить поиск по короткому лм коду " + shortLmCode);
        searchProductPage.clearSearchInputByClearBtn();
        searchProductPage.searchByPhrase(shortLmCode);
        searchProductPage.verifyUrlContainsString(ParamNames.bylmCodeParamName + shortLmCode);
        searchProductPage.shouldProductCardContainsText(shortLmCode);
        searchProductPage.clearSearchInputByClearBtn();

        //Step 5
        log.step("Выполнить поиск по короткому штрих-коду " + shortBarCode);
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

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("Выполнить поиск по поисковой фразе " + searchPhrase);
        searchProductPage.<SearchProductPage>searchByPhrase(searchPhrase)
                .shouldSearchCriterionIs(true, searchPhrase)
                .shouldBreadCrumbsContainsNomenclatureName(true, EnvConstants.BASIC_USER_DEPARTMENT_ID);

        //Step 2
        log.step("Очистить поисковую строку по нажатию на кнопку \"крест\" в поисковом инпуте");
        searchProductPage.clearSearchInputByClearBtn()
                .shouldSearchCriterionIs(false, searchPhrase)
                .shouldBreadCrumbsContainsNomenclatureName(false, EnvConstants.BASIC_USER_DEPARTMENT_ID);
        ;
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

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("Перейти на уровень товарной иерархии - все отделы");
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(allDepartments);
        ProductItemDataList allDepartmentsData = resultsMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(allDepartmentsData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        //Step 2
        log.step("Выбрать отдел");
        searchProductPage.choseNomenclature(dept, null, null, null);
        searchProductPage.verifyUrlContainsString(ParamNames.departmentId + dept.substring(2));
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(dept);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, allDepartments);
        ProductItemDataList departmentData = resultsMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(departmentData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        //Step 3
        log.step("Выбрать подотдел");
        searchProductPage.choseNomenclature(dept, subDept, null, null);
        searchProductPage.verifyUrlContainsString(ParamNames.subdepartmentId + subDept);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(subDept);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, dept);
        ProductItemDataList subDepartmentData = resultsMap.get(2).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(subDepartmentData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        //Step 4
        log.step("Выбрать тип");
        searchProductPage.choseNomenclature(dept, subDept, classId, null);
        searchProductPage.verifyUrlContainsString(ParamNames.classId + classId.substring(1));
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(classId);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, subDept);
        ProductItemDataList classData = resultsMap.get(3).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(classData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        //Step 5
        log.step("Выбрать подтип");
        searchProductPage.choseNomenclature(dept, subDept, classId, subClassId);
        searchProductPage.verifyUrlContainsString(ParamNames.subclassId + subClassId.substring(1));
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(subClassId);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, classId);
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

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.choseNomenclature("005", null, null, null);

        //Step 1
        log.step("Выбрать тип сортировки - по лмкоду DESC");
        searchProductPage.choseSortType(SearchProductPage.SortType.LM_CODE_DESC);
        searchProductPage.verifyUrlContainsString(ParamNames.sortBy + "lmCode%7CDESC");
        searchProductPage.shouldProductsAreSorted(SearchProductPage.SortType.LM_CODE_DESC);
        searchProductPage.shouldSortComboBoxContainsText(SearchProductPage.SortType.LM_CODE_DESC.getName());

        //Step 2
        log.step("Выбрать тип сортировки - по лмкоду ASC");
        searchProductPage.choseSortType(SearchProductPage.SortType.LM_CODE_ASC);
        searchProductPage.verifyUrlContainsString(ParamNames.sortBy + "lmCode%7CASC");
        searchProductPage.shouldProductsAreSorted(SearchProductPage.SortType.LM_CODE_ASC);
        searchProductPage.shouldSortComboBoxContainsText(SearchProductPage.SortType.LM_CODE_ASC.getName());

        //Step 3
        log.step("Выбрать тип сортировки - по названию DESC");
        searchProductPage.choseSortType(SearchProductPage.SortType.NAME_DESC);
        searchProductPage.verifyUrlContainsString(ParamNames.sortBy + "name%7CDESC");
        searchProductPage.shouldProductsAreSorted(SearchProductPage.SortType.NAME_DESC);
        searchProductPage.shouldSortComboBoxContainsText(SearchProductPage.SortType.NAME_DESC.getName());

        //Step 4
        log.step("Выбрать тип сортировки - по названию ASC");
        searchProductPage.choseSortType(SearchProductPage.SortType.NAME_ASC);
        searchProductPage.verifyUrlContainsString(ParamNames.sortBy + "name%7CASC");
        searchProductPage.shouldProductsAreSorted(SearchProductPage.SortType.NAME_ASC);
        searchProductPage.shouldSortComboBoxContainsText(SearchProductPage.SortType.NAME_ASC.getName());

        //Step 5
        log.step("Выбрать тип сортировки - по умолчанию (по популярности товара)");
        searchProductPage.choseSortType(SearchProductPage.SortType.DEFAULT);
        searchProductPage.shouldResponseEntityEqualsToViewEntity(response.asJson(), SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED)
                .verifyUrlContainsStringNot(ParamNames.sortBy);
        searchProductPage.shouldSortComboBoxContainsText(SearchProductPage.SortType.DEFAULT.getName());
    }

    @Test(description = "C23384739 searchHistory")
    public void testSearchHistory() throws Exception {
        String searchCriterion = "qqqq";
        int notesQuantity = 11;
        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("Создать историю поиска из " + notesQuantity + " элементов");
        searchProductPage.shouldSearchHistoryContainsEachElement(searchProductPage.createSearchHistory(notesQuantity));

        //Step 2
        log.step("Ввести поисковой критерий который идентичен хотя бы 1 записи в истории поиска");
        searchProductPage.enterStringInSearchInput(searchCriterion)
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

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);

        //Step 1
        log.step("Выбрать двух поставщиков и осуществить поиск по фильтру поставщиков");
        searchProductPage.choseSupplier(false, FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE)
                .shouldSupplierComboBoxContainsCorrectText(false, FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE)
                .applyFilters()
                .verifyUrlContainsString(ParamNames.supplierId + FIRST_SUPPLIER_CODE + "%2C" + SECOND_SUPPLIER_CODE);
        ProductItemDataList fewSuppliersData = resultMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(fewSuppliersData, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        //Step 2
        log.step("Очистить выбранный фильтр по поставщику по нажатию на кнопку \"Очистить\" и выбрать одного поставщика");
        searchProductPage.deleteAllChosenSuppliers()
                .choseSupplier(false, FIRST_SUPPLIER_CODE)
                .shouldChosenSupplierCheckboxHasCorrectCondition(true, FIRST_SUPPLIER_NAME)
                .shouldSupplierComboBoxContainsCorrectText(false, FIRST_SUPPLIER_NAME);

        //Step 3
        log.step("Отменить выбор поставщика по нажатию на крест в овальном элементе с названием поставщика");
        searchProductPage.deleteChosenSuppliers(false, FIRST_SUPPLIER_NAME)
                .shouldChosenSupplierCheckboxHasCorrectCondition(false, FIRST_SUPPLIER_NAME)
                .shouldSupplierComboBoxContainsCorrectText(true);
    }

    @Issue("PUZ2-2209")
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

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("Выбрать фильтр \"Дата AVS\", указать дату и выполнить поиск");
        searchProductPage.choseCheckboxFilter(false, SearchProductPage.Filters.AVS)
                .shouldCheckboxFilterHasCorrectCondition(true, SearchProductPage.Filters.AVS)
                .choseAvsDate(avsDate)
                .shouldAvsContainerContainsCorrectText(false, avsDate)
                .applyFilters();
        ProductItemDataList avsDateResponse = resultsMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(avsDateResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED)
                .clearAllFilters();

        //Step 2
        log.step("Выбрать фильтр \"Дата AVS\", не выбирая даты");
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.AVS)
                .shouldAvsContainerContainsCorrectText(true, null);
        ProductItemDataList avsNeqNullResponse = resultsMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(avsNeqNullResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);

        //Step 3
        log.step("Выбрать фильтр \"Дата AVS\", ввести дату вручную и выполнить поиск");
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
                sendRequestsSearchProductsBy(myShopFiltersParam, bestPriceParams, limitedOfferParams, orderTypeParams);

        FiltersData filtersData = new FiltersData();
        filtersData.setGammaFilters(new String[]{"Гамма А", "Гамма S"});
        filtersData.setTopFilters(new String[]{"Топ 1", "Топ 2"});
        filtersData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.HAS_AVAILABLE_STOCK,
                SearchProductPage.Filters.TOP_EM, SearchProductPage.Filters.CTM});

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);

        //Step 1
        log.step("Выбрать фильтры: гамма А, гамма S, топ 1, топ 2, есть теор. запас, топ ЕМ, CTM и выполнить поиск по ним");
        searchProductPage.choseSeveralFilters(filtersData, true);
        ProductItemDataList myShopResponse = resultMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(myShopResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.gamma + "A%2CS", ParamNames.top + "1%2C2",
                ParamNames.hasAvailableStock + "true", ParamNames.topEM + "true", ParamNames.ctm + "true");

        filtersData.clearFilterData();
        searchProductPage.clearAllFilters();

        //Step 2
        log.step("Выбрать фильтры: топ 1000, лучшая цена и выполнить поиск по ним");
        filtersData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.TOP_1000,
                SearchProductPage.Filters.BEST_PRICE});
        searchProductPage.choseSeveralFilters(filtersData, true);
        ProductItemDataList bestPrice = resultMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(bestPrice, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.bestPrice + "true", ParamNames.top1000 + "true",
                ParamNames.shopId + EnvConstants.BASIC_USER_SHOP_ID);

        //Step 3
        log.step("Выбрать фильтр \"предложение ограничено\" и выполнить поиск по нему");
        searchProductPage.clearAllFilters();
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.LIMITED_OFFER);
        ProductItemDataList limitedOffer = resultMap.get(2).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(limitedOffer, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.limitedOffer + "true");

        //Step 4
        log.step("Выбрать фильтр \"под заказ\" и выполнить поиск по нему");
        searchProductPage.clearAllFilters();
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.ORDERED);
        ProductItemDataList orderType = resultMap.get(3).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(orderType, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.orderType + "MBO");
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
                sendRequestsSearchProductsBy(ctmParam, bestPriceParams, limitedOfferParams, orderTypeParams);

        FiltersData filtersData = new FiltersData();
        filtersData.setGammaFilters(new String[]{"Гамма А", "Гамма S"});
        filtersData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.CTM});

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("Переключиться на группу фильтров \"Вся гамма ЛМ\" и выбрать фильтры:" +
                " гамма А, гамма S, сtm и выполнить поиск по ним");
        searchProductPage.switchFiltersFrame(SearchProductPage.FilterFrame.ALL_GAMMA_LM);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);
        searchProductPage.choseSeveralFilters(filtersData, true);
        ProductItemDataList ctmResponse = resultMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(ctmResponse, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.gamma + "A%2CS", ParamNames.ctm + "true");
        searchProductPage.verifyUrlContainsStringNot(ParamNames.shopId);

        filtersData.clearFilterData();
        searchProductPage.clearAllFilters();

        //Step 2
        log.step("Выбрать фильтры: лучшая цена, топ 1000 и выполнить поиск по ним");
        filtersData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.TOP_1000,
                SearchProductPage.Filters.BEST_PRICE});
        searchProductPage.choseSeveralFilters(filtersData, true);
        ProductItemDataList bestPrice = resultMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(bestPrice, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.bestPrice + "true", ParamNames.top1000 + "true");
        searchProductPage.verifyUrlContainsStringNot(ParamNames.shopId);

        //Step 3
        log.step("Выбрать фильтр \"предложение ограничено\" и выполнить поиск по нему");
        searchProductPage.clearAllFilters();
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.LIMITED_OFFER);
        ProductItemDataList limitedOffer = resultMap.get(2).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(limitedOffer, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.limitedOffer + "true");
        searchProductPage.verifyUrlContainsStringNot(ParamNames.shopId);

        //Step 4
        log.step("выбрать фильтр \"под заказ\" и выполнить поиск по нему");
        searchProductPage.clearAllFilters();
        searchProductPage.choseCheckboxFilter(true, SearchProductPage.Filters.ORDERED);
        ProductItemDataList orderType = resultMap.get(3).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(orderType, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.orderType + "MBO");
        searchProductPage.verifyUrlContainsStringNot(ParamNames.shopId);
    }

    @Test(description = "C23384975 switching between My shop frame and All gamma frame")
    public void testSwitchMyShopToAllGamma() throws Exception {
        LocalDate avsDate = LocalDate.of(2020, 4, 9);
        LocalDate allGammaAvsDate = LocalDate.of(2020, 3, 2);
        final String FIRST_SUPPLIER_CODE = "1001123001";
        final String SECOND_SUPPLIER_CODE = "1002258015";

        GetCatalogSearch myShopParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setGamma("A,S")
                .setTop("1,2")
                .setHasAvailableStock(true)
                .setSupId(FIRST_SUPPLIER_CODE + "," + SECOND_SUPPLIER_CODE)
                .setAvsDate(String.format("between%%7C%s-0%s-0%sT00:00:00.000Z%%7C%s-0%s-%sT00:00:00.000Z",
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(),
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1));

        GetCatalogSearch allGammaParams = new GetCatalogSearch()
                .setGamma("P,T")
                .setLimitedOffer(true)
                .setAvsDate(String.format("between%%7C%s-0%s-0%sT00:00:00.000Z%%7C%s-0%s-0%sT00:00:00.000Z",
                        allGammaAvsDate.getYear(), allGammaAvsDate.getMonthValue(), allGammaAvsDate.getDayOfMonth(),
                        allGammaAvsDate.getYear(), allGammaAvsDate.getMonthValue(), allGammaAvsDate.getDayOfMonth() + 1));

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultMap =
                sendRequestsSearchProductsBy(allGammaParams, myShopParams);

        FiltersData myShopFilterData = new FiltersData();
        myShopFilterData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.HAS_AVAILABLE_STOCK});
        myShopFilterData.setTopFilters(new String[]{"Топ 1", "Топ 2"});
        myShopFilterData.setGammaFilters(new String[]{"Гамма А", "Гамма S"});
        myShopFilterData.setAvsDate(avsDate);
        myShopFilterData.setSuppliers(new String[]{FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE});

        FiltersData allGammaFilterData = new FiltersData();
        allGammaFilterData.setGammaFilters(new String[]{"Гамма P", "Гамма T"});
        allGammaFilterData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.LIMITED_OFFER});
        allGammaFilterData.setAvsDate(allGammaAvsDate);

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);

        //Step 1
        log.step("Выбрать несколько фильтров из группы фильтров \"Мой магазин\" и выполнить поиск по ним");
        searchProductPage.choseSeveralFilters(myShopFilterData, true);
        searchProductPage.checkFiltersChosen(myShopFilterData);

        //Step 2
        log.step("Переключится на группу фильтров \"Вся гамма ЛМ\"");
        searchProductPage.switchFiltersFrame(SearchProductPage.FilterFrame.ALL_GAMMA_LM);
        searchProductPage.checkFiltersNotChosen(myShopFilterData);

        //Step 3
        log.step("Выбрать несколько фильтров из группы фильтров \"Вся гамма ЛМ\" и выполнить поиск по ним");
        searchProductPage.choseSeveralFilters(allGammaFilterData, true);
        searchProductPage.checkFiltersChosen(allGammaFilterData);
        ProductItemDataList allGammaData = resultMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(allGammaData,
                SearchProductPage.FilterFrame.ALL_GAMMA_LM, SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsStringNot(ParamNames.shopId);

        //Step 4
        log.step("Переключится на группу фильтров \"Мой магазин\" и выполнить поиск по ранее выбранным фильтрам");
        searchProductPage.switchFiltersFrame(SearchProductPage.FilterFrame.MY_SHOP);
        searchProductPage.checkFiltersChosen(myShopFilterData);
        searchProductPage.applyFilters();
        ProductItemDataList myShopData = resultMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(myShopData,
                SearchProductPage.FilterFrame.MY_SHOP, SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.shopId);
    }

    @Test(description = "C22782968 Clear filters")
    public void testClearFilters() throws Exception {
        GetCatalogSearch myShopDefaultParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setPageSize(defaultPageSize);

        GetCatalogSearch myShopAllCatalogParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        GetCatalogSearch allGammaAllCatalogParams = new GetCatalogSearch()
                .setPageSize(defaultPageSize);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultMap =
                sendRequestsSearchProductsBy(myShopDefaultParams, myShopAllCatalogParams, allGammaAllCatalogParams);

        LocalDate avsDate = LocalDate.of(2020, 4, 9);
        final String FIRST_SUPPLIER_CODE = "1001123001";
        final String SECOND_SUPPLIER_CODE = "1002258015";

        FiltersData myShopFilterData = new FiltersData();
        myShopFilterData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.BEST_PRICE,
                SearchProductPage.Filters.HAS_AVAILABLE_STOCK});
        myShopFilterData.setTopFilters(new String[]{"Топ 1", "Топ 2"});
        myShopFilterData.setSuppliers(new String[]{FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE});
        myShopFilterData.setAvsDate(avsDate);

        FiltersData allGammaFilterData = new FiltersData();
        allGammaFilterData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.LIMITED_OFFER,
                SearchProductPage.Filters.CTM});
        allGammaFilterData.setGammaFilters(new String[]{"Гамма S", "Гамма P"});
        allGammaFilterData.setAvsDate(avsDate);

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("Выбрать несколько фильтров из группы фильтров \"Мой магазин\", выполнить поиск по ним и очистить " +
                "фильтры по нажатию на кнопку в виде метлы");
        searchProductPage.choseSeveralFilters(myShopFilterData, true);
        searchProductPage.clearAllFilters();
        searchProductPage.checkFiltersNotChosen(myShopFilterData);
        ProductItemDataList myShopDefaultResponse = resultMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(myShopDefaultResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsStringNot(ParamNames.top, ParamNames.hasAvailableStock, ParamNames.bestPrice,
                ParamNames.supplierId, ParamNames.avsDate);

        //Step 2
        log.step("Выбрать несколько фильтров из группы фильтров \"Мой магазин\", выполнить поиск по ним и очистить " +
                "фильтры по нажатию на элемент в хлебных крошках");
        searchProductPage.choseSeveralFilters(myShopFilterData, true);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);
        searchProductPage.checkFiltersNotChosen(myShopFilterData);
        ProductItemDataList myShopAllDepartmentsResponse = resultMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(myShopAllDepartmentsResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsStringNot(ParamNames.top, ParamNames.hasAvailableStock, ParamNames.bestPrice,
                ParamNames.supplierId, ParamNames.avsDate, ParamNames.departmentId);

        //Step 3
        log.step("Выбрать несколько фильтров из группы фильтров \"Мой магазин\", выполнить поиск по ним и очистить " +
                "фильтры по нажатию на кнопку \"Очистить фильтры\" в области результатов поиска");
        searchProductPage.choseSeveralFilters(myShopFilterData, true);
        searchProductPage.clearAllFiltersInProductFrame();
        searchProductPage.checkFiltersNotChosen(myShopFilterData);
        searchProductPage.shouldResponseEntityEqualsToViewEntity(myShopAllDepartmentsResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsStringNot(ParamNames.top, ParamNames.hasAvailableStock, ParamNames.bestPrice,
                ParamNames.supplierId, ParamNames.avsDate);

        //Step 4
        log.step("Выбрать несколько фильтров из группы фильтров \"Вся гамма ЛМ\", выполнить поиск по ним и очистить " +
                "фильтры по нажатию на элемент в хлебных крошках");
        searchProductPage.switchFiltersFrame(SearchProductPage.FilterFrame.ALL_GAMMA_LM);
        searchProductPage.choseNomenclature("0" + EnvConstants.BASIC_USER_DEPARTMENT_ID, null, null, null);
        searchProductPage.choseSeveralFilters(allGammaFilterData, true);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);
        searchProductPage.checkFiltersNotChosen(allGammaFilterData);
        ProductItemDataList allGammaAllDepartmentsResponse = resultMap.get(2).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(allGammaAllDepartmentsResponse, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsStringNot(ParamNames.gamma, ParamNames.top, ParamNames.hasAvailableStock, ParamNames.bestPrice,
                ParamNames.supplierId, ParamNames.avsDate, ParamNames.shopId, ParamNames.limitedOffer, ParamNames.ctm);

        //Step 5
        log.step("Выбрать несколько фильтров из группы фильтров \"Вся гамма ЛМ\", выполнить поиск по ним и очистить " +
                "фильтры по нажатию на кнопку в виде метлы");
        searchProductPage.choseSeveralFilters(allGammaFilterData, true);
        searchProductPage.clearAllFilters();
        searchProductPage.checkFiltersNotChosen(allGammaFilterData);
        searchProductPage.shouldResponseEntityEqualsToViewEntity(allGammaAllDepartmentsResponse, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsStringNot(ParamNames.gamma, ParamNames.top, ParamNames.hasAvailableStock, ParamNames.bestPrice,
                ParamNames.supplierId, ParamNames.avsDate, ParamNames.shopId, ParamNames.limitedOffer, ParamNames.ctm);

        //Step 6
        log.step("Выбрать несколько фильтров из группы фильтров \"Вся гамма ЛМ\", выполнить поиск по ним и очистить " +
                "фильтры по нажатию на кнопку \"Очистить фильтры\" в области результатов поиска");
        searchProductPage.choseSeveralFilters(allGammaFilterData, true);
        searchProductPage.clearAllFiltersInProductFrame();
        searchProductPage.checkFiltersNotChosen(allGammaFilterData);
        searchProductPage.shouldResponseEntityEqualsToViewEntity(allGammaAllDepartmentsResponse, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsStringNot(ParamNames.gamma, ParamNames.top, ParamNames.hasAvailableStock, ParamNames.bestPrice,
                ParamNames.supplierId, ParamNames.avsDate, ParamNames.shopId, ParamNames.limitedOffer, ParamNames.ctm);

        //Step 7
        log.step("Выбрать несколько фильтров из группы фильтров \"Вся гамма ЛМ\", не применяя их, ввести в поисковую " +
                "строку фразу и выполнить поиск по ней");
        searchProductPage.choseSeveralFilters(allGammaFilterData, false);
        searchProductPage.searchByPhrase("1234");
        searchProductPage.checkFiltersNotChosen(allGammaFilterData);
    }

    @Test(description = "C23385397 search without submit")
    public void testSearchWithoutSubmit() throws Exception {
        String byLmCodeParamValue = "1234";
        String deptId = EnvConstants.BASIC_USER_DEPARTMENT_ID;

        GetCatalogSearch nomenclatureParam = new GetCatalogSearch()
                .setByLmCode(byLmCodeParamValue)
                .setDepartmentId(deptId)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        GetCatalogSearch filterParam = new GetCatalogSearch()
                .setByLmCode(byLmCodeParamValue)
                .setGamma("A")
                .setDepartmentId(deptId)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(defaultPageSize);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultMap =
                sendRequestsSearchProductsBy(nomenclatureParam, filterParam);

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);
        searchProductPage.navigateToPreviousNomenclatureElement(allDepartments);

        //Step 1
        log.step("Заполнить поисковую строку и выбрать элемент товарной иерархии");
        searchProductPage.fillSearchInput(byLmCodeParamValue);
        searchProductPage.choseNomenclature(0 + deptId, null, null, null);
        ProductItemDataList nomenclatureResponse = resultMap.get(0).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(nomenclatureResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.bylmCodeParamName + byLmCodeParamValue);

        //Step 2
        log.step("ввести поисковую фразу в поисковую строку");
        searchProductPage.clearSearchInputByClearBtn();
        searchProductPage.fillSearchInput(byLmCodeParamValue);

        //Step 3
        log.step("Выбрать подотдел в товарной иерархии и перейти на уровень отдела");
        searchProductPage.choseNomenclature(0 + deptId, "1505", null, null);
        searchProductPage.navigateToPreviousNomenclatureElement(deptId);
        searchProductPage.shouldResponseEntityEqualsToViewEntity(nomenclatureResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.bylmCodeParamName + byLmCodeParamValue);

        //Step 4
        log.step("ввести поисковую фразу в поисковую строку, выбрать фильтр и осуществить поиск");
        searchProductPage.clearSearchInputByClearBtn();
        searchProductPage.fillSearchInput(byLmCodeParamValue);
        searchProductPage.choseGammaFilter("Гамма А");
        searchProductPage.applyFilters();
        ProductItemDataList filterResponse = resultMap.get(1).getData();
        searchProductPage.shouldResponseEntityEqualsToViewEntity(filterResponse, SearchProductPage.FilterFrame.MY_SHOP,
                SearchProductPage.ViewMode.EXTENDED);
        searchProductPage.verifyUrlContainsString(ParamNames.bylmCodeParamName + byLmCodeParamValue);
    }

    @Test(description = "C23385398 search without changes")
    public void testSearchWithoutChanges() throws Exception {
        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("Ввести в поисковую строку фразу и несколько раз выполнить поиск по ней");
        searchProductPage.fillSearchInput("1234").
                shouldRequestHasBeenInitializedNTimes(3, true);

        //Step 2
        log.step("Выполнить несколько раз поиск по нажатию на кнопку \"Показать товары\"");
        searchProductPage.shouldRequestHasBeenInitializedNTimes(4, false);
    }

    @Test(description = "C23388802 search by browser url input")
    public void testSearchByUrl() throws Exception {
        LocalDate avsDate = LocalDate.of(2020, 4, 9);
        final String FIRST_SUPPLIER_CODE = "1001123001";
        final String SECOND_SUPPLIER_CODE = "1002258015";
        final String LM_CODE_PART = "1234";

        FiltersData myShopFilterData = new FiltersData();
        myShopFilterData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.BEST_PRICE,
                SearchProductPage.Filters.HAS_AVAILABLE_STOCK});
        myShopFilterData.setTopFilters(new String[]{"Топ 1", "Топ 2"});
        myShopFilterData.setSuppliers(new String[]{FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE});
        myShopFilterData.setAvsDate(avsDate);

        FiltersData allGammaFilterData = new FiltersData();
        allGammaFilterData.setCheckBoxes(new SearchProductPage.Filters[]{SearchProductPage.Filters.LIMITED_OFFER,
                SearchProductPage.Filters.CTM});
        allGammaFilterData.setGammaFilters(new String[]{"Гамма S", "Гамма P"});
        allGammaFilterData.setAvsDate(avsDate);

        Map<String, String> myShopParamMap = new HashMap<String, String>();
        myShopParamMap.put(ParamNames.shopId, EnvConstants.BASIC_USER_SHOP_ID);
        myShopParamMap.put(ParamNames.departmentId, EnvConstants.BASIC_USER_DEPARTMENT_ID);
        myShopParamMap.put(ParamNames.bylmCodeParamName, LM_CODE_PART);
        myShopParamMap.put(ParamNames.sortBy, "name%7CASC");
        myShopParamMap.put(ParamNames.bestPrice, "true");
        myShopParamMap.put(ParamNames.hasAvailableStock, "true");
        myShopParamMap.put(ParamNames.top, "1,2");
        myShopParamMap.put(ParamNames.supplierId, FIRST_SUPPLIER_CODE + "," + SECOND_SUPPLIER_CODE);
        myShopParamMap.put(ParamNames.avsDate, String.format("between%%7C%s-0%s-0%sT00:00:00.000Z%%7C%s-0%s-%sT00:00:00.000Z", +
                avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(), +
                avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1));

        Map<String, String> allGammaParamMap = new HashMap<String, String>();
        allGammaParamMap.put(ParamNames.limitedOffer, "true");
        allGammaParamMap.put(ParamNames.ctm, "true");
        allGammaParamMap.put(ParamNames.gamma, "S,P");
        allGammaParamMap.put(ParamNames.avsDate, String.format("between%%7C%s-0%s-0%sT00:00:00.000Z%%7C%s-0%s-%sT00:00:00.000Z", +
                avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(), +
                avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1));

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("Перейти по url с параметрами фильтров, номенклатуры, сортировки, поисковой фразы и фильтру \"Мой магазин\"");
        searchProductPage.navigateToWithFilters(myShopParamMap);
        searchProductPage.checkFiltersChosen(myShopFilterData);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, EnvConstants.BASIC_USER_DEPARTMENT_ID);
        searchProductPage.shouldSearchCriterionIs(true, LM_CODE_PART);
        searchProductPage.shouldSearchInputContainsText(LM_CODE_PART);
        searchProductPage.shouldSortComboBoxContainsText(SearchProductPage.SortType.NAME_ASC.getName());

        //Step 2
        log.step("Перейти по url с параметрами фильтров и фильтру \"Вся гамма ЛМ\"");
        searchProductPage.navigateToWithFilters(allGammaParamMap);
        searchProductPage.checkFiltersChosen(allGammaFilterData);
    }

    @Test(description = "C23388851 navigate to product card")
    public void testNavigateToProductCard() throws Exception {
        String lmCode = "11284539";

        //Pre-conditions
        SearchProductPage searchProductPage = loginAndGoTo(SearchProductPage.class);

        //Step 1
        log.step("Перейти в карточку товара при примененном расширенном режиме отображения и фильтре \"Мой магазин\"");
        ExtendedProductCardPage extendedProductCardPage = searchProductPage.navigateToProductCart(lmCode,
                SearchProductPage.FilterFrame.MY_SHOP, SearchProductPage.ViewMode.EXTENDED);
        extendedProductCardPage.verifyRequiredElement();

        //Step 2
        log.step("Перейти в карточку товара при примененном табличном режиме отображения и фильтре \"Мой магазин\"");
        searchProductPage.switchToWindow();
        searchProductPage.switchViewMode(SearchProductPage.ViewMode.LIST);
        extendedProductCardPage = searchProductPage.navigateToProductCart(lmCode,
                SearchProductPage.FilterFrame.MY_SHOP, SearchProductPage.ViewMode.LIST);
        extendedProductCardPage.verifyRequiredElement();

        //Step 3
        log.step("Перейти в карточку товара при примененном расширенном режиме отображения и фильтре \"Вся гамма ЛМ\"");
        searchProductPage.switchToWindow();
        searchProductPage.switchFiltersFrame(SearchProductPage.FilterFrame.ALL_GAMMA_LM);
        ProductCardPage productCardPage = searchProductPage.navigateToProductCart(lmCode, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.EXTENDED);
        productCardPage.verifyRequiredElement();

        //Step 4
        log.step("Перейти в карточку товара при примененном табличном режиме отображения и фильтре \"Вся гамма ЛМ\"");
        searchProductPage.switchToWindow();
        searchProductPage.switchViewMode(SearchProductPage.ViewMode.LIST);
        productCardPage = searchProductPage.navigateToProductCart(lmCode, SearchProductPage.FilterFrame.ALL_GAMMA_LM,
                SearchProductPage.ViewMode.LIST);
        productCardPage.verifyRequiredElement();

    }

}
