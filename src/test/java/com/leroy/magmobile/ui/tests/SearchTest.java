package com.leroy.magmobile.ui.tests;

import com.leroy.constants.EnvConstants;
import com.leroy.core.api.Module;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.ServiceItemDataList;
import com.leroy.magmobile.api.enums.CatalogSearchFields;
import com.leroy.magmobile.api.enums.SortingOrder;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogServicesSearch;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.models.search.FiltersData;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.PricesAndQuantityPage;
import com.leroy.magmobile.ui.pages.sales.ProductCardPage;
import com.leroy.magmobile.ui.pages.sales.product_and_service.AddServicePage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SimilarProductsPage;
import com.leroy.magmobile.ui.pages.search.FilterPage;
import com.leroy.magmobile.ui.pages.search.NomenclatureSearchPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.pages.search.SuppliersSearchPage;
import com.leroy.magmobile.ui.pages.search.modal.SortPage;
import io.qameta.allure.Issue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Guice(modules = {Module.class})
public class SearchTest extends AppBaseSteps {

    private CatalogSearchClient searchClient;

    @BeforeClass
    public void setUp() {
        searchClient = apiClientProvider.getCatalogSearchClient();
    }

    private static final String ALL_DEPARTMENTS_TEXT = "Все отделы";

    private GetCatalogSearch buildDefaultCatalogSearchParams() {
        return new GetCatalogSearch()
                .setPageSize(3)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setStartFrom(1)
                .setHasAvailableStock(true);
    }

    private HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> sendRequestsSearchProductsBy(
            GetCatalogSearch... paramsArray) {
        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> resultMap = new HashMap<>();
        int i = 0;
        for (GetCatalogSearch param : paramsArray) {
            ThreadApiClient<ProductItemDataList, CatalogSearchClient> myThread = new ThreadApiClient<>(
                    searchClient);
            myThread.sendRequest(client -> client.searchProductsBy(param));
            resultMap.put(i, myThread);
            i++;
        }
        return resultMap;
    }

    private HashMap<Integer, ThreadApiClient<ServiceItemDataList, CatalogSearchClient>> sendRequestsSearchServicesBy(
            GetCatalogServicesSearch... paramsArray) {
        HashMap<Integer, ThreadApiClient<ServiceItemDataList, CatalogSearchClient>> resultMap = new HashMap<>();
        int i = 0;
        for (GetCatalogServicesSearch param : paramsArray) {
            ThreadApiClient<ServiceItemDataList, CatalogSearchClient> myThread = new ThreadApiClient<>(
                    searchClient);
            myThread.sendRequest(client -> client.searchServicesBy(param));
            resultMap.put(i, myThread);
            i++;
        }
        return resultMap;
    }

    @Test(description = "C3200996 Поиск товара по критериям", priority = 1)
    public void testC3200996() throws Exception {
        String lmCode = "10008698";
        String searchContext = "Тепломир радиатор";
        String shortSearchPhrase = "12";
        String barCode = "5902120110575";
        String shortLmCode = "1506";
        String shortBarCode = "590212011";
        int entityCount = 3;

        GetCatalogSearch byLmParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setByLmCode(lmCode);

        GetCatalogSearch byBarCodeParams = buildDefaultCatalogSearchParams()
                .setByBarCode(barCode);

        GetCatalogSearch byShortLmCodeParams = buildDefaultCatalogSearchParams()
                .setByLmCode(shortLmCode);

        GetCatalogSearch byShortBarCodeParams = buildDefaultCatalogSearchParams()
                .setByBarCode(shortBarCode);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(byLmParams, byBarCodeParams, byShortLmCodeParams, byShortBarCodeParams);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);

        // Step 1
        step("Нажмите на поле Поиск товаров и услуг");
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false)
                .verifyRequiredElements();
        // Step 2
        step("Перейдите в окно выбора единицы номенклатуры");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow()
                .verifyRequiredElements();
        // Step 3
        step("Вернитесь на окно выбора отдела");
        nomenclatureSearchPage.returnBackNTimes(1)
                .verifyNomenclatureBackBtnVisibility(false);
        // Step 4
        step("Нажмите 'Показать все товары'");
        nomenclatureSearchPage.clickShowAllProductsBtn()
                .verifyRequiredElements()
                .shouldCountOfProductsOnPageMoreThan(1);

        // Step 5
        step("Введите полное значение для поиска по ЛМ коду| 10008698");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        ProductDescriptionPage productCardPage = new ProductDescriptionPage()
                .verifyRequiredElements(true)
                .shouldProductLMCodeIs(lmCode);
        searchProductPage = productCardPage.returnBack();
        ProductItemDataList d1 = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(d1, SearchProductPage.CardType.COMMON, entityCount);

        // Step 6
        step("Введите название товара для поиска");
        searchProductPage.enterTextInSearchFieldAndSubmit(searchContext);
        searchProductPage.shouldCardsContainText(
                searchContext, SearchProductPage.CardType.COMMON, 3);

        // Step 7
        step("Введите название товара для поиска");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortSearchPhrase);
        searchProductPage.shouldCardsContainText(
                shortSearchPhrase, SearchProductPage.CardType.COMMON, 3);

        // Step 8
        step("Ввести штрихкод вручную");
        searchProductPage.enterTextInSearchFieldAndSubmit(barCode);
        productCardPage = new ProductDescriptionPage()
                .verifyRequiredElements(true)
                .shouldProductBarCodeIs(barCode);
        searchProductPage = productCardPage.returnBack();
        ProductItemDataList d2 = apiThreads.get(1).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(
                d2, SearchProductPage.CardType.COMMON, entityCount);

        // Step 9
        step("Введите часть ЛМ кода для поиска");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortLmCode);
        ProductItemDataList d3 = apiThreads.get(2).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(
                d3, SearchProductPage.CardType.COMMON, entityCount);

        // Step 10
        step("Ввести в поисковую строку положительное число длинной >8 символов (" + shortBarCode + ") и инициировать поиск");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortBarCode);
        ProductItemDataList d4 = apiThreads.get(3).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(
                d4, SearchProductPage.CardType.COMMON, entityCount);
    }

    @Test(description = "C22846686 Мой магазин. Выбор фильтров каждого блока фильтров", priority = 1)
    public void testC22846686() throws Exception {
        LocalDate avsDate = LocalDate.of(2019, 5, 23);
        String supplierSearchContext = "1000743002";
        final String TOP = "0";
        final String GAMMA = "B";
        final String departmentId = "2";
        int entityCount = 3;

        GetCatalogSearch gammaParam = buildDefaultCatalogSearchParams()
                .setGamma(GAMMA)
                .setDepartmentId(departmentId);

        GetCatalogSearch topParam = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setStartFrom(1)
                .setPageSize(entityCount)
                .setTop(TOP)
                .setDepartmentId(departmentId);

        GetCatalogSearch hasAvailableStockParam = buildDefaultCatalogSearchParams()
                .setDepartmentId(departmentId);

        GetCatalogSearch orderedProductTypeParam = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setStartFrom(1)
                .setPageSize(entityCount)
                .setDepartmentId(departmentId)
                .setOrderType("MBO");

        GetCatalogSearch avsParam = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setStartFrom(1)
                .setPageSize(entityCount)
                .setDepartmentId(departmentId)
                .setAvsDate(String.format("between%%7C%s-0%s-%sT00:00:00.000Z%%7C%s-0%s-%sT00:00:00.000Z",
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(),
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1));

        GetCatalogSearch supplierIdParam = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setStartFrom(1)
                .setPageSize(entityCount)
                .setDepartmentId(departmentId)
                .setSupId(supplierSearchContext);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(gammaParam, topParam, hasAvailableStockParam, orderedProductTypeParam, avsParam, supplierIdParam);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId("00" + departmentId, null, null, null);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        FilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        step("выбрать одну из гамм");
        filterPage.choseGammaFilter(FilterPage.GAMMA + " " + GAMMA);
        filterPage.applyChosenFilters();
        ProductItemDataList gammaResponse = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(
                gammaResponse, SearchProductPage.CardType.COMMON, entityCount);

        // Step 2
        step("выбрать один из топов");
        searchProductPage.goToFilterPage();
        filterPage.clickShowAllFiltersBtn();
        filterPage.clearAllFilters();
        filterPage.choseTopFilter(FilterPage.TOP + " " + TOP);
        filterPage.applyChosenFilters();
        ProductItemDataList topResponse = apiThreads.get(1).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(
                topResponse, SearchProductPage.CardType.COMMON, entityCount);

        // Step 3
        step("выбрать 1 из чек-боксов блока с типами товаров");
        searchProductPage.goToFilterPage();
        filterPage.clickShowAllFiltersBtn();
        filterPage.clearAllFilters();
        filterPage.choseCheckBoxFilter(FilterPage.HAS_AVAILABLE_STOCK);
        filterPage.applyChosenFilters();
        ProductItemDataList bestPriceResponse = apiThreads.get(2).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(
                bestPriceResponse, SearchProductPage.CardType.COMMON, entityCount);

        // Step 4
        step("выбрать тип товара");
        searchProductPage.goToFilterPage();
        filterPage.clearAllFilters();
        filterPage.choseProductType(FilterPage.ORDERED_PRODUCT_TYPE);
        filterPage.applyChosenFilters();
        ProductItemDataList orderedProductResponse = apiThreads.get(3).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(
                orderedProductResponse, SearchProductPage.CardType.COMMON, entityCount);

        // Step 5
        step("выбрать 1 поставщика");
        searchProductPage.goToFilterPage();
        filterPage.clearAllFilters();
        SuppliersSearchPage suppliersSearchPage = filterPage.goToSuppliersSearchPage(false);
        suppliersSearchPage.verifyRequiredElements();
        suppliersSearchPage.searchForAndChoseSupplier(supplierSearchContext);
        suppliersSearchPage.applyChosenSupplier();
        filterPage.applyChosenFilters();
        ProductItemDataList supplierResponse = apiThreads.get(5).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(
                supplierResponse, SearchProductPage.CardType.COMMON, entityCount);

        // Step 6
        step("выбрать дату авс");
        searchProductPage.goToFilterPage();
        filterPage.clearAllFilters();
        filterPage.choseAvsDate(avsDate);
        filterPage.applyChosenFilters();

        // Step 7
        searchProductPage.verifyRequiredElements();
        ProductItemDataList avsDateResponse = apiThreads.get(4).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(
                avsDateResponse, SearchProductPage.CardType.COMMON, entityCount);
    }

    @Test(description = "C22789209 Вся гамма ЛМ. Выбор фильтров каждого раздела", priority = 1)
    public void testC22789209() throws Exception {
        LocalDate avsDate = LocalDate.of(2020, 3, 2);
        final String GAMMA = "A";
        final String departmentId = "11";
        int entityCount = 3;

        GetCatalogSearch gammaParam = new GetCatalogSearch()
                .setGamma(GAMMA)
                .setPageSize(3)
                .setStartFrom(1)
                .setDepartmentId(departmentId);

        GetCatalogSearch ctmParam = new GetCatalogSearch()
                .setCtm(true)
                .setPageSize(3)
                .setStartFrom(1)
                .setDepartmentId(departmentId);

        GetCatalogSearch commonProductTypeParam = new GetCatalogSearch()
                .setOrderType("S")
                .setPageSize(3)
                .setStartFrom(1)
                .setDepartmentId(departmentId);

        GetCatalogSearch avsParam = new GetCatalogSearch()
                .setAvsDate(String.format("between%%7C%s-0%s-0%sT00:00:00.000Z%%7C%s-0%s-0%sT00:00:00.000Z",
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(), avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1))
                .setPageSize(3)
                .setStartFrom(1)
                .setDepartmentId(departmentId);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(gammaParam, ctmParam, commonProductTypeParam, avsParam);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId("0" + departmentId, null, null, null);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        FilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        step("выбрать овальный чек-бокс \"Вся гамма ЛМ\"");
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.shouldFilterHasBeenChosen(FilterPage.ALL_GAMMA_FRAME_TYPE);

        // Step 2
        step("выбрать одну из гамм");
        FilterPage allGammaFilterPage = new FilterPage();
        allGammaFilterPage.choseGammaFilter(FilterPage.GAMMA + " " + GAMMA);
        allGammaFilterPage.applyChosenFilters();
        ProductItemDataList gammaResponse = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(gammaResponse,
                SearchProductPage.CardType.ALL_GAMMA, entityCount);

        // Step 3
        step("выбрать 1 из чек-боксов блока с типами товаров");
        searchProductPage.goToFilterPage();
        allGammaFilterPage.choseGammaFilter(FilterPage.GAMMA + " " + GAMMA);
        allGammaFilterPage.choseCheckBoxFilter(FilterPage.CTM);
        allGammaFilterPage.applyChosenFilters();
        ProductItemDataList ctmProductResponse = apiThreads.get(1).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(ctmProductResponse,
                SearchProductPage.CardType.ALL_GAMMA, entityCount);

        // Step 4
        step("выбрать тип товара");
        searchProductPage.goToFilterPage();
        allGammaFilterPage.choseCheckBoxFilter(FilterPage.CTM);
        allGammaFilterPage.choseProductType(FilterPage.COMMON_PRODUCT_TYPE);
        allGammaFilterPage.applyChosenFilters();
        ProductItemDataList commonProductTypeProductResponse = apiThreads.get(2).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(commonProductTypeProductResponse,
                SearchProductPage.CardType.ALL_GAMMA, entityCount);

        // Step 5
        step("выбрать дату авс");
        searchProductPage.goToFilterPage();
        allGammaFilterPage.clearAllFilters();
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        allGammaFilterPage.choseAvsDate(avsDate);

        // Step 6
        step("подтвердить примененные фильтры");
        allGammaFilterPage.applyChosenFilters();
        searchProductPage.verifyRequiredElements();
        ProductItemDataList avsDateProductResponse = apiThreads.get(3).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(avsDateProductResponse,
                SearchProductPage.CardType.ALL_GAMMA, entityCount);
    }

    @Test(description = "C22789172 На поисковой запрос не вернулись результаты", priority = 2)
    public void testC22789172() throws Exception {
        final String byName = "АFHF13dasf";
        //TODO добавить проверку на отклонение по координатам

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);


        // Step 1
        step("Ввести в поле поиска значение, результат поиска по которому не вернется");
        searchProductPage.enterTextInSearchFieldAndSubmit(byName);
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);

        // Step 2
        step("выбрать более 1 фильтра и нажать \"Показать товары\"");
        FilterPage myShopFilterPage = searchProductPage.goToFilterPage();
        myShopFilterPage.choseGammaFilter(FilterPage.GAMMA + " ET");
        searchProductPage = myShopFilterPage.applyChosenFilters();

        searchProductPage.verifyRequiredElements();
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);
        searchProductPage.shouldDiscardAllFiltersBtnBeDisplayed();

        // Step 3
        step("Нажать на кнопку \"Cбросить фильтры\"");
        searchProductPage.discardFilters();
        searchProductPage.shouldNotDiscardAllFiltersBtnBeDisplayed();
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);

        // Step 4
        step("перейти в фильтры");
        searchProductPage.goToFilterPage();
        myShopFilterPage.scrollHorizontalWidget(FilterPage.GAMMA, FilterPage.GAMMA + " ET");
        myShopFilterPage.shouldFilterHasNotBeenChosen(FilterPage.GAMMA + " ET");
    }

    @Test(description = "C22789176 Вывод истории поиска", priority = 1)
    public void testC22789176() throws Exception {
        int searchPhrasesCount = 21;

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);

        // Step 1
        step("Нажать на поисковую строку");
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.shouldFirstSearchMsgBeDisplayed();

        // Step 2
        step("ввести любую неповторяющуюся поисковую фразу и выполнить поиск " + searchPhrasesCount + " раз");
        List<String> searchPhrases = searchProductPage.createSearchHistory(searchPhrasesCount);
        // На странице должно отображаться не более 20 записей, значит лишнее убираем
        searchPhrases.remove(0);
        Collections.reverse(searchPhrases);

        // Step 3
        step("Перезайти в поиск");
        searchProductPage.backToSalesPage();
        searchProductPage = mainProductAndServicesPage.clickSearchBar(true);
        searchProductPage.shouldSearchHistoryListIs(searchPhrases);

        // Step 4
        step("Начать вводить значение идентичное одному из ранее введенных");
        String exampleText = searchPhrases.get(searchPhrases.size() / 2);
        searchProductPage.enterTextInSearchField(exampleText)
                .verifySearchHistoryContainsSearchPhrase(exampleText);
    }

    @Test(description = "C22790468 Гамма ЛМ. Отсутствие: действий с товаром, истории продаж, поставки", priority = 2)
    public void testC22790468() throws Exception {
        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);

        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        FilterPage myShopFilterPage = searchProductPage.goToFilterPage();

        // Step 1
        step("Выбрать фильтр \"Вся гамма ЛМ\" и перейти на страницу результатов поиска");
        FilterPage allGammaFilterPage = myShopFilterPage
                .switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        allGammaFilterPage.applyChosenFilters();
        searchProductPage.verifyProductCardsHaveAllGammaView();

        // Step 2
        step("Перейти в одну из карточек товара");
        ProductDescriptionPage productDescriptionCardPage = searchProductPage.selectProductCardByIndex(1);
        productDescriptionCardPage.verifyCardHasGammaView();

        // Step 3
        step("Перейти во вкладку \"Аналогичные товары\"");
        SimilarProductsPage similarProductsPage = productDescriptionCardPage.switchTab(productDescriptionCardPage.SIMILAR_PRODUCTS);
        similarProductsPage.verifyProductCardsHaveAllGammaView();

        // Step 4
        step("Вернуться на вкладку \"Описание\" и Нажать на строку \"Цены в магазинах\"");
        productDescriptionCardPage = similarProductsPage.switchTab(similarProductsPage.DESCRIPTION);
        PricesAndQuantityPage pricesAndQuantityPage = productDescriptionCardPage.goToPricesAndQuantityPage();
        pricesAndQuantityPage.shouldNotSupplyBtnBeDisplayed();

    }

    @Test(description = "C22789191 Сортировка результатов поиска", priority = 2)
    public void testC22789191() throws Exception {
        // Pre-conditions
        int countOfCheckedProducts = 3;
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId("0" + EnvConstants.BASIC_USER_DEPARTMENT_ID, "1505", null, null);
        nomenclatureSearchPage.clickShowAllProductsBtn();

        // Step 1
        step("Раскрыть модальное окно сортировки");
        SortPage sortPage = searchProductPage.openSortPage()
                .verifyRequiredElements();

        // Step 2
        step("Выбрать сортировку по ЛМ-коду по возрастающей");
        searchProductPage = sortPage.selectSort(SortPage.SORT_BY_LM_ASC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_LM_ASC, SearchProductPage.CardType.COMMON, countOfCheckedProducts);

        // Step 3
        step("повторить шаг 1-2 для сортировки по лм-коду по убывающей");
        searchProductPage.openSortPage()
                .selectSort(SortPage.SORT_BY_LM_DESC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_LM_DESC, SearchProductPage.CardType.COMMON, countOfCheckedProducts);

        // Step 4
        step("повторить шаг 1-2 для сортировки по запасу по возрастающей");
        searchProductPage.openSortPage()
                .selectSort(SortPage.SORT_BY_ALPHABET_ASC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_ALPHABET_ASC, SearchProductPage.CardType.COMMON, countOfCheckedProducts);

        // Step 5
        step("повторить шаг 1-2 для сортировки по запасу по убывающей");
        searchProductPage.openSortPage()
                .selectSort(SortPage.SORT_BY_ALPHABET_DESC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_ALPHABET_DESC, SearchProductPage.CardType.COMMON, countOfCheckedProducts);
    }

    @Test(description = "C22789201 Номенклатура, Навигация и Поиск по структурным элементам номенклатуры", priority = 1)
    public void testC22789201() throws Exception {
        String dept = "015";
        String subDept = "1510";
        String classId = "0030";
        String subClassId = "0020";
        int entityCount = 3;

        GetCatalogSearch subclassParams = buildDefaultCatalogSearchParams()
                .setDepartmentId(dept.replaceAll("^0+", ""))
                .setSubDepartmentId(subDept.replaceAll("^0+", ""))
                .setClassId(classId.replaceAll("^0+", ""))
                .setSubclassId(subClassId.replaceAll("^0+", ""));

        GetCatalogSearch classParams = buildDefaultCatalogSearchParams()
                .setDepartmentId(dept.replaceAll("^0+", ""))
                .setSubDepartmentId(subDept.replaceAll("^0+", ""))
                .setClassId(classId.replaceAll("^0+", ""));

        GetCatalogSearch subdepartmentParams = buildDefaultCatalogSearchParams()
                .setDepartmentId(dept.replaceAll("^0+", ""))
                .setSubDepartmentId(subDept.replaceAll("^0+", ""));

        GetCatalogSearch departmentParams = buildDefaultCatalogSearchParams()
                .setDepartmentId(dept.replaceAll("^0+", ""));

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(subclassParams, classParams, subdepartmentParams, departmentParams);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);

        // Step 1
        step("Перейти на страницу выбора номенклатуры");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.verifyRequiredElements();
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept);

        // Step 2
        step("Перейти в список всех отделов");
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs("");
        nomenclatureSearchPage.shouldDepartmentsCountIs15();

        // Step 3
        step("нажать по кнопке \"показать все товары\"");
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(ALL_DEPARTMENTS_TEXT, true);

        // Step 4
        step("повтороить шаг 2-3 для отделов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.choseDepartmentId(dept, null, null, null);
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(dept, false);
        ProductItemDataList departmentNomenclatureResponce = apiThreads.get(3).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(departmentNomenclatureResponce,
                SearchProductPage.CardType.COMMON, entityCount);

        // Step 5
        step("повтороить шаг 2-3 для подотделов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, null, null);
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept + subDept);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(subDept, false);
        ProductItemDataList subdepartmentNomenclatureResponce = apiThreads.get(2).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(subdepartmentNomenclatureResponce,
                SearchProductPage.CardType.COMMON, entityCount);

        // Step 6
        step("повтороить шаг 2-3 для классов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(2);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, classId, null);
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept + subDept + classId);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(classId, false);
        ProductItemDataList classNomenclatureResponce = apiThreads.get(1).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(classNomenclatureResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 7
        step("повтороить шаг 2-3 для подклассов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(3);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, classId, subClassId);
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept + subDept + classId + subClassId);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(subClassId, false);
        ProductItemDataList subclassNomenclatureResponce = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(subclassNomenclatureResponce, SearchProductPage.CardType.COMMON, entityCount);

    }

    @Test(description = "C22789173 Поиск товара по одному введенному символу", priority = 2)
    public void testSearchByOneSymbol() throws Exception {
        final String searchContext = "1";

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);

        // Step 1
        step("ввести в поисковую строку 1 цифру");
        searchProductPage.enterTextInSearchField(searchContext);
        searchProductPage.shouldProgressBasIsNotVisible();

        // Step 2
        step("инициировать поиск");
        searchProductPage.submitSearch(true);
        searchProductPage.shouldNotFirstSearchMsgBeDisplayed();

        // Step 3
        step("очистить поисковую строку");
        searchProductPage.clearSearchInput();
        searchProductPage.shouldScannerBtnIsVisible();

        // Step 4
        step("нажать на поисковую строку и инициировать поиск");
        searchProductPage.submitSearch(true);
        //BUG есть возможность отправить поисковой запрос при пустой поисковой строке
        searchProductPage.shouldProgressBasIsNotVisible();
    }

    @Test(description = "C3200999 Проверка пагинации", priority = 2)
    public void testSearchPagePagination() throws Exception {
        String searchCriterion = "12";
        String dept = "005";
        final String GAMMA = "A";
        final int ENTITY_COUNT = 20;

        GetCatalogSearch paginationParams = new GetCatalogSearch()
                .setDepartmentId(dept.replaceAll("^0+", ""))
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.NAME, SortingOrder.ASC)
                .setByNameLike(searchCriterion)
                .setGamma(GAMMA)
                .setStartFrom(1)
                .setPageSize(ENTITY_COUNT);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(paginationParams);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);

        // Step 1
        step("Выбрать подотдел в номенклатуре");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId(dept, null, null, null);
        nomenclatureSearchPage.clickShowAllProductsBtn();

        // Step 2
        step("Введите неполное название или неполный ЛМ код товара");
        searchProductPage.enterTextInSearchFieldAndSubmit(searchCriterion);
        searchProductPage.verifyClearTextInputBtnIsVisible();

        // Step 3
        step("Выбрать отличный от дефолтного способ сортировки");
        SortPage sortPage = searchProductPage.openSortPage();
        sortPage.selectSort(SortPage.SORT_BY_ALPHABET_ASC);

        // Step 4
        step("Выбрать любой фильтр на странице выбора фильтров");
        FilterPage myShopFilterPage = searchProductPage.goToFilterPage();
        myShopFilterPage.choseGammaFilter(FilterPage.GAMMA + " " + GAMMA);
        myShopFilterPage.choseCheckBoxFilter(FilterPage.HAS_AVAILABLE_STOCK);
        myShopFilterPage.applyChosenFilters();

        // Step 5
        step("Проскролить вниз до упора");
        ProductItemDataList paginationResponce = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(paginationResponce, SearchProductPage.CardType.COMMON, ENTITY_COUNT);

    }

    @Test(description = "C22789202 Выбор фильтра поставщиков", priority = 2)
    public void testSuppliersFilter() throws Exception {
        final String FIRST_SUPPLIER_CODE = "1001123001";
        final String FIRST_SUPPLIER_NAME = "ООО Бард-Спб";
        final String SECOND_SUPPLIER_CODE = "12301";
        final String SECOND_SUPPLIER_NAME = "САЗИ";
        final String DEPT_ID = "1";

        GetCatalogSearch supplierIdParam = buildDefaultCatalogSearchParams()
                .setDepartmentId(DEPT_ID)
                .setSupId(FIRST_SUPPLIER_CODE + "," + SECOND_SUPPLIER_CODE);

        GetCatalogSearch defaultSearchParam = buildDefaultCatalogSearchParams()
                .setDepartmentId(DEPT_ID);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(supplierIdParam, defaultSearchParam);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId("00" + DEPT_ID, null, null, null);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        FilterPage myShopFilterPage = searchProductPage.goToFilterPage();

        // Step 1
        step("Перейти на страницу выбора поставщика");
        myShopFilterPage.clickShowAllFiltersBtn();
        SuppliersSearchPage suppliersSearchPage = myShopFilterPage.goToSuppliersSearchPage(false);
        suppliersSearchPage.verifyRequiredElements().shouldCountOfSuppliersIsMoreThan(1);
        //suppliersSearchPage.shouldSuppliersSortedByDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);   // #bug функционала departmentId пользака не подтягивается

        // Step 2
        step("ввести в поисковую строку код поставщика");
        suppliersSearchPage.searchForAndChoseSupplier(FIRST_SUPPLIER_CODE);
        suppliersSearchPage.shouldSupplierCardsContainText(FIRST_SUPPLIER_CODE);

        // Step 3
        step("выбрать поставщика и подтвердить выбор");
        suppliersSearchPage.applyChosenSupplier();
        myShopFilterPage.shouldSupplierButtonContainsText(1, FIRST_SUPPLIER_NAME);

        // Step 4
        step("повторить шаг 1-2, но искать по наименованию поставщика");
        myShopFilterPage.goToSuppliersSearchPage(false);
        suppliersSearchPage.searchForAndChoseSupplier(SECOND_SUPPLIER_NAME);
        suppliersSearchPage.shouldSupplierCardsContainText(SECOND_SUPPLIER_NAME);

        // Step 5
        step("выбрать поставщика и подтвердить выбор");
        suppliersSearchPage.applyChosenSupplier();
        myShopFilterPage.shouldSupplierButtonContainsText(2, null);

        // Step 6
        step("Применить фильтры выбранные фильтры");
        myShopFilterPage.applyChosenFilters();
        ProductItemDataList suppliersResponce = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(suppliersResponce,
                SearchProductPage.CardType.COMMON, 3);

        // Step 7
        step("Вернуться на страницу выбора фильтров и очистить фильтр поставщиков по нажатию на \"крест\"");
        searchProductPage.goToFilterPage();
        myShopFilterPage.clearSuppliersFilter("Выбрано");
        myShopFilterPage.shouldSupplierButtonContainsText(0, null);

        // Step 8
        step("Нажать \"показать товары\"");
        myShopFilterPage.applyChosenFilters();
        ProductItemDataList defaultParamsResponce = apiThreads.get(1).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(defaultParamsResponce,
                SearchProductPage.CardType.COMMON, 3);

        // Step 9
        step("Повторить шаг 1-2 и выбрать поставщика");
        searchProductPage.goToFilterPage();
        myShopFilterPage.clickShowAllFiltersBtn();
        myShopFilterPage.goToSuppliersSearchPage(false);
        suppliersSearchPage.searchForAndChoseSupplier(FIRST_SUPPLIER_CODE);
        suppliersSearchPage.shouldSupplierCheckboxIsSelected(FIRST_SUPPLIER_CODE, true);
        suppliersSearchPage.shouldNameOfChosenIsDisplayedInOvalElement(FIRST_SUPPLIER_NAME);

        // Step 10
        step("Нажать на крест на овальном элементе с именем поставщика");
        suppliersSearchPage.cancelChosenSuppler();
        suppliersSearchPage.shouldSupplierCheckboxIsSelected(FIRST_SUPPLIER_CODE, false);

        // Step 11
        step("Перейти на страницу выбора фильтров по нажатию на зеленую кнопку назад");
        suppliersSearchPage.clickBackBtn();
        myShopFilterPage.shouldSupplierButtonContainsText(0, null);
    }

    @Test(description = "C22883205 Поиск услуг", priority = 2)
    public void testServicesSearch() throws Exception {
        final String SERVICE_SHORT_LM_CODE = "4905510";
        final String SERVICE_FULL_LM_CODE = "49055102";
        final String SERVICE_SHORT_NAME = "Овер";
        final String SERVICE_FULL_NAME = "Оверлок";
        final String DEPARTMENT_ID = "5";
        final String SHORT_BARCODE = "590212011";

        GetCatalogServicesSearch servicesSearchParams = new GetCatalogServicesSearch();
        GetCatalogServicesSearch servicesSearchDepartmentParams = new GetCatalogServicesSearch().setDepartmentId(DEPARTMENT_ID);
        GetCatalogServicesSearch servicesSearchShortLmCodeParams = new GetCatalogServicesSearch().setLmCode(SERVICE_SHORT_LM_CODE);
        GetCatalogServicesSearch servicesSearchFullLmCodeParams = new GetCatalogServicesSearch().setLmCode(SERVICE_FULL_LM_CODE);
        GetCatalogServicesSearch servicesSearchShortNameParams = new GetCatalogServicesSearch().setName(SERVICE_SHORT_NAME);
        GetCatalogServicesSearch servicesSearchFullNameParams = new GetCatalogServicesSearch().setName(SERVICE_FULL_NAME);

        HashMap<Integer, ThreadApiClient<ServiceItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchServicesBy(servicesSearchParams, servicesSearchDepartmentParams, servicesSearchShortLmCodeParams, servicesSearchFullLmCodeParams, servicesSearchShortNameParams, servicesSearchFullNameParams);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);

        // Step 1
        step("Перейти на страницу выбора номенклатуры и выполнить поиск по всем отделам");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        ServiceItemDataList allServicesResponce = apiThreads.get(0).getData();
        searchProductPage.shouldServicesResponceEqualsContent(allServicesResponce, 5);

        // Step 2
        step("Выполнить поиск услуг по неполному лм коду");
        searchProductPage.enterTextInSearchFieldAndSubmit(SERVICE_SHORT_LM_CODE);
        ServiceItemDataList shortLmCodeServicesResponce = apiThreads.get(2).getData();
        searchProductPage.shouldServicesResponceEqualsContent(shortLmCodeServicesResponce, 2);
        searchProductPage.shouldCardsContainText(SERVICE_SHORT_LM_CODE, SearchProductPage.CardType.SERVICE, 2);

        // Step 3
        step("Выполнить поиск услуг по полному лм коду");
        searchProductPage.enterTextInSearchFieldAndSubmit(SERVICE_FULL_LM_CODE);
        AddServicePage addServicePage = new AddServicePage();
        addServicePage.verifyRequiredElements()
                .shouldServiceNameAndLmCodeBeOnPage(SERVICE_FULL_NAME, SERVICE_FULL_LM_CODE);
        addServicePage.returnBack();
        ServiceItemDataList fullLmCodeServicesResponce = apiThreads.get(3).getData();
        searchProductPage.shouldServicesResponceEqualsContent(fullLmCodeServicesResponce, 1);
        searchProductPage.shouldCardsContainText(SERVICE_FULL_LM_CODE, SearchProductPage.CardType.SERVICE, 1);

        // Step 4
        step("Выполнить поиск по одному отделу, в котором есть услуги");
        searchProductPage.clearSearchInput();
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.choseDepartmentId("00" + DEPARTMENT_ID, null, null, null);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        ServiceItemDataList departmentServicesResponce = apiThreads.get(1).getData();
        searchProductPage.shouldServicesResponceEqualsContent(departmentServicesResponce, 2);

        // Step 5
        step("Выполнить поиск услуги по неполному наименованию");
        searchProductPage.enterTextInSearchFieldAndSubmit(SERVICE_SHORT_NAME);
        addServicePage.returnBack();
        ServiceItemDataList shortNameServicesResponce = apiThreads.get(4).getData();
        searchProductPage.shouldServicesResponceEqualsContent(shortNameServicesResponce, 1);
        searchProductPage.shouldCardsContainText(SERVICE_SHORT_NAME, SearchProductPage.CardType.SERVICE, 1);

        // Step 6
        step("Выполнить поиск услуги по полному наименованию");
        searchProductPage.enterTextInSearchFieldAndSubmit(SERVICE_FULL_NAME);
        addServicePage.verifyRequiredElements()
                .shouldServiceNameAndLmCodeBeOnPage(SERVICE_FULL_NAME, SERVICE_FULL_LM_CODE);
        addServicePage.returnBack();
        ServiceItemDataList fullNameServicesResponce = apiThreads.get(5).getData();
        searchProductPage.shouldServicesResponceEqualsContent(fullNameServicesResponce, 1);
        searchProductPage.shouldCardsContainText(SERVICE_FULL_NAME, SearchProductPage.CardType.SERVICE, 1);

        // Step 7
        step("Выполнить поиск по короткому штрихкоду");
        searchProductPage.enterTextInSearchFieldAndSubmit(SHORT_BARCODE);
        ProductCardPage productCardPage = new ProductCardPage();
        productCardPage.returnBack();
        searchProductPage.shouldNotCardsBeOnPage(SearchProductPage.CardType.SERVICE);
    }

    @Test(description = "C22789203 очистка выбранных фильтров. При выборе хотя бы 1. Активация кнопки \"Метла\"", priority = 2)
    public void testClearAllFilters() throws Exception {
        LocalDate avsDate = LocalDate.of(2020, 2, 19);
        final String SUPPLIER_CODE = "1001123001";
        final String TOP = "1";
        final String GAMMA = "B";
        int entityCount = 3;

        GetCatalogSearch defaultParams = new GetCatalogSearch()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setPageSize(entityCount)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setStartFrom(1);
        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(defaultParams);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);

        // Step 1
        step("Выбрать фильтр \"Вся гамма ЛМ\"");
        FilterPage filterPage = searchProductPage.goToFilterPage();
        FilterPage allGammaFilterPage = filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        allGammaFilterPage.shouldClearAllFiltersBeOnPage(true);

        // Step 2
        step("применить выбранные фильтры и перезайти на страницу выбора фильтров");
        allGammaFilterPage.applyChosenFilters();
        searchProductPage.goToFilterPage();
        allGammaFilterPage.shouldClearAllFiltersBeOnPage(true);

        // Step 3
        step("Очистить фильтры при помощи кнопки \"Метла\"");
        allGammaFilterPage.clearAllFilters();
        filterPage.shouldFilterHasBeenChosen(FilterPage.MY_SHOP_FRAME_TYPE);

        // Step 4
        step("выбрать фильтры из каждого блока фильтров для общего фильтра \"Мой магазин\"");
        FiltersData filtersData1 = new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE);
        filtersData1.setGamma(FilterPage.GAMMA + " " + GAMMA);
        filtersData1.setTop(FilterPage.TOP + " " + TOP);
        filtersData1.setTopEM(true);
        filtersData1.setProductType(FilterPage.ORDERED_PRODUCT_TYPE);
        filtersData1.setSupplier(SUPPLIER_CODE);
        filtersData1.setDateAvs(avsDate);

        filterPage.choseFilters(filtersData1);
        filterPage.shouldFiltersAreSelected(filtersData1);

        // Step 5
        step("выбрать фильтры из каждого блока фильтров для общего фильтра \"Вся гамма ЛМ\"");
        FiltersData filtersData2 = new FiltersData(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filtersData2.setGamma(FilterPage.GAMMA + " P");
        filtersData2.setBestPrice(true);
        filterPage.choseFilters(filtersData2);
        filtersData2.unionWith(filtersData1);
        filterPage.shouldFiltersAreSelected(filtersData2);

        // Step 6
        step("Нажать на кнопку \"Метла\"");
        filterPage.clearAllFilters();
        filtersData2.setFilterFrame(FilterPage.MY_SHOP_FRAME_TYPE);
        filterPage.shouldFiltersAreNotSelected(filtersData2);
        filterPage.shouldSupplierButtonContainsText(0, null);
        filtersData2.setFilterFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.shouldFiltersAreNotSelected(filtersData2);

        // Step 7
        step("Нажать \"Показать товары\"");
        allGammaFilterPage.switchFiltersFrame(FilterPage.MY_SHOP_FRAME_TYPE);
        filterPage.applyChosenFilters();
        ProductItemDataList defaultParamsResponce = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(defaultParamsResponce,
                SearchProductPage.CardType.COMMON, entityCount);
    }

    @Test(description = "C22789213 Сброс фильтров при нажатии кнопки Назад железная и стрелочка", priority = 2)
    public void testClearAllFiltersIfReturnBack() throws Exception {
        final String TOP = " 0";
        final String DEPT_ID = "3";

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);
        FilterPage myShopFilterPage = searchProductPage.goToFilterPage();

        // Step 1
        step("Выбрать любой фильтр");
        myShopFilterPage.choseTopFilter(FilterPage.TOP + TOP);

        // Step 2
        step("перейти назад на страницу поиска товаров и услуг");
        myShopFilterPage.returnBack();

        // Step 3
        step("перейти на страницу фильтров");
        searchProductPage.goToFilterPage();
        myShopFilterPage.shouldFilterHasNotBeenChosen(FilterPage.TOP + TOP);

        // Step 4
        step("нажать \"показать товары\"");
        myShopFilterPage.applyChosenFilters();

        // Step 5
        step("выбрать любой элемент номенклатуры, отличающийся от текущего");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId("00" + DEPT_ID, null, null, null);
        nomenclatureSearchPage.clickShowAllProductsBtn();

        // Step 6
        step("выбрать сортировку, отличающуюся от текущей");
        SortPage sortPage = searchProductPage.openSortPage();
        sortPage.selectSort(SortPage.SORT_BY_LM_ASC);

        // Step 7
        step("Со страницы поиска перейти назад, на страницу продаж");
        searchProductPage.backToSalesPage();

        // Step 8
        step("Перейти на страницу поиска");
        mainProductAndServicesPage.clickSearchBar(true);
        searchProductPage.verifyRequiredElements()
                .shouldFilterCounterEquals(1)
                .shouldSelectedNomenclatureIs(EnvConstants.BASIC_USER_DEPARTMENT_ID, false);

        // Step 9
        step("перейти в фильтры");
        searchProductPage.goToFilterPage();
        myShopFilterPage.shouldFilterHasNotBeenChosen(FilterPage.TOP + TOP);

        // Step 10
        step("перейти на страницу поиска и открыть модальное окно сортировки");
        myShopFilterPage.returnBack();
        searchProductPage.openSortPage();
        sortPage.shouldSortIsChosen(SortPage.DEFAULT_SORT);
    }

    @Test(description = "C22789208 Поведение чек-бокса AVS", priority = 2)
    public void testAvsFilterBehaviour() throws Exception {
        LocalDate avsDate = LocalDate.of(2020, 3, 3);
        LocalDate anyAvsDate = LocalDate.of(2020, 2, 19);
        final String DEPT_ID = EnvConstants.BASIC_USER_DEPARTMENT_ID;

        GetCatalogSearch avsParam = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(3)
                .setStartFrom(1)
                .setAvsDate(String.format("between%%7C%s-0%s-0%sT00:00:00.000Z%%7C%s-0%s-0%sT00:00:00.000Z",
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(),
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1))
                .setDepartmentId(DEPT_ID);

        GetCatalogSearch avsNeqNullParam = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setPageSize(3)
                .setStartFrom(1)
                .setAvsDate("neq|null")
                .setDepartmentId(DEPT_ID);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(avsParam, avsNeqNullParam);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);
        FilterPage myShopFilterPage = searchProductPage.goToFilterPage();

        // Step 1
        step("Выбрать любую дату по нажатию на поле \"Дата AVS\"");
        myShopFilterPage.clickShowAllFiltersBtn();
        myShopFilterPage.choseCheckBoxFilter(FilterPage.HAS_AVAILABLE_STOCK);
        myShopFilterPage.choseAvsDate(avsDate);
        myShopFilterPage.shouldAvsDateIsCorrect(avsDate);
        myShopFilterPage.shouldClearAvsDateBtnIsVisible();

        // Step 2
        step("Применить фильтр");
        myShopFilterPage.applyChosenFilters();
        ProductItemDataList avsParamsResponce = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(avsParamsResponce, SearchProductPage.CardType.COMMON, 3);

        // Step 3
        step("Убрать чек-бокс напротив слова AVS");
        searchProductPage.goToFilterPage();
        myShopFilterPage.choseCheckBoxFilter(FilterPage.AVS);
        myShopFilterPage.shouldAvsDateIsCorrect(null);
        myShopFilterPage.shouldElementHasNotBeenSelected(FilterPage.AVS);

        // Step 4
        step("установить чек-бокс AVS, не выбирая даты, и применить фильтры");
        myShopFilterPage.choseCheckBoxFilter(FilterPage.AVS);
        myShopFilterPage.applyChosenFilters();
        ProductItemDataList avsNeqNullParamsResponce = apiThreads.get(1).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(avsNeqNullParamsResponce, SearchProductPage.CardType.COMMON, 3);

        // Step 5
        step("Выбрать дату AVS и очистить поле по нажатию на \"крест\"");
        searchProductPage.goToFilterPage();
        myShopFilterPage.choseAvsDate(anyAvsDate);
        myShopFilterPage.clearAvsDate();
        myShopFilterPage.shouldAvsDateIsCorrect(null);
        myShopFilterPage.shouldAddAvsDateBtnIsVisible();
    }

    @Issue("LFRONT-3360")
    @Test(description = "C22789211 Переключение между фильтрами Мой магазин и Вся гамма ЛМ. Сброс фильтров предыдущего окна и установка тех же фильтров на новое окно", priority = 2)
    public void testSwitchBetweenMyShopAndAllGammaFilters() throws Exception {
        LocalDate avsDate = LocalDate.of(2020, 2, 19);
        final String SUPPLIER_CODE = "11007015";
        final String SUPPLIER_NAME = "ООО РемиЛинг 2000";
        final String TOP = "0";
        final String GAMMA = "S";
        final String DEPT_ID = EnvConstants.BASIC_USER_DEPARTMENT_ID;

        GetCatalogSearch myShopFilterParam = buildDefaultCatalogSearchParams()
                .setGamma(GAMMA)
                .setTop(TOP)
                .setSupId(SUPPLIER_CODE)
                .setDepartmentId(DEPT_ID);

        GetCatalogSearch allGammaFilterParam = new GetCatalogSearch()
                .setPageSize(3)
                .setStartFrom(1)
                .setGamma(GAMMA)
                .setDepartmentId(DEPT_ID);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(myShopFilterParam, allGammaFilterParam);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);
        FilterPage myShopFilterPage = searchProductPage.goToFilterPage();

        // Step 1
        step("Выбрать фильтр из каждого раздела для группы фильтров \"Мой магазин\"");
        FiltersData filtersData1 = new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE);
        filtersData1.setGamma(FilterPage.GAMMA + " " + GAMMA);
        filtersData1.setTop(FilterPage.TOP + " " + TOP);
        filtersData1.setTopEM(true);
        filtersData1.setProductType(FilterPage.ORDERED_PRODUCT_TYPE);
        filtersData1.setSupplier(SUPPLIER_CODE);
        filtersData1.setDateAvs(avsDate);
        myShopFilterPage.choseFilters(filtersData1);
        myShopFilterPage.shouldSupplierButtonContainsText(1, SUPPLIER_NAME);
        myShopFilterPage.shouldFiltersAreSelected(filtersData1);

        // Step 2
        step("Перейти на группу фильтров \"Вся гамма ЛМ\"");
        FilterPage allGammaFilterPage = myShopFilterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filtersData1.setFilterFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        myShopFilterPage.shouldFiltersAreSelected(filtersData1);

        // Step 3
        step("Перейти в группу фильтров \"Мой магазин\"");
        allGammaFilterPage.switchFiltersFrame(FilterPage.MY_SHOP_FRAME_TYPE);
        filtersData1.setFilterFrame(FilterPage.MY_SHOP_FRAME_TYPE);
        myShopFilterPage.shouldFiltersAreSelected(filtersData1);
        myShopFilterPage.shouldSupplierButtonContainsText(1, SUPPLIER_NAME); //#bug

        // Step 4
        step("очистить выбранные фильтры");
        myShopFilterPage.clearAllFilters();
        myShopFilterPage.shouldFiltersAreNotSelected(filtersData1);

        // Step 5
        step("выбрать любую гамму, топ, поставщика");
        filtersData1 = new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE);
        filtersData1.setGamma(FilterPage.GAMMA + " " + GAMMA);
        filtersData1.setTop(FilterPage.TOP + " " + TOP);
        filtersData1.setSupplier(SUPPLIER_CODE);
        myShopFilterPage.choseFilters(filtersData1);
        myShopFilterPage.shouldFiltersAreSelected(filtersData1);
        myShopFilterPage.shouldSupplierButtonContainsText(1, SUPPLIER_NAME);

        // Step 6
        step("применить фильтры");
        myShopFilterPage.applyChosenFilters();
        ProductItemDataList myShopFilterResponce = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(myShopFilterResponce,
                SearchProductPage.CardType.COMMON, 3);

        // Step 7
        step("применить фильтры");
        searchProductPage.goToFilterPage();
        myShopFilterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        allGammaFilterPage.applyChosenFilters();
        ProductItemDataList allGammaFilterResponce = apiThreads.get(1).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(allGammaFilterResponce,
                SearchProductPage.CardType.ALL_GAMMA, 3);
    }

    @Test(description = "C22789214 Счетчик фильтров в меню поиска", priority = 2)
    public void testFilterCounter() throws Exception {
        LocalDate avsDate = LocalDate.of(2020, 2, 19);
        final String FIRST_SUPPLIER_CODE = "1001123001";
        final String SECOND_SUPPLIER_CODE = "12301";
        FiltersData filtersData = new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE);
        filtersData.setTopEM(true);
        filtersData.setDateAvs(avsDate);
        filtersData.setSupplier(FIRST_SUPPLIER_CODE);
        filtersData.setTop(FilterPage.TOP + " 0");

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);
        FilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        step("Выбрать фильтр topEm, дату AVS и двух поставщиков в фильтре поставщиков");
        filterPage.choseFilters(filtersData);
        filterPage.shouldFiltersAreSelected(filtersData);
        filterPage.selectSupplier(SECOND_SUPPLIER_CODE);
        filterPage.shouldSupplierButtonContainsText(2, null);

        // Step 2
        step("Применить фильтры");
        filterPage.applyChosenFilters();
        searchProductPage.shouldFilterCounterEquals(5);

        // Step 3
        step("Перейти на страницу фильтров и переключится на группу фильтров \"Вся гамма ЛМ\"");
        searchProductPage.goToFilterPage();
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.shouldFilterHasBeenChosen(FilterPage.ALL_GAMMA_FRAME_TYPE);

        // Step 4
        step("Применить выбранные фильтры");
        filterPage.applyChosenFilters();
        searchProductPage.shouldFilterCounterEquals(2);
    }

    @Test(description = "C22887951 Сортировка с фильтром Вся гамма ЛМ", priority = 2)
    public void testAllGammaSort() throws Exception {
        final String GAMMA = "A";

        GetCatalogSearch allGammaLmDescParams = new GetCatalogSearch()
                .setPageSize(3)
                .setStartFrom(1)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setSortBy(CatalogSearchFields.NAME, SortingOrder.ASC)
                .setGamma(GAMMA);

        GetCatalogSearch allGammaLmAscParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setPageSize(3)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.ASC)
                .setGamma(GAMMA);

        GetCatalogSearch myShopLmAscParams = buildDefaultCatalogSearchParams()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.ASC)
                .setGamma(GAMMA);

        GetCatalogSearch myShopStockAscParams = buildDefaultCatalogSearchParams()
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setSortBy(CatalogSearchFields.NAME, SortingOrder.ASC)
                .setGamma(GAMMA);

        HashMap<Integer, ThreadApiClient<ProductItemDataList, CatalogSearchClient>> apiThreads =
                sendRequestsSearchProductsBy(allGammaLmDescParams, allGammaLmAscParams, myShopLmAscParams, myShopStockAscParams);

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);
        FilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        step("Выбрать \"Вся гамма ЛМ\"");
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.choseGammaFilter(FilterPage.GAMMA + " " + GAMMA);

        // Step 2
        step("Нажать \"показать товары\"");
        filterPage.applyChosenFilters();

        // Step 3
        step("Нажать на кнопку сортировки");
        SortPage sortPage = searchProductPage.openSortPage();
        sortPage.verifyRequiredElements();

        // Step 4
        step("Выбрать сортировку по лм коду ASC");
        sortPage.selectSort(SortPage.SORT_BY_LM_ASC);
        ProductItemDataList allGammaLmAscResponse = apiThreads.get(1).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(allGammaLmAscResponse, SearchProductPage.CardType.ALL_GAMMA, 3);

        // Step 5
        step("Перейти в фильтры и переключиться на фильтр \"Мой магазин\"");
        searchProductPage.goToFilterPage();
        filterPage.switchFiltersFrame(FilterPage.MY_SHOP_FRAME_TYPE);

        // Step 6
        step("Нажать \"показать товары\"");
        filterPage.applyChosenFilters();
        ProductItemDataList myShopLmAscResponse = apiThreads.get(2).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(myShopLmAscResponse, SearchProductPage.CardType.COMMON, 3);

        // Step 7
        step("Выбрать вид сортировки \"по остатку ASC\"");
        searchProductPage.openSortPage();
        sortPage.selectSort(SortPage.SORT_BY_ALPHABET_ASC);
        ProductItemDataList myShopStockAscResponse = apiThreads.get(3).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(myShopStockAscResponse, SearchProductPage.CardType.COMMON, 3);

        // Step 8
        step("Перейти в фильтры, выбрать фильтр \"Вся гамма ЛМ\" и показать товары");
        searchProductPage.goToFilterPage();
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.applyChosenFilters();
        ProductItemDataList allGammaLmDescResponse = apiThreads.get(0).getData();
        searchProductPage.shouldCatalogResponseEqualsContent(allGammaLmDescResponse, SearchProductPage.CardType.ALL_GAMMA, 3);
    }

    @Test(description = "C22789207 Сокрытие части фильтров", priority = 2)
    public void testFiltersHide() throws Exception {
        final String TOP = "0";

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(true);

        // Step 1
        step("Перейти на страницу выбора фильтров");
        FilterPage filterPage = searchProductPage.goToFilterPage();
        filterPage.shouldShowAllFiltersBtnIsVisible();

        // Step 2
        step("Нажать на \"Показать все фильтры\"");
        filterPage.clickShowAllFiltersBtn();
        filterPage.shouldFilterPageHasExtendedView();

        // Step 3
        step("Выбрать любую гамму/топ и применить фильтр");
        filterPage.choseTopFilter(FilterPage.TOP + " " + TOP);
        filterPage.applyChosenFilters();

        // Step 4
        step("Перейти в фильтры");
        searchProductPage.goToFilterPage();
        filterPage.shouldShowAllFiltersBtnIsVisible();

        // Step 5
        step("Нажать на \"Показать все фильтры\", выбрать фильтр среди вновь отображенных и нажать \"Показать товары\"");
        filterPage.clearAllFilters();
        filterPage.clickShowAllFiltersBtn();
        filterPage.choseCheckBoxFilter(FilterPage.BEST_PRICE);
        filterPage.applyChosenFilters();

        // Step 6
        step("Перейти в фильтры");
        searchProductPage.goToFilterPage();
        filterPage.shouldFilterPageHasExtendedView();
    }

}
