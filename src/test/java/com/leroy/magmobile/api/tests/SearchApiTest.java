package com.leroy.magmobile.api.tests;

import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.ProductData;
import com.leroy.umbrella_extension.magmobile.data.ProductItemData;
import com.leroy.umbrella_extension.magmobile.data.ServiceData;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemData;
import com.leroy.umbrella_extension.magmobile.enums.CatalogSearchFields;
import com.leroy.umbrella_extension.magmobile.enums.SortingOrder;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogSearch;
import com.leroy.umbrella_extension.magmobile.requests.GetCatalogServicesSearch;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.TestCase;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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

    private void isResponseSuccessfulAndContainsMoreThanOneEntity(Response response, List<?> responseData) {
        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
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
        Response<ProductData> response = magMobileClient.searchProductsBy(byLmCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains not the only 1 object", responseData, hasSize(1));
        for (ProductItemData data : responseData) {
            assertThat("product lmCode " + data.getLmCode() + " has not matches with " + lmCode, data.getLmCode(), equalTo(lmCode));
        }
    }

    @TestCase(22893254)
    @Test(description = "C22893254 search by short lmCode")
    public void testSearchByShortLmCode() {
        final String lmCode = "123";

        GetCatalogSearch byLmCodeParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setByLmCode(lmCode);
        Response<ProductData> response = magMobileClient.searchProductsBy(byLmCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
        for (ProductItemData data : responseData) {
            assertThat("product lmCode " + data.getLmCode() + " has not contains " + lmCode, data.getLmCode(), containsString(lmCode));
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
        Response<ProductData> response = magMobileClient.searchProductsBy(byBarCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains not the only 1 object", responseData, hasSize(1));
        for (ProductItemData data : responseData) {
            assertThat("product barcode " + data.getBarCode() + " has not matches with " + barCode, data.getBarCode(), equalTo(barCode));
        }
    }

    @TestCase(22893256)
    @Test(description = "C22893256 search by short barCode")
    public void testSearchByShortBarCode() {
        final String barCode = "460586527";

        GetCatalogSearch byBarCodeParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setByBarCode(barCode);
        Response<ProductData> response = magMobileClient.searchProductsBy(byBarCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
        for (ProductItemData data : responseData) {
            assertThat("product barcode " + data.getBarCode() + " has not contains " + barCode, data.getBarCode(), containsString(barCode));
        }
    }

    @TestCase(22893248)
    @Test(description = "C22893248 search by name")
    public void testSearchByName() {
        final String name = "Тепломир радиатор";

        GetCatalogSearch byNameParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setByNameLike(name);
        Response<ProductData> response = magMobileClient.searchProductsBy(byNameParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
        String[] searchWords = name.split(" ");
        for (ProductItemData data : responseData) {
            for (String eachWord : searchWords) {
                assertThat("title " + data.getTitle() + " of product " + data.getLmCode() + " has not contains word " + eachWord, data.getTitle().toLowerCase(), containsString(eachWord.toLowerCase()));
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
        Response<ProductData> response = magMobileClient.searchProductsBy(byLmCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response.size", responseData, hasSize(0));
    }

    @TestCase(22893329)
    @Test(description = "C22893329 search by gamma filter")
    public void testSearchByGammaFilter() {
        final String GAMMA = "S";

        GetCatalogSearch byGammaParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setGamma(GAMMA);
        Response<ProductData> response = magMobileClient.searchProductsBy(byGammaParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("gamma in product " + data.getLmCode() + " is " + data.getGamma(), data.getGamma(), equalTo(GAMMA));
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
                .setPageSize(20)
                .setGamma(FIRST_GAMMA + "," + SECOND_GAMMA);
        Response<ProductData> response = magMobileClient.searchProductsBy(byGammaParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("gamma in product " + data.getLmCode() + " is " + data.getGamma(), data.getGamma(), isOneOf(FIRST_GAMMA, SECOND_GAMMA));
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
                .setPageSize(20)
                .setTop(TOP);
        Response<ProductData> response = magMobileClient.searchProductsBy(byTopParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("top in product " + data.getLmCode() + " is " + data.getTop(), data.getTop(), equalTo(Integer.valueOf(TOP)));
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
                .setPageSize(20)
                .setTop(FIRST_TOP + "," + SECOND_TOP);
        Response<ProductData> response = magMobileClient.searchProductsBy(byTopParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("top in product " + data.getLmCode() + " is " + data.getTop(), data.getTop(), isOneOf(Integer.valueOf(FIRST_TOP), Integer.valueOf(SECOND_TOP)));
        }
    }

    @TestCase(22893333)
    @Test(description = "C22893333 search by hasAvailableStock filter")
    public void testSearchByHasAvailableStockFilter() {
        final boolean hasAvailableStock = true;

        GetCatalogSearch byStockParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setHasAvailableStock(hasAvailableStock);
        Response<ProductData> response = magMobileClient.searchProductsBy(byStockParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("available stock in product " + data.getLmCode() + " is " + data.getAvailableStock(), data.getAvailableStock(), greaterThan(0f));
        }
    }

    @TestCase(22893334)
    @Test(description = "C22893334 search by topEm filter")
    public void testSearchByTopEmFilter() {
        final boolean topEm = true;

        GetCatalogSearch byTopEmParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setTopEM(topEm);
        Response<ProductData> response = magMobileClient.searchProductsBy(byTopEmParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("topEm in product " + data.getLmCode() + " is " + data.getTopEM(), data.getTopEM(), equalTo(topEm));
        }
    }

    //может работать некорректно, т.к. есть бизнес дефект
    @TestCase(22893335)
    @Test(description = "C22893335 search by bestPrice filter")
    public void testSearchByBestPriceFilter() {
        final boolean bestPrice = true;

        GetCatalogSearch byBestPriceParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setBestPrice(bestPrice);
        Response<ProductData> response = magMobileClient.searchProductsBy(byBestPriceParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("price category in product " + data.getLmCode() + " is " + data.getPriceCategory(), data.getPriceCategory(), equalTo("BPR"));
        }
    }

    @TestCase(22893336)
    @Test(description = "C22893336 search by top1000 filter")
    public void testSearchByTop1000Filter() {
        final boolean top1000 = true;

        GetCatalogSearch byTop1000Params = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setTop1000(top1000);
        Response<ProductData> response = magMobileClient.searchProductsBy(byTop1000Params);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("top1000 in product " + data.getLmCode() + " is " + data.getTop1000(), data.getTop1000(), equalTo(top1000));
        }
    }

    //может работать некорректно, т.к. есть бизнес дефект
    @TestCase(22893337)
    @Test(description = "C22893337 search by limitedOffer filter")
    public void testSearchByLimitedOfferFilter() {
        final boolean LimitedOffer = true;

        GetCatalogSearch byLimitedOfferParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setLimitedOffer(LimitedOffer);
        Response<ProductData> response = magMobileClient.searchProductsBy(byLimitedOfferParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("price category in product " + data.getLmCode() + " is " + data.getPriceCategory(), data.getPriceCategory(), equalTo("LOF"));
        }
    }

    @TestCase(22893338)
    @Test(description = "C22893338 search by ctm filter")
    public void testSearchByCtmFilter() {
        final boolean ctm = true;

        GetCatalogSearch byCtmParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setCtm(ctm);
        Response<ProductData> response = magMobileClient.searchProductsBy(byCtmParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("ctm in product " + data.getLmCode() + " is " + data.getCtm(), data.getCtm(), equalTo(ctm));
        }
    }

    @TestCase(22893341)
    @Test(description = "C22893341 search by supplier filter")
    public void testSearchBySupplierFilter() {
        final String SUPPLIER_CODE = "1001123001";

        GetCatalogSearch bySupplierParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setSupId(SUPPLIER_CODE);
        Response<ProductData> response = magMobileClient.searchProductsBy(bySupplierParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("supplier in product " + data.getLmCode() + " is " + data.getSupCode(), data.getSupCode(), equalTo(SUPPLIER_CODE));
        }
    }

    @TestCase(22893342)
    @Test(description = "C22893342 search by multiply supplier filter")
    public void testSearchByMultiplySupplierFilter() {
        final String FIRST_SUPPLIER_CODE = "1002978015";
        final String SECOND_SUPPLIER_CODE = "1003509015";

        GetCatalogSearch bySupplierParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(30)
                .setSupId(FIRST_SUPPLIER_CODE + "," + SECOND_SUPPLIER_CODE);
        Response<ProductData> response = magMobileClient.searchProductsBy(bySupplierParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("supplier in product " + data.getLmCode() + " is " + data.getSupCode(), data.getSupCode(), isOneOf(FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE));
        }
    }

    @TestCase(22893343)
    @Test(description = "C22893343 search by avs neq null filter")
    public void testSearchByAvsNeqNullFilter() {

        GetCatalogSearch byAvsNeqNullParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setAvsDate("neq|null");
        Response<ProductData> response = magMobileClient.searchProductsBy(byAvsNeqNullParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("avs in product " + data.getLmCode() + " is " + data.getAvsDate(), data.getAvsDate(), notNullValue());
        }
    }

    @TestCase(22893344)
    @Test(description = "C22893344 search by avs date filter")
    public void testSearchByAvsDateFilter() {
        LocalDate avsDate = LocalDate.of(2019, 3, 19);

        GetCatalogSearch byAvsDateParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setAvsDate(String.format("between%%7C%s-0%s-%sT00:00:00.000Z%%7C%s-0%s-%sT00:00:00.000Z",
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(),
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1));

        Response<ProductData> response = magMobileClient.searchProductsBy(byAvsDateParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("avs in product " + data.getLmCode() + " is " + data.getAvsDate(), data.getAvsDate().toString(), containsString(avsDate.toString()));
        }
    }

    @TestCase(22893345)
    @Test(description = "C22893345 search by few filters")
    public void testSearchByFewFilters() {
        final boolean ctm = true;
        final boolean topEm = true;
        final String GAMMA = "B";
        final String TOP = "0";
        final String avs = "neq|null";
        final String SUPPLIER_CODE = "1002978015";

        GetCatalogSearch fewCategoriesParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setTop(TOP)
                .setGamma(GAMMA)
                .setAvsDate(avs)
                .setCtm(ctm)
                .setTopEM(topEm);
        Response<ProductData> response = magMobileClient.searchProductsBy(fewCategoriesParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat("response is not successful", response.isSuccessful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("ctm in product " + data.getLmCode() + " is " + data.getCtm(), data.getCtm(), equalTo(true));
            assertThat("topEM in product " + data.getLmCode() + " is " + data.getTopEM(), data.getTopEM(), equalTo(topEm));
            assertThat("avs in product " + data.getLmCode() + " is " + data.getAvsDate(), data.getAvsDate(), notNullValue());
            assertThat("gamma in product " + data.getLmCode() + " is " + data.getGamma(), data.getGamma(), equalTo(GAMMA));
            assertThat("top in product " + data.getLmCode() + " is " + data.getTop(), data.getTop(), equalTo(Integer.valueOf(TOP)));
            assertThat("supplier in product " + data.getLmCode() + " is " + data.getSupCode(), data.getSupCode(), equalTo(SUPPLIER_CODE));
        }
    }

    @TestCase(22893348)
    @Test(description = "C22893348 sort by lmCode DESC")
    public void testSortByLmCodeDesc() {

        GetCatalogSearch byLmDescSortParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20);

        Response<ProductData> response = magMobileClient.searchProductsBy(byLmDescSortParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> lmCodes = new ArrayList<>();
        for (ProductItemData data : responseData) {
            lmCodes.add(data.getLmCode());
        }
        lmCodes.sort((x, y) -> Integer.parseInt(y) - Integer.parseInt(x));
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getLmCode() + " not matches with " + lmCodes.get(i), lmCodes.get(i), equalTo(responseData.get(i).getLmCode()));
        }
    }

    @TestCase(22893349)
    @Test(description = "C22893349 sort by lmCode ASC")
    public void testSortByLmCodeAsc() {

        GetCatalogSearch byLmAscSortParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.ASC)
                .setPageSize(20);

        Response<ProductData> response = magMobileClient.searchProductsBy(byLmAscSortParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> lmCodes = new ArrayList<>();
        for (ProductItemData data : responseData) {
            lmCodes.add(data.getLmCode());
        }
        lmCodes.sort(Comparator.comparingInt((x) -> Integer.parseInt(x)));
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getLmCode() + " not matches with " + lmCodes.get(i), lmCodes.get(i), equalTo(responseData.get(i).getLmCode()));
        }
    }

    @TestCase(22893350)
    @Test(description = "C22893350 sort by availableStock DESC")
    public void testSortByAvailableStockDesc() {

        GetCatalogSearch byAvailableStockSortParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.AVAILABLE_STOCK, SortingOrder.DESC)
                .setPageSize(10);

        Response<ProductData> response = magMobileClient.searchProductsBy(byAvailableStockSortParams);
        List<ProductItemData> responseData = response.asJson().getItems();

        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<Float> availableStocks = new ArrayList<>();
        for (ProductItemData data : responseData) {
            availableStocks.add(data.getAvailableStock());
        }
        for (int y = 0; y < availableStocks.size(); y++) {
            for (int i = availableStocks.size() - 1; i > y; i--) {
                if (availableStocks.get(i) > availableStocks.get(y)) {
                    float tmp = availableStocks.get(i);
                    availableStocks.set(i, availableStocks.get(y));
                    availableStocks.set(y, tmp);
                }
            }
        }
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getAvailableStock() + " not matches with " + availableStocks.get(i), availableStocks.get(i), equalTo(responseData.get(i).getAvailableStock()));
        }
    }

    @TestCase(22893351)
    @Test(description = "C22893351 sort by availableStock ASC")
    public void testSortByAvailableStockAsc() {

        GetCatalogSearch byAvailableStockSortParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.AVAILABLE_STOCK, SortingOrder.ASC)
                .setPageSize(20);

        Response<ProductData> response = magMobileClient.searchProductsBy(byAvailableStockSortParams);

        List<ProductItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<Float> availableStocks = new ArrayList<>();
        for (ProductItemData data : responseData) {
            availableStocks.add(data.getAvailableStock());
        }
        for (int y = 0; y < availableStocks.size(); y++) {
            for (int i = availableStocks.size() - 1; i > y; i--) {
                if (availableStocks.get(i) < availableStocks.get(y)) {
                    float tmp = availableStocks.get(i);
                    availableStocks.set(i, availableStocks.get(y));
                    availableStocks.set(y, tmp);
                }
            }
        }
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getAvailableStock() + " not matches with " + availableStocks.get(i), availableStocks.get(i), equalTo(responseData.get(i).getAvailableStock()));
        }
    }

    @TestCase(22893405)
    @Test(description = "C22893405 search by short name")
    public void testSearchServicesByShortName() {
        final String name = "Овер";

        GetCatalogServicesSearch byNameParams = new GetCatalogServicesSearch()
                .setName(name);

        Response<ServiceData> response = magMobileClient.searchServicesBy(byNameParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service title has not contains " + name, data.getTitle(), containsString(name));
        }
    }

    @TestCase(22893406)
    @Test(description = "C22893406 search by full name")
    public void testSearchServicesByFullName() {
        final String name = "Оверлок";

        GetCatalogServicesSearch byNameParams = new GetCatalogServicesSearch()
                .setName(name);

        Response<ServiceData> response = magMobileClient.searchServicesBy(byNameParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service title has not matches " + name, data.getTitle(), equalTo(name));
        }
    }

    @TestCase(22893407)
    @Test(description = "C22893407 search by short lmCode")
    public void testSearchServicesByShortLmCode() {
        final String shortLmCode = "4905510";

        GetCatalogServicesSearch byLmCodeParams = new GetCatalogServicesSearch()
                .setLmCode(shortLmCode);

        Response<ServiceData> response = magMobileClient.searchServicesBy(byLmCodeParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service lmCode has not contains " + shortLmCode, data.getLmCode(), containsString(shortLmCode));
        }
    }

    @TestCase(22893408)
    @Test(description = "C22893408 search by lmCode")
    public void testSearchServicesByFullLmCode() {
        final String shortLmCode = "4905510";

        GetCatalogServicesSearch byLmCodeParams = new GetCatalogServicesSearch()
                .setLmCode(shortLmCode);

        Response<ServiceData> response = magMobileClient.searchServicesBy(byLmCodeParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service lmCode has not matches " + shortLmCode, data.getLmCode(), containsString(shortLmCode));
        }
    }

    @TestCase(22893410)
    @Test(description = "C22893410 search by department")
    public void testSearchServicesByDepartmentId() {
        final String departmentId = "2";

        GetCatalogServicesSearch byDepartmentIdParams = new GetCatalogServicesSearch()
                .setDepartmentId(departmentId);

        Response<ServiceData> response = magMobileClient.searchServicesBy(byDepartmentIdParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service department has not matches " + departmentId, data.getDepartmentId(), equalTo("0"+departmentId));
        }
    }

    @TestCase(22893411)
    @Test(description = "C22893411 search all services")
    public void testSearchAllServices() {
        GetCatalogServicesSearch allServicesParams = new GetCatalogServicesSearch();

        Response<ServiceData> response = magMobileClient.searchServicesBy(allServicesParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        assertThat("Services count is less than expected", responseData.size(), greaterThanOrEqualTo(5));
    }
}
