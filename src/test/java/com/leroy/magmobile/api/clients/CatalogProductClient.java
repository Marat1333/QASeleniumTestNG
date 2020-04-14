package com.leroy.magmobile.api.clients;

import com.leroy.constants.EnvConstants;
import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.product.*;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProduct;
import com.leroy.magmobile.api.data.catalog.product.reviews.ReviewData;
import com.leroy.magmobile.api.data.catalog.product.reviews.ReviewDataResponse;
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

    public Response<CatalogProductData> searchProduct(String lmCode) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(sessionData.getUserShopId());
        return execute(req, CatalogProductData.class);
    }

    public Response<CatalogProductData> searchProduct(
            String lmCode, SalesDocumentsConst.GiveAwayPoints pointOfGiveAway, Extend extend) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(EnvConstants.BASIC_USER_SHOP_ID);
        req.setPointOfGiveAway(pointOfGiveAway.getApiVal());
        req.setExtend(extend.toString());
        return execute(req, CatalogProductData.class);
    }

    public Response<CatalogReviewsOfProduct> getProductReviews(String lmCode, int startFrom, int page) {
        GetCatalogProductReviews params = new GetCatalogProductReviews()
                .setLmCode(lmCode)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setStartFrom(startFrom)
                .setPageNumber(page)
                .setPageSize(10);
        return execute(params, CatalogReviewsOfProduct.class);
    }

    public Response<Nomenclature> getNomenclature(GetCatalogNomenclature params) {
        return execute(params, Nomenclature.class);
    }

    public Response<CatalogSupplierData> getSupplyInfo(String lmCode, String shopId) {
        GetCatalogSupplier params = new GetCatalogSupplier()
                .setLmCode(lmCode)
                .setShopId(shopId);
        return execute(params, CatalogSupplierData.class);
    }

    public Response<SalesHistory> getProductSales(String lmCode) {
        GetCatalogProductSales params = new GetCatalogProductSales()
                .setLmCode(lmCode)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID);
        return execute(params, SalesHistory.class);
    }

    public Response<CatalogShops> getProductShopsPriceAndQuantity(String lmCode, String... shops) {
        GetCatalogShops params = new GetCatalogShops()
                .setLmCode(lmCode);

        String shopsAsString = String.join(",", shops);
        params.setShopId(shopsAsString);
        return execute(params, CatalogShops.class);
    }

    public Response<CatalogSimilarProducts> getSimilarProducts(String lmCode, Extend extend) {
        GetCatalogSimilarProducts params = new GetCatalogSimilarProducts()
                .setLmCode(lmCode)
                .setShopId(EnvConstants.BASIC_USER_SHOP_ID)
                .setExtend(extend.toString());
        return execute(params, CatalogSimilarProducts.class);
    }

    public Response<ReviewDataResponse> sendReview(ReviewData data) {
        PostCatalogProductReviewCreate params = new PostCatalogProductReviewCreate()
                .jsonBody(data);
        return execute(params, ReviewDataResponse.class);
    }

}
