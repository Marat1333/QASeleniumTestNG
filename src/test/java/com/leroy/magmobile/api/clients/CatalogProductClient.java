package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.catalog.product.ProductCardData;
import com.leroy.magmobile.api.requests.catalog_product.GetCatalogProduct;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogProductClient extends MagMobileClient {
    public Response<ProductCardData> searchProductByLmCode(GetCatalogProduct params) {
        return execute(params, ProductCardData.class);
    }
}
