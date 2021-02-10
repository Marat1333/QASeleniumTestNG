package com.leroy.magportal.api.tests.productSearch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV1;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV2;
import com.leroy.common_mashups.catalogs.data.NearestShopsData;
import com.leroy.common_mashups.catalogs.data.NearestShopsDataV2;
import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.magportal.api.helpers.ShopsHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import java.util.List;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class BaseCatalogTest extends BaseMagPortalApiTest {

    @Inject
    protected SearchProductHelper searchProductHelper;
    @Inject
    protected CatalogProductClient catalogProductClient;
    @Inject
    protected ShopsHelper shopsHelper;

    protected String lmCode;

    protected void isNearestShopsDataValid(Response<NearestShopsData> response) {
        isResponseOk(response);
        List<NearestShopsData> shopsData = response.asJsonList(NearestShopsData.class);
        assertThat("Shops data does NOT contain any data", shopsData, hasSize(greaterThan(0)));
        for (NearestShopsData nearestShop : shopsData) {
            String data = "ShopId: " + nearestShop.getId() + ". LmCode: " + lmCode + ". ";
            softAssert().isTrue(nearestShop.getId() > 0, data + "No Shop Id provided");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(nearestShop.getName()),
                    data + "No Shop Name provided");
            softAssert().isTrue(nearestShop.getDistance() >= 0, data + "No Distance provided");
            softAssert().isTrue(nearestShop.getPrice() > 0, data + "No Price provided");
            softAssert().isTrue(nearestShop.getAvailableStock() >= 0,
                    data + "No AvailableStock provided");
        }
        softAssert().verifyAll();
    }

    protected void isNearestShopsDataV2Valid(Response<NearestShopsDataV2> response) {
        isResponseOk(response);
        List<NearestShopsDataV2> shopsData = response.asJsonList(NearestShopsDataV2.class);
        assertThat("Shops data does NOT contain any data", shopsData, hasSize(greaterThan(0)));
        for (NearestShopsDataV2 nearestShop : shopsData) {
            String data = "ShopId: " + nearestShop.getShopId() + ". LmCode: " + lmCode + ". ";
            softAssert().isTrue(nearestShop.getShopId() > 0, data + "No Shop Id provided");
//            softAssert().isTrue(nearestShop.getDistance() >= 0, data + "No Distance provided");//TODO field is absent in DEV
            softAssert().isTrue(nearestShop.getPrice() > 0, data + "No Price provided");
            softAssert().isTrue(nearestShop.getAvailableStock() >= 0,
                    data + "No AvailableStock provided");
        }
        softAssert().verifyAll();
    }

    protected void isCatalogProductValid(Response<CatalogProductData> response) {
        isResponseOk(response);
        CatalogProductData productData = response.asJson(CatalogProductData.class);
        String data = "LmCode: " + lmCode + ": ";

        softAssert().isTrue(productData.getLmCode().equals(lmCode),
                data + "Invalid lmCode is provided: " + productData.getLmCode());
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(productData.getTitle()),
                data + "No Title provided");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(productData.getBarCode()),
                data + "No BarCode provided");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(productData.getDescription()),
                data + "No Description provided");
    }

    protected void isSimilarProductsValid(Response<?> response, boolean isV1) {
        isResponseOk(response);
        if (isV1) {
            CatalogSimilarProductsDataV1 data = response.asJson(CatalogSimilarProductsDataV1.class);
            assertThat("total count", data.getSubstitutes(), hasSize(greaterThan(0)));
            assertThat("product items", data.getComplements(), hasSize(greaterThan(0)));
        } else {
            CatalogSimilarProductsDataV2 data = response.asJson(CatalogSimilarProductsDataV2.class);
            assertThat("total count", data.getTotalCount(), greaterThan(0));
            assertThat("product items", data.getItems(), hasSize(greaterThan(0)));
        }
    }
}
