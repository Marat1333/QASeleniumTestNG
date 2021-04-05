package com.leroy.magportal.api.tests.productSearch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV1;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV2;
import com.leroy.common_mashups.catalogs.data.NearestShopsData;
import com.leroy.common_mashups.catalogs.data.NearestShopsDataV2;
import com.leroy.common_mashups.catalogs.data.NomenclatureData;
import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import com.leroy.constants.sales.SalesDocumentsConst;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogTest extends BaseCatalogTest {

    @BeforeClass
    private void setUp() {
        lmCode = searchProductHelper.getProductLmCode();
    }

    @Test(description = "C23718698 GET Nearest Shops")
    public void testNearestShops() {
        Response<NearestShopsData> response = catalogProductClient.getNearestShopsInfo(lmCode);
        isNearestShopsDataValid(response);
    }

    @Test(description = "C23718699 GET Nearest Shops for Random shop")
    public void testNearestShopsForRandomShop() {
        Response<NearestShopsData> response = catalogProductClient
                .getNearestShopsInfo(lmCode, shopsHelper.getRandomShopId().toString());
        isNearestShopsDataValid(response);
    }

    @Test(description = "C23718700 GET Nearest Shops V2")
    public void testNearestShopsV2() {
        Response<NearestShopsDataV2> response = catalogProductClient.getNearestShopsInfoV2(lmCode);
        isNearestShopsDataV2Valid(response);
    }

    @Test(description = "C23718701 GET Nearest Shops V2 for Random shop")
    public void testNearestShopsForRandomShopV2() {
        Response<NearestShopsDataV2> response = catalogProductClient
                .getNearestShopsInfoV2(lmCode, shopsHelper.getRandomShopId().toString());
        isNearestShopsDataV2Valid(response);
    }

    @Test(description = "C23718702 GET Nomenclature")
    public void testNomenclature() {
        Response<?> response = catalogProductClient.getNomenclature();
        isResponseOk(response);
        List<NomenclatureData> nomenclatureData = response.asJsonList(NomenclatureData.class);
        assertThat("count of departments", nomenclatureData.size(), equalTo(16)); //16 отделов
    }

    @Test(description = "C23718703 GET Catalog Product V2")
    public void testCatalogProductV2() {
        Response<CatalogProductData> response = catalogProductClient.getProductV2(lmCode);
        isCatalogProductValid(response);
    }

    @Test(description = "C23718704 GET Catalog Product V2 Extended")
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

    @Test(description = "C23718705 GET Catalog Similar Products")
    public void testCatalogSimilarProductsV1() {
        Response<CatalogSimilarProductsDataV1> response = catalogProductClient
                .getSimilarProductsV1(lmCode);
        isSimilarProductsValid(response, true);
    }

    @Test(description = "C23718706 GET Catalog Similar Products Extended")
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

    @Test(description = "C23718707 GET Catalog Similar Products V2")
    public void testCatalogSimilarProductsV2() {
        Response<CatalogSimilarProductsDataV2> response = catalogProductClient
                .getSimilarProductsV2(lmCode);
        isSimilarProductsValid(response, false);
    }

    @Test(description = "C23718708 GET Catalog Similar Products V2 Extended")
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
