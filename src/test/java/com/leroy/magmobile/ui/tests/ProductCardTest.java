package com.leroy.magmobile.ui.tests;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.catalogs.data.ProductDataList;
import com.leroy.common_mashups.catalogs.data.CatalogComplementaryProductsDataV2;
import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import com.leroy.common_mashups.catalogs.data.CatalogShopsData;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV2;
import com.leroy.common_mashups.catalogs.data.product.SalesHistoryData;
import com.leroy.common_mashups.catalogs.data.product.reviews.CatalogReviewsOfProductList;
import com.leroy.common_mashups.catalogs.data.supply.CatalogSupplierDataOld;
import com.leroy.common_mashups.catalogs.requests.GetCatalogProductSearchRequest;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.UserSessionData;
import com.leroy.magmobile.api.clients.ShopKladrClient;
import com.leroy.magmobile.api.data.shops.PriceAndStockData;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.api.enums.ReviewOptions;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.more.SearchShopPage;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.FirstLeaveReviewPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductCardPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ReviewsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SalesHistoryPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SecondLeaveReviewPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SimilarProductsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SpecificationsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SuccessReviewSendingPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.PeriodOfUsageModalPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SalesHistoryUnitsModalPage;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.PricesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.ProductPricesQuantitySupplyPage;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.ShopPricesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.ShopsStocksPage;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.StocksPage;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.SuppliesPage;
import com.leroy.magmobile.ui.pages.search.FilterPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.qameta.allure.AllureId;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.util.LatLongUtils;
import org.testng.annotations.Test;

public class ProductCardTest extends AppBaseSteps {

    @Inject
    private CatalogProductClient catalogSearchClient;
    @Inject
    private CatalogProductClient catalogProductClient;
    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private ShopKladrClient shopKladrClient;

    @Override
    public UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData sessionData = super.initTestClassUserSessionDataTemplate();
        sessionData.setUserShopId("32");
        return sessionData;
    }

    private String getRandomLmCode() {

        ProductDataList productDataList = catalogSearchClient.searchProductsBy(new GetCatalogProductSearchRequest().setPageSize(24)).asJson();
        List<ProductData> productIData = productDataList.getItems();
        anAssert().isTrue(productIData.size() > 0, "size must be more than 0");
        return productIData.get((int) (Math.random() * productIData.size())).getLmCode();
    }

    private List<ShopData> sortShopsByDistance(List<ShopData> shopDataList) {
        ShopData userShopData = shopDataList.stream().filter((i) -> i.getId().equals(EnvConstants.BASIC_USER_SHOP_ID)).findAny().orElse(null);
        LatLong currentShop = new LatLong(userShopData.getLat(), userShopData.getLongitude());
        for (ShopData data : shopDataList) {
            LatLong goalShop = new LatLong(data.getLat(), data.getLongitude());
            data.setDistance(LatLongUtils.sphericalDistance(currentShop, goalShop));
        }
        List<ShopData> sortedShopDataList = shopDataList.stream().sorted(Comparator.comparingDouble(ShopData::getDistance)).collect(Collectors.toList());
        //?????????????? ?? ?????????????? ????????????????????
        sortedShopDataList.remove(0);
        return sortedShopDataList;
    }

    private List<ShopData> setPricesAndStockForEachShopData(List<CatalogShopsData> catalogShopsData, List<ShopData> shopData) {
        for (ShopData data : shopData) {
            for (CatalogShopsData catalogData : catalogShopsData) {
                if (data.getId().equals(catalogData.getShopId())) {
                    data.setPriceAndStock(new PriceAndStockData(catalogData.getPrice(), catalogData.getAvailableStock()));
                }
            }
        }
        return shopData;
    }

    private List<CatalogProductData> sortByAvailableStockDesc(List<CatalogProductData> dataList) {
        dataList.sort(Comparator.comparingDouble(CatalogProductData::getAvailableStock));
        Collections.reverse(dataList);
        return dataList;
    }

    @Step("?????????????? ?? ???????????????? ???????????? {lmCode}")
    private void openProductCard(String lmCode) throws Exception {
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
    }

    @Test(description = "C3201001 ?????????????????? ?????????????? ????????????")
    @AllureId("12522")
    public void testSalesHistory() throws Exception {
        String lmCode = getRandomLmCode();
        String notUserShop = "2";
        List<SalesHistoryData> salesHistoryUserShopDataList = catalogProductClient
                .getProductSales(lmCode, getUserSessionData().getUserShopId()).asJsonList(SalesHistoryData.class);
        List<SalesHistoryData> salesHistoryNotUserShopDataList = catalogProductClient
                .getProductSales(lmCode, notUserShop).asJsonList(SalesHistoryData.class);

        //Pre-conditions
        openProductCard(lmCode);

        //Step 1
        step("?????????? ?????????? ?? ?????????????? ?? ?????????????? ?????? ????????????");
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage();
        SalesHistoryPage salesHistoryPage = productDescriptionPage.goToSalesHistoryPage();
        salesHistoryPage.shouldSalesHistoryIsCorrect(salesHistoryUserShopDataList, false);

        //Step 2
        step("?????????????? ?????????????????????? ???????????? ??????????????????????");
        SalesHistoryUnitsModalPage salesHistoryUnitsModalPage = salesHistoryPage.openSalesHistoryUnitsModal();
        salesHistoryUnitsModalPage.choseOption(SalesHistoryUnitsModalPage.Option.PRICE);
        salesHistoryPage.shouldSalesHistoryIsCorrect(salesHistoryUserShopDataList, true);

        //Step 3
        step("?????????????????? ???????????? ???? ???????????? ?? ???????????? ????????????????");
        SearchShopPage searchShopPage = salesHistoryPage.openSearchShopPage();
        searchShopPage.searchForShopAndSelectById(notUserShop);
        salesHistoryPage = new SalesHistoryPage();
        salesHistoryPage.shouldSalesHistoryIsCorrect(salesHistoryNotUserShopDataList, true);
    }

    @Test(description = "C3201007 ?????????????????? ?????????????? ????????????????????????????")
    @AllureId("12525")
    public void testCharacteristics() throws Exception {
        String lmCode = getRandomLmCode();
        ProductData data = catalogProductClient.getProduct(lmCode).asJson();

        // Pre-conditions
        openProductCard(lmCode);

        //Step 1
        step("?????????????? ???? ?????????????? \"????????????????????????????\"");
        ProductCardPage productCardPage = new ProductCardPage();
        SpecificationsPage specificationsPage = productCardPage.switchTab(ProductCardPage.Tabs.SPECIFICATION);
        specificationsPage.shouldDataIsCorrect(data);
    }

    @Test(description = "C3201005 ?????????????????? ?????????????? ????????????")
    @AllureId("12526")
    public void testReview() throws Exception {
        String lmCode = "10009084";
        CatalogReviewsOfProductList reviewsList = catalogProductClient.getProductReviews(lmCode, 1, 100).asJson();

        // Pre-conditions
        openProductCard(lmCode);

        //Step 1
        step("?????????????? ???? ?????????????? \"????????????\"");
        ProductCardPage productCardPage = new ProductCardPage();
        ReviewsPage reviewsPage = productCardPage.switchTab(ProductCardPage.Tabs.REVIEWS);
        reviewsPage.shouldReviewsCountIsCorrect(reviewsList);
        reviewsPage.shouldReviewsAreCorrect(reviewsList);

        //Step 2
        step("?????????????? ?? ???????????? \"????????????\" ???? ?????????????? ???? ???????????? ?? ??????-?????? ??????????????");
        ProductDescriptionPage productDescriptionPage = productCardPage.switchTab(ProductCardPage.Tabs.DESCRIPTION);
        productDescriptionPage.shouldReviewCountIsCorrect(reviewsList);
        reviewsPage = productDescriptionPage.goToReviewsPage();
        reviewsPage.verifyRequiredElements();
    }

    @Test(description = "C3201004 ?????????????????? ?????????????? ?????????????????????? ????????????")
    @AllureId("12527")
    public void testSimilarProducts() throws Exception {
        String lmCode = "10009965";
        CatalogProductClient.Extend extendParam = CatalogProductClient.Extend.builder()
                .rating(true).logistic(true).inventory(true).build();
        CatalogSimilarProductsDataV2 data = catalogProductClient.getSimilarProductsV2(lmCode, extendParam).asJson();

        // Pre-conditions
        openProductCard(lmCode);

        //Step 1
        step("?????????????? ???? ???????????????? ?????????????????????? ??????????????");
        ProductCardPage productCardPage = new ProductCardPage();
        SimilarProductsPage similarProductsPage = productCardPage.switchTab(ProductCardPage.Tabs.SIMILAR_PRODUCTS);
        similarProductsPage.shouldCatalogResponseEqualsContent(data, SearchProductPage.CardType.COMMON, data.getTotalCount());

        //Step 2
        step("?????????????????? ?????????????????????? ?????????????????? ???????????????? ???????????? ?? ?????????????? ?????????????????????? ????????????");
        SearchProductPage searchProductPage = productCardPage.returnBack();
        FilterPage filterPage = searchProductPage.goToFilterPage();
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.applyChosenFilters(ProductCardPage.class);
        similarProductsPage = productCardPage.switchTab(ProductCardPage.Tabs.SIMILAR_PRODUCTS);
        similarProductsPage.verifyProductCardsHaveAllGammaView();
        similarProductsPage.shouldCatalogResponseEqualsContent(data, SearchProductPage.CardType.ALL_GAMMA, data.getTotalCount());
    }

    @Test(description = "C3201006 ???????????????? ?????????? ?? ????????????")
    @AllureId("12528")
    public void testLeaveReview() throws Exception {
        String lmCode = getRandomLmCode();
        String comment = "akdfnadksjfndjskanfkjadsnfkjsandfkjnsajdkfnsakjdfnkjsdanfknsajfdnsajdkfnadskjfnjsanfjsnafdks";
        String shortComment = "asdafsf";

        // Pre-conditions
        openProductCard(lmCode);

        //Step 1
        step("?????????????? ???? ?????????????? \"????????????\" ?? ???????????? ???? ???????????? \"???????????????? ??????????\"");
        ProductCardPage productCardPage = new ProductCardPage();
        ReviewsPage reviewsPage = productCardPage.switchTab(ProductCardPage.Tabs.REVIEWS);
        FirstLeaveReviewPage firstLeaveReviewPage = reviewsPage.leaveReview();
        firstLeaveReviewPage.shouldLmCodeIsCorrect(lmCode);
        firstLeaveReviewPage.shouldRatesIsCorrect(FirstLeaveReviewPage.CommonRate.NOT_CHOSEN,
                FirstLeaveReviewPage.PriceAndQuantityRate.NOT_CHOSEN, FirstLeaveReviewPage.PriceAndQuantityRate.NOT_CHOSEN);
        firstLeaveReviewPage.shouldPeriodOfUsageIsCorrect(ReviewOptions.DEFAULT);

        //Step 2
        step("?????????????????? ???????? ???????????? ?????????? ????????????");
        firstLeaveReviewPage.makeRates(3, 5, 4);
        firstLeaveReviewPage.shouldRatesIsCorrect(FirstLeaveReviewPage.CommonRate.GOOD,
                FirstLeaveReviewPage.PriceAndQuantityRate.SATISFACTORILY, FirstLeaveReviewPage.PriceAndQuantityRate.PERFECT);
        PeriodOfUsageModalPage periodOfUsageModalPage = firstLeaveReviewPage.callPeriodOfUsageModal();
        periodOfUsageModalPage.chosePeriodOfUsage(ReviewOptions.TIME_USAGE_LESS_YEAR);
        firstLeaveReviewPage.shouldPeriodOfUsageIsCorrect(ReviewOptions.TIME_USAGE_LESS_YEAR);

        //Step 3
        step("?????????????? ???? ???????????? ?????????? ???????????? ?? ???????????? ?? ?????????????????? ?????????????????????? ??????????????, ???? ?????????????????????? ???????????? ?? 40 ????????????????");
        SecondLeaveReviewPage secondLeaveReviewPage = firstLeaveReviewPage.goToNextReviewPage();
        secondLeaveReviewPage.leaveComment(shortComment);
        secondLeaveReviewPage.shouldControlIsVisible(shortComment);

        //Step 4
        step("?????????????? ???? ???????????? ?????????? ???????????? ?? ????????????, ?????????????????? ?????? ???????? ?????????????????? ?? ?????????????????? ??????????");
        secondLeaveReviewPage.fillAllFields(comment, shortComment, shortComment);
        secondLeaveReviewPage.shouldControlIsVisible(comment);
        SuccessReviewSendingPage successReviewSendingPage = secondLeaveReviewPage.sendReview();
        successReviewSendingPage.verifyRequiredElements();

        //Step 5
        step("?????????????????? ?? ????????????");
        successReviewSendingPage.backToProduct();
        reviewsPage.verifyRequiredElements();
    }

    @Test(description = "C23409157 ?????????????????? ?????????????????? ?? ???????????????????? ???? ?????????????? \"????????????????\"")
    @AllureId("12530")
    public void testSupply() throws Exception {
        String lmCode = "10009340";
        CatalogSupplierDataOld data = catalogProductClient.getSupplyInfo(lmCode).asJson();

        // Pre-conditions
        openProductCard(lmCode);

        //Step 1
        step("?????????????? ???? ?????????????? \"????????????????????????????\" ?? ???????????? ???? ???????????? \"??????????????????\"");
        ProductCardPage productCardPage = new ProductCardPage();
        SpecificationsPage specificationsPage = productCardPage.switchTab(ProductCardPage.Tabs.SPECIFICATION);
        SuppliesPage suppliesPage = specificationsPage.goToSupplyInfoPage();
        suppliesPage.verifyRequiredElements();
        suppliesPage.shouldAllSupplyDataIsCorrect(data);

        //Step 2
        step("?????????????? ???? ?????????????? \"????????????????????????????\", ???????????? ???? ???????????? \"????????\" ?? ?????????????? ???????????? \"????????????????\"");
        suppliesPage.goBack();
        productCardPage = new ProductCardPage();
        productCardPage.switchTab(ProductCardPage.Tabs.DESCRIPTION);
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage();
        ProductPricesQuantitySupplyPage productPricesQuantitySupplyPage = productDescriptionPage.goToPricesAndQuantityPage();
        productPricesQuantitySupplyPage.switchTab(ProductPricesQuantitySupplyPage.Tabs.SUPPLY);
        suppliesPage.verifyRequiredElements();
    }

    @Test(description = "C23409225 ?????????????????? ???????????????????? ???? ?????????????? ???????????????? ????????????")
    @AllureId("12531")
    public void testDescription() throws Exception {
        String lmCode = getRandomLmCode();
        CatalogProductClient.Extend extendOptions = CatalogProductClient.Extend.builder()
                .inventory(true).logistic(true).rating(true).build();
        ProductData data = catalogProductClient.getProduct(lmCode, SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR,
                extendOptions).asJson();

        // Pre-conditions
        openProductCard(lmCode);

        //Step 1
        step("?????????????? ???? ?????????????? \"???????????????? ????????????\"");
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage();
        productDescriptionPage.shouldDataIsCorrect(data);
    }

    @Test(description = "C3201002 ?????????????????? ???????????? ???? ???????????????? ????????, ??????????")
    @AllureId("12523")
    public void testStocksSales() throws Exception {
        String lmCode = "10008698";
        CatalogProductClient.Extend extendOptions = CatalogProductClient.Extend.builder()
                .inventory(true).logistic(true).rating(true).build();

        ProductData data = catalogProductClient.getProduct("35", lmCode, SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR,
                extendOptions).asJson();

        // Pre-conditions
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        mainProductAndServicesPage = setShopAndDepartmentForUser(mainProductAndServicesPage, "35", "1").goToSales();
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);


        //Step 1
        step("?????????????? ???? ?????????????? \"???????????????? ????????????\" ?? ?????????????? ???????????????? ?? ?????????????????????? ?? ????????");
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage();
        ProductPricesQuantitySupplyPage productPricesQuantitySupplyPage = productDescriptionPage.goToPricesAndQuantityPage();
        PricesPage pricesPage = new PricesPage();
        pricesPage.shouldDataIsCorrect(data);

        //Step 2
        step("?????????????? ?????????????? ?? ?????????????????????? ?? ????????????");
        StocksPage stocksPage = productPricesQuantitySupplyPage.switchTab(ProductPricesQuantitySupplyPage.Tabs.STOCKS);
        //TODO will be changed soon
        stocksPage.shouldDataIsCorrect(data);

        //Step 3
        step("?????????????????? ?????????????????? ???? ???????????????? ?? ?????????????????????? ?? ????????????");
        productPricesQuantitySupplyPage.goBack();
        productDescriptionPage = new ProductDescriptionPage();
        productDescriptionPage.goToStocksPage();
        stocksPage.verifyRequiredElements();
    }

    @Test(description = "C3201003 ?????????????????? ???????? ?? ?????????? ?? ?????????????????? ??????????????????")
    @AllureId("12524")
    public void testPricesAndStocksInNearestShops() throws Exception {
        String searchId = "34";
        String searchName = "?????? ??";

        //get all shop id
        List<ShopData> shopDataList = shopKladrClient.getShops().asJsonList(ShopData.class);
        List<String> shopIdList = shopDataList.stream().map(ShopData::getId).collect(Collectors.toList());
        String[] shopIdArray = new String[shopIdList.size()];
        shopIdArray = shopIdList.toArray(shopIdArray);

        //get prices and quantity in all shops
        String lmCode = getRandomLmCode();
        List<CatalogShopsData> catalogShopsList = catalogProductClient.getProductShopsPriceAndQuantity(lmCode, shopIdArray).asJsonList(CatalogShopsData.class);
        List<ShopData> sortedShopDataList = sortShopsByDistance(shopDataList);
        sortedShopDataList = setPricesAndStockForEachShopData(catalogShopsList, sortedShopDataList);

        // Pre-conditions
        openProductCard(lmCode);

        //Step 1
        step("?????????????? ???? ?????????????? \"???????????????? ????????????\" ?? ?????????????? ???????????????? ?? ?????????????????????? ?? ????????");
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage();
        ProductPricesQuantitySupplyPage productPricesQuantitySupplyPage = productDescriptionPage.goToPricesAndQuantityPage();
        PricesPage pricesPage = new PricesPage();
        pricesPage.shouldShopPricesAreCorrect(sortedShopDataList);

        //Step 2
        step("?????????????????? ???????? ???? ???????? ?????????????????? ???? ????????????");
        ShopPricesPage shopPricesPage = pricesPage.goToShopListPage();
        shopPricesPage.shouldShopPricesAreCorrect(sortedShopDataList);

        //Step 3
        step("???????????? ?????????????? ???? id");
        shopPricesPage.searchShopBy(searchId);
        shopPricesPage.shouldShopCardsContainsSearchCriterion(searchId);

        //Step 4
        step("???????????? ?????????????? ???? ??????????");
        shopPricesPage.searchShopBy(searchName);
        shopPricesPage.shouldShopCardsContainsSearchCriterion(searchName);

        //Step 5
        step("?????????????? ???? ???????????????? ?? ?????????????????????? ???? ????????????????");
        shopPricesPage.goToPreviousPage();
        StocksPage stocksPage = productPricesQuantitySupplyPage.switchTab(ProductPricesQuantitySupplyPage.Tabs.STOCKS);
        stocksPage.shouldShopStocksAreCorrect(sortedShopDataList);

        //Step 6
        step("?????????????????? ?????????????? ???? ???????? ?????????????????? ???? ????????????");
        ShopsStocksPage shopsStocksPage = stocksPage.goToShopListPage();
        shopsStocksPage.shouldShopStocksAreCorrect(sortedShopDataList);

        //Step 7
        step("???????????? ?????????????? ???? id");
        shopsStocksPage.searchShopBy(searchId);
        shopsStocksPage.shouldShopCardsContainsSearchCriterion(searchId);

        //Step 8
        step("???????????? ?????????????? ???? ??????????");
        shopsStocksPage.searchShopBy(searchName);
        shopsStocksPage.shouldShopCardsContainsSearchCriterion(searchName);
    }

    @Test(description = "C23409226 ?????????????????? ?????????????????????????????? ????????????")
    @AllureId("12532")
    public void testComplementaryProducts() throws Exception {
        CatalogComplementaryProductsDataV2 lmWithComplementaryData = searchProductHelper.getComplementaryProductData(false);
        CatalogComplementaryProductsDataV2 lmWithoutComplementaryData = searchProductHelper.getComplementaryProductData(true);
        String lmWithComplementary = lmWithComplementaryData.getParentLmCode();
        String lmWithoutComplementary = lmWithoutComplementaryData.getParentLmCode();
        List<ProductData> sortedProductData = lmWithComplementaryData.getItems();

        //preconditions
        openProductCard(lmWithComplementary);

        //Step 1
        step("?????????????? ?? ???????????????? ???????????? ?? ???????????????????????????????? ????????????????");
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage();
        productDescriptionPage.shouldComplementaryProductsAreCorrect(sortedProductData, SearchProductPage.CardType.COMMON);

        //Step 2
        step("?????????????? ?? ?????????????????????? ???????????????? ???????????? ?? ???????????????????????????????? ????????????????");
        SearchProductPage searchProductPage = productDescriptionPage.returnBack();
        searchProductPage.clearSearchInput();
        FilterPage filterPage = searchProductPage.goToFilterPage();
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        searchProductPage = filterPage.applyChosenFilters();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmWithComplementary);
        productDescriptionPage = new ProductDescriptionPage();
        productDescriptionPage.shouldComplementaryProductsAreCorrect(sortedProductData, SearchProductPage.CardType.ALL_GAMMA);

        //Step 3
        step("?????????????? ?? ???????????????? ???????????? ?????? ?????????????????????????????? ??????????????");
        searchProductPage = productDescriptionPage.returnBack();
        searchProductPage.enterTextInSearchFieldAndSubmit(lmWithoutComplementary);
        productDescriptionPage = new ProductDescriptionPage();
        productDescriptionPage.shouldComplementaryProductsAreCorrect(lmWithoutComplementaryData.getItems(), SearchProductPage.CardType.ALL_GAMMA);
    }

}
