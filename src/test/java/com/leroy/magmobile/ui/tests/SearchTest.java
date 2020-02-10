package com.leroy.magmobile.ui.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.common.*;
import com.leroy.magmobile.ui.pages.common.modal.SortPage;
import com.leroy.magmobile.ui.pages.sales.PricesAndQuantityPage;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SimilarProductsPage;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.enums.CatalogSearchFields;
import com.leroy.umbrella_extension.magmobile.enums.SortingOrder;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.BaseModule;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.util.*;
import java.util.List;

@Guice(modules = {BaseModule.class})
public class SearchTest extends AppBaseSteps {

    @Inject
    private MagMobileClient apiClient;

    private static final String ALL_DEPARTMENTS_TEXT = "Все отделы";

    private GetCatalogSearch buildDefaultCatalogSearchParams() {
        return new GetCatalogSearch()
                .setPageSize(10)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1);
    }

    @Test(description = "C3200996 Поиск товара по критериям", priority = 1)
    public void testC3200996() throws Exception {
        String lmCode = "10008698";
        String searchContext = "Тепломир радиатор";
        String barCode = "5902120110575";
        String shortLmCode = "1234";
        String shortBarCode = "590212011";
        int entityCount = 10;

        GetCatalogSearch byLmParams = new GetCatalogSearch()
                .setPageSize(10)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setByLmCode(lmCode);

        GetCatalogSearch byNameParams = new GetCatalogSearch()
                .setPageSize(10)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setByNameLike(searchContext);

        GetCatalogSearch byBarCodeParams = new GetCatalogSearch()
                .setPageSize(10)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setByBarCode(barCode);

        GetCatalogSearch byShortLmCodeParams = new GetCatalogSearch()
                .setPageSize(10)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setByLmCode(shortLmCode);

        GetCatalogSearch byShortBarCodeParams = new GetCatalogSearch()
                .setPageSize(10)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setByBarCode(shortBarCode);

        Response<ProductItemListResponse> lmResponce = apiClient.searchProductsBy(byLmParams);
        //Response<ProductItemListResponse> nameLikeResponce = apiClient.searchProductsBy(byNameParams);
        Response<ProductItemListResponse> barcodeResponce = apiClient.searchProductsBy(byBarCodeParams);
        Response<ProductItemListResponse> shortLmResponce = apiClient.searchProductsBy(byShortLmCodeParams);
        Response<ProductItemListResponse> shortBarcodeResponce = apiClient.searchProductsBy(byShortBarCodeParams);

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);

        // Step 1
        log.step("Нажмите на поле Поиск товаров и услуг");
        SearchProductPage searchProductPage = salesPage.clickSearchBar(false)
                .verifyRequiredElements();
        // Step 2
        log.step("Перейдите в окно выбора единицы номенклатуры");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow()
                .verifyRequiredElements();
        // Step 3
        log.step("Вернитесь на окно выбора отдела");
        nomenclatureSearchPage.returnBackNTimes(1)
                .shouldTitleWithNomenclatureIs("")
                .verifyNomenclatureBackBtnVisibility(false);
        // Step 4
        log.step("Нажмите 'Показать все товары'");
        nomenclatureSearchPage.clickShowAllProductsBtn()
                .verifyRequiredElements()
                .shouldCountOfProductsOnPageMoreThan(1)
                .shouldSelectedNomenclatureIs(ALL_DEPARTMENTS_TEXT, true);

        // Step 5
        log.step("Введите полное значение для поиска по ЛМ коду| 10008698");
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
        ProductDescriptionPage productCardPage = new ProductDescriptionPage(context)
                .verifyRequiredElements(true)
                .shouldProductLMCodeIs(lmCode);
        searchProductPage = productCardPage.returnBack();
        searchProductPage.shouldCatalogResponseEqualsContent(lmResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 6
        log.step("Введите название товара для поиска");
        searchProductPage.enterTextInSearchFieldAndSubmit(searchContext);
        //Иногда работает некорректно, потому что сортировка по ЛМ коду не применяется в мэшапе и порядок всегда разный
        /*searchProductPage.shouldCatalogResponseEqualsContent(
                nameLikeResponce, SearchProductPage.CardType.COMMON, entityCount);*/
        searchProductPage.shouldProductCardsContainText(
                searchContext, SearchProductPage.CardType.COMMON, 3);

        // Step 7
        log.step("Ввести штрихкод вручную");
        searchProductPage.enterTextInSearchFieldAndSubmit(barCode);
        productCardPage = new ProductDescriptionPage(context)
                .verifyRequiredElements(true)
                .shouldProductBarCodeIs(barCode);
        searchProductPage = productCardPage.returnBack();
        searchProductPage.shouldCatalogResponseEqualsContent(
                barcodeResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 8
        log.step("Введите часть ЛМ кода для поиска");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortLmCode);
        searchProductPage.shouldCatalogResponseEqualsContent(
                shortLmResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 9
        log.step("Ввести в поисковую строку положительное число длинной >8 символов (" + shortBarCode + ") и инициировать поиск");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortBarCode);
        searchProductPage.shouldCatalogResponseEqualsContent(
                shortBarcodeResponce, SearchProductPage.CardType.COMMON, entityCount);
    }

    @Test(description = "C22846686 Мой магазин. Выбор фильтров каждого блока фильтров", priority = 1)
    public void testC22846686() throws Exception {
        LocalDate avsDate = LocalDate.of(2019, 8, 19);
        String supplierSearchContext = "1001123001";
        final String TOP = "0";
        final String GAMMA = "B";
        final String departmentId = "5";
        int entityCount = 10;

        GetCatalogSearch gammaParam = buildDefaultCatalogSearchParams()
                .setGamma(GAMMA)
                .setDepartmentId(departmentId);

        GetCatalogSearch topParam = buildDefaultCatalogSearchParams()
                .setTop(TOP)
                .setDepartmentId(departmentId);

        GetCatalogSearch bestPriceParam = buildDefaultCatalogSearchParams()
                .setBestPrice(true)
                .setDepartmentId(departmentId);

        GetCatalogSearch orderedProductTypeParam = buildDefaultCatalogSearchParams()
                .setOrderType("MBO");

        GetCatalogSearch avsParam = buildDefaultCatalogSearchParams()
                .setAvsDate(String.format("between%%7C%s-0%s-%sT00:00:00.000Z%%7C%s-0%s-%sT00:00:00.000Z",
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(),
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1));

        GetCatalogSearch supplierIdParam = buildDefaultCatalogSearchParams()
                .setSupId(supplierSearchContext);

        Response<ProductItemListResponse> avsDateProductResponce = apiClient.searchProductsBy(avsParam);
        Response<ProductItemListResponse> gammaProductResponce = apiClient.searchProductsBy(gammaParam);
        Response<ProductItemListResponse> topProductResponce = apiClient.searchProductsBy(topParam);
        Response<ProductItemListResponse> bestPriceProductResponce = apiClient.searchProductsBy(bestPriceParam);
        Response<ProductItemListResponse> orderedProductTypeProductResponce = apiClient.searchProductsBy(orderedProductTypeParam);


        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(false);
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId("00" + departmentId, null, null, null);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        MyShopFilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        log.step("выбрать одну из гамм");
        filterPage.choseGammaFilter(FilterPage.GAMMA + " " + GAMMA);
        filterPage.shouldFilterHasBeenChosen(FilterPage.GAMMA + " " + GAMMA);
        filterPage.applyChosenFilters();
        searchProductPage.shouldCatalogResponseEqualsContent(
                gammaProductResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 2
        log.step("выбрать один из топов");
        searchProductPage.goToFilterPage();
        filterPage.clearAllFilters();
        filterPage.shouldFilterHasNotBeenChosen(FilterPage.GAMMA + " " + GAMMA);
        filterPage.choseTopFilter();
        filterPage.shouldFilterHasBeenChosen(MyShopFilterPage.TOP + " " + TOP);
        filterPage.applyChosenFilters();
        searchProductPage.shouldCatalogResponseEqualsContent(
                topProductResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 3
        log.step("выбрать 1 из чек-боксов блока с типами товаров");
        searchProductPage.goToFilterPage();
        filterPage.clearAllFilters();
        filterPage.shouldFilterHasNotBeenChosen(MyShopFilterPage.TOP + " " + TOP);
        filterPage.choseCheckBoxFilter(FilterPage.BEST_PRICE);
        filterPage.shouldElementHasBeenSelected(FilterPage.BEST_PRICE);
        filterPage.applyChosenFilters();
        searchProductPage.shouldCatalogResponseEqualsContent(
                bestPriceProductResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 4
        log.step("выбрать тип товара");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.goToFilterPage();
        filterPage.clearAllFilters();
        filterPage.choseProductType(filterPage.ORDERED_PRODUCT_TYPE);
        filterPage.shouldFilterHasBeenChosen(filterPage.ORDERED_PRODUCT_TYPE);
        filterPage.applyChosenFilters();
        searchProductPage.shouldCatalogResponseEqualsContent(
                orderedProductTypeProductResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 5
        log.step("выбрать 1 поставщика");
        searchProductPage.goToFilterPage();
        filterPage.clearAllFilters();
        SuppliersSearchPage suppliersSearchPage = filterPage.goToSuppliersSearchPage(false);
        suppliersSearchPage.verifyRequiredElements();

        suppliersSearchPage.searchAndConfirmSupplier(supplierSearchContext);
        suppliersSearchPage.shouldCountOfSuppliersIs(1);
        suppliersSearchPage.shouldSupplierCardsContainText(supplierSearchContext);
        suppliersSearchPage.shouldSupplierCheckboxIsSelected(supplierSearchContext, true);
        suppliersSearchPage.applyChosenSupplier();
        filterPage.applyChosenFilters();
        Response<ProductItemListResponse> supplierProductResponce = apiClient.searchProductsBy(supplierIdParam);

        searchProductPage.shouldCatalogResponseEqualsContent(
                supplierProductResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 6
        log.step("выбрать дату авс");
        searchProductPage.goToFilterPage();
        filterPage.clearAllFilters();
        filterPage.choseAvsDate(avsDate);
        filterPage.shouldElementHasBeenSelected(filterPage.AVS);
        filterPage.applyChosenFilters();

        // Step 7
        searchProductPage.verifyRequiredElements();
        searchProductPage.shouldCatalogResponseEqualsContent(
                avsDateProductResponce, SearchProductPage.CardType.COMMON, entityCount);
    }

    @Test(description = "C22789209 Вся гамма ЛМ. Выбор фильтров каждого раздела", priority = 1)
    public void testC22789209() throws Exception {
        LocalDate avsDate = LocalDate.of(2019, 12, 5);
        final String GAMMA = "A";
        final String departmentId = "11";
        int entityCount = 10;

        GetCatalogSearch gammaParam = new GetCatalogSearch()
                .setGamma(GAMMA)
                .setPageSize(10)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setDepartmentId(departmentId);

        GetCatalogSearch ctmParam = new GetCatalogSearch()
                .setCtm(true)
                .setPageSize(10)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1);

        GetCatalogSearch commonProductTypeParam = new GetCatalogSearch()
                .setOrderType("S")
                .setPageSize(10)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1);

        GetCatalogSearch avsParam = new GetCatalogSearch()
                .setAvsDate(String.format("between%%7C%s-%s-0%sT00:00:00.000Z%%7C%s-%s-0%sT00:00:00.000Z",
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(), avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1))
                .setPageSize(10)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1);

        Response<ProductItemListResponse> avsDateProductResponce = apiClient.searchProductsBy(avsParam);
        Response<ProductItemListResponse> gammaProductResponce = apiClient.searchProductsBy(gammaParam);
        Response<ProductItemListResponse> ctmProductResponce = apiClient.searchProductsBy(ctmParam);
        Response<ProductItemListResponse> commonProductTypeProductResponce = apiClient.searchProductsBy(commonProductTypeParam);

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(false);
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId("0" + departmentId, null, null, null);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        FilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        log.step("выбрать овальный чек-бокс \"Вся гамма ЛМ\"");
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.shouldFilterHasBeenChosen(FilterPage.ALL_GAMMA_FRAME_TYPE);

        // Step 2
        log.step("выбрать одну из гамм");
        AllGammaFilterPage allGammaFilterPage = new AllGammaFilterPage(context);
        allGammaFilterPage.choseGammaFilter(FilterPage.GAMMA + " " + GAMMA);
        allGammaFilterPage.shouldFilterHasBeenChosen(FilterPage.GAMMA + " " + GAMMA);
        allGammaFilterPage.applyChosenFilters();
        searchProductPage.shouldCatalogResponseEqualsContent(gammaProductResponce,
                SearchProductPage.CardType.ALL_GAMMA, entityCount);

        // Step 3
        log.step("выбрать 1 из чек-боксов блока с типами товаров");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.goToFilterPage();
        allGammaFilterPage.choseGammaFilter(FilterPage.GAMMA + " " + GAMMA);
        allGammaFilterPage.shouldFilterHasNotBeenChosen(FilterPage.GAMMA + " " + GAMMA);
        allGammaFilterPage.choseCheckBoxFilter(AllGammaFilterPage.CTM);
        allGammaFilterPage.shouldElementHasBeenSelected(AllGammaFilterPage.CTM);
        allGammaFilterPage.applyChosenFilters();
        searchProductPage.shouldCatalogResponseEqualsContent(ctmProductResponce,
                SearchProductPage.CardType.ALL_GAMMA, entityCount);

        // Step 4
        log.step("выбрать тип товара");
        searchProductPage.goToFilterPage();
        allGammaFilterPage.choseCheckBoxFilter(AllGammaFilterPage.CTM);
        allGammaFilterPage.choseProductType(allGammaFilterPage.COMMON_PRODUCT_TYPE);
        allGammaFilterPage.shouldFilterHasBeenChosen(allGammaFilterPage.COMMON_PRODUCT_TYPE);
        allGammaFilterPage.applyChosenFilters();
        searchProductPage.shouldCatalogResponseEqualsContent(commonProductTypeProductResponce,
                SearchProductPage.CardType.ALL_GAMMA, entityCount);

        // Step 5
        log.step("выбрать дату авс");
        searchProductPage.goToFilterPage();
        allGammaFilterPage.clearAllFilters();
        filterPage.switchFiltersFrame(MyShopFilterPage.ALL_GAMMA_FRAME_TYPE);
        allGammaFilterPage.choseAvsDate(avsDate);
        allGammaFilterPage.shouldElementHasBeenSelected(allGammaFilterPage.AVS);

        // Step 6
        log.step("подтвердить примененные фильтры");
        allGammaFilterPage.applyChosenFilters();
        searchProductPage.verifyRequiredElements();
        searchProductPage.shouldCatalogResponseEqualsContent(avsDateProductResponce,
                SearchProductPage.CardType.ALL_GAMMA, entityCount);
    }

    @Test(description = "C22789172 На поисковой запрос не вернулись результаты", priority = 2)
    public void testC22789172() throws Exception {
        final String byName = "АFHF13dasf";

        GetCatalogSearch byNameParams = new GetCatalogSearch()
                .setPageSize(10)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setByNameLike(byName)
                .setDepartmentId("15");

        Response<ProductItemListResponse> nameLikeResponce = apiClient.searchProductsBy(byNameParams);

        //TODO добавить проверку на отклонение по координатам

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(false);


        // Step 1
        log.step("Ввести в поле поиска значение, результат поиска по которому не вернется");
        searchProductPage.enterTextInSearchFieldAndSubmit(byName);
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);

        // Step 2
        log.step("выбрать более 1 фильтра и нажать \"Показать товары\"");
        MyShopFilterPage myShopFilterPage = searchProductPage.goToFilterPage();
        myShopFilterPage.choseGammaFilter(MyShopFilterPage.GAMMA + " ET");
        myShopFilterPage.shouldFilterHasBeenChosen(MyShopFilterPage.GAMMA + " ET");
        searchProductPage = myShopFilterPage.applyChosenFilters();

        searchProductPage.verifyRequiredElements();
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);
        searchProductPage.shouldDiscardAllFiltersBtnBeDisplayed();

        // Step 3
        log.step("Нажать на кнопку \"Cбросить фильтры\"");
        searchProductPage.discardFilters();
        searchProductPage.shouldNotDiscardAllFiltersBtnBeDisplayed();
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);

        // Step 4
        log.step("перейти в фильтры");
        searchProductPage.goToFilterPage();
        myShopFilterPage.scrollHorizontalWidget(MyShopFilterPage.GAMMA, MyShopFilterPage.GAMMA + " ET");
        myShopFilterPage.shouldFilterHasNotBeenChosen(MyShopFilterPage.GAMMA + " ET");
    }


    @Test(description = "C22789176 Вывод истории поиска", priority = 1)
    public void testC22789176() throws Exception {
        int searchPhrasesCount = 21;

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);

        // Step 1
        log.step("Нажать на поисковую строку");
        SearchProductPage searchProductPage = salesPage.clickSearchBar(false);
        searchProductPage.shouldFirstSearchMsgBeDisplayed();

        // Step 2
        log.step("ввести любую неповторяющуюся поисковую фразу и выполнить поиск " + searchPhrasesCount + " раз");
        List<String> searchPhrases = searchProductPage.createSearchHistory(searchPhrasesCount);
        // На странице должно отображаться не более 20 записей, значит лишнее убираем
        searchPhrases.remove(0);
        Collections.reverse(searchPhrases);

        // Step 3
        log.step("Перезайти в поиск");
        searchProductPage.backToSalesPage();
        searchProductPage = salesPage.clickSearchBar(true);
        searchProductPage.shouldSearchHistoryListIs(searchPhrases);

        // Step 4
        log.step("Начать вводить значение идентичное одному из ранее введенных");
        String exampleText = searchPhrases.get(searchPhrases.size() / 2);
        searchProductPage.enterTextInSearchField(exampleText)
                .verifySearchHistoryContainsSearchPhrase(exampleText);
    }

    @Test(description = "C22790468 Гамма ЛМ. Отсутствие: действий с товаром, истории продаж, поставки", priority = 2)
    public void testC22790468() throws Exception {
        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);

        MyShopFilterPage myShopFilterPage = new SalesPage(context)
                .clickSearchBar(false)
                .goToFilterPage();

        // Step 1
        log.step("Выбрать фильтр \"Вся гамма ЛМ\" и перейти на страницу результатов поиска");
        AllGammaFilterPage allGammaFilterPage = myShopFilterPage
                .switchFiltersFrame(MyShopFilterPage.ALL_GAMMA_FRAME_TYPE);
        SearchProductPage searchProductPage = allGammaFilterPage.applyChosenFilters();
        searchProductPage.verifyProductCardsHaveAllGammaView();

        // Step 2
        log.step("Перейти в одну из карточек товара");
        ProductDescriptionPage productDescriptionCardPage = searchProductPage.selectProductCardByIndex(1);
        productDescriptionCardPage.verifyCardHasGammaView();

        // Step 3
        log.step("Перейти во вкладку \"Аналогичные товары\"");
        SimilarProductsPage similarProductsPage = productDescriptionCardPage.switchTab(productDescriptionCardPage.SIMILAR_PRODUCTS);
        similarProductsPage.verifyProductCardsHaveAllGammaView();

        // Step 4
        log.step("Вернуться на вкладку \"Описание\" и Нажать на строку \"Цены в магазинах\"");
        productDescriptionCardPage = similarProductsPage.switchTab(similarProductsPage.DESCRIPTION);
        PricesAndQuantityPage pricesAndQuantityPage = productDescriptionCardPage.goToPricesAndQuantityPage();
        pricesAndQuantityPage.shouldNotSupplyBtnBeDisplayed();

    }

    @Test(description = "C22789191 Сортировка результатов поиска", priority = 2)
    public void testC22789191() throws Exception {
        // Pre-conditions
        int countOfCheckedProducts = 11;
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(false);

        // Step 1
        log.step("Раскрыть модальное окно сортировки");
        SortPage sortPage = searchProductPage.openSortPage()
                .verifyRequiredElements();

        // Step 2
        log.step("Выбрать сортировку по ЛМ-коду по возрастающей");
        searchProductPage = sortPage.selectSort(SortPage.SORT_BY_LM_ASC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_LM_ASC, SearchProductPage.CardType.COMMON, countOfCheckedProducts);

        // Step 3
        log.step("повторить шаг 1-2 для сортировки по лм-коду по убывающей");
        searchProductPage.openSortPage()
                .selectSort(SortPage.SORT_BY_LM_DESC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_LM_DESC, SearchProductPage.CardType.COMMON, countOfCheckedProducts);

        // Step 4
        log.step("повторить шаг 1-2 для сортировки по запасу по возрастающей");
        searchProductPage.openSortPage()
                .selectSort(SortPage.SORT_BY_AVAILABLE_STOCK_ASC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_AVAILABLE_STOCK_ASC, SearchProductPage.CardType.COMMON, countOfCheckedProducts);

        // Step 5
        log.step("повторить шаг 1-2 для сортировки по запасу по убывающей");
        searchProductPage.openSortPage()
                .selectSort(SortPage.SORT_BY_AVAILABLE_STOCK_DESC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_AVAILABLE_STOCK_DESC, SearchProductPage.CardType.COMMON, countOfCheckedProducts);
    }

    @Test(description = "C22789201 Номенклатура, Навигация и Поиск по структурным элементам номенклатуры", priority = 1)
    public void testC22789201() throws Exception {
        String dept = "015";
        String subDept = "1510";
        String classId = "0030";
        String subClassId = "0020";
        int entityCount = 10;

        GetCatalogSearch subclassParams = new GetCatalogSearch()
                .setDepartmentId(dept.replaceAll("^0+", ""))
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setPageSize(10)
                .setSubDepartmentId(subDept.replaceAll("^0+", ""))
                .setClassId(classId.replaceAll("^0+", ""))
                .setSubclassId(subClassId.replaceAll("^0+", ""));

        GetCatalogSearch classParams = new GetCatalogSearch()
                .setDepartmentId(dept.replaceAll("^0+", ""))
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setPageSize(10)
                .setSubDepartmentId(subDept.replaceAll("^0+", ""))
                .setClassId(classId.replaceAll("^0+", ""));

        GetCatalogSearch subdepartmentParams = new GetCatalogSearch()
                .setDepartmentId(dept.replaceAll("^0+", ""))
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setPageSize(10)
                .setSubDepartmentId(subDept.replaceAll("^0+", ""));

        GetCatalogSearch departmentParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1)
                .setPageSize(10)
                .setDepartmentId(dept.replaceAll("^0+", ""));

        Response<ProductItemListResponse> subclassNomenclatureResponce = apiClient.searchProductsBy(subclassParams);
        Response<ProductItemListResponse> classNomenclatureResponce = apiClient.searchProductsBy(classParams);
        Response<ProductItemListResponse> subdepartmentNomenclatureResponce = apiClient.searchProductsBy(subdepartmentParams);
        Response<ProductItemListResponse> departmentNomenclatureResponce = apiClient.searchProductsBy(departmentParams);

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(true);

        // Step 1
        log.step("Перейти на страницу выбора номенклатуры");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.verifyRequiredElements();
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept);

        // Step 2
        log.step("Перейти в список всех отделов");
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs("");
        nomenclatureSearchPage.shouldDepartmentsCountIs15();

        // Step 3
        log.step("нажать по кнопке \"показать все товары\"");
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(ALL_DEPARTMENTS_TEXT, true);

        // Step 4
        log.step("повтороить шаг 2-3 для отделов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.choseDepartmentId(dept, null, null, null);

        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(dept, false);
        searchProductPage.shouldCatalogResponseEqualsContent(departmentNomenclatureResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 5
        log.step("повтороить шаг 2-3 для подотделов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, null, null);

        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept + subDept);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(subDept, false);
        searchProductPage.shouldCatalogResponseEqualsContent(subdepartmentNomenclatureResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 6
        log.step("повтороить шаг 2-3 для классов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(2);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, classId, null);

        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept + subDept + classId);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(classId, false);
        searchProductPage.shouldCatalogResponseEqualsContent(classNomenclatureResponce, SearchProductPage.CardType.COMMON, entityCount);

        // Step 7
        log.step("повтороить шаг 2-3 для подклассов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(3);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, classId, subClassId);

        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept + subDept + classId + subClassId);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(subClassId, false);
        searchProductPage.shouldCatalogResponseEqualsContent(subclassNomenclatureResponce, SearchProductPage.CardType.COMMON, entityCount);

    }

    @Test(description = "C22789173 Поиск товара по одному введенному символу", priority = 2)
    public void testSearchByOneSymbol() throws Exception {
        final String searchContext = "1";

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(true);

        // Step 1
        log.step("ввести в поисковую строку 1 цифру");
        searchProductPage.enterTextInSearchField(searchContext);
        searchProductPage.shouldProgressBarIsInvisible();

        // Step 2
        log.step("инициировать поиск");
        searchProductPage.submitSearch();
        searchProductPage.shouldNotFirstSearchMsgBeDisplayed();

        // Step 3
        log.step("очистить поисковую строку");
        searchProductPage.clearSearchInput();
        searchProductPage.shouldScannerBtnIsVisible();

        // Step 4
        log.step("нажать на поисковую строку и инициировать поиск");
        searchProductPage.enterTextInSearchFieldAndSubmit("");
        searchProductPage.shouldProgressBarIsInvisible();
    }

    @Test(description = "C3200999 Проверка пагинации", priority = 2)
    public void testSearchPagePagination() throws Exception {
        String shortLmCode = "12";
        String dept = "015";
        String subDept = "1510";
        final String GAMMA = "A";
        final int ENTITY_COUNT = 20;

        GetCatalogSearch paginationParams = new GetCatalogSearch()
                .setDepartmentId(dept.replaceAll("^0+", ""))
                .setSubDepartmentId(subDept.replaceAll("^0+", ""))
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.AVAILABLE_STOCK, SortingOrder.ASC)
                .setByLmCode(shortLmCode)
                .setGamma(GAMMA)
                .setStartFrom(1)
                .setPageSize(ENTITY_COUNT);

        Response<ProductItemListResponse> paginationResponce = apiClient.searchProductsBy(paginationParams);

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(true);

        // Step 1
        log.step("Введите неполное название или неполный ЛМ код товара");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortLmCode);
        searchProductPage.verifyClearTextInputBtnIsVisible();

        // Step 2
        log.step("Выбрать подотдел в номенклатуре");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, null, null);
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept + subDept);
        nomenclatureSearchPage.clickShowAllProductsBtn();

        // Step 3
        log.step("Выбрать отличный от дефолтного способ сортировки");
        SortPage sortPage = searchProductPage.openSortPage();
        sortPage.selectSort(SortPage.SORT_BY_AVAILABLE_STOCK_ASC);
        searchProductPage.shouldProductCardsBeSorted(SortPage.SORT_BY_AVAILABLE_STOCK_ASC, SearchProductPage.CardType.COMMON, 3);

        // Step 4
        log.step("Выбрать любой фильтр на странице выбора фильтров");
        MyShopFilterPage myShopFilterPage = searchProductPage.goToFilterPage();
        myShopFilterPage.choseGammaFilter(MyShopFilterPage.GAMMA + " " + GAMMA);
        myShopFilterPage.shouldFilterHasBeenChosen(MyShopFilterPage.GAMMA + " " + GAMMA);
        myShopFilterPage.applyChosenFilters();

        // Step 5
        log.step("Проскролить вниз до упора");
        searchProductPage.shouldCatalogResponseEqualsContent(paginationResponce, SearchProductPage.CardType.COMMON, ENTITY_COUNT);

    }

    @Test(description = "C22789202 Выбор фильтра поставщиков", priority = 2)
    public void testSuppliersFilter() throws Exception {
        final String FIRST_SUPPLIER_CODE = "1001123001";
        final String FIRST_SUPPLIER_NAME = "ООО Бард-Спб";
        final String SECOND_SUPPLIER_CODE = "12301";
        final String SECOND_SUPPLIER_NAME = "САЗИ";
        String supplierParamValue;

        GetCatalogSearch supplierIdParam = new GetCatalogSearch()
                .setPageSize(3)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1);

        GetCatalogSearch defaultSearchParam = new GetCatalogSearch()
                .setPageSize(3)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1);

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(true);
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        MyShopFilterPage myShopFilterPage = searchProductPage.goToFilterPage();

        // Step 1
        log.step("Перейти на страницу выбора поставщика");
        SuppliersSearchPage suppliersSearchPage = myShopFilterPage.goToSuppliersSearchPage(false);
        suppliersSearchPage.verifyRequiredElements().shouldCountOfSuppliersIs(3);
        //suppliersSearchPage.shouldSuppliersSortedByDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID);   //баг функционала departmentId пользака не подтягивается

        // Step 2
        log.step("ввести в поисковую строку код поставщика");
        suppliersSearchPage.searchAndConfirmSupplier(FIRST_SUPPLIER_CODE);
        suppliersSearchPage.shouldSupplierCardsContainText(FIRST_SUPPLIER_CODE);

        // Step 3
        log.step("выбрать поставщика и подтвердить выбор");
        suppliersSearchPage.applyChosenSupplier();
        myShopFilterPage.shouldSupplierButtonContainsText(1, FIRST_SUPPLIER_NAME);

        // Step 4
        log.step("повторить шаг 1-2, но искать по наименованию поставщика");
        myShopFilterPage.goToSuppliersSearchPage(false);
        suppliersSearchPage.searchAndConfirmSupplier(SECOND_SUPPLIER_NAME);
        suppliersSearchPage.shouldSupplierCardsContainText(SECOND_SUPPLIER_NAME);

        // Step 5
        log.step("выбрать поставщика и подтвердить выбор");
        suppliersSearchPage.applyChosenSupplier();
        supplierParamValue = FIRST_SUPPLIER_CODE + "," + SECOND_SUPPLIER_CODE;
        supplierIdParam.setSupId(supplierParamValue);
        Response<ProductItemListResponse> suppliersResponce = apiClient.searchProductsBy(supplierIdParam);
        myShopFilterPage.shouldSupplierButtonContainsText(2, null);

        // Step 6
        log.step("Применить фильтры выбранные фильтры");
        myShopFilterPage.applyChosenFilters();
        searchProductPage.shouldCatalogResponseEqualsContent(suppliersResponce, SearchProductPage.CardType.COMMON, 3);

        // Step 7
        log.step("Вернуться на страницу выбора фильтров и очистить фильтр поставщиков по нажатию на \"крест\"");
        searchProductPage.goToFilterPage();
        myShopFilterPage.clearSuppliersFilter("Выбрано");
        myShopFilterPage.shouldSupplierButtonContainsText(0, null);

        // Step 8
        log.step("Нажать \"показать товары\"");
        myShopFilterPage.applyChosenFilters();
        Response<ProductItemListResponse> defaultParamsResponce = apiClient.searchProductsBy(defaultSearchParam);
        searchProductPage.shouldCatalogResponseEqualsContent(defaultParamsResponce, SearchProductPage.CardType.COMMON, 3);

        // Step 9
        log.step("Повторить шаг 1-2 и выбрать поставщика");
        searchProductPage.goToFilterPage();
        myShopFilterPage.goToSuppliersSearchPage(false);
        suppliersSearchPage.searchAndConfirmSupplier(FIRST_SUPPLIER_CODE);
        suppliersSearchPage.shouldSupplierCheckboxIsSelected(FIRST_SUPPLIER_CODE, true);
        suppliersSearchPage.shouldNameOfChosenIsDisplayedInOvalElement(FIRST_SUPPLIER_NAME);
        // Step 10
        log.step("Нажать на крест на овальном элементе с именем поставщика");
        suppliersSearchPage.cancelChosenSuppler();
        suppliersSearchPage.shouldSupplierCheckboxIsSelected(FIRST_SUPPLIER_CODE, false);

        // Step 11
        log.step("Перейти на страницу выбора фильтров по нажатию на зеленую кнопку назад");
        suppliersSearchPage.clickBackBtn();
        myShopFilterPage.shouldSupplierButtonContainsText(0, null);
    }

    //TODO Добавить тест на проверку отображения и получения услуг

}
