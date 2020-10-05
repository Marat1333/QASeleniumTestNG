package com.leroy.magportal.api.clients;

import com.leroy.magmobile.api.data.catalog.CatalogSearchFilter;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.requests.catalog_search.GetCatalogSearch;
import io.qameta.allure.Step;
import java.util.List;

public class CatalogSearchClient extends com.leroy.magmobile.api.clients.CatalogSearchClient {

    @Step("Return list of products for specified ShopId")
    public List<ProductItemData> getProductsForShop(int countOfProducts,
            String shopId) {
        return this
                .searchProductsBy(new GetCatalogSearch().setPageSize(countOfProducts)
                        .setHasAvailableStock(true).setShopId(shopId)).asJson().getItems();
    }

    @Step("Return list of products for specified ShopId")
    public ProductItemData getProductByLmCode(String lmCode) {
        CatalogSearchFilter filter = new CatalogSearchFilter();
        filter.setLmCode(lmCode);
        return this.searchProductsBy(filter).asJson().getItems().stream().findFirst().get();
    }

    public List<ProductItemData> getProducts(int necessaryCount) {
        return this
                .searchProductsBy(new GetCatalogSearch().setPageSize(necessaryCount)
                        .setHasAvailableStock(true).setShopId(getUserSessionData().getUserShopId()))
                        .asJson().getItems();
    }
}
