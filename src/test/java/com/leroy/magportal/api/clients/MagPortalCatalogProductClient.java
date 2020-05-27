package com.leroy.magportal.api.clients;

import com.leroy.magmobile.api.clients.MagMobileClient;
import com.leroy.magportal.api.data.CatalogSimilarProductsData;
import com.leroy.magportal.api.requests.GetCatalogProductSimilars;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class MagPortalCatalogProductClient extends MagMobileClient {

    @Step("Get similar and complement products")
    public Response<CatalogSimilarProductsData> getSimilarProducts(String lmCode) {
        return execute(new GetCatalogProductSimilars().setLmCode(lmCode).setShopId(
                userSessionData.getUserShopId()),
                CatalogSimilarProductsData.class);
    }
}
