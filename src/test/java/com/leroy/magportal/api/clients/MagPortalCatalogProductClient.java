package com.leroy.magportal.api.clients;

import com.leroy.magmobile.api.clients.MagMobileClient;
import com.leroy.magportal.api.data.catalog.products.CatalogProductData;
import com.leroy.magportal.api.data.catalog.products.CatalogSimilarProductsData;
import com.leroy.magportal.api.data.catalog.shops.NearestShopsData;
import com.leroy.magportal.api.requests.GetCatalogProduct;
import com.leroy.magportal.api.requests.GetCatalogProductSimilars;
import com.leroy.magportal.api.requests.GetNearestShops;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class MagPortalCatalogProductClient extends MagMobileClient {

    @Step("Get similar and complement products")
    public Response<CatalogSimilarProductsData> getSimilarProducts(String lmCode) {
        return execute(new GetCatalogProductSimilars()
                        .setLmCode(lmCode)
                        .setShopId(userSessionData.getUserShopId()),
                CatalogSimilarProductsData.class);
    }

    @Step("Get product data")
    public Response<CatalogProductData> getProductData(String lmCode) {
        return execute(new GetCatalogProduct()
                .setLmCode(lmCode)
                .setShopId(userSessionData.getUserShopId()), CatalogProductData.class);
    }

    @Step("Get stocks and prices in nearest shops")
    public Response<NearestShopsData> getNearestShopsInfo(String lmCode) {
        return execute(new GetNearestShops()
                .setLmCode(lmCode)
                .setShopId(userSessionData.getUserShopId()), NearestShopsData.class);
    }
}