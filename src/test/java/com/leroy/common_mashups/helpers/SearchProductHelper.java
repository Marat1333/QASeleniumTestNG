package com.leroy.common_mashups.helpers;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.CatalogSearchClient;
import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.ProductItemDataList;
import com.leroy.magmobile.api.data.catalog.ServiceItemData;
import com.leroy.magmobile.api.data.catalog.ServiceItemDataList;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogServicesSearch;
import com.leroy.magportal.api.helpers.BaseHelper;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SearchProductHelper extends BaseHelper {

    @Inject
    private CatalogSearchClient catalogSearchClient;

    @Step("Find {necessaryCount} services")
    public List<ServiceItemData> getServices(int necessaryCount) {
        GetCatalogServicesSearch params = new GetCatalogServicesSearch();
        params.setShopId(userSessionData().getUserShopId())
                .setStartFrom(1)
                .setPageSize(necessaryCount); // TODO не работает. Почему?
        Response<ServiceItemDataList> resp = catalogSearchClient.searchServicesBy(params);
        List<ServiceItemData> services =
                resp.asJson().getItems().stream().limit(necessaryCount)
                        .collect(Collectors.toList());
        return services;
    }

    @Step("Find {necessaryCount} products")
    public List<ProductItemData> getProducts(int necessaryCount, CatalogSearchFilter filtersData) {
        String[] badLmCodes = {"10008698",
                "10008751"}; // Из-за отсутствия синхронизации бэков на тесте, мы можем получить некорректные данные
        if (filtersData == null) {
            filtersData = new CatalogSearchFilter();
        }
        Response<ProductItemDataList> resp = catalogSearchClient.searchProductsBy(filtersData);
        assertThat("Catalog search request:", resp, successful());
        List<ProductItemData> items = resp.asJson().getItems();
        List<ProductItemData> resultList = new ArrayList<>();
        int i = 0;
        for (ProductItemData item : items) {
            if (!Arrays.asList(badLmCodes).contains(item.getLmCode())) {
                if (filtersData.getAvs() == null
                        || !filtersData.getAvs() && item.getAvsDate() == null
                        || filtersData.getAvs() && item.getAvsDate() != null) {
                    if (filtersData.getHasAvailableStock() == null ||
                            (filtersData.getHasAvailableStock() && item.getAvailableStock() > 0 ||
                                    !filtersData.getHasAvailableStock()
                                            && item.getAvailableStock() <= 0)) {
                        resultList.add(item);
                        i++;
                    }
                }
            }
            if (necessaryCount == i) {
                break;
            }
        }
        assertThat("Catalog search request:", resultList, hasSize(greaterThan(0)));
        return resultList;
    }

    public List<ProductItemData> getProducts(int necessaryCount) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setHasAvailableStock(true);
        return getProducts(necessaryCount, filter);
    }

    public List<ProductItemData> getProducts(int necessaryCount, boolean isAvs, boolean isTopEm) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setHasAvailableStock(true);
        filter.setTopEM(isTopEm);
        filter.setAvs(isAvs);
        return getProducts(necessaryCount, filter);
    }

    public List<String> getProductLmCodes(int necessaryCount) {
        List<ProductItemData> productItemResponseList = getProducts(necessaryCount, null);
        return productItemResponseList.stream().map(ProductItemData::getLmCode)
                .collect(Collectors.toList());
    }

    public List<String> getProductLmCodes(int necessaryCount, boolean isAvs, boolean isTopEm) {
        List<ProductItemData> productItemResponseList = getProducts(necessaryCount, isAvs, isTopEm);
        return productItemResponseList.stream().map(ProductItemData::getLmCode)
                .collect(Collectors.toList());
    }

    @Step("Return list of products for specified ShopId")
    public List<ProductItemData> getProductsForShop(int countOfProducts,
            String shopId) {
        return catalogSearchClient
                .searchProductsBy(new GetCatalogSearch().setPageSize(countOfProducts)
                        .setHasAvailableStock(true).setShopId(shopId)).asJson().getItems();
    }

    @Step("Return list of products for specified ShopId")
    public ProductItemData getProductByLmCode(String lmCode) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setLmCode(lmCode);
        return catalogSearchClient.searchProductsBy(filter).asJson().getItems().stream().findFirst()
                .orElse(null);
    }

}
