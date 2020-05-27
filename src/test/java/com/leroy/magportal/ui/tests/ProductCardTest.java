package com.leroy.magportal.ui.tests;

import com.leroy.constants.EnvConstants;
import com.leroy.core.ContextProvider;
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
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class ProductCardTest extends WebBaseSteps {
    private static SearchProductPage searchProductPage;

    private String lmCode;

    @BeforeMethod
    private void precondition() throws Exception {
        searchProductPage = loginAndGoTo(SearchProductPage.class);
        lmCode = getRandomLmCode();
    }

    private <T> T navigateToNeededCard(String lmCode, FilterFrame frame) throws Exception {
        return searchProductPage.searchProductCardByLmCode(lmCode, frame);
    }

    private <T> T navigateToProductCardByUrl(String lmCode, boolean isAllGammaView) {
        WebDriver driver = ContextProvider.getDriver();
        String isAllGammaViewParam = "?isAllGammaView=";
        driver.get(EnvConstants.URL_MAG_PORTAL + "/catalogproducts/product/" + lmCode + isAllGammaViewParam + isAllGammaView);
        return isAllGammaView ? (T) new ProductCardPage() : (T) new ExtendedProductCardPage();
    }

    private String getRandomLmCode() {
        CatalogSearchClient catalogSearchClient = apiClientProvider.getCatalogSearchClient();
        ProductItemDataList productItemDataList = catalogSearchClient.searchProductsBy(new GetCatalogSearch().setPageSize(24)).asJson();
        List<ProductItemData> productItemData = productItemDataList.getItems();
        anAssert().isTrue(productItemData.size() > 0, "size must be more than 0");
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

    @Test(description = "C22790552 additional products tabs")
    public void testAdditionalProducts() throws Exception{
        String lessThan4Similars = "11912697";
        String moreThan4Similars = "15057456";
        String lessThan4Complements = "10813144";
        String moreThan4Complements = "15057456";

        CatalogSimilarProductsData data = apiClientProvider.getMagPortalCatalogProductClientProvider().getSimilarProducts(lessThan4Similars).asJson();
        List<ProductItemData> lessThan4SimilarList = data.getSubstitutes();

        data = apiClientProvider.getMagPortalCatalogProductClientProvider().getSimilarProducts(moreThan4Similars).asJson();
        List<ProductItemData> moreThan4SimilarList = data.getSubstitutes();

        data = apiClientProvider.getMagPortalCatalogProductClientProvider().getSimilarProducts(lessThan4Complements).asJson();
        List<ProductItemData> lessThan4ComplementsList = data.getComplements();

        data = apiClientProvider.getMagPortalCatalogProductClientProvider().getSimilarProducts(moreThan4Complements).asJson();
        List<ProductItemData> moreThan4ComplementsList = data.getComplements();


        //Step 1
        step("открыть карточку товара, у которого не более 4х аналогичных товаров");
        ExtendedProductCardPage extendedProductCardPage = navigateToProductCardByUrl(lessThan4Similars, false);
        extendedProductCardPage.shouldAllAdditionalProductsIsVisible(lessThan4SimilarList);

        //Step 2
        step("открыть карточку товара, у которого более 4х аналогичных товаров");
        extendedProductCardPage = navigateToProductCardByUrl(moreThan4Similars, false);
        extendedProductCardPage.shouldAllAdditionalProductsIsVisible(moreThan4SimilarList);

        //Step 3
        step("открыть карточку товара, у которого не более 4х комплементарных товаров");
        extendedProductCardPage = navigateToProductCardByUrl(lessThan4Complements, false);
        extendedProductCardPage.switchExtraInfoTabs(ExtendedProductCardPage.Tab.COMPLEMENT_PRODUCTS);
        extendedProductCardPage.shouldAllAdditionalProductsIsVisible(lessThan4ComplementsList);

        //Step 4
        step("открыть карточку товара, у которого более 4х комплементарных товаров");
        extendedProductCardPage = navigateToProductCardByUrl(moreThan4Complements, false);
        extendedProductCardPage.switchExtraInfoTabs(ExtendedProductCardPage.Tab.COMPLEMENT_PRODUCTS);
        extendedProductCardPage.shouldAllAdditionalProductsIsVisible(moreThan4ComplementsList);

    }
}
