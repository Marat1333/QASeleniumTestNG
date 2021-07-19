package com.leroy.product_search.api.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.oneOf;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.common_mashups.catalogs.data.CatalogComplementaryProductsDataV2;
import com.leroy.common_mashups.catalogs.data.CatalogShopsData;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV1;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV2;
import com.leroy.common_mashups.catalogs.data.NearestShopsData;
import com.leroy.common_mashups.catalogs.data.NearestShopsDataV2;
import com.leroy.common_mashups.catalogs.data.NomenclatureData;
import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.catalogs.data.product.SalesHistoryData;
import com.leroy.common_mashups.catalogs.data.product.reviews.CatalogReviewsOfProductList;
import com.leroy.common_mashups.catalogs.data.product.reviews.ReviewData;
import com.leroy.common_mashups.catalogs.data.supply.CatalogSupplierDataOld;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.api.data.user.UserData;
import java.util.List;
import java.util.Random;

import io.qameta.allure.TmsLink;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.annotation.Obsolete;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.TestCase;
import ru.leroymerlin.qa.core.base.TestCases;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogTest extends BaseCatalogTest {

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private CatalogProductClient catalogProductClient;

    private final String lmProductWithReviews = "10073940";
    private final String lmProductWithSalesHistory = "10073940";

//    @Override//TODO It's NOT working anymore
//    protected UserSessionData initTestClassUserSessionDataTemplate() {
//        UserSessionData userSessionData = super.initTestClassUserSessionDataTemplate();
//        userSessionData.setUserShopId("32");
//        return super.initTestClassUserSessionDataTemplate();
//    }

    @BeforeClass
    private void setUp() {
        getUserSessionData().setUserShopId("32");
        lmCode = searchProductHelper.getRandomProduct().getLmCode();
    }

    @Test(description = "C23195046 GET nomenclature", groups = "productSearch")
    @TmsLink("3172")
    public void testNomenclature() {
        Response<?> response = catalogProductClient.getNomenclature();
        isResponseOk(response);
        List<NomenclatureData> nomenclatureData = response.asJsonList(NomenclatureData.class);
        assertThat("count of departments", nomenclatureData.size(), greaterThanOrEqualTo(15)); //15 отделов
    }

    @Test(description = "C3172856 get catalog product", groups = "productSearch")
    @TmsLink("3155")
    public void testCatalogProduct() {
        Response<ProductData> catalogProductDataResponse = catalogProductClient.getProduct(lmCode,
                SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR, CatalogProductClient.Extend.builder().inventory(true)
                        .logistic(true).rating(true).build());
        isResponseOk(catalogProductDataResponse);
        ProductData data = catalogProductDataResponse.asJson();
        assertThat("product lmCode", data.getLmCode(), notNullValue());
        assertThat("product barCode", data.getBarCode(), notNullValue());
    }

    @Test(description = "C23195047 GET reviews by lmCode", groups = "productSearch")
    @TmsLink("3173")
    public void testCatalogProductReviews() {
        Response<CatalogReviewsOfProductList> reviewsOfProductResponse = catalogProductClient.getProductReviews(
                lmProductWithReviews, 1, 3);
        isResponseOk(reviewsOfProductResponse);
        CatalogReviewsOfProductList data = reviewsOfProductResponse.asJson();
        assertThat("total count", data.getTotalCount(), greaterThan(0));
        assertThat("count of items with reviews", data.getItems(), hasSize(greaterThan(0)));
    }

    @Test(description = "C23195048 GET info about sales history", groups = "productSearch")
    @TmsLink("3174")
    public void testCatalogProductSales() {
        Response<?> salesHistoryResponse = catalogProductClient.getProductSales(lmProductWithSalesHistory,
                getUserSessionData().getUserShopId());
        isResponseOk(salesHistoryResponse);
        List<SalesHistoryData> salesHistoryData = salesHistoryResponse.asJsonList(SalesHistoryData.class);
        assertThat("Count of items", salesHistoryData.size(), greaterThan(0));
        for (SalesHistoryData rowData : salesHistoryData) {
            assertThat("year", rowData.getYearmonth(), not(emptyOrNullString()));
            assertThat("month", rowData.getYearmonth(), not(emptyOrNullString()));
            assertThat("yearmonth", rowData.getYearmonth(),
                    equalTo(rowData.getYear() + rowData.getMonth()));
            assertThat("quantity", rowData.getQuantity(), greaterThan(0.0));
            assertThat("amount", rowData.getAmount(), greaterThan(0.0));
        }
    }

    @Test(description = "C3161101 catalog shops - get remains info by lm code", groups = "productSearch")
    @TmsLink("3154")
    @Obsolete
    public void testCatalogShops() {
        String[] shops = {"32", "5", "69"};
        Response<?> catalogShopsResponse = catalogProductClient.getProductShopsPriceAndQuantity(
                lmCode, shops);
        isResponseOk(catalogShopsResponse);
        List<CatalogShopsData> catalogShopsList = catalogShopsResponse.asJsonList(CatalogShopsData.class);
        assertThat("Catalog shops size", catalogShopsList, hasSize(shops.length));
        for (CatalogShopsData data : catalogShopsList) {
            assertThat("shopId", String.valueOf(data.getShopId()), oneOf(shops));
            assertThat("price", data.getPrice(), greaterThan(0.0));
        }
    }

    @Test(description = "C3254678 GET catalog/supplier", groups = "productSearch")
    @TmsLink("3163")
    public void testCatalogSupplier() {
        Response<CatalogSupplierDataOld> response = catalogProductClient.getSupplyInfo(lmCode);
        isResponseOk(response);
        CatalogSupplierDataOld data = response.asJson();
        assertThat("supplier code", data.getCode(), not(emptyOrNullString()));
        assertThat("store id",
                data.getStoreId(), equalTo(Integer.parseInt(getUserSessionData().getUserShopId())));
    }

    @Test(description = "C23195049 POST product review default values", groups = "productSearch")
    @TmsLink("3175")
    public void testSendReview() {
        UserData userData = new UserData();
        userData.setLdap(getUserSessionData().getUserLdap());

        ShopData shopData = new ShopData();
        shopData.setId(getUserSessionData().getUserShopId());

        ReviewData reviewData = new ReviewData();
        reviewData.setLmCode(RandomStringUtils.randomNumeric(6));
        reviewData.setRating(4);
        reviewData.setPriceRating(3);
        reviewData.setQualityRating(5);
        reviewData.setBody(RandomStringUtils.randomAlphanumeric(50));
        reviewData.setRecommended(new Random().nextBoolean());
        reviewData.setUser(userData);
        reviewData.setShop(shopData);

        Response<JsonNode> response = catalogProductClient.sendReview(reviewData);
        isResponseOk(response);
        assertThat(response.asJson().get("id").asText(), not(emptyOrNullString()));
    }

    @Test(description = "C23416163 GET /catalog/complementary-products", groups = "productSearch")
    @TmsLink("3176")
    public void testComplementaryProducts() {
        Response<CatalogComplementaryProductsDataV2> response = catalogProductClient.getComplementaryProducts(
                searchProductHelper.getRandomProduct().getLmCode());
        isResponseOk(response);
        CatalogComplementaryProductsDataV2 complementaryProductsData = response.asJson();
        assertThat(complementaryProductsData.getItems(), notNullValue());
        assertThat(complementaryProductsData.getTotalCount(), notNullValue());
    }

    @Test(description = "C23718698 GET Nearest Shops", groups = "productSearch")
    @TmsLink("2021")
    public void testNearestShops() {
        Response<NearestShopsData> response = catalogProductClient.getNearestShopsInfo(lmCode);
        isNearestShopsDataValid(response);
    }

    @Test(description = "C23718699 GET Nearest Shops for Random shop", groups = "productSearch")
    @TmsLink("2022")
    public void testNearestShopsForRandomShop() {
        Response<NearestShopsData> response = catalogProductClient
            .getNearestShopsInfo(lmCode, shopsHelper.getRandomShopId().toString());
        isNearestShopsDataValid(response);
    }

    @Test(description = "C23718700 GET Nearest Shops V2", groups = "productSearch")
    @TmsLink("2023")
    public void testNearestShopsV2() {
        Response<NearestShopsDataV2> response = catalogProductClient.getNearestShopsInfoV2(lmCode);
        isNearestShopsDataV2Valid(response);
    }

    @Test(description = "C23718701 GET Nearest Shops V2 for Random shop", groups = "productSearch")
    @TmsLink("2024")
    public void testNearestShopsForRandomShopV2() {
        Response<NearestShopsDataV2> response = catalogProductClient
            .getNearestShopsInfoV2(lmCode, shopsHelper.getRandomShopId().toString());
        isNearestShopsDataV2Valid(response);
    }

    @Test(description = "C23718703 GET Catalog Product V2", groups = "productSearch")
    @TmsLink("2026")
    public void testCatalogProductV2() {
        Response<CatalogProductData> response = catalogProductClient.getProductV2(lmCode);
        isCatalogProductValid(response);
    }

    @Test(description = "C23718704 GET Catalog Product V2 Extended", groups = "productSearch")
    @TmsLink("2027")
    public void testCatalogProductV2Extend() {
        Response<CatalogProductData> response = catalogProductClient.getProductV2(lmCode,
            SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR,
            CatalogProductClient.Extend.builder()
                .inventory(true)
                .logistic(true)
                .rating(true)
                .build());
        isCatalogProductValid(response);
    }

    @TestCases(cases = {
        @TestCase(3254677),
        @TestCase(23718705)
    })
    @Test(description = "C23718705 GET Catalog Similar Products", groups = "productSearch")
    @TmsLink("2028")
    @TmsLink("3162")
    public void testCatalogSimilarProductsV1() {
        Response<CatalogSimilarProductsDataV1> response = catalogProductClient
            .getSimilarProductsV1(lmCode);
        isSimilarProductsValid(response, true);
    }

    @Test(description = "C23718706 GET Catalog Similar Products Extended", groups = "productSearch")
    @TmsLink("2029")
    public void testCatalogSimilarProductsV1Extend() {
        CatalogProductClient.Extend extendParam = CatalogProductClient.Extend.builder()
            .rating(true)
            .logistic(true)
            .inventory(true)
            .build();
        Response<CatalogSimilarProductsDataV1> response = catalogProductClient
            .getSimilarProductsV1(lmCode, extendParam);
        isSimilarProductsValid(response, true);
    }

    @TestCases(cases = {
        @TestCase(3254677),
        @TestCase(23718707)
    })
    @Test(description = "C23718707 GET Catalog Similar Products V2", groups = "productSearch")
    @TmsLink("2030")
    public void testCatalogSimilarProductsV2() {
        Response<CatalogSimilarProductsDataV2> response = catalogProductClient
            .getSimilarProductsV2(lmCode);
        isSimilarProductsValid(response, false);
    }

    @Test(description = "C23718708 GET Catalog Similar Products V2 Extended", groups = "productSearch")
    @TmsLink("2031")
    public void testCatalogSimilarProductsV2Extend() {
        CatalogProductClient.Extend extendParam = CatalogProductClient.Extend.builder()
            .rating(true)
            .logistic(true)
            .inventory(true)
            .build();
        Response<CatalogSimilarProductsDataV2> response = catalogProductClient
            .getSimilarProductsV2(lmCode, extendParam);
        isSimilarProductsValid(response, false);
    }
}
