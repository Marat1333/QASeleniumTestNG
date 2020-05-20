package com.leroy.magportal.ui.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.ApiClientProvider;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magportal.api.data.CatalogSimilarProductsData;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.products.ExtendedProductCardPage;
import com.leroy.magportal.ui.pages.products.ProductCardPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage.FilterFrame;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class ProductCardTest extends WebBaseSteps {
    private static SearchProductPage searchProductPage;

    @Inject
    private ApiClientProvider apiClientProvider;

    private String lmCode;

    @BeforeMethod
    private void precondition() throws Exception {
        searchProductPage = loginAndGoTo(SearchProductPage.class);
        apiClientProvider.setSessionData(sessionData);
        lmCode = getRandomLmCode();
    }

    private <T> T navigateToNeededCard(String lmCode, FilterFrame frame) throws Exception {
        return searchProductPage.searchProductCardByLmCode(lmCode, frame);
    }

    private <T> T navigateToProductCardByUrl(String lmCode, boolean isAllGammaView) {
        String isAllGammaViewParam = "?isAllGammaView=";
        driver.get(EnvConstants.URL_MAG_PORTAL + "/catalogproducts/product/" + lmCode + isAllGammaViewParam + isAllGammaView);
        return isAllGammaView ? (T) new ProductCardPage(context) : (T) new ExtendedProductCardPage(context);
    }

    private String getRandomLmCode() {
        CatalogSearchClient catalogSearchClient = apiClientProvider.getCatalogSearchClient();
        ProductItemDataList productItemDataList = catalogSearchClient.searchProductsBy(new GetCatalogSearch().setPageSize(24)).asJson();
        List<ProductItemData> productItemData = productItemDataList.getItems();
        return productItemData.get((int) (Math.random() * productItemData.size())).getLmCode();
    }

    private String getRandomSimilarProductLmCode(String sourceLmCode) {
        CatalogSimilarProductsData data = apiClientProvider.getMagPortalCatalogProductClientProvider().getSimilarProducts(sourceLmCode).asJson();
        List<ProductItemData> resultList = data.getSubstitutes();
        String result = resultList.get((int) (Math.random() * resultList.size())).getLmCode();
        return result;
    }

    private String getRandomComplementProductLmCode(String sourceLmCode) {
        CatalogSimilarProductsData data = apiClientProvider.getMagPortalCatalogProductClientProvider().getSimilarProducts(sourceLmCode).asJson();
        List<ProductItemData> resultList = data.getComplements();
        String result = resultList.get((int) (Math.random() * resultList.size())).getLmCode();
        return result;
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

    @Test(description = "C23388974 go to card by url")
    public void testGoToCardByUrl() {
        ExtendedProductCardPage extendedProductCardPage = navigateToProductCardByUrl(lmCode, false);
        extendedProductCardPage.verifyRequiredElements();

        ProductCardPage productCardPage = navigateToProductCardByUrl(lmCode, true);
        productCardPage.verifyRequiredElements();
    }

    @Test(description = "C22790554 Go to another card (similar or related products)")
    public void testGoToAdditionalProductCard() throws Exception {
        String sourceLmCode = "15057456";
        String similarProductLmCode = getRandomSimilarProductLmCode(sourceLmCode);
        String complementProductLmCode = getRandomComplementProductLmCode(sourceLmCode);

        ExtendedProductCardPage extendedProductCardPage = navigateToProductCardByUrl(sourceLmCode, false);

        extendedProductCardPage.goToAdditionalProduct(similarProductLmCode, ExtendedProductCardPage.Tab.SIMILAR_PRODUCTS);
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(similarProductLmCode);

        extendedProductCardPage.navigateBack();
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(sourceLmCode);

        extendedProductCardPage.navigateForward();
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(similarProductLmCode);

        extendedProductCardPage.navigateBack();
        extendedProductCardPage.goToAdditionalProduct(complementProductLmCode, ExtendedProductCardPage.Tab.COMPLEMENT_PRODUCTS);
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(complementProductLmCode);

        extendedProductCardPage.navigateBack();
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(sourceLmCode);

        extendedProductCardPage.navigateForward();
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(complementProductLmCode);
    }
}
