package com.leroy.magmobile.api.clients;

import com.leroy.constants.SalesDocumentsConst;
import com.leroy.magmobile.api.data.catalog.product.*;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProduct;
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
        req.setShopId(sessionData.getUserShopId());
        req.setPointOfGiveAway(pointOfGiveAway.getApiVal());
        req.setExtend(extend.toString());
        return execute(req, CatalogProductData.class);
    }

    public Response<CatalogReviewsOfProduct> getProductReviews(GetCatalogProductReviews params) {
        return execute(params, CatalogReviewsOfProduct.class);
    }

    public Response<Nomenclature> getNomenclature(GetCatalogNomenclature params){
        return execute(params, Nomenclature.class);
    }

    public Response<CatalogSupplierData> getSupplyInfo(GetCatalogSupplier params){
        return execute(params, CatalogSupplierData.class);
    }

    public Response<GetCatalogProductSales> getProductSales(GetCatalogProductSales params){
        return execute(params, GetCatalogProductSales.class);
    }

    public Response<CatalogShops> getProductShopsPriceAndQuantity(GetCatalogShops params){
        return execute(params, CatalogShops.class);
    }

    public Response<CatalogSimilarProducts> getSimilarProducts(GetCatalogSimilarProducts params){
        return execute(params, CatalogSimilarProducts.class);
    }

    public Response<ReviewDataResponse>sendReview(PostCatalogProductReviewCreate params){
        return execute(params, ReviewDataResponse.class);
    }

}
