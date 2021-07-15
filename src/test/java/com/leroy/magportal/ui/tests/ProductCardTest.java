package com.leroy.magportal.ui.tests;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV1;
import com.leroy.common_mashups.catalogs.data.NearestShopsData;
import com.leroy.common_mashups.catalogs.data.ProductDataList;
import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.catalogs.requests.GetCatalogProductSearchRequest;
import com.leroy.constants.EnvConstants;
import com.leroy.core.ContextProvider;
import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.constants.search.CatalogSearchParams;
import com.leroy.magportal.ui.models.search.NomenclaturePath;
import com.leroy.magportal.ui.pages.cart_estimate.CartPage;
import com.leroy.magportal.ui.pages.cart_estimate.EstimatePage;
import com.leroy.magportal.ui.pages.products.ExtendedProductCardPage;
import com.leroy.magportal.ui.pages.products.ProductCardPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage;
import com.leroy.magportal.ui.pages.products.SearchProductPage.FilterFrame;
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProductCardTest extends WebBaseSteps {

    @Inject
    private CatalogProductClient catalogSearchClient;

    @BeforeMethod
    private void precondition() throws Exception {
        loginAndGoTo(SearchProductPage.class);
    }

    private <T> T navigateToNeededCard(String lmCode, FilterFrame frame) throws Exception {
        SearchProductPage searchProductPage = new SearchProductPage();
        return searchProductPage.searchProductCardByLmCode(lmCode, frame);
    }

    private <T> T navigateToProductCardByUrl(String lmCode, boolean isAllGammaView) {
        WebDriver driver = ContextProvider.getDriver();
        String isAllGammaViewParam = "?isAllGammaView=";
        driver.get(EnvConstants.URL_MAG_PORTAL + "/orders/catalogproducts/product/" + lmCode + isAllGammaViewParam + isAllGammaView);
        return isAllGammaView ? (T) new ProductCardPage() : (T) new ExtendedProductCardPage();
    }

    private String getRandomLmCode() {
        ProductDataList productDataList = catalogSearchClient.searchProductsBy(new GetCatalogProductSearchRequest().setPageSize(24)).asJson();
        List<ProductData> productIData = productDataList.getItems();
        anAssert().isTrue(productIData.size() > 0, "size must be more than 0");
        return productIData.get((int) (Math.random() * productIData.size())).getLmCode();
    }

    private String getRandomSimilarProductLmCode(String sourceLmCode) {
        CatalogSimilarProductsDataV1 data = catalogSearchClient.getSimilarProductsV1(sourceLmCode).asJson();
        List<ProductData> resultList = data.getSubstitutes();
        String result = resultList.get((int) (Math.random() * resultList.size())).getLmCode();
        return result;
    }

    private String getRandomComplementProductLmCode(String sourceLmCode) {
        CatalogSimilarProductsDataV1 data = catalogSearchClient.getSimilarProductsV1(sourceLmCode).asJson();
        List<ProductData> resultList = data.getComplements();
        String result = resultList.get((int) (Math.random() * resultList.size())).getLmCode();
        return result;
    }

    @Test(description = "C23388813 add Product to cart")
    public void testAddProductToCart() throws Exception {
        String lmCode = getRandomLmCode();

        //Pre-condition
        ExtendedProductCardPage extendedProductCardPage = navigateToNeededCard(lmCode, FilterFrame.MY_SHOP);

        //Step 1
        step("Добавить товар в корзину");
        CartPage cartPage = extendedProductCardPage.addProductToCart();
        cartPage.shouldDocumentHasProducts(Collections.singletonList(lmCode));
    }

    @Test(description = "C23388814 add Product to estimate")
    public void testAddProductToEstimate() throws Exception {
        String lmCode = getRandomLmCode();

        //Pre-condition
        ExtendedProductCardPage extendedProductCardPage = navigateToNeededCard(lmCode, FilterFrame.MY_SHOP);

        //Step 1
        step("Добавить товар в смету");
        EstimatePage estimatePage = extendedProductCardPage.addProductToEstimate();
        estimatePage.shouldDocumentHasProducts(Collections.singletonList(lmCode));
    }

    @Test(description = "C23388974 go to card by url")
    public void testGoToCardByUrl() {
        String lmCode = getRandomLmCode();

        //Step 1
        step("Перейти в расширенную карточку товара по URL");
        ExtendedProductCardPage extendedProductCardPage = navigateToProductCardByUrl(lmCode, false);
        extendedProductCardPage.verifyRequiredElements();

        //Step 2
        step("Перейти в укороченную карточку товара по URL");
        ProductCardPage productCardPage = navigateToProductCardByUrl(lmCode, true);
        productCardPage.verifyRequiredElements();
    }

    @Test(description = "C22790554 Go to another card (similar or related products)")
    public void testGoToAdditionalProductCard() throws Exception {
        String sourceLmCode = "15057456";
        String similarProductLmCode = getRandomSimilarProductLmCode(sourceLmCode);
        String complementProductLmCode = getRandomComplementProductLmCode(sourceLmCode);

        //Pre-conditions
        ExtendedProductCardPage extendedProductCardPage = navigateToProductCardByUrl(sourceLmCode, false);

        //Step 1
        step("Перейти в любую карточку аналогичного товара");
        extendedProductCardPage.goToAdditionalProduct(similarProductLmCode, ExtendedProductCardPage.Tab.SIMILAR_PRODUCTS);
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(similarProductLmCode);

        //Step 2
        step("Перейти назад в исходную карточку товара");
        extendedProductCardPage.doubleNavigateBack();
        extendedProductCardPage = new ExtendedProductCardPage();
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(sourceLmCode);

        //Step 3
        step("Перейти вперед в карточку аналогичного товара");
        extendedProductCardPage.navigateForward();
        extendedProductCardPage = new ExtendedProductCardPage();
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(similarProductLmCode);

        //Step 4
        step("Перейти назад в исходную карточку товара и затем перейти в любую карточку комплементарных товаров");
        extendedProductCardPage.doubleNavigateBack();
        extendedProductCardPage = new ExtendedProductCardPage();
        extendedProductCardPage.goToAdditionalProduct(complementProductLmCode, ExtendedProductCardPage.Tab.COMPLEMENT_PRODUCTS);
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(complementProductLmCode);

        //Step 5
        step("Перейти назад в исходную карточку товара");
        extendedProductCardPage.doubleNavigateBack();
        extendedProductCardPage = new ExtendedProductCardPage();
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(sourceLmCode);

        //Step 6
        step("Перейти вперед в карточку комплементарного товара");
        extendedProductCardPage.navigateForward();
        extendedProductCardPage = new ExtendedProductCardPage();
        extendedProductCardPage.shouldProductCardContainsLmOrBarCode(complementProductLmCode);
    }

    @Test(description = "C22790552 additional products tabs")
    public void testAdditionalProducts() throws Exception {
        String lessThan4Similar = "11912697";
        String moreThan4Similar = "15057456";
        String lessThan4Complements = "10813144";
        String moreThan4Complements = "15057456";

        CatalogSimilarProductsDataV1 data = catalogSearchClient.getSimilarProductsV1(lessThan4Similar).asJson();
        List<ProductData> lessThan4SimilarList = data.getSubstitutes();

        data = catalogSearchClient.getSimilarProductsV1(moreThan4Similar).asJson();
        List<ProductData> moreThan4SimilarList = data.getSubstitutes();

        data = catalogSearchClient.getSimilarProductsV1(lessThan4Complements).asJson();
        List<ProductData> lessThan4ComplementsList = data.getComplements();

        data = catalogSearchClient.getSimilarProductsV1(moreThan4Complements).asJson();
        List<ProductData> moreThan4ComplementsList = data.getComplements();


        //Step 1
        step("открыть карточку товара, у которого не более 4х аналогичных товаров");
        ExtendedProductCardPage extendedProductCardPage = navigateToProductCardByUrl(lessThan4Similar, false);
        extendedProductCardPage.shouldAllAdditionalProductsIsVisible(lessThan4SimilarList);

        //Step 2
        step("открыть карточку товара, у которого более 4х аналогичных товаров");
        extendedProductCardPage = navigateToProductCardByUrl(moreThan4Similar, false);
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

    @Test(description = "C22789188 Check Breadcrumbs")
    public void testBreadCrumbsNavigation() throws Exception {
        String lmCode = getRandomLmCode();

        //Pre-condition
        ExtendedProductCardPage extendedProductCardPage = navigateToProductCardByUrl(lmCode, false);

        NomenclaturePath path = extendedProductCardPage.getNomenclaturePath();
        String allDepartments = path.getAllDepartments();
        String department = path.getDepartmentId();
        String subDepartment = path.getSubDepartmentId();
        String classId = path.getClassId();
        String subClass = path.getSubClassId();

        //Step 1
        step("Перейти на уровень подтипа");
        SearchProductPage searchProductPage = extendedProductCardPage.navigateToSearchByNomenclatureAttribute(subClass);
        /*searchProductPage.shouldUrlContains(CatalogSearchParams.departmentId, CatalogSearchParams.subdepartmentId,
                CatalogSearchParams.classId, CatalogSearchParams.subclassId);*/
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(subClass);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, allDepartments, department, subDepartment,
                classId);
        extendedProductCardPage = navigateToProductCardByUrl(lmCode, false);

        //Step 2
        step("Перейти на уровень типа");
        searchProductPage = extendedProductCardPage.navigateToSearchByNomenclatureAttribute(classId);
        /*searchProductPage.shouldUrlContains(CatalogSearchParams.departmentId, CatalogSearchParams.subdepartmentId,
                CatalogSearchParams.classId);*/
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(classId);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, allDepartments, department, subDepartment);
        extendedProductCardPage = navigateToProductCardByUrl(lmCode, false);

        //Step 3
        step("Перейти на уровень подотдела");
        searchProductPage = extendedProductCardPage.navigateToSearchByNomenclatureAttribute(subDepartment);
        //searchProductPage.shouldUrlContains(CatalogSearchParams.departmentId, CatalogSearchParams.subdepartmentId);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(subDepartment);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, allDepartments, department);
        extendedProductCardPage = navigateToProductCardByUrl(lmCode, false);

        //Step 4
        step("Перейти на уровень отдела");
        searchProductPage = extendedProductCardPage.navigateToSearchByNomenclatureAttribute(department);
        //searchProductPage.shouldUrlContains(CatalogSearchParams.departmentId);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(department);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, allDepartments);
        extendedProductCardPage = navigateToProductCardByUrl(lmCode, false);

        //Step 5
        step("Перейти на уровень всех отделов");
        searchProductPage = extendedProductCardPage.navigateToSearchByNomenclatureAttribute(allDepartments);
        //searchProductPage.shouldUrlNotContains(CatalogSearchParams.departmentId);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(allDepartments);
        searchProductPage.navigateBack();
        extendedProductCardPage = new ExtendedProductCardPage();
        extendedProductCardPage.verifyRequiredElements();
        ProductCardPage productCardPage = navigateToProductCardByUrl(lmCode, true);

        //Step 6
        step("Повторить шаг 1 для укороченной карточки товара");
        searchProductPage = productCardPage.navigateToSearchByNomenclatureAttribute(subClass);
        /*searchProductPage.shouldUrlContains(CatalogSearchParams.departmentId, CatalogSearchParams.subdepartmentId,
                CatalogSearchParams.classId, CatalogSearchParams.subclassId);*/
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(subClass);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, allDepartments, department, subDepartment,
                classId);
        productCardPage = navigateToProductCardByUrl(lmCode, true);

        //Step 7
        step("Повторить шаг 2 для укороченной карточки товара");
        searchProductPage = productCardPage.navigateToSearchByNomenclatureAttribute(classId);
        /*searchProductPage.shouldUrlContains(CatalogSearchParams.departmentId, CatalogSearchParams.subdepartmentId,
                CatalogSearchParams.classId);*/
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(classId);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, allDepartments, department, subDepartment);
        productCardPage = navigateToProductCardByUrl(lmCode, true);

        //Step 8
        step("Повторить шаг 3 для укороченной карточки товара");
        searchProductPage = productCardPage.navigateToSearchByNomenclatureAttribute(subDepartment);
        //searchProductPage.shouldUrlContains(CatalogSearchParams.departmentId, CatalogSearchParams.subdepartmentId);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(subDepartment);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, allDepartments, department);
        productCardPage = navigateToProductCardByUrl(lmCode, true);

        //Step 9
        step("Повторить шаг 4 для укороченной карточки товара");
        searchProductPage = productCardPage.navigateToSearchByNomenclatureAttribute(department);
        //searchProductPage.shouldUrlContains(CatalogSearchParams.departmentId);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(department);
        searchProductPage.shouldBreadCrumbsContainsNomenclatureName(true, allDepartments);
        productCardPage = navigateToProductCardByUrl(lmCode, true);

        //Step 10
        step("Повторить шаг 5 для укороченной карточки товара");
        searchProductPage = productCardPage.navigateToSearchByNomenclatureAttribute(allDepartments);
        searchProductPage.shouldUrlNotContains(CatalogSearchParams.departmentId);
        searchProductPage.shouldCurrentNomenclatureElementNameIsDisplayed(allDepartments);
        searchProductPage.navigateBack();
        productCardPage = new ProductCardPage();
        productCardPage.verifyRequiredElements();
    }

    @Test(description = "C23389190 Check short card data")
    public void testCheckShortCardData() throws Exception {
        String lmCode = getRandomLmCode();
        CatalogProductData data = catalogSearchClient.getProductData(lmCode).asJson();

        //Step 1
        step("Перейти в укороченную карточку товара");
        ProductCardPage productCardPage = navigateToProductCardByUrl(lmCode, true);
        productCardPage.shouldProductCardContainsAllData(data);
    }

    @Test(description = "C22782997 Check extended card data")
    public void testCheckExtendedCardData() throws Exception {
        String lmCode = getRandomLmCode();
        CatalogProductData data = catalogSearchClient.getProductData(lmCode).asJson();

        //Step 1
        step("Перейти в расширенную карточку товара");
        ExtendedProductCardPage extendedProductCardPage = navigateToProductCardByUrl(lmCode, false);
        extendedProductCardPage.shouldProductCardContainsAllData(data);
    }

    @Test(description = "C22790551 Price and stocks in nearest shops")
    public void testCheckNearestShopsInfo() throws Exception {
        String shopId = "3";
        String shopName = "Химки";
        String lmCode = getRandomLmCode();
        List<NearestShopsData> nearestShopsList = catalogSearchClient.getNearestShopsInfo(lmCode).asJsonList(NearestShopsData.class);

        //Pre-condition
        ExtendedProductCardPage extendedProductCardPage = navigateToProductCardByUrl(lmCode, false);

        //Step 1
        step("Перейти на вкладку \"Цены и стоки в ближайших магазинах\"");
        extendedProductCardPage.switchExtraInfoTabs(ExtendedProductCardPage.Tab.PRICES_AND_STOCKS_IN_OTHER_SHOPS);
        extendedProductCardPage.shouldNearestShopInfoIsCorrect(nearestShopsList);

        //Step 2
        step("выполнить поиск магазина по id");
        extendedProductCardPage.searchShop(shopId);
        extendedProductCardPage.shouldFoundShopsIsCorrect(shopId);

        //Step 3
        step("выполнить поиск магазина по имени");
        extendedProductCardPage.searchShop(shopName);
        extendedProductCardPage.shouldFoundShopsIsCorrect(shopName);
    }
}
