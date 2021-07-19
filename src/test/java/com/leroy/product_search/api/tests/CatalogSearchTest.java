package com.leroy.product_search.api.tests;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.oneOf;

import com.google.inject.Inject;
import com.leroy.common_mashups.catalogs.clients.CatalogProductClient;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.catalogs.data.ProductDataList;
import com.leroy.common_mashups.catalogs.data.ServiceItemData;
import com.leroy.common_mashups.catalogs.data.ServiceItemDataList;
import com.leroy.common_mashups.catalogs.requests.GetCatalogProductSearchRequest;
import com.leroy.common_mashups.catalogs.requests.GetCatalogServicesRequest;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierData;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.enums.CatalogSearchFields;
import com.leroy.magmobile.api.enums.SortingOrder;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import io.qameta.allure.Issue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.TestCase;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogSearchTest extends BaseProjectApiTest {

    private void isResponseSuccessfulAndContainsMoreThanOneEntity(Response<?> response, List<?> responseData) {
        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
    }

    @Inject
    private CatalogProductClient catalogSearchClient;

    @TestCase(3161100)
    @AllureId("3109")
    @Test(description = "C3161100 search by lmCode", groups = "productSearch")
    public void testSearchByLmCode() {
        final String lmCode = "18546124";

        GetCatalogProductSearchRequest byLmCodeParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByLmCode(lmCode);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byLmCodeParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains not the only 1 object", responseData, hasSize(1));
        for (ProductData data : responseData) {
            assertThat("product lmCode " + data.getLmCode() + " has not matches with " + lmCode, data.getLmCode(), equalTo(lmCode));
        }
    }

    @TestCase(22893254)
    @AllureId("3110")
    @Test(description = "C22893254 search by short lmCode", groups = "productSearch")
    public void testSearchByShortLmCode() {
        final String lmCode = "1234";

        GetCatalogProductSearchRequest byLmCodeParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setByLmCode(lmCode);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byLmCodeParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
        for (ProductData data : responseData) {
            assertThat(String.format("Product with lmCode=%s and barCode=%s", data.getLmCode(), data.getBarCode()),
                    data.getLmCode().contains(lmCode) || data.getBarCode().contains(lmCode));
        }
    }

    @TestCase(22893255)
    @AllureId("3111")
    @Test(description = "C22893255 search by barCode", groups = "productSearch")
    public void testSearchByBarCode() {
        final String barCode = "4605865275387";

        GetCatalogProductSearchRequest byBarCodeParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByBarCode(barCode);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byBarCodeParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains not the only 1 object", responseData, hasSize(1));
        for (ProductData data : responseData) {
            assertThat("product barcode " + data.getBarCode() + " has not matches with " + barCode, data.getBarCode(), equalTo(barCode));
        }
    }

    @TestCase(22893256)
    @AllureId("3112")
    @Test(description = "C22893256 search by short barCode", groups = "productSearch")
    public void testSearchByShortBarCode() {
        final String barCode = "460586527";

        GetCatalogProductSearchRequest byBarCodeParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setByBarCode(barCode);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byBarCodeParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
        for (ProductData data : responseData) {
            assertThat("product barcode " + data.getBarCode() + " has not contains " + barCode, data.getBarCode(), containsString(barCode));
        }
    }

    @TestCase(22893248)
    @AllureId("3108")
    @Test(description = "C22893248 search by shortName", groups = "productSearch")
    public void testSearchByName() {
        //final String name = "Тепломир радиатор"; TODO почему бэки возвращают другие значения?
        final String name = "12";

        GetCatalogProductSearchRequest byNameParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setPageSize(20)
                .setByNameLike(name);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byNameParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
        String[] searchWords = name.split(" ");
        for (ProductData data : responseData) {
            for (String eachWord : searchWords) {
                assertThat("title " + data.getTitle() + " of product " + data.getLmCode() + " has not contains word " + eachWord,
                        data.getTitle(), containsStringIgnoringCase(eachWord));
            }
        }
    }

    @TestCase(22893258)
    @AllureId("3113")
    @Test(description = "C22893258 search by long (length>8) lmCode", groups = "productSearch")
    public void testSearchByLongLmCode() {
        //часть штрихкода, по которому можно найти товар
        final String longLmCode = "464001538";

        GetCatalogProductSearchRequest byLmCodeParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setByLmCode(longLmCode);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byLmCodeParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        for (ProductData data : responseData)
            assertThat("barCode has not contains " + longLmCode, data.getBarCode(), containsString(longLmCode));
    }

    @TestCase(22893329)
    @AllureId("3114")
    @Test(description = "C22893329 search by gamma filter", groups = "productSearch")
    public void testSearchByGammaFilter() {
        final String GAMMA = "S";

        GetCatalogProductSearchRequest byGammaParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setGamma(GAMMA);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byGammaParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("gamma in product " + data.getLmCode() + " is " + data.getGamma(), data.getGamma(), equalTo(GAMMA));
        }
    }

    @TestCase(22893330)
    @AllureId("3115")
    @Test(description = "C22893330 search by multiply gamma filter", groups = "productSearch")
    public void testSearchByMultiplyGammaFilter() {
        final String FIRST_GAMMA = "Bc";
        final String SECOND_GAMMA = "ET";

        GetCatalogProductSearchRequest byGammaParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setGamma(FIRST_GAMMA + "," + SECOND_GAMMA);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byGammaParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("gamma in product " + data.getLmCode() + " is " + data.getGamma(), data.getGamma(), oneOf(FIRST_GAMMA, SECOND_GAMMA));
        }
    }

    @TestCase(22893331)
    @AllureId("3116")
    @Test(description = "C22893331 search by top filter", groups = "productSearch")
    public void testSearchByTopFilter() {
        final String TOP = "1";

        GetCatalogProductSearchRequest byTopParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setTop(TOP);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byTopParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("top in product " + data.getLmCode() + " is " + data.getTop(),
                    data.getTop(), equalTo(TOP));
        }
    }

    @TestCase(22893332)
    @AllureId("3117")
    @Test(description = "C22893332 search by multiply top filter", groups = "productSearch")
    public void testSearchByMultiplyTopFilter() {
        final String FIRST_TOP = "1";
        final String SECOND_TOP = "2";

        GetCatalogProductSearchRequest byTopParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setTop(FIRST_TOP + "," + SECOND_TOP);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byTopParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("top in product " + data.getLmCode() + " is " + data.getTop(),
                    data.getTop(), oneOf(FIRST_TOP, SECOND_TOP));
        }
    }

    @Test(description = "C22893333 search by hasAvailableStock filter", groups = "productSearch")
    @AllureId("3118")
    public void testSearchByHasAvailableStockFilter() {
        final boolean hasAvailableStock = true;

        GetCatalogProductSearchRequest byStockParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setPageSize(20)
                .setHasAvailableStock(hasAvailableStock);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byStockParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            //стоки на разных бэках отличаются
            assertThat("available stock in product " + data.getLmCode() + " is " + data.getAvailableStock(),
                    data.getAvailableStock(), greaterThan(0.0));
        }
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C22893334 search by topEm filter", groups = "productSearch")
    @AllureId("3119")
    public void testSearchByTopEmFilter() {
        final boolean topEm = true;

        GetCatalogProductSearchRequest byTopEmParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setTopEM(topEm);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byTopEmParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("topEm in product " + data.getLmCode() + " is " + data.getTopEM(), data.getTopEM(), equalTo(topEm));
        }
    }

    //может работать некорректно, т.к. есть бизнес дефект
    @TestCase(22893335)
    @AllureId("3138")
    @Test(description = "C22893335 search by bestPrice filter", groups = "productSearch")
    public void testSearchByBestPriceFilter() {
        final boolean bestPrice = true;

        GetCatalogProductSearchRequest byBestPriceParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setBestPrice(bestPrice);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byBestPriceParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("price category in product " + data.getLmCode() + " is " + data.getPriceCategory(), data.getPriceCategory(), equalTo("BPR"));
        }
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C22893336 search by top1000 filter", groups = "productSearch")
    @AllureId("3120")
    public void testSearchByTop1000Filter() {
        final boolean top1000 = true;

        GetCatalogProductSearchRequest byTop1000Params = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setTop1000(top1000);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byTop1000Params);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("top1000 in product " + data.getLmCode() + " is " + data.getTop1000(), data.getTop1000(), equalTo(top1000));
        }
    }

    //может работать некорректно, т.к. есть бизнес дефект
    @Issue("BUSINESS_DEFECT")
    @Test(description = "C22893337 search by limitedOffer filter", groups = "productSearch")
    @AllureId("3139")
    public void testSearchByLimitedOfferFilter() {
        final boolean limitedOffer = true;

        GetCatalogProductSearchRequest byLimitedOfferParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setLimitedOffer(limitedOffer);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byLimitedOfferParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("price category in product " + data.getLmCode() + " is " + data.getPriceCategory(),
                    data.getPriceCategory(), equalTo("LOF"));
        }
    }

    @TestCase(22893338)
    @AllureId("3121")
    @Test(description = "C22893338 search by ctm filter", groups = "productSearch")
    public void testSearchByCtmFilter() {
        final boolean ctm = true;

        GetCatalogProductSearchRequest byCtmParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setCtm(ctm);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byCtmParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("ctm in product " + data.getLmCode() + " is " + data.getCtm(), data.getCtm(), equalTo(ctm));
        }
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C22893341 search by supplier filter", groups = "productSearch")
    @AllureId("3122")
    public void testSearchBySupplierFilter() {
        final String SUPPLIER_CODE = "1001123001";

        GetCatalogProductSearchRequest bySupplierParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setSupId(SUPPLIER_CODE);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(bySupplierParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            //Данные берутся с разных бэков. В одном поставщик для товара есть, в другом - нет
            assertThat("supplier in product " + data.getLmCode() + " is " + data.getSupCode(), data.getSupCode(),
                    equalTo(SUPPLIER_CODE));
        }
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C22893342 search by multiply supplier filter", groups = "productSearch")
    @AllureId("3123")
    public void testSearchByMultiplySupplierFilter() {
        final String FIRST_SUPPLIER_CODE = "1002978015";
        final String SECOND_SUPPLIER_CODE = "1003509015";

        GetCatalogProductSearchRequest bySupplierParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(30)
                .setSupId(FIRST_SUPPLIER_CODE + "," + SECOND_SUPPLIER_CODE);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(bySupplierParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            //Данные берутся с разных бэков. В одном поставщик для товара есть, в другом - нет
            assertThat("supplier in product " + data.getLmCode() + " is " + data.getSupCode(), data.getSupCode(),
                    oneOf(FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE));
        }
    }

    @Test(description = "C22893343 search by avs neq null filter", groups = "productSearch")
    @AllureId("3124")
    public void testSearchByAvsNeqNullFilter() {

        GetCatalogProductSearchRequest byAvsNeqNullParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setAvsDate("neq|null");
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byAvsNeqNullParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("avs in product " + data.getLmCode() + " is " + data.getAvsDate(), data.getAvsDate(), notNullValue());
        }
    }

    @TestCase(22893344)
    @AllureId("3125")
    @Test(description = "C22893344 search by avs date filter", groups = "productSearch")
    public void testSearchByAvsDateFilter() {
        LocalDate avsDate = LocalDate.of(2019, 3, 19);

        GetCatalogProductSearchRequest byAvsDateParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setAvsDate(String.format("between%%7C%s-0%s-%sT00:00:00.000Z%%7C%s-0%s-%sT00:00:00.000Z",
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth(),
                        avsDate.getYear(), avsDate.getMonthValue(), avsDate.getDayOfMonth() + 1));

        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byAvsDateParams);

        List<ProductData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("avs in product " + data.getLmCode() + " is " + data.getAvsDate(),
                    data.getAvsDate(), containsString(avsDate.toString()));
        }
    }

    @TestCase(22893345)
    @AllureId("3126")
    @Test(description = "C22893345 search by few filters", groups = "productSearch")
    public void testSearchByFewFilters() {
        final boolean ctm = true;
        final boolean topEm = true;
        final String GAMMA = "B";
        final String TOP = "0";
        final String avs = "neq|null";
        final String SUPPLIER_CODE = "1002978015";

        GetCatalogProductSearchRequest fewCategoriesParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setTop(TOP)
                .setGamma(GAMMA)
                .setAvsDate(avs)//TODO fail due to: "neq|null"
                .setCtm(ctm)
                .setTopEM(topEm);
        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(fewCategoriesParams);
        assertThat(response, successful());

        List<ProductData> responseData = response.asJson().getItems();
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductData data : responseData) {
            assertThat("ctm in product " + data.getLmCode() + " is " + data.getCtm(), data.getCtm(), equalTo(ctm));
            assertThat("topEM in product " + data.getLmCode() + " is " + data.getTopEM(), data.getTopEM(), equalTo(topEm));
            assertThat("avs in product " + data.getLmCode() + " is " + data.getAvsDate(), data.getAvsDate(), notNullValue());
            assertThat("gamma in product " + data.getLmCode() + " is " + data.getGamma(), data.getGamma(), equalTo(GAMMA));
            assertThat("top in product " + data.getLmCode() + " is " + data.getTop(), data.getTop(), equalTo(TOP));
            //можем найти по одному из поставщиков, а отдаст по главному // TODO
            //assertThat("supplier in product " + data.getLmCode() + " is " + data.getSupCode(), data.getSupCode(), equalTo(SUPPLIER_CODE));
        }
    }

    @TestCase(22893348)
    @AllureId("3127")
    @Test(description = "C22893348 sort by lmCode DESC", groups = "productSearch")
    public void testSortByLmCodeDesc() {

        GetCatalogProductSearchRequest byLmDescSortParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setDepartmentId(5);

        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byLmDescSortParams);

        List<ProductData> responseData = response.asJson().getItems();

        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> lmCodes = responseData.stream()
                .map(ProductData::getLmCode)
                .sorted((x, y) -> Integer.parseInt(y) - Integer.parseInt(x))
                .collect(Collectors.toList());
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getLmCode() + " not matches with " + lmCodes.get(i),
                    responseData.get(i).getLmCode(), equalTo(lmCodes.get(i)));
        }
    }

    @TestCase(22893349)
    @AllureId("3128")
    @Test(description = "C22893349 sort by lmCode ASC", groups = "productSearch")
    public void testSortByLmCodeAsc() {

        GetCatalogProductSearchRequest byLmAscSortParams = new GetCatalogProductSearchRequest()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.ASC)
                .setPageSize(20);

        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byLmAscSortParams);

        List<ProductData> responseData = response.asJson().getItems();

        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> lmCodes = responseData.stream().map(ProductData::getLmCode)
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getLmCode() + " not matches with " + lmCodes.get(i),
                    lmCodes.get(i), equalTo(responseData.get(i).getLmCode()));
        }
    }

    @TestCase(22893350)
    @AllureId("3131")
    @Test(description = "C22893350 sort by availableStock DESC", groups = "productSearch", enabled = false)
    public void testSortByAvailableStockDesc() {

        GetCatalogProductSearchRequest byAvailableStockSortParams = new GetCatalogProductSearchRequest()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.AVAILABLE_STOCK, SortingOrder.DESC)
                .setPageSize(10);

        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byAvailableStockSortParams);
        List<ProductData> responseData = response.asJson().getItems();

        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<Double> availableStocks = responseData.stream().map(ProductData::getAvailableStock)
                .collect(Collectors.toList());
        for (int y = 0; y < availableStocks.size(); y++) {
            for (int i = availableStocks.size() - 1; i > y; i--) {
                if (availableStocks.get(i) > availableStocks.get(y)) {
                    double tmp = availableStocks.get(i);
                    availableStocks.set(i, availableStocks.get(y));
                    availableStocks.set(y, tmp);
                }
            }
        }
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getAvailableStock() + " not matches with " + availableStocks.get(i),
                    availableStocks.get(i), equalTo(responseData.get(i).getAvailableStock()));
        }
    }

    @TestCase(22893351)
    @AllureId("3132")
    @Test(description = "C22893351 sort by availableStock ASC", groups = "productSearch", enabled = false)
    public void testSortByAvailableStockAsc() {

        GetCatalogProductSearchRequest byAvailableStockSortParams = new GetCatalogProductSearchRequest()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.AVAILABLE_STOCK, SortingOrder.ASC)
                .setPageSize(20);

        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byAvailableStockSortParams);

        List<ProductData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<Double> availableStocks = new ArrayList<>();
        for (ProductData data : responseData) {
            availableStocks.add(data.getAvailableStock());
        }
        for (int y = 0; y < availableStocks.size(); y++) {
            for (int i = availableStocks.size() - 1; i > y; i--) {
                if (availableStocks.get(i) < availableStocks.get(y)) {
                    double tmp = availableStocks.get(i);
                    availableStocks.set(i, availableStocks.get(y));
                    availableStocks.set(y, tmp);
                }
            }
        }
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getAvailableStock() + " not matches with " + availableStocks.get(i),
                    availableStocks.get(i), equalTo(responseData.get(i).getAvailableStock()));
        }
    }

    @TestCase(23183649)
    @AllureId("3129")
    @Test(description = "C23183649 sort by name DESC", groups = "productSearch")
    public void testSortByNameDesc() {

        GetCatalogProductSearchRequest byAvailableStockSortParams = new GetCatalogProductSearchRequest()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.NAME, SortingOrder.DESC)
                .setPageSize(20);

        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byAvailableStockSortParams);

        List<ProductData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> alphabetOrder = new ArrayList<>();
        for (ProductData data : responseData) {
            alphabetOrder.add(data.getTitle());
        }
        alphabetOrder.sort(Comparator.reverseOrder());

        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getTitle() + " not matches with " + alphabetOrder.get(i),
                    alphabetOrder.get(i), equalTo(responseData.get(i).getTitle()));
        }
    }

    @TestCase(23183650)
    @AllureId("3130")
    @Test(description = "C23183650 sort by name ASC", groups = "productSearch")
    public void testSortByNameAsc() {

        GetCatalogProductSearchRequest byAvailableStockSortParams = new GetCatalogProductSearchRequest()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.NAME, SortingOrder.ASC)
                .setPageSize(20);

        Response<ProductDataList> response = catalogSearchClient.searchProductsBy(byAvailableStockSortParams);

        List<ProductData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> alphabetOrder = new ArrayList<>();
        for (ProductData data : responseData) {
            alphabetOrder.add(data.getTitle());
        }
        alphabetOrder.sort(String::compareTo);

        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getTitle() + " not matches with " + alphabetOrder.get(i),
                    alphabetOrder.get(i), equalTo(responseData.get(i).getTitle()));
        }
    }

    @TestCase(22893405)
    @AllureId("3166")
    @Test(description = "C22893405 search by short name", groups = "productSearch")
    public void testSearchServicesByShortName() {
        final String name = "Овер";

        GetCatalogServicesRequest byNameParams = new GetCatalogServicesRequest()
                .setName(name);

        Response<ServiceItemDataList> response = catalogSearchClient.searchServicesBy(byNameParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service title has not contains " + name, data.getTitle(), containsString(name));
        }
    }

    @TestCase(22893406)
    @AllureId("3167")
    @Test(description = "C22893406 search by full name", groups = "productSearch")
    public void testSearchServicesByFullName() {
        final String name = "Оверлок";

        GetCatalogServicesRequest byNameParams = new GetCatalogServicesRequest()
                .setName(name);

        Response<ServiceItemDataList> response = catalogSearchClient.searchServicesBy(byNameParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service title has not matches " + name, data.getTitle(), equalTo(name));
        }
    }

    @TestCase(22893407)
    @AllureId("3168")
    @Test(description = "C22893407 search by short lmCode", groups = "productSearch")
    public void testSearchServicesByShortLmCode() {
        final String shortLmCode = "4905510";

        GetCatalogServicesRequest byLmCodeParams = new GetCatalogServicesRequest()
                .setLmCode(shortLmCode);

        Response<ServiceItemDataList> response = catalogSearchClient.searchServicesBy(byLmCodeParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service lmCode has not contains " + shortLmCode, data.getLmCode(), containsString(shortLmCode));
        }
    }

    @TestCase(22893408)
    @AllureId("3169")
    @Test(description = "C22893408 search by lmCode", groups = "productSearch")
    public void testSearchServicesByFullLmCode() {
        final String shortLmCode = "4905510";

        GetCatalogServicesRequest byLmCodeParams = new GetCatalogServicesRequest()
                .setLmCode(shortLmCode);

        Response<ServiceItemDataList> response = catalogSearchClient.searchServicesBy(byLmCodeParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service lmCode has not matches " + shortLmCode, data.getLmCode(),
                    containsString(shortLmCode));
        }
    }

    @TestCase(22893410)
    @AllureId("3170")
    @Test(description = "C22893410 search by department", groups = "productSearch")
    public void testSearchServicesByDepartmentId() {
        final int departmentId = 2;

        GetCatalogServicesRequest byDepartmentIdParams = new GetCatalogServicesRequest()
                .setDepartmentId(departmentId);

        Response<ServiceItemDataList> response = catalogSearchClient.searchServicesBy(byDepartmentIdParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service department has not matches " + departmentId,
                    data.getDepartmentId(), equalTo(departmentId));
        }
    }

    @TestCase(22893411)
    @AllureId("3171")
    @Test(description = "C22893411 search all services", groups = "productSearch")
    public void testSearchAllServices() {
        GetCatalogServicesRequest allServicesParams = new GetCatalogServicesRequest();

        Response<ServiceItemDataList> response = catalogSearchClient.searchServicesBy(allServicesParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        assertThat("Services count is less than expected", responseData.size(), greaterThanOrEqualTo(5));
    }

    @Test(description = "C23195045 search supplier by code", groups = "productSearch")
    @AllureId("3143")
    public void testSearchSupplierByCode() {
        String query = "123";
        Response<SupplierDataList> response = catalogSearchClient.searchSupplierBy(query, 3);
        isResponseOk(response);
        SupplierDataList supplierDataList = response.asJson();
        assertThat("item counts", supplierDataList.getItems(), hasSize(3));
        for (SupplierData supplierData : supplierDataList.getItems()) {
            assertThat("Supplier id", supplierData.getSupplierId(), containsString(query));
            assertThat("Supplier name", supplierData.getName(), not(emptyOrNullString()));
        }
    }

    @Test(description = "C23195044 search supplier by name", groups = "productSearch")
    @AllureId("3142")
    public void testSearchSupplierByName() {
        String query = "сази";
        Response<SupplierDataList> response = catalogSearchClient.searchSupplierBy(query, 30);
        isResponseOk(response);
        SupplierDataList supplierDataList = response.asJson();
        assertThat("item counts", supplierDataList.getItems(), hasSize(greaterThan(0)));
        for (SupplierData supplierData : supplierDataList.getItems()) {
            assertThat("Supplier id", supplierData.getSupplierId(), not(emptyOrNullString()));
            assertThat("Supplier name", supplierData.getName(), containsStringIgnoringCase(query));
        }
    }
}
