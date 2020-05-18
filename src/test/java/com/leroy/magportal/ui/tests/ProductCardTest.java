package com.leroy.magportal.ui.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.ApiClientProvider;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.products.ExtendedProductCardPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage.FilterFrame;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductCardTest extends WebBaseSteps {
    private static SearchProductPage searchProductPage;

    @Inject
    private ApiClientProvider apiClientProvider;

    @BeforeMethod
    private void precondition() throws Exception {
        searchProductPage = loginAndGoTo(SearchProductPage.class);
    }

    private static <T> T navigateToNeededCard(String lmCode, FilterFrame frame) throws Exception {
        if (frame.equals(FilterFrame.MY_SHOP)) {
            return (T) searchProductPage.<ExtendedProductCardPage>searchProductCardByLmCode(lmCode, frame);
        }else {
            return (T) searchProductPage.<ProductCardTest>searchProductCardByLmCode(lmCode, frame);
        }
    }

    private String getRandomLmCode() {
        apiClientProvider.setSessionData(sessionData);
        CatalogSearchClient catalogSearchClient = apiClientProvider.getCatalogSearchClient();
        ProductItemDataList productItemDataList = catalogSearchClient.searchProductsBy(new GetCatalogSearch().setPageSize(24)).asJson();
        return productItemDataList.getItems().get((int) (Math.random() * 24)).getLmCode();
    }

    @Test(description = "C23388813 add Product to cart")
    public void testAddProductToCart() throws Exception {
        ExtendedProductCardPage extendedProductCardPage = navigateToNeededCard(getRandomLmCode(), FilterFrame.MY_SHOP);
        extendedProductCardPage.addProductToCart();
    }
}
