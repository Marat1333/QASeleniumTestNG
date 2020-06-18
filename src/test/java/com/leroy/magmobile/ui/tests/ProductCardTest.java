package com.leroy.magmobile.ui.tests;

import com.leroy.core.api.Module;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.CatalogProductClient;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.catalog.product.SalesHistoryData;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProductList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.ui.AppBaseSteps;
import com.leroy.magmobile.ui.pages.more.SearchShopPage;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.ProductCardPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ReviewsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SalesHistoryPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SpecificationsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.modal.SalesHistoryUnitsModalPage;
import com.leroy.magmobile.ui.pages.search.SearchProductPage;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
    private void preconditions(String lmCode) throws Exception{
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
    public void testReview() throws Exception{
        String lmCode = "10009260";
        CatalogReviewsOfProductList reviewsList = catalogProductClient.getProductReviews(lmCode, 1, 100).asJson();

        // Pre-conditions
        preconditions(lmCode);
        //TODO verification on descriptionPage

        //Step 1
        step("Перейти во вкладку \"Отзывы\"");
        ProductCardPage productCardPage = new ProductCardPage();
        ReviewsPage reviewsPage = productCardPage.switchTab(ProductCardPage.Tabs.REVIEWS);
        reviewsPage.shouldReviewsCountIsCorrect(reviewsList);
        reviewsPage.shouldReviewsAreCorrect(reviewsList);
    }
}
