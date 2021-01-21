package com.leroy.magmobile.api.clients;

import com.leroy.constants.EnvConstants;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.ServiceItemDataList;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogServicesSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetSupplierSearch;
import com.leroy.magportal.api.data.catalog.products.CatalogProductData;
import com.leroy.magportal.api.data.catalog.products.CatalogSimilarProductsData;
import com.leroy.magportal.api.data.catalog.shops.NearestShopsData;
import com.leroy.magportal.api.requests.product.GetCatalogProduct;
import com.leroy.magportal.api.requests.product.GetCatalogProductSimilars;
import com.leroy.magportal.api.requests.product.GetNearestShops;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogSearchClient extends BaseMashupClient {
    //back-end limit
    protected final static int MAX_PAGE_SIZE = 90;

    private String oldGatewayUrl;

    @Override
    protected void init() {
        gatewayUrl = EnvConstants.SEARCH_API_HOST;
        oldGatewayUrl = EnvConstants.MAIN_API_HOST;
    }

    /**
     * ---------- Requests -------------
     **/

    @Step("Search for products")
    public Response<ProductItemDataList> searchProductsBy(GetCatalogSearch params) {
        params.setLdapHeader(getUserSessionData().getUserLdap());
        return execute(params, ProductItemDataList.class);
    }

    @Step("Search for products")
    public Response<ProductItemDataList> searchProductsBy(CatalogSearchFilter filters, Integer startFrom, Integer pageSize) {
        GetCatalogSearch req = new GetCatalogSearch();
        req.setLdapHeader(getUserSessionData().getUserLdap());
        req.setShopId(getUserSessionData().getUserShopId());
        if (filters.getDepartmentId() != null)
            req.setDepartmentId(filters.getDepartmentId());
        if (filters.getHasAvailableStock() != null)
            req.setHasAvailableStock(filters.getHasAvailableStock());
        if (filters.getTopEM() != null)
            req.setTopEM(filters.getTopEM());
        if (filters.getBestPrice() != null)
            req.setBestPrice(filters.getBestPrice());
        if (filters.getTop1000() != null)
            req.setTop1000(filters.getTop1000());
        if (filters.getLmCode() != null)
            req.setByLmCode(filters.getLmCode());
        if (filters.getAvs() != null && filters.getAvs())
            req.setAvsDate("neq|null");
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
        params.setLdapHeader(getUserSessionData().getUserLdap());
        return execute(params, ServiceItemDataList.class, oldGatewayUrl);
    }

    @Step("Search for suppliers by query={query}, pageSize={pageSize}")
    public Response<SupplierDataList> searchSupplierBy(String query, int pageSize) {
        GetSupplierSearch params = new GetSupplierSearch()
                .setQuery(query)
                .setPageSize(pageSize);
        return execute(params, SupplierDataList.class);
    }

    @Step("Return products list")
    public List<ProductItemData> getProductsList() {
        ProductItemDataList productItemDataList = this.searchProductsBy(new GetCatalogSearch().setPageSize(MAX_PAGE_SIZE)
                .setShopId(getUserSessionData().getUserShopId())).asJson();
        return productItemDataList.getItems();
    }

    @Step("Return random product")
    public ProductItemData getRandomProduct() {
        ProductItemDataList productItemDataList = this.searchProductsBy(new GetCatalogSearch().setPageSize(MAX_PAGE_SIZE)
                .setHasAvailableStock(true).setShopId(getUserSessionData().getUserShopId())).asJson();
        List<ProductItemData> productItemData = productItemDataList.getItems();
        productItemData = productItemData.stream().filter(i -> i.getTitle() != null).collect(Collectors.toList());
        return productItemData.get((int) (Math.random() * productItemData.size()));
    }

    @Step("Return random product")
    public List<ProductItemData> getRandomUniqueProductsWithTitles(int countOfProducts) {
        List<ProductItemData> randomProductsList = new ArrayList<>();
        ProductItemDataList productItemDataList = this.searchProductsBy(new GetCatalogSearch().setPageSize(MAX_PAGE_SIZE)
                .setHasAvailableStock(true).setShopId(getUserSessionData().getUserShopId())).asJson();
        List<ProductItemData> productItemData = productItemDataList.getItems();
        productItemData = productItemData.stream().filter(i -> i.getTitle() != null).collect(Collectors.toList());
        int randomIndex;
        for (int i = 0; i < countOfProducts; i++) {
            randomIndex = (int) (Math.random() * productItemData.size());
            randomProductsList.add(productItemData.get(randomIndex));
            productItemData.remove(randomIndex);
        }
        return randomProductsList;
    }

    @Step("Get similar and complement products")
    public Response<CatalogSimilarProductsData> getSimilarProducts(String lmCode) {
        return execute(new GetCatalogProductSimilars()
                        .setLmCode(lmCode)
                        .setShopId(getUserSessionData().getUserShopId()),
                CatalogSimilarProductsData.class);
    }

    @Step("Get product data")
    public Response<CatalogProductData> getProductData(String lmCode) {
        return execute(new GetCatalogProduct()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId()), CatalogProductData.class);
    }

    @Step("Get stocks and prices in nearest shops")
    public Response<NearestShopsData> getNearestShopsInfo(String lmCode) {
        return execute(new GetNearestShops()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId()), NearestShopsData.class);
    }

}
