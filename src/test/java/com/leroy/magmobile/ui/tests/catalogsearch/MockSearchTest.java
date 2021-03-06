package com.leroy.magmobile.ui.tests.catalogsearch;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.common_mashups.catalogs.data.ProductDataList;
import com.leroy.common_mashups.catalogs.requests.GetCatalogProductSearchRequest;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.search.NomenclatureSearchPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import com.leroy.magmobile.ui.tests.BaseUiMagMobMockTest;

import java.util.HashMap;

import io.qameta.allure.AllureId;
import org.testng.annotations.Test;

public class MockSearchTest extends BaseUiMagMobMockTest {

    @Inject
    CatalogProductClient searchClient;

    private final String CURRENT_SHOP = "32";

    private GetCatalogProductSearchRequest buildDefaultCatalogSearchParams() {
        return new GetCatalogProductSearchRequest()
                .setPageSize(10)
                .setShopId(CURRENT_SHOP)
                .setPageNumber(1)
                .setStartFrom(1)
                .setLastItemId("")
                .setHasAvailableStock(true);
    }

    private HashMap<Integer, ThreadApiClient<ProductDataList, CatalogProductClient>> sendRequestsSearchProductsBy(
            GetCatalogProductSearchRequest... paramsArray) {
        HashMap<Integer, ThreadApiClient<ProductDataList, CatalogProductClient>> resultMap = new HashMap<>();
        int i = 0;
        for (GetCatalogProductSearchRequest param : paramsArray) {
            ThreadApiClient<ProductDataList, CatalogProductClient> myThread = new ThreadApiClient<>(
                    searchClient);
            myThread.sendRequest(client -> client.searchProductsBy(param));
            resultMap.put(i, myThread);
            i++;
        }
        return resultMap;
    }

    @Test(description = "C3200996 Поиск товара по критериям")
    @AllureId("12547")
    public void testC3200996() throws Exception {
        String lmCode = "10008698";
        String searchContext = "дрель";
        String barCode = "5902120110575";
        String shortLmCode = "1506";
        String shortBarCode = "590212011";

        GetCatalogProductSearchRequest byLmParams = new GetCatalogProductSearchRequest()
                .setShopId(CURRENT_SHOP)
                .setByLmCode(lmCode);

        GetCatalogProductSearchRequest byNameParam = buildDefaultCatalogSearchParams().setByNameLike(searchContext);

        GetCatalogProductSearchRequest byBarCodeParams = new GetCatalogProductSearchRequest()
                .setByBarCode(barCode)
                .setShopId(CURRENT_SHOP);

        GetCatalogProductSearchRequest byShortLmCodeParams = buildDefaultCatalogSearchParams()
                .setByLmCode(shortLmCode);

        GetCatalogProductSearchRequest byShortBarCodeParams = buildDefaultCatalogSearchParams()
                .setByBarCode(shortBarCode);

        HashMap<Integer, ThreadApiClient<ProductDataList, CatalogProductClient>> apiThreads =
                sendRequestsSearchProductsBy(byLmParams, byNameParam, byBarCodeParams, byShortLmCodeParams, byShortBarCodeParams);

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
        searchProductPage.shouldCatalogResponseEqualsContent(apiThreads.get(0).getData(), SearchProductPage.CardType.COMMON);

        // Step 6
        step("Введите название товара для поиска");
        searchProductPage.enterTextInSearchFieldAndSubmit(searchContext);

        searchProductPage.shouldCatalogResponseEqualsContent(
                apiThreads.get(1).getData(), SearchProductPage.CardType.COMMON);

        // Step 7
        step("Ввести штрихкод вручную");
        searchProductPage.enterTextInSearchFieldAndSubmit(barCode);
        productCardPage = new ProductDescriptionPage()
                .verifyRequiredElements(true)
                .shouldProductBarCodeIs(barCode);
        searchProductPage = productCardPage.returnBack();
        searchProductPage.shouldCatalogResponseEqualsContent(
                apiThreads.get(2).getData(), SearchProductPage.CardType.COMMON);

        // Step 8
        step("Введите часть ЛМ кода для поиска");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortLmCode);
        searchProductPage.shouldCatalogResponseEqualsContent(
                apiThreads.get(3).getData(), SearchProductPage.CardType.COMMON);

        // Step 9
        step("Ввести в поисковую строку положительное число длинной >8 символов (" + shortBarCode + ") и инициировать поиск");
        searchProductPage.enterTextInSearchFieldAndSubmit(shortBarCode);
        searchProductPage.shouldCatalogResponseEqualsContent(
                apiThreads.get(4).getData(), SearchProductPage.CardType.COMMON);
    }

}
