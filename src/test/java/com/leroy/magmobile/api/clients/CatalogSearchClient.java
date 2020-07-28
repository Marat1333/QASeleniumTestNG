package com.leroy.magmobile.api.clients;

import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.ServiceItemDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogServicesSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetSupplierSearch;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogSearchClient extends MagMobileClient {
    //back-end limit
    final static int MAX_PAGE_SIZE = 90;

    /**
     * ---------- Requests -------------
     **/

    @Step("Search for products")
    public Response<ProductItemDataList> searchProductsBy(GetCatalogSearch params) {
        params.setLdap(userSessionData.getUserLdap());
        return execute(params, ProductItemDataList.class);
    }

    @Step("Search for products")
    public Response<ProductItemDataList> searchProductsBy(CatalogSearchFilter filters, Integer startFrom, Integer pageSize) {
        GetCatalogSearch req = new GetCatalogSearch();
        req.setLdap(userSessionData.getUserLdap());
        if (filters.getHasAvailableStock() != null)
            req.setHasAvailableStock(filters.getHasAvailableStock());
        if (filters.getTopEM() != null)
            req.setTopEM(filters.getTopEM());
        if (filters.getBestPrice() != null)
            req.setBestPrice(filters.getBestPrice());
        if (filters.getTop1000() != null)
            req.setTop1000(filters.getTop1000());
        if (startFrom != null)
            req.setStartFrom(startFrom);
        if (pageSize != null)
            req.setPageSize(pageSize);
        return execute(req, ProductItemDataList.class);
    }

    public Response<ProductItemDataList> searchProductsBy(CatalogSearchFilter filters) {
        return searchProductsBy(filters, null, null);
    }

    @Step("Search for services")
    public Response<ServiceItemDataList> searchServicesBy(GetCatalogServicesSearch params) {
        params.setLdap(userSessionData.getUserLdap());
        return execute(params, ServiceItemDataList.class);
    }

    @Step("Search for suppliers by query={query}, pageSize={pageSize}")
    public Response<SupplierDataList> searchSupplierBy(String query, int pageSize) {
        GetSupplierSearch params = new GetSupplierSearch()
                .setQuery(query)
                .setPageSize(pageSize);
        return execute(params, SupplierDataList.class);
    }

    @Step("Return random product")
    public ProductItemData getRandomProduct() {
        ProductItemDataList productItemDataList = this.searchProductsBy(new GetCatalogSearch().setPageSize(24)).asJson();
        List<ProductItemData> productItemData = productItemDataList.getItems();
        return productItemData.get((int) (Math.random() * productItemData.size()));
    }

    @Step("Return random product")
    public List<ProductItemData> getRandomUniqueProductsWithTitles(int countOfProducts) {
        List<ProductItemData> randomProductsList = new ArrayList<>();
        ProductItemDataList productItemDataList = this.searchProductsBy(new GetCatalogSearch().setPageSize(MAX_PAGE_SIZE)).asJson();
        List<ProductItemData> productItemData = productItemDataList.getItems();
        productItemData = productItemData.stream().filter(i->i.getTitle()!=null).collect(Collectors.toList());
        int randomIndex;
        for (int i = 0; i < countOfProducts; i++) {
            randomIndex = (int) (Math.random() * productItemData.size());
            randomProductsList.add(productItemData.get(randomIndex));
            productItemData.remove(randomIndex);
        }
        return randomProductsList;
    }

}
