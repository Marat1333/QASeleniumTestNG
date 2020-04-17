package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.catalog.product.CatalogSimilarProducts;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProductList;
import com.leroy.magmobile.api.data.catalog.product.reviews.ReviewData;
import com.leroy.magmobile.api.data.catalog.supply.CatalogSupplierData;
import com.leroy.magmobile.api.requests.catalog.*;
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

    public Response<CatalogProductData> getProduct(String lmCode) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(sessionData.getUserShopId());
        return execute(req, CatalogProductData.class);
    }

    public Response<CatalogProductData> getProduct(
            String lmCode, SalesDocumentsConst.GiveAwayPoints pointOfGiveAway, Extend extend) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(sessionData.getUserShopId());
        req.setPointOfGiveAway(pointOfGiveAway.getApiVal());
        req.setExtend(extend.toString());
        return execute(req, CatalogProductData.class);
    }

    public Response<CatalogReviewsOfProductList> getProductReviews(String lmCode, int pageNumber, int pageSize) {
        GetCatalogProductReviews params = new GetCatalogProductReviews()
                .setLmCode(lmCode)
                .setShopId(sessionData.getUserShopId())
                .setPageNumber(pageNumber)
                .setPageSize(pageSize);
        return execute(params, CatalogReviewsOfProductList.class);
    }

    public Response<Object> getNomenclature() {
        GetCatalogNomenclatureRequest req = new GetCatalogNomenclatureRequest();
        return execute(req, Object.class);
    }

    public Response<CatalogSupplierData> getSupplyInfo(String lmCode) {
        GetCatalogSupplier params = new GetCatalogSupplier()
                .setLmCode(lmCode)
                .setShopId(sessionData.getUserShopId());
        return execute(params, CatalogSupplierData.class);
    }

    public Response<Object> getProductSales(String lmCode) {
        GetCatalogProductSales params = new GetCatalogProductSales()
                .setLmCode(lmCode)
                .setShopId(sessionData.getUserShopId());
        return execute(params, Object.class);
    }

    public Response<Object> getProductShopsPriceAndQuantity(String lmCode, String... shops) {
        GetCatalogShops params = new GetCatalogShops()
                .setLmCode(lmCode);
        String shopsAsString = String.join(",", shops);
        params.setShopId(shopsAsString);
        return execute(params, Object.class);
    }

    public Response<CatalogSimilarProducts> getSimilarProducts(String lmCode, Extend extend) {
        GetCatalogSimilarProductsReq params = new GetCatalogSimilarProductsReq()
                .setLmCode(lmCode)
                .setShopId(sessionData.getUserShopId())
                .setExtend(extend.toString());
        return execute(params, CatalogSimilarProducts.class);
    }

    public Response<JsonNode> sendReview(ReviewData data) {
        PostCatalogProductReviewCreate params = new PostCatalogProductReviewCreate()
                .jsonBody(data);
        return execute(params, JsonNode.class);
    }

}
