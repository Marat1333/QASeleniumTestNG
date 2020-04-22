package com.leroy.magmobile.api.clients;

import com.leroy.constants.EnvConstants;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.ServiceItemData;
import com.leroy.magmobile.api.data.catalog.ServiceItemDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogServicesSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetSupplierSearch;
import com.leroy.magmobile.ui.models.search.FiltersData;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class CatalogSearchClient extends MagMobileClient {

    /**
     * ---------- Requests -------------
     **/

    @Step("Search for products")
    public Response<ProductItemDataList> searchProductsBy(GetCatalogSearch params) {
        params.setLdap(sessionData.getUserLdap());
        return execute(params, ProductItemDataList.class);
    }

    @Step("Search for services")
    public Response<ServiceItemDataList> searchServicesBy(GetCatalogServicesSearch params) {
        params.setLdap(sessionData.getUserLdap());
        return execute(params, ServiceItemDataList.class);
    }

    @Step("Search for suppliers by query={query}, pageSize={pageSize}")
    public Response<SupplierDataList> searchSupplierBy(String query, int pageSize) {
        GetSupplierSearch params = new GetSupplierSearch()
                .setQuery(query)
                .setPageSize(pageSize);
        return execute(params, SupplierDataList.class);
    }

    // Help methods

    @Step("Find {necessaryCount} services")
    public List<ServiceItemData> getServices(int necessaryCount) {
        GetCatalogServicesSearch params = new GetCatalogServicesSearch();
        params.setShopId(sessionData.getUserShopId())
                .setStartFrom(1)
                .setPageSize(necessaryCount); // TODO не работает. Почему?
        Response<ServiceItemDataList> resp = searchServicesBy(params);
        List<ServiceItemData> services =
                resp.asJson().getItems().stream().limit(necessaryCount).collect(Collectors.toList());
        return services;
    }

    @Step("Find {necessaryCount} products")
    public List<ProductItemData> getProducts(int necessaryCount, FiltersData filtersData) {
        if (filtersData == null)
            filtersData = new FiltersData();
        String[] badLmCodes = {"10008698", "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        GetCatalogSearch params = new GetCatalogSearch()
                .setShopId(sessionData.getUserShopId())
                .setDepartmentId(sessionData.getUserDepartmentId())
                .setTopEM(filtersData.isTopEM())
                .setHasAvailableStock(filtersData.isHasAvailableStock());
        Response<ProductItemDataList> resp = searchProductsBy(params);
        assertThat("Catalog search request:", resp, successful());
        List<ProductItemData> items = resp.asJson().getItems();
        List<ProductItemData> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemData item : items) {
            if (!Arrays.asList(badLmCodes).contains(item.getLmCode()))
                if (!filtersData.isAvs() && item.getAvsDate() == null ||
                        filtersData.isAvs() && item.getAvsDate() != null) {
                    resultList.add(item);
                    i++;
                }
            if (necessaryCount == i)
                break;
        }
        assertThat("Catalog search request:", resultList, hasSize(greaterThan(0)));
        return resultList;
    }

    public List<ProductItemData> getProducts(int necessaryCount) {
        return getProducts(necessaryCount, null);
    }

    public List<String> getProductLmCodes(int necessaryCount) {
        List<ProductItemData> productItemResponseList = getProducts(necessaryCount, null);
        return productItemResponseList.stream().map(ProductItemData::getLmCode).collect(Collectors.toList());
    }


}
