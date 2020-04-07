package com.leroy.magmobile.api.clients;

import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.requests.catalog_product.GetCatalogProduct;
import lombok.Builder;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogProductClient extends MagMobileClient {

    @Builder
    public static class Extend {
        private boolean rating;
        private boolean logistic;
        private boolean inventory;

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            if (rating)
                str.append("rating,");
            if (logistic)
                str.append("logistic,");
            if (inventory)
                str.append("inventory,");
            if (str.length() > 0) {
                str.setLength(str.length() - 1);
            }
            return str.toString();
        }
    }

    public Response<CatalogProductData> searchProduct(String lmCode) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(sessionData.getUserShopId());
        return execute(req, CatalogProductData.class);
    }

    public Response<CatalogProductData> searchProduct(
            String lmCode, Extend extend) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(sessionData.getUserShopId());
        req.setExtend(extend.toString());
        return execute(req, CatalogProductData.class);
    }

    public Response<CatalogProductData> searchProduct(
            String lmCode, SalesDocumentsConst.GiveAwayPoints pointOfGiveAway, Extend extend) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(sessionData.getUserShopId());
        req.setPointOfGiveAway(pointOfGiveAway.getApiVal());
        req.setExtend(extend.toString());
        return execute(req, CatalogProductData.class);
    }

}
