package com.leroy.magmobile.api.tests.catalog_search;

import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.ServiceItemData;
import com.leroy.magmobile.api.data.catalog.ServiceItemDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierData;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.enums.CatalogSearchFields;
import com.leroy.magmobile.api.enums.SortingOrder;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogServicesSearch;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import io.qameta.allure.Issue;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.base.TestCase;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CatalogSearchTest extends BaseProjectApiTest {

    private void isResponseSuccessfulAndContainsMoreThanOneEntity(Response<?> response, List<?> responseData) {
        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
    }

    private CatalogSearchClient client() {
        return apiClientProvider.getCatalogSearchClient();
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
        Response<ProductItemDataList> response = client().searchProductsBy(byLmCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains not the only 1 object", responseData, hasSize(1));
        for (ProductItemData data : responseData) {
            assertThat("product lmCode " + data.getLmCode() + " has not matches with " + lmCode, data.getLmCode(), equalTo(lmCode));
        }
    }

    @TestCase(22893254)
    @Test(description = "C22893254 search by short lmCode")
    public void testSearchByShortLmCode() {
        final String lmCode = "1234";

        GetCatalogSearch byLmCodeParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setByLmCode(lmCode);
        Response<ProductItemDataList> response = client().searchProductsBy(byLmCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
        for (ProductItemData data : responseData) {
            assertThat(String.format("Product with lmCode=%s and barCode=%s", data.getLmCode(), data.getBarCode()),
                    data.getLmCode().contains(lmCode) || data.getBarCode().contains(lmCode));
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
        Response<ProductItemDataList> response = client().searchProductsBy(byBarCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
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
        Response<ProductItemDataList> response = client().searchProductsBy(byBarCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
        for (ProductItemData data : responseData) {
            assertThat("product barcode " + data.getBarCode() + " has not contains " + barCode, data.getBarCode(), containsString(barCode));
        }
    }

    @TestCase(22893248)
    @Test(description = "C22893248 search by shortName")
    public void testSearchByName() {
        //final String name = "Тепломир радиатор"; TODO почему бэки возвращают другие значения?
        final String name = "12";

        GetCatalogSearch byNameParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setPageSize(20)
                .setByNameLike(name);
        Response<ProductItemDataList> response = client().searchProductsBy(byNameParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));
        String[] searchWords = name.split(" ");
        for (ProductItemData data : responseData) {
            for (String eachWord : searchWords) {
                assertThat("title " + data.getTitle() + " of product " + data.getLmCode() + " has not contains word " + eachWord,
                        data.getTitle(), containsStringIgnoringCase(eachWord));
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
        Response<ProductItemDataList> response = client().searchProductsBy(byLmCodeParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        for (ProductItemData data : responseData)
            assertThat("barCode has not contains " + longLmCode, data.getBarCode(), containsString(longLmCode));
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
        Response<ProductItemDataList> response = client().searchProductsBy(byGammaParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
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
        Response<ProductItemDataList> response = client().searchProductsBy(byGammaParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("gamma in product " + data.getLmCode() + " is " + data.getGamma(), data.getGamma(), oneOf(FIRST_GAMMA, SECOND_GAMMA));
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
        Response<ProductItemDataList> response = client().searchProductsBy(byTopParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
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
        Response<ProductItemDataList> response = client().searchProductsBy(byTopParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("top in product " + data.getLmCode() + " is " + data.getTop(), data.getTop(), oneOf(Integer.valueOf(FIRST_TOP), Integer.valueOf(SECOND_TOP)));
        }
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C22893333 search by hasAvailableStock filter")
    public void testSearchByHasAvailableStockFilter() {
        final boolean hasAvailableStock = true;

        GetCatalogSearch byStockParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setPageSize(20)
                .setHasAvailableStock(hasAvailableStock);
        Response<ProductItemDataList> response = client().searchProductsBy(byStockParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            //стоки на разных бэках отличаются
            assertThat("available stock in product " + data.getLmCode() + " is " + data.getAvailableStock(),
                    data.getAvailableStock(), greaterThan(0f));
        }
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C22893334 search by topEm filter")
    public void testSearchByTopEmFilter() {
        final boolean topEm = true;

        GetCatalogSearch byTopEmParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setTopEM(topEm);
        Response<ProductItemDataList> response = client().searchProductsBy(byTopEmParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
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
        Response<ProductItemDataList> response = client().searchProductsBy(byBestPriceParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
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
        Response<ProductItemDataList> response = client().searchProductsBy(byTop1000Params);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("top1000 in product " + data.getLmCode() + " is " + data.getTop1000(), data.getTop1000(), equalTo(top1000));
        }
    }

    //может работать некорректно, т.к. есть бизнес дефект
    @Issue("BUSINESS_DEFECT")
    @Test(description = "C22893337 search by limitedOffer filter")
    public void testSearchByLimitedOfferFilter() {
        final boolean limitedOffer = true;

        GetCatalogSearch byLimitedOfferParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(10)
                .setLimitedOffer(limitedOffer);
        Response<ProductItemDataList> response = client().searchProductsBy(byLimitedOfferParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("price category in product " + data.getLmCode() + " is " + data.getPriceCategory(),
                    data.getPriceCategory(), equalTo("LOF"));
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
        Response<ProductItemDataList> response = client().searchProductsBy(byCtmParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("ctm in product " + data.getLmCode() + " is " + data.getCtm(), data.getCtm(), equalTo(ctm));
        }
    }

    @Issue("BACKEND_ISSUE")
    @Test(description = "C22893341 search by supplier filter")
    public void testSearchBySupplierFilter() {
        final String SUPPLIER_CODE = "1001123001";

        GetCatalogSearch bySupplierParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setSupId(SUPPLIER_CODE);
        Response<ProductItemDataList> response = client().searchProductsBy(bySupplierParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            //Данные берутся с разных бэков. В одном поставщик для товара есть, в другом - нет
            assertThat("supplier in product " + data.getLmCode() + " is " + data.getSupCode(), data.getSupCode(),
                    equalTo(SUPPLIER_CODE));
        }
    }

    @Issue("BACKEND_ISSUE")
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
        Response<ProductItemDataList> response = client().searchProductsBy(bySupplierParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            //Данные берутся с разных бэков. В одном поставщик для товара есть, в другом - нет
            assertThat("supplier in product " + data.getLmCode() + " is " + data.getSupCode(), data.getSupCode(),
                    oneOf(FIRST_SUPPLIER_CODE, SECOND_SUPPLIER_CODE));
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
        Response<ProductItemDataList> response = client().searchProductsBy(byAvsNeqNullParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
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

        Response<ProductItemDataList> response = client().searchProductsBy(byAvsDateParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("avs in product " + data.getLmCode() + " is " + data.getAvsDate(),
                    data.getAvsDate().toString(), containsString(avsDate.toString()));
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
        Response<ProductItemDataList> response = client().searchProductsBy(fewCategoriesParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        assertThat(response, successful());
        assertThat("response contains 0 objects", responseData.size(), greaterThan(0));

        for (ProductItemData data : responseData) {
            assertThat("ctm in product " + data.getLmCode() + " is " + data.getCtm(), data.getCtm(), equalTo(ctm));
            assertThat("topEM in product " + data.getLmCode() + " is " + data.getTopEM(), data.getTopEM(), equalTo(topEm));
            assertThat("avs in product " + data.getLmCode() + " is " + data.getAvsDate(), data.getAvsDate(), notNullValue());
            assertThat("gamma in product " + data.getLmCode() + " is " + data.getGamma(), data.getGamma(), equalTo(GAMMA));
            assertThat("top in product " + data.getLmCode() + " is " + data.getTop(), data.getTop(), equalTo(Integer.valueOf(TOP)));
            //можем найти по одному из поставщиков, а отдаст по главному // TODO
            //assertThat("supplier in product " + data.getLmCode() + " is " + data.getSupCode(), data.getSupCode(), equalTo(SUPPLIER_CODE));
        }
    }

    @TestCase(22893348)
    @Test(description = "C22893348 sort by lmCode DESC")
    public void testSortByLmCodeDesc() {

        GetCatalogSearch byLmDescSortParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.DESC)
                .setPageSize(20)
                .setDepartmentId(5);

        Response<ProductItemDataList> response = client().searchProductsBy(byLmDescSortParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> lmCodes = responseData.stream()
                .map(ProductItemData::getLmCode)
                .sorted((x, y) -> Integer.parseInt(y) - Integer.parseInt(x))
                .collect(Collectors.toList());
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getLmCode() + " not matches with " + lmCodes.get(i),
                    lmCodes.get(i), equalTo(responseData.get(i).getLmCode()));
        }
    }

    @TestCase(22893349)
    @Test(description = "C22893349 sort by lmCode ASC")
    public void testSortByLmCodeAsc() {

        GetCatalogSearch byLmAscSortParams = new GetCatalogSearch()
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.LM_CODE, SortingOrder.ASC)
                .setPageSize(20);

        Response<ProductItemDataList> response = client().searchProductsBy(byLmAscSortParams);

        List<ProductItemData> responseData = response.asJson().getItems();

        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> lmCodes = responseData.stream().map(ProductItemData::getLmCode)
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());
        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getLmCode() + " not matches with " + lmCodes.get(i),
                    lmCodes.get(i), equalTo(responseData.get(i).getLmCode()));
        }
    }

    @TestCase(22893350)
    @Test(description = "C22893350 sort by availableStock DESC", enabled = false)
    public void testSortByAvailableStockDesc() {

        GetCatalogSearch byAvailableStockSortParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.AVAILABLE_STOCK, SortingOrder.DESC)
                .setPageSize(10);

        Response<ProductItemDataList> response = client().searchProductsBy(byAvailableStockSortParams);
        List<ProductItemData> responseData = response.asJson().getItems();

        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<Float> availableStocks = responseData.stream().map(ProductItemData::getAvailableStock)
                .collect(Collectors.toList());
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
            assertThat("Sorting order is wrong: " + responseData.get(i).getAvailableStock() + " not matches with " + availableStocks.get(i),
                    availableStocks.get(i), equalTo(responseData.get(i).getAvailableStock()));
        }
    }

    @TestCase(22893351)
    @Test(description = "C22893351 sort by availableStock ASC", enabled = false)
    public void testSortByAvailableStockAsc() {

        GetCatalogSearch byAvailableStockSortParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.AVAILABLE_STOCK, SortingOrder.ASC)
                .setPageSize(20);

        Response<ProductItemDataList> response = client().searchProductsBy(byAvailableStockSortParams);

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
            assertThat("Sorting order is wrong: " + responseData.get(i).getAvailableStock() + " not matches with " + availableStocks.get(i),
                    availableStocks.get(i), equalTo(responseData.get(i).getAvailableStock()));
        }
    }

    @TestCase(23183649)
    @Test(description = "C23183649 sort by name DESC")
    public void testSortByNameDesc() {

        GetCatalogSearch byAvailableStockSortParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.NAME, SortingOrder.DESC)
                .setPageSize(20);

        Response<ProductItemDataList> response = client().searchProductsBy(byAvailableStockSortParams);

        List<ProductItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> alphabetOrder = new ArrayList<>();
        for (ProductItemData data : responseData) {
            alphabetOrder.add(data.getTitle());
        }
        alphabetOrder.sort(Comparator.reverseOrder());

        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getTitle() + " not matches with " + alphabetOrder.get(i),
                    alphabetOrder.get(i), equalTo(responseData.get(i).getTitle()));
        }
    }

    @TestCase(23183650)
    @Test(description = "C23183650 sort by name ASC")
    public void testSortByNameAsc() {

        GetCatalogSearch byAvailableStockSortParams = new GetCatalogSearch()
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setDepartmentId(EnvConstants.BASIC_USER_DEPARTMENT_ID)
                .setStartFrom(1)
                .setSortBy(CatalogSearchFields.NAME, SortingOrder.ASC)
                .setPageSize(20);

        Response<ProductItemDataList> response = client().searchProductsBy(byAvailableStockSortParams);

        List<ProductItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);

        List<String> alphabetOrder = new ArrayList<>();
        for (ProductItemData data : responseData) {
            alphabetOrder.add(data.getTitle());
        }
        alphabetOrder.sort(String::compareTo);

        for (int i = 0; i < responseData.size(); i++) {
            assertThat("Sorting order is wrong: " + responseData.get(i).getTitle() + " not matches with " + alphabetOrder.get(i),
                    alphabetOrder.get(i), equalTo(responseData.get(i).getTitle()));
        }
    }

    @TestCase(22893405)
    @Test(description = "C22893405 search by short name")
    public void testSearchServicesByShortName() {
        final String name = "Овер";

        GetCatalogServicesSearch byNameParams = new GetCatalogServicesSearch()
                .setName(name);

        Response<ServiceItemDataList> response = client().searchServicesBy(byNameParams);

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

        Response<ServiceItemDataList> response = client().searchServicesBy(byNameParams);

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

        Response<ServiceItemDataList> response = client().searchServicesBy(byLmCodeParams);

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

        Response<ServiceItemDataList> response = client().searchServicesBy(byLmCodeParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service lmCode has not matches " + shortLmCode, data.getLmCode(),
                    containsString(shortLmCode));
        }
    }

    @TestCase(22893410)
    @Test(description = "C22893410 search by department")
    public void testSearchServicesByDepartmentId() {
        final int departmentId = 2;

        GetCatalogServicesSearch byDepartmentIdParams = new GetCatalogServicesSearch()
                .setDepartmentId(departmentId);

        Response<ServiceItemDataList> response = client().searchServicesBy(byDepartmentIdParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        for (ServiceItemData data : responseData) {
            assertThat("Service department has not matches " + departmentId,
                    data.getDepartmentId(), equalTo(departmentId));
        }
    }

    @TestCase(22893411)
    @Test(description = "C22893411 search all services")
    public void testSearchAllServices() {
        GetCatalogServicesSearch allServicesParams = new GetCatalogServicesSearch();

        Response<ServiceItemDataList> response = client().searchServicesBy(allServicesParams);

        List<ServiceItemData> responseData = response.asJson().getItems();
        isResponseSuccessfulAndContainsMoreThanOneEntity(response, responseData);
        assertThat("Services count is less than expected", responseData.size(), greaterThanOrEqualTo(5));
    }

    @Test(description = "C23195045 search supplier by code")
    public void testSearchSupplierByCode() {
        String query = "123";
        Response<SupplierDataList> response = client().searchSupplierBy(query, 3);
        isResponseOk(response);
        SupplierDataList supplierDataList = response.asJson();
        assertThat("item counts", supplierDataList.getItems(), hasSize(3));
        for (SupplierData supplierData : supplierDataList.getItems()) {
            assertThat("Supplier id", supplierData.getSupplierId(), containsString(query));
            assertThat("Supplier name", supplierData.getName(), not(emptyOrNullString()));
        }
    }

    @Test(description = "C23195044 search supplier by name")
    public void testSearchSupplierByName() {
        String query = "сази";
        Response<SupplierDataList> response = client().searchSupplierBy(query, 30);
        isResponseOk(response);
        SupplierDataList supplierDataList = response.asJson();
        assertThat("item counts", supplierDataList.getItems(), hasSize(greaterThan(0)));
        for (SupplierData supplierData : supplierDataList.getItems()) {
            assertThat("Supplier id", supplierData.getSupplierId(), not(emptyOrNullString()));
            assertThat("Supplier name", supplierData.getName(), containsStringIgnoringCase(query));
        }
    }
}
