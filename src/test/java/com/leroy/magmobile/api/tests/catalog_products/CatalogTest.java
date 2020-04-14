package com.leroy.magmobile.api.tests.catalog_products;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.clients.CatalogProductClient;
import com.leroy.magmobile.api.data.catalog.product.*;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProduct;
import com.leroy.magmobile.api.data.catalog.product.reviews.ReviewData;
import com.leroy.magmobile.api.data.catalog.product.reviews.ReviewDataResponse;
import com.leroy.magmobile.api.data.shop.ShopData;
import com.leroy.magmobile.api.data.user.UserData;
import com.leroy.magmobile.api.enums.ReviewOptions;
import com.leroy.magmobile.api.requests.catalog.GetCatalogNomenclature;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CatalogTest extends BaseProjectApiTest {

    @Inject
    CatalogProductClient client;

    @Test(description = "C23195046 GET nomenclature")
    public void testNomenclature() {
        GetCatalogNomenclature nomenclatureParams = new GetCatalogNomenclature();
        Response<Nomenclature> response = client.getNomenclature(nomenclatureParams);
        isResponseOk(response);
        List<NomenclatureData> nomenclatureData = response.asJson();
        assertThat(nomenclatureData.size(), equalTo(15)); //15 отделов
    }

    @Test(description = "C3172856 get catalog product")
    public void testCatalogProduct() {
        String lmCode = EnvConstants.TOP_EM_PRODUCT_1_LM_CODE;
        Response<CatalogProductData> catalogProductDataResponse = client.searchProduct(lmCode,
                SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR, CatalogProductClient.Extend.builder().inventory(true)
                        .logistic(true).rating(true).build());
        isResponseOk(catalogProductDataResponse);
        CatalogProductData data = catalogProductDataResponse.asJson();
        assertThat(data.getLmCode(), notNullValue());
        assertThat(data.getBarCode(), notNullValue());
    }

    @Test(description = "C23195047 GET reviews by lmCode")
    public void testCatalogProductReviews() {
        Response<CatalogReviewsOfProduct> reviewsOfProductResponse = client.getProductReviews(EnvConstants.PRODUCT_1_LM_CODE, 1, 1);
        isResponseOk(reviewsOfProductResponse);
        CatalogReviewsOfProduct data = reviewsOfProductResponse.asJson();
        assertThat(data.getTotalCount(), greaterThanOrEqualTo(0));
    }

    @Test(description = "C23195048 GET info about sales history")
    public void testCatalogProductSales() {
        Response<SalesHistory> salesHistoryResponse = client.getProductSales(EnvConstants.PRODUCT_1_LM_CODE);
        isResponseOk(salesHistoryResponse);
        List<SalesHistoryData> salesHistoryData = salesHistoryResponse.asJson();
        assertThat(salesHistoryData.size(), greaterThan(0));
    }

    @Test(description = "C3161101 catalog shops - get remains info by lm code")
    public void testCatalogShops() {
        String[] shops = {EnvConstants.BASIC_USER_SHOP_ID, "5", "69"};
        Response<CatalogShops> catalogShopsResponse = client.getProductShopsPriceAndQuantity(EnvConstants.PRODUCT_1_LM_CODE,
                shops);
        isResponseOk(catalogShopsResponse);
        List<CatalogShopsData> catalogShopsList = catalogShopsResponse.asJson();
        assertThat(catalogShopsList.size(), greaterThan(0));
        for (CatalogShopsData data : catalogShopsList) {
            assertThat(String.valueOf(data.getShopId()), oneOf(shops));
        }
    }

    @Test(description = "C3254677 GET catalog/similarProducts")
    public void testCatalogSimilarProducts() {
        Response<CatalogSimilarProducts> catalogSimilarProductsResponse = client.getSimilarProducts(EnvConstants.PRODUCT_2_LM_CODE,
                CatalogProductClient.Extend.builder().rating(true).logistic(true).inventory(true).build());
        isResponseOk(catalogSimilarProductsResponse);
        CatalogSimilarProducts data = catalogSimilarProductsResponse.asJson();
        assertThat(data.getTotalCount(), greaterThanOrEqualTo(0));
    }

    @Test(description = "C3254678 GET catalog/supplier")
    public void testCatalogSupplier() {
        String shopId = EnvConstants.BASIC_USER_SHOP_ID;
        Response<CatalogSupplierData> response = client.getSupplyInfo(EnvConstants.PRODUCT_1_LM_CODE, shopId);
        isResponseOk(response);
        CatalogSupplierData data = response.asJson();
        assertThat(data.getCode(), notNullValue());
        assertThat(String.valueOf(data.getStoreId()), equalTo(shopId));
    }

    @Test(description = "C23195049 POST product review default values")
    public void testSendReview() {
        //Если еще где-нибудь увидишь можно будет вынести в BaseApiTest
        UserData userData = new UserData();
        userData.setFullName(EnvConstants.BASIC_USER_FULL_NAME);
        userData.setLdap(EnvConstants.BASIC_USER_LDAP);
        userData.setName(EnvConstants.BASIC_USER_NAME);

        ShopData shopData = new ShopData();
        shopData.setCityName(EnvConstants.BASIC_USER_CITY_NAME);
        shopData.setDepartmentName(EnvConstants.BASIC_USER_DEPARTMENT_NAME);
        shopData.setName(EnvConstants.BASIC_USER_SHOP_NAME);
        shopData.setDepartmentId(Integer.parseInt(EnvConstants.BASIC_USER_DEPARTMENT_ID));
        shopData.setId(Integer.parseInt(EnvConstants.BASIC_USER_SHOP_ID));

        ReviewData reviewData = new ReviewData();
        reviewData.setLmCode(EnvConstants.PRODUCT_1_LM_CODE);
        reviewData.setRating(4);
        reviewData.setPriceRating(3);
        reviewData.setQualityRating(5);
        reviewData.setTimeUsage(ReviewOptions.TIME_USAGE_LESS_MONTH.getName());
        reviewData.setBody("asdfasdfsa");
        reviewData.setRecommended(false);
        reviewData.setPros(" ");
        reviewData.setCons("./$%");
        reviewData.setUser(userData);
        reviewData.setShop(shopData);

        Response<ReviewDataResponse> response = client.sendReview(reviewData);
        isResponseOk(response);
        assertThat(response.asJson().getId(), notNullValue());
    }

}
