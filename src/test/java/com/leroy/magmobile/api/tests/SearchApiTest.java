package com.leroy.magmobile.api.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;
import com.leroy.umbrella_extension.magmobile.enums.CatalogSearchFields;
import com.leroy.umbrella_extension.magmobile.enums.SortingOrder;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import org.rocksdb.Env;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.TestCase;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SearchApiTest extends BaseProjectTest {

    @Inject
    private MagMobileClient magMobileClient;

    private GetCatalogSearch buildDefaultCatalogSearchParams() {
        return new GetCatalogSearch()
                .setPageSize(10)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setStartFrom(1);
    }

    @TestCase(3161100)
    @Test(description = "C3161100 search by lmCode")
    public void testSearchByLmCode() {
        final String lmCode = "18546124";

        GetCatalogSearch byLmCodeParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByLmCode(lmCode);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byLmCodeParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData, hasSize(1));
        for (ProductItemResponse data : responseData) {
            assertThat(data.getLmCode(), equalTo(lmCode));
        }
    }

    @TestCase(22893254)
    @Test(description = "C22893254 search by short lmCode")
    public void testSearchByShortLmCode() {
        final String lmCode = "123";

        GetCatalogSearch byLmCodeParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByLmCode(lmCode);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byLmCodeParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData.size(), not(0));
        for (ProductItemResponse data : responseData) {
            assertThat(data.getLmCode(), containsString(lmCode));
        }
    }

    @TestCase(22893255)
    @Test(description = "C22893255 search by barCode")
    public void testSearchByBarCode() {
        final String barCode = "4605865275387";

        GetCatalogSearch byBarCodeParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByBarCode(barCode);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byBarCodeParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData, hasSize(1));
        for (ProductItemResponse data : responseData) {
            assertThat(data.getBarCode(), equalTo(barCode));
        }
    }

    @TestCase(22893256)
    @Test(description = "C22893256 search by short barCode")
    public void testSearchByShortBarCode() {
        final String barCode = "460586527";

        GetCatalogSearch byBarCodeParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByBarCode(barCode);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byBarCodeParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData.size(), not(0));
        for (ProductItemResponse data : responseData) {
            assertThat(data.getBarCode(), containsString(barCode));
        }
    }

    @TestCase(22893248)
    @Test(description = "C22893248 search by name")
    public void testSearchByName() {
        final String name = "Тепломир радиатор";

        GetCatalogSearch byNameParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByNameLike(name);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byNameParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData.size(), not(0));
        String[] searchWords = name.split(" ");
        for (ProductItemResponse data : responseData) {
            for (String eachWord : searchWords) {
                assertThat(data.getTitle().toLowerCase(), containsString(eachWord.toLowerCase()));
            }
        }
    }

    @TestCase(22893258)
    @Test(description = "C22893258 search by long (length>8) lmCode")
    public void testSearchByLongLmCode() {
        //часть штрихкода, по которому можно найти товар
        final String longLmCode = "464001538";

        GetCatalogSearch byLmCodeParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByLmCode(longLmCode);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byLmCodeParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData, hasSize(0));
    }

    @TestCase(22893329)
    @Test(description = "C22893329 search by gamma filter")
    public void testSearchByGammaFilter() {
        final String GAMMA = "S";

        GetCatalogSearch byGammaParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setGamma(GAMMA);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byGammaParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData.size(), not(0));

        for (ProductItemResponse data : responseData) {
            assertThat(data.getGamma(), equalTo(GAMMA));
        }
    }

    @TestCase(22893330)
    @Test(description = "C22893330 search by multiply gamma filter")
    public void testSearchByMultiplyGammaFilter() {
        final String FIRST_GAMMA = "Bc";
        final String SECOND_GAMMA = "ET";

        GetCatalogSearch byGammaParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setGamma(FIRST_GAMMA + "," + SECOND_GAMMA);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byGammaParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData.size(), not(0));

        for (ProductItemResponse data : responseData) {
            assertThat(data.getGamma(), isOneOf(FIRST_GAMMA, SECOND_GAMMA));
        }
    }

    @TestCase(22893331)
    @Test(description = "C22893331 search by top filter")
    public void testSearchByTopFilter() {
        final String TOP = "1";

        GetCatalogSearch byTopParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setTop(TOP);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byTopParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData.size(), not(0));

        for (ProductItemResponse data : responseData) {
            assertThat(data.getTop(), equalTo(Integer.valueOf(TOP)));
        }
    }

    @TestCase(22893332)
    @Test(description = "C22893332 search by multiply top filter")
    public void testSearchByMultiplyTopFilter() {
        final String FIRST_TOP = "1";
        final String SECOND_TOP = "2";

        GetCatalogSearch byTopParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setTop(FIRST_TOP + "," + SECOND_TOP);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byTopParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData.size(), not(0));

        for (ProductItemResponse data : responseData) {
            assertThat(data.getTop(), isOneOf(Integer.valueOf(FIRST_TOP), Integer.valueOf(SECOND_TOP)));
        }
    }

    @TestCase(22893331)
    @Test(description = "C22893331 search by top filter")
    public void testSearchByHasAvailableStockFilter() {
        final boolean hasAvailableStock = true;

        GetCatalogSearch byStockParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setHasAvailableStock(hasAvailableStock);
        Response<ProductItemListResponse> response = magMobileClient.searchProductsBy(byStockParams);

        List<ProductItemResponse> responseData = response.asJson().getItems();

        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(responseData.size(), not(0));

        for (ProductItemResponse data : responseData) {
            assertThat(data.getAvailableStock(), greaterThan(0f));
        }
    }
}
