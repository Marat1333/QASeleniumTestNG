package com.leroy.magmobile.ui.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.models.UserData;
import com.leroy.magmobile.ui.pages.common.*;
import com.leroy.magmobile.ui.pages.common.modal.SortPage;
import com.leroy.magmobile.ui.pages.sales.PricesAndQuantityPage;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SimilarProductsPage;
import com.leroy.magmobile.ui.AppBaseSteps;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.BaseModule;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.magmobile.MagMobileClient;
import org.testng.annotations.Guice;
import ru.leroymerlin.qa.core.clients.magmobile.data.ProductItemListResponse;
import ru.leroymerlin.qa.core.clients.magmobile.enums.CatalogSearchFields;
import ru.leroymerlin.qa.core.clients.magmobile.enums.SortingOrder;
import ru.leroymerlin.qa.core.clients.magmobile.requests.GetCatalogSearch;

import javax.annotation.Priority;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Guice(modules = {BaseModule.class})
public class SearchTest extends AppBaseSteps {

    @Inject
    private MagMobileClient apiClient;

    private static final String ALL_DEPARTMENTS_TEXT = "Все отделы";

    @Test(description = "C3200996 Поиск товара по критериям", priority = 2)
    public void testC3200996() throws Exception {

        String lmCode = "10008698";
        String searchContext = "Тепломир радиатор";
        String barCode = "5902120110575";
        String shortLmCode = "100";
        String shortBarCode = "590212011";

        GetCatalogSearch byLmParams = new GetCatalogSearch()
                .pageSize("10")
                .shopId(EnvConstants.BASIC_USER_SHOP_ID)
                .sortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .startFrom("1")
                .byLmCode(lmCode);

        GetCatalogSearch byNameParams = new GetCatalogSearch()
                .pageSize("10")
                .shopId(EnvConstants.BASIC_USER_SHOP_ID)
                .sortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .startFrom("1")
                .byNameLike(searchContext);

        GetCatalogSearch byBarCodeParams = new GetCatalogSearch()
                .pageSize("10")
                .shopId(EnvConstants.BASIC_USER_SHOP_ID)
                .sortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .startFrom("1")
                .byBarCode(barCode);

        GetCatalogSearch byShortLmCodeParams = new GetCatalogSearch()
                .pageSize("10")
                .shopId(EnvConstants.BASIC_USER_SHOP_ID)
                .sortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .startFrom("1")
                .byLmCode(shortLmCode);

        GetCatalogSearch byShortBarCodeParams = new GetCatalogSearch()
                .pageSize("10")
                .shopId(EnvConstants.BASIC_USER_SHOP_ID)
                .sortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .startFrom("1")
                .byBarCode(shortBarCode);

        Response<ProductItemListResponse> lmResponce = apiClient.searchProductsBy(byLmParams);
        Response<ProductItemListResponse> nameLikeResponce = apiClient.searchProductsBy(byNameParams);
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

        // Step 6
        log.step("Введите название товара для поиска");
        searchProductPage.enterTextInSearchFieldAndSubmit(searchContext);
        searchProductPage.shouldProductCardsContainText(searchContext);

        // Step 7
        log.step("Ввести штрихкод вручную");
        searchProductPage.enterTextInSearchFieldAndSubmit(barCode);

        //TODO Добавить проверку что мы оказались на странице с карточкой товара

        productCardPage = new ProductDescriptionPage(context)
                .shouldProductBarCodeIs(barCode);
        searchProductPage = productCardPage.returnBack();

        // Step 8
        log.step("Введите часть ЛМ кода для поиска");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortLmCode);
        searchProductPage.shouldProductCardsContainText(shortLmCode);

        //TODO Добавить проверку ответа на поисковой запрос

        // Step 9
        log.step("Ввести в поисковую строку положительное число длинной >8 символов (" + shortBarCode + ") и инициировать поиск");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortBarCode);
        searchProductPage.shouldProductCardsContainText(shortBarCode);
    }

    @Test(description = "C22846686 Мой магазин. Выбор фильтров каждого блока фильтров", priority = 2)
    public void testC22846686() throws Exception {
        LocalDate avsDate = LocalDate.of(2019, 12, 5);
        String supplierSearchContext = "123";

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(false);
        SuppliersSearchPage suppliersSearchPage = new SuppliersSearchPage(context);
        MyShopFilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        log.step("выбрать одну из гамм");
        filterPage.choseGammaFilter(filterPage.GAMMA + " A");
        filterPage.shouldFilterHasBeenChosen(filterPage.GAMMA + " A");

        // Step 2
        log.step("выбрать один из топов");
        filterPage.choseTopFilter();
        filterPage.shouldFilterHasBeenChosen(filterPage.TOP);

        // Step 3
        log.step("выбрать 1 из чек-боксов блока с типами товаров");
        filterPage.choseCheckBoxFilter(filterPage.BEST_PRICE);
        filterPage.shouldElementHasBeenSelected(filterPage.BEST_PRICE);

        // Step 4
        log.step("выбрать тип товара");
        filterPage.choseProductType(filterPage.ORDERED_PRODUCT_TYPE);
        filterPage.shouldFilterHasBeenChosen(filterPage.ORDERED_PRODUCT_TYPE);


        // Step 5
        log.step("выбрать 1 поставщика");
        filterPage.goToSuppliersSearchPage();
        suppliersSearchPage.verifyRequiredElements();

        suppliersSearchPage.searchSupplier(supplierSearchContext);
        suppliersSearchPage.shouldCountOfProductsOnPageMoreThan(0);
        suppliersSearchPage.shouldProductCardsContainText(supplierSearchContext);
        suppliersSearchPage.verifyElementIsSelected(supplierSearchContext);
        suppliersSearchPage.applyChosenSupplier();

        // Step 6
        log.step("выбрать дату авс");
        filterPage.choseAvsDate(avsDate);
        filterPage.shouldElementHasBeenSelected(filterPage.AVS);

        // Step 7
        log.step("подтвердить примененные фильтры");
        filterPage.applyChosenFilters();
        searchProductPage.verifyRequiredElements();

        // TODO добавить проверку респонса на соответсвие полученных сущностей выбранным фильтрам
    }

    @Test(description = "C22789209 Вся гамма ЛМ. Выбор фильтров каждого раздела", priority = 2)
    public void testC22789209() throws Exception {
        LocalDate avsDate = LocalDate.of(2019, 12, 5);

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(false);
        FilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        log.step("выбрать овальный чек-бокс \"Вся гамма ЛМ\"");
        filterPage.switchFiltersFrame(filterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.shouldFilterHasBeenChosen(filterPage.ALL_GAMMA_FRAME_TYPE);

        // Step 2
        log.step("выбрать одну из гамм");
        AllGammaFilterPage allGammaFilterPage = new AllGammaFilterPage(context);
        allGammaFilterPage.choseGammaFilter(filterPage.GAMMA + " B");
        allGammaFilterPage.shouldFilterHasBeenChosen(filterPage.GAMMA + " B");

        // Step 3
        log.step("выбрать 1 из чек-боксов блока с типами товаров");
        allGammaFilterPage.choseCheckBoxFilter(allGammaFilterPage.CTM);
        allGammaFilterPage.shouldElementHasBeenSelected(allGammaFilterPage.CTM);

        // Step 4
        log.step("выбрать тип товара");
        allGammaFilterPage.choseProductType(allGammaFilterPage.ORDERED_PRODUCT_TYPE);
        allGammaFilterPage.shouldFilterHasBeenChosen(allGammaFilterPage.ORDERED_PRODUCT_TYPE);

        // Step 5
        log.step("выбрать дату авс");
        allGammaFilterPage.choseAvsDate(avsDate);
        allGammaFilterPage.shouldElementHasBeenSelected(allGammaFilterPage.AVS);

        // Step 6
        log.step("подтвердить примененные фильтры");
        allGammaFilterPage.applyChosenFilters();
        searchProductPage.verifyRequiredElements();

        // TODO добавить проверку респонса на соответсвие полученных сущностей выбранным фильтрам
    }

    @Test(description = "C22789172 На поисковой запрос не вернулись результаты", priority = 2)
    public void testC22789172() throws Exception {
        UserData seller = new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS);

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        SearchProductPage searchProductPage = salesPage.clickSearchBar(false);
        String byName = "А13";

        // Step 1
        log.step("Ввести в поле поиска значение, результат поиска по которому не вернется");
        searchProductPage.enterTextInSearchFieldAndSubmit(byName);
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);

        // Step 2
        log.step("выбрать более 1 фильтра и нажать \"Показать товары\"");
        MyShopFilterPage myShopFilterPage = searchProductPage.goToFilterPage();
        myShopFilterPage.scrollHorizontalWidget(myShopFilterPage.GAMMA, myShopFilterPage.GAMMA + " ET");
        myShopFilterPage.choseGammaFilter(myShopFilterPage.GAMMA + " ET");
        myShopFilterPage.shouldFilterHasBeenChosen(myShopFilterPage.GAMMA + " ET");
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
        myShopFilterPage.scrollHorizontalWidget(myShopFilterPage.GAMMA, myShopFilterPage.GAMMA + " ET");
        myShopFilterPage.shouldFilterHasNotBeenChosen(myShopFilterPage.GAMMA + " ET");

        //TODO Проверить пустой ответ API

    }


    @Test(description = "C22789176 Вывод истории поиска", priority = 1)
    public void testC22789176() throws Exception {
        int searchPhrasesCount = 21;

        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);

        // Step 1
        log.step("Нажать на поисковую строку");
        SearchProductPage searchProductPage = salesPage.clickSearchBar(true);
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
                .switchFiltersFrame(myShopFilterPage.ALL_GAMMA_FRAME_TYPE);
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
                .shouldProductCardsBeSorted(SortPage.SORT_BY_LM_ASC, countOfCheckedProducts);

        // Step 3
        log.step("повторить шаг 1-2 для сортировки по лм-коду по убывающей");
        searchProductPage.openSortPage()
                .selectSort(SortPage.SORT_BY_LM_DESC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_LM_DESC, countOfCheckedProducts);

        // Step 4
        log.step("повторить шаг 1-2 для сортировки по запасу по возрастающей");
        searchProductPage.openSortPage()
                .selectSort(SortPage.SORT_BY_AVAILABLE_STOCK_ASC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_AVAILABLE_STOCK_ASC, countOfCheckedProducts);

        // Step 5
        log.step("повторить шаг 1-2 для сортировки по запасу по убывающей");
        searchProductPage.openSortPage()
                .selectSort(SortPage.SORT_BY_AVAILABLE_STOCK_DESC)
                .shouldProductCardsBeSorted(SortPage.SORT_BY_AVAILABLE_STOCK_DESC, countOfCheckedProducts);

        //TODO Проверить сортировку ответа сервиса
    }

    @Test(description = "C22789201 Номенклатура, Навигация и Поиск по структурным элементам номенклатуры", priority = 2)
    public void testC22789201() throws Exception {
        // Pre-conditions
        SalesPage salesPage = loginAndGoTo(SalesPage.class);
        String dept = "015";
        String subDept = "1510";
        String classId = "0030";
        String subClassId = "0020";

        GetCatalogSearch subclassParams = new GetCatalogSearch()
                .departmentId(dept.replaceAll("^0+",""))
                .shopId(EnvConstants.BASIC_USER_SHOP_ID)
                .sortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .startFrom("1")
                .pageSize("10")
                .subDepartmentId(subDept.replaceAll("^0+",""))
                .classId(classId.replaceAll("^0+",""))
                .subclassId(subClassId.replaceAll("^0+",""));

        GetCatalogSearch classParams = new GetCatalogSearch()
                .departmentId(dept.replaceAll("^0+",""))
                .shopId(EnvConstants.BASIC_USER_SHOP_ID)
                .sortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .startFrom("1")
                .pageSize("10")
                .subDepartmentId(subDept.replaceAll("^0+",""))
                .classId(classId.replaceAll("^0+",""));

        GetCatalogSearch subdepartmentParams = new GetCatalogSearch()
                .departmentId(dept.replaceAll("^0+",""))
                .shopId(EnvConstants.BASIC_USER_SHOP_ID)
                .sortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .startFrom("1")
                .pageSize("10")
                .subDepartmentId(subDept.replaceAll("^0+",""));

        GetCatalogSearch departmentParams = new GetCatalogSearch()
                .departmentId(dept.replaceAll("^0+",""))
                .shopId(EnvConstants.BASIC_USER_SHOP_ID)
                .sortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .startFrom("1")
                .pageSize("10");

        Response <ProductItemListResponse> subclassNomenclatureResponce = apiClient.searchProductsBy(subclassParams);
        Response <ProductItemListResponse> classNomenclatureResponce = apiClient.searchProductsBy(classParams);
        Response <ProductItemListResponse> subdepartmentNomenclatureResponce = apiClient.searchProductsBy(subdepartmentParams);
        Response <ProductItemListResponse> departmentNomenclatureResponce = apiClient.searchProductsBy(departmentParams);

        SearchProductPage searchProductPage = salesPage.clickSearchBar(true);

        // Step 1
        log.step("Перейти на страницу выбора номенклатуры");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.verifyRequiredElements();

        // Step 2
        log.step("Перейти в список всех отделов");
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.shouldTitleWithNomenclatureIs("");

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
        searchProductPage.shouldResponceEqualsContent(departmentNomenclatureResponce, true);

        // Step 5
        log.step("повтороить шаг 2-3 для подотделов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(1);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, null, null);

        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept+subDept);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(subDept, false);
        searchProductPage.shouldResponceEqualsContent(subdepartmentNomenclatureResponce, true);

        // Step 6
        log.step("повтороить шаг 2-3 для классов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(2);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, classId, null);

        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept+subDept+classId);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(classId, false);
        searchProductPage.shouldResponceEqualsContent(classNomenclatureResponce, true);

        // Step 7
        log.step("повтороить шаг 2-3 для подклассов");
        searchProductPage.goToNomenclatureWindow();
        nomenclatureSearchPage.returnBackNTimes(3);
        nomenclatureSearchPage.choseDepartmentId(dept, subDept, classId, subClassId);

        nomenclatureSearchPage.shouldTitleWithNomenclatureIs(dept+subDept+classId+subClassId);
        nomenclatureSearchPage.clickShowAllProductsBtn();
        searchProductPage.shouldSelectedNomenclatureIs(subClassId, false);
        searchProductPage.shouldResponceEqualsContent(subclassNomenclatureResponce, true);

    }

    //TODO Добавить тест на проверку отображения и получения услуг

}
