package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.ServiceItemDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogServicesSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetSupplierSearch;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

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


}
