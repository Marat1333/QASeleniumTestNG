package com.leroy.magmobile.api.tests.catalog_products;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.oneOf;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.UserSessionData;
import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.common_mashups.catalogs.data.NomenclatureData;
import com.leroy.common_mashups.catalogs.data.CatalogComplementaryProductsDataV2;
import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import com.leroy.common_mashups.catalogs.data.CatalogShopsData;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV2;
import com.leroy.common_mashups.catalogs.data.product.SalesHistoryData;
import com.leroy.common_mashups.catalogs.data.product.reviews.CatalogReviewsOfProductList;
import com.leroy.common_mashups.catalogs.data.product.reviews.ReviewData;
import com.leroy.common_mashups.catalogs.data.supply.CatalogSupplierDataOld;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.api.data.user.UserData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogTest extends BaseProjectApiTest {

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private CatalogProductClient catalogProductClient;

    private String lmCode;
    private final String lmProductWithReviews = "10073940";
    private final String lmProductWithSalesHistory = "10073940";

    @Override
    protected UserSessionData initTestClassUserSessionDataTemplate() {
        UserSessionData userSessionData = super.initTestClassUserSessionDataTemplate();
        userSessionData.setUserShopId("32");
        return super.initTestClassUserSessionDataTemplate();
    }

    @BeforeClass
    private void setUp() {
        lmCode = searchProductHelper.getRandomProduct().getLmCode();
    }

    @Test(description = "C23195046 GET nomenclature")
    public void testNomenclature() {
        Response<?> response = catalogProductClient.getNomenclature();
        isResponseOk(response);
        List<NomenclatureData> nomenclatureData = response.asJsonList(NomenclatureData.class);
        assertThat("count of departments", nomenclatureData.size(), equalTo(15)); //15 отделов
    }

    @Test(description = "C3172856 get catalog product")
    public void testCatalogProduct() {
        Response<CatalogProductData> catalogProductDataResponse = catalogProductClient.getProduct(lmCode,
                SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR, CatalogProductClient.Extend.builder().inventory(true)
                        .logistic(true).rating(true).build());
        isResponseOk(catalogProductDataResponse);
        CatalogProductData data = catalogProductDataResponse.asJson();
        assertThat("product lmCode", data.getLmCode(), notNullValue());
        assertThat("product barCode", data.getBarCode(), notNullValue());
    }

    @Test(description = "C23195047 GET reviews by lmCode")
    public void testCatalogProductReviews() {
        Response<CatalogReviewsOfProductList> reviewsOfProductResponse = catalogProductClient.getProductReviews(
                lmProductWithReviews, 1, 3);
        isResponseOk(reviewsOfProductResponse);
        CatalogReviewsOfProductList data = reviewsOfProductResponse.asJson();
        assertThat("total count", data.getTotalCount(), greaterThan(0));
        assertThat("count of items with reviews", data.getItems(), hasSize(greaterThan(0)));
    }

    @Test(description = "C23195048 GET info about sales history")
    public void testCatalogProductSales() {
        Response<?> salesHistoryResponse = catalogProductClient.getProductSales(lmProductWithSalesHistory);
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

    @Test(description = "C3161101 catalog shops - get remains info by lm code")
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

    @Test(description = "C3254677 GET catalog/similarProducts")
    public void testCatalogSimilarProducts() {
        CatalogProductClient.Extend extendParam = CatalogProductClient.Extend.builder()
                .rating(true).logistic(true).inventory(true).build();
        Response<CatalogSimilarProductsDataV2> catalogSimilarProductsResponse =
                catalogProductClient.getSimilarProducts(lmCode, extendParam);
        isResponseOk(catalogSimilarProductsResponse);
        CatalogSimilarProductsDataV2 data = catalogSimilarProductsResponse.asJson();
        assertThat("total count", data.getTotalCount(), greaterThan(0));
        assertThat("product items", data.getItems(), hasSize(greaterThan(0)));
    }

    @Test(description = "C3254678 GET catalog/supplier")
    public void testCatalogSupplier() {
        Response<CatalogSupplierDataOld> response = catalogProductClient.getSupplyInfo(lmCode);
        isResponseOk(response);
        CatalogSupplierDataOld data = response.asJson();
        assertThat("supplier code", data.getCode(), not(emptyOrNullString()));
        assertThat("store id",
                data.getStoreId(), equalTo(Integer.parseInt(getUserSessionData().getUserShopId())));
    }

    @Test(description = "C23195049 POST product review default values")
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

    @Test(description = "C23416163 GET /catalog/complementary-products")
    public void testComplementaryProducts() {
        Response<CatalogComplementaryProductsDataV2> response = catalogProductClient.getComplementaryProducts(
                searchProductHelper.getRandomProduct().getLmCode());
        isResponseOk(response);
        CatalogComplementaryProductsDataV2 complementaryProductsData = response.asJson();
        assertThat(complementaryProductsData.getItems(), notNullValue());
        assertThat(complementaryProductsData.getTotalCount(), notNullValue());
    }
}
