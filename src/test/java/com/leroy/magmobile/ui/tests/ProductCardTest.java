package com.leroy.magmobile.ui.tests;

import com.leroy.core.api.Module;
import com.leroy.magmobile.api.clients.CatalogProductClient;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.catalog.product.CatalogSimilarProducts;
import com.leroy.magmobile.api.data.catalog.product.SalesHistoryData;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProductList;
import com.leroy.magmobile.api.data.catalog.supply.CatalogSupplierData;
import com.leroy.magmobile.api.enums.ReviewOptions;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.more.SearchShopPage;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductCardPage;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.ProductPricesQuantitySupplyPage;
import com.leroy.magmobile.ui.pages.sales.product_card.*;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.PeriodOfUsageModalPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SalesHistoryUnitsModalPage;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.SuppliesPage;
import com.leroy.magmobile.ui.pages.search.FilterPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.List;

@Guice(modules = {Module.class})
public class ProductCardTest extends AppBaseSteps {

    private CatalogSearchClient catalogSearchClient;
    private CatalogProductClient catalogProductClient;

    @BeforeClass
    private void initCatalogProductClient() {
        catalogProductClient = apiClientProvider.getCatalogProductClient();
    }

    private String getRandomLmCode() {
        catalogSearchClient = apiClientProvider.getCatalogSearchClient();
        ProductItemDataList productItemDataList = catalogSearchClient.searchProductsBy(new GetCatalogSearch().setPageSize(24)).asJson();
        List<ProductItemData> productItemData = productItemDataList.getItems();
        anAssert().isTrue(productItemData.size() > 0, "size must be more than 0");
        return productItemData.get((int) (Math.random() * productItemData.size())).getLmCode();
    }

    @Step("Перейти в карточку товара {lmCode}")
    private void preconditions(String lmCode) throws Exception {
        MainProductAndServicesPage mainProductAndServicesPage = loginAndGoTo(MainProductAndServicesPage.class);
        SearchProductPage searchProductPage = mainProductAndServicesPage.clickSearchBar(false);
        searchProductPage.enterTextInSearchFieldAndSubmit(lmCode);
    }

    @Test(description = "C3201001 Проверить Историю Продаж")
    public void testSalesHistory() throws Exception {
        String lmCode = getRandomLmCode();
        String notUserShop = "2";
        List<SalesHistoryData> salesHistoryUserShopDataList = catalogProductClient
                .getProductSales(lmCode, getUserSessionData().getUserShopId()).asJsonList(SalesHistoryData.class);
        List<SalesHistoryData> salesHistoryNotUserShopDataList = catalogProductClient
                .getProductSales(lmCode, notUserShop).asJsonList(SalesHistoryData.class);

        //Pre-conditions
        preconditions(lmCode);

        //Step 1
        step("Найти товар и перейти в историю его продаж");
        ProductDescriptionPage productDescriptionPage = new ProductDescriptionPage();
        SalesHistoryPage salesHistoryPage = productDescriptionPage.goToSalesHistoryPage();
        salesHistoryPage.shouldSalesHistoryIsCorrect(salesHistoryUserShopDataList, false);

        //Step 2
        step("Выбрать стоимостной формат отображения");
        SalesHistoryUnitsModalPage salesHistoryUnitsModalPage = salesHistoryPage.openSalesHistoryUnitsModal();
        salesHistoryUnitsModalPage.choseOption(SalesHistoryUnitsModalPage.Option.PRICE);
        salesHistoryPage.shouldSalesHistoryIsCorrect(salesHistoryUserShopDataList, true);

        //Step 3
        step("Проверить данные по товару в другом магазине");
        SearchShopPage searchShopPage = salesHistoryPage.openSearchShopPage();
        searchShopPage.searchForShopAndSelectById(notUserShop);
        salesHistoryPage.shouldSalesHistoryIsCorrect(salesHistoryNotUserShopDataList, true);
    }

    @Test(description = "C3201007 Проверить вкладку Характеристики")
    public void testCharacteristics() throws Exception {
        String lmCode = getRandomLmCode();
        CatalogProductData data = catalogProductClient.getProduct(lmCode).asJson();

        // Pre-conditions
        preconditions(lmCode);

        //Step 1
        step("Перейти во вкладку \"Характеристики\"");
        ProductCardPage productCardPage = new ProductCardPage();
        SpecificationsPage specificationsPage = productCardPage.switchTab(ProductCardPage.Tabs.SPECIFICATION);
        specificationsPage.shouldDataIsCorrect(data);
    }

    @Test(description = "C3201005 Проверить вкладку Отзывы")
    public void testReview() throws Exception {
        String lmCode = getRandomLmCode();
        CatalogReviewsOfProductList reviewsList = catalogProductClient.getProductReviews(lmCode, 1, 100).asJson();

        // Pre-conditions
        preconditions(lmCode);

        //Step 1
        step("Перейти во вкладку \"Отзывы\"");
        ProductCardPage productCardPage = new ProductCardPage();
        ReviewsPage reviewsPage = productCardPage.switchTab(ProductCardPage.Tabs.REVIEWS);
        reviewsPage.shouldReviewsCountIsCorrect(reviewsList);
        reviewsPage.shouldReviewsAreCorrect(reviewsList);

        //Step 2
        step("Перейти в раздел \"Отзывы\" по нажатию на кнопку с кол-вом отзывов");
        ProductDescriptionPage productDescriptionPage = productCardPage.switchTab(ProductCardPage.Tabs.DESCRIPTION);
        productDescriptionPage.shouldReviewCountIsCorrect(reviewsList);
        reviewsPage = productDescriptionPage.goToReviewsPage();
        reviewsPage.verifyRequiredElements();
    }

    @Test(description = "C3201004 Проверить вкладку Аналогичные товары")
    public void testSimilarProducts() throws Exception {
        String lmCode = getRandomLmCode();
        CatalogProductClient.Extend extendParam = CatalogProductClient.Extend.builder()
                .rating(true).logistic(true).inventory(true).build();
        CatalogSimilarProducts data = catalogProductClient.getSimilarProducts(lmCode, extendParam).asJson();

        // Pre-conditions
        preconditions(lmCode);

        //Step 1
        step("Перейти на страницу аналогичных товаров");
        ProductCardPage productCardPage = new ProductCardPage();
        SimilarProductsPage similarProductsPage = productCardPage.switchTab(ProductCardPage.Tabs.SIMILAR_PRODUCTS);
        similarProductsPage.shouldCatalogResponseEqualsContent(data, SearchProductPage.CardType.COMMON, data.getTotalCount());

        //Step 2
        step("Проверить отображение урезанных карточек товара в разделе аналогичные товары");
        SearchProductPage searchProductPage = productCardPage.returnBack();
        FilterPage filterPage = searchProductPage.goToFilterPage();
        filterPage.switchFiltersFrame(FilterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.applyChosenFilters();
        //из-за автоперехода в карточку, ждет элементов на странице поиска
        productCardPage = new ProductCardPage();
        similarProductsPage = productCardPage.switchTab(ProductCardPage.Tabs.SIMILAR_PRODUCTS);
        similarProductsPage.verifyProductCardsHaveAllGammaView();
        similarProductsPage.shouldCatalogResponseEqualsContent(data, SearchProductPage.CardType.ALL_GAMMA, data.getTotalCount());
    }

    @Test(description = "C3201006 Оставить Отзыв о товаре")
    public void testLeaveReview() throws Exception{
        String lmCode = getRandomLmCode();
        String comment = "akdfnadksjfndjskanfkjadsnfkjsandfkjnsajdkfnsakjdfnkjsdanfknsajfdnsajdkfnadskjfnjsanfjsnafdks";
        String shortComment = "asdafsf";

        // Pre-conditions
        preconditions(lmCode);

        //Step 1
        step("Перейти во вкладку \"Отзывы\" и нажать на кнопку \"Оставить отзыв\"");
        ProductCardPage productCardPage = new ProductCardPage();
        ReviewsPage reviewsPage = productCardPage.switchTab(ProductCardPage.Tabs.REVIEWS);
        FirstLeaveReviewPage firstLeaveReviewPage = reviewsPage.leaveReview();
        firstLeaveReviewPage.shouldLmCodeIsCorrect(lmCode);
        firstLeaveReviewPage.shouldRatesIsCorrect(FirstLeaveReviewPage.CommonRate.NOT_CHOSEN,
                FirstLeaveReviewPage.PriceAndQuantityRate.NOT_CHOSEN, FirstLeaveReviewPage.PriceAndQuantityRate.NOT_CHOSEN);
        firstLeaveReviewPage.shouldPeriodOfUsageIsCorrect(ReviewOptions.DEFAULT);

        //Step 2
        step("Заполнить поля первой формы отзыва");
        firstLeaveReviewPage.makeRates(3, 5, 4);
        firstLeaveReviewPage.shouldRatesIsCorrect(FirstLeaveReviewPage.CommonRate.GOOD,
                FirstLeaveReviewPage.PriceAndQuantityRate.SATISFACTORILY, FirstLeaveReviewPage.PriceAndQuantityRate.PERFECT);
        PeriodOfUsageModalPage periodOfUsageModalPage = firstLeaveReviewPage.callPeriodOfUsageModal();
        periodOfUsageModalPage.chosePeriodOfUsage(ReviewOptions.TIME_USAGE_LESS_YEAR);
        firstLeaveReviewPage.shouldPeriodOfUsageIsCorrect(ReviewOptions.TIME_USAGE_LESS_YEAR);

        //Step 3
        step("Перейти на вторую форму отзыва о товаре и заполнить комментарий строкой, не превышающей длинну в 40 символов");
        SecondLeaveReviewPage secondLeaveReviewPage = firstLeaveReviewPage.goToNextReviewPage();
        secondLeaveReviewPage.leaveComment(shortComment);
        secondLeaveReviewPage.shouldControlIsVisible(shortComment);

        //Step 4
        step("Перейти на вторую форму отзыва о товаре, заполнить все поля корректно и отправить отзыв");
        secondLeaveReviewPage.fillAllFields(comment, shortComment, shortComment);
        secondLeaveReviewPage.shouldControlIsVisible(comment);
        SuccessReviewSendingPage successReviewSendingPage = secondLeaveReviewPage.sendReview();
        successReviewSendingPage.verifyRequiredElements();

        //Step 5
        step("Вернуться к товару");
        successReviewSendingPage.backToProduct();
        reviewsPage.verifyRequiredElements();
    }

    @Test(description = "C23409157 Проверить навигацию и информацию во вкладке \"Поставки\"")
    public void testSupply() throws Exception{
        String lmCode = getRandomLmCode();
        CatalogSupplierData data = catalogProductClient.getSupplyInfo(lmCode).asJson();

        // Pre-conditions
        preconditions(lmCode);

        //Step 1
        step("Перейти во вкладку \"Характеристики\" и нажать на кнопку \"Поставщик\"");
        ProductCardPage productCardPage = new ProductCardPage();
        SpecificationsPage specificationsPage = productCardPage.switchTab(ProductCardPage.Tabs.SPECIFICATION);
        SuppliesPage suppliesPage = specificationsPage.goToSupplyInfoPage();
        suppliesPage.verifyRequiredElements();
        suppliesPage.shouldAllSupplyDataIsCorrect(data);

        //Step 2
        step("Перейти во вкладку \"Характеристики\", нажать на кнопку \"Цены\" и открыть раздел \"Поставки\"");
        suppliesPage.goBack();
        ProductDescriptionPage productDescriptionPage = productCardPage.switchTab(ProductCardPage.Tabs.DESCRIPTION);
        ProductPricesQuantitySupplyPage productPricesQuantitySupplyPage = productDescriptionPage.goToPricesAndQuantityPage();
        productPricesQuantitySupplyPage.switchTab(ProductPricesQuantitySupplyPage.Tabs.SUPPLY);
        suppliesPage.verifyRequiredElements();
    }

}
