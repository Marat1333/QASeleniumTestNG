package com.leroy.magportal.api.clients;

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
}
