package com.leroy.magportal.ui.tests;

import com.google.inject.Inject;
import com.leroy.magmobile.api.ApiClientProvider;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.products.ExtendedProductCardPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage.FilterFrame;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductCardTest extends WebBaseSteps {
    private static SearchProductPage searchProductPage;

    @Inject
    private ApiClientProvider apiClientProvider;

    private String lmCode;

    @BeforeMethod
    private void precondition() throws Exception {
        searchProductPage = loginAndGoTo(SearchProductPage.class);
        lmCode = getRandomLmCode();
    }

    private <T> T navigateToNeededCard(String lmCode, FilterFrame frame) throws Exception {
        return searchProductPage.searchProductCardByLmCode(lmCode, frame);
    }

    private String getRandomLmCode() {
        apiClientProvider.setSessionData(sessionData);
        CatalogSearchClient catalogSearchClient = apiClientProvider.getCatalogSearchClient();
        ProductItemDataList productItemDataList = catalogSearchClient.searchProductsBy(new GetCatalogSearch().setPageSize(24)).asJson();
        return productItemDataList.getItems().get((int) (Math.random() * 24)).getLmCode();
    }

    @Test(description = "C23388813 add Product to cart")
    public void testAddProductToCart() throws Exception {
        ExtendedProductCardPage extendedProductCardPage = navigateToNeededCard(lmCode, FilterFrame.MY_SHOP);
        CartPage cartPage = extendedProductCardPage.addProductToCart();
        cartPage.shouldAnyOrderContainsLmCode(lmCode);
    }

    @Test(description = "C23388814 add Product to estimate")
    public void testAddProductToEstimate() throws Exception {
        ExtendedProductCardPage extendedProductCardPage = navigateToNeededCard(lmCode, FilterFrame.MY_SHOP);
        EstimatePage estimatePage = extendedProductCardPage.addProductToEstimate();
        estimatePage.shouldAnyOrderContainsLmCode(lmCode);
    }
}
