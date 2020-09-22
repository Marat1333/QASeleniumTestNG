package com.leroy.magmobile.api.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.data.catalog.ProductItemData;
import com.leroy.magmobile.api.data.catalog.product.CatalogComplementaryProducts;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.api.data.catalog.product.CatalogSimilarProducts;
import com.leroy.magmobile.api.data.catalog.product.reviews.CatalogReviewsOfProductList;
import com.leroy.magmobile.api.data.catalog.product.reviews.ReviewData;
import com.leroy.magmobile.api.data.catalog.supply.CatalogSupplierData;
import com.leroy.magmobile.api.requests.catalog.*;
import io.qameta.allure.Step;
import lombok.Builder;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

public class CatalogProductClient extends BaseMashupClient {

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

    @Step("Get Catalog Product for lmCode={lmCode}")
    public Response<CatalogProductData> getProduct(String lmCode) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(getUserSessionData().getUserShopId());
        return execute(req, CatalogProductData.class);
    }

    @Step("Get Catalog Product for lmCode={lmCode}, pointOfGiveAway={pointOfGiveAway}, extend={extend}")
    public Response<CatalogProductData> getProduct(
            String lmCode, SalesDocumentsConst.GiveAwayPoints pointOfGiveAway, Extend extend) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(getUserSessionData().getUserShopId());
        req.setPointOfGiveAway(pointOfGiveAway.getApiVal());
        req.setExtend(extend.toString());
        return execute(req, CatalogProductData.class);
    }

    @Step("Get Catalog Product for lmCode={lmCode}, pointOfGiveAway={pointOfGiveAway}, extend={extend}")
    public Response<CatalogProductData> getProduct(String shopId,
                                                   String lmCode, SalesDocumentsConst.GiveAwayPoints pointOfGiveAway, Extend extend) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(shopId);
        req.setPointOfGiveAway(pointOfGiveAway.getApiVal());
        req.setExtend(extend.toString());
        return execute(req, CatalogProductData.class);
    }

    @Step("Get Product Reviews for lmCode={lmCode}, pageNumber={pageNumber}, pageSize={pageSize}")
    public Response<CatalogReviewsOfProductList> getProductReviews(String lmCode, int pageNumber, int pageSize) {
        GetCatalogProductReviews params = new GetCatalogProductReviews()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId())
                .setPageNumber(pageNumber)
                .setPageSize(pageSize);
        return execute(params, CatalogReviewsOfProductList.class);
    }

    @Step("Get nomenclature")
    public Response<Object> getNomenclature() {
        GetCatalogNomenclatureRequest req = new GetCatalogNomenclatureRequest();
        return execute(req, Object.class);
    }

    @Step("Get Supply Info for lmCode={lmCode}")
    public Response<CatalogSupplierData> getSupplyInfo(String lmCode) {
        GetCatalogSupplier params = new GetCatalogSupplier()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId());
        return execute(params, CatalogSupplierData.class);
    }

    @Step("Get Product Sales for lmCode={lmCode}")
    public Response<Object> getProductSales(String lmCode, String shopId) {
        GetCatalogProductSales params = new GetCatalogProductSales()
                .setLmCode(lmCode)
                .setShopId(shopId);
        return execute(params, Object.class);
    }

    @Step("Get Product Sales for lmCode={lmCode}")
    public Response<Object> getProductSales(String lmCode) {
        return getProductSales(lmCode, getUserSessionData().getUserShopId());
    }

    @Step("Get price and quantity of products by shops for lmCode={lmCode} and shops: {shops}")
    public Response<Object> getProductShopsPriceAndQuantity(String lmCode, String... shops) {
        GetCatalogShops params = new GetCatalogShops()
                .setLmCode(lmCode);
        String shopsAsString = String.join(",", shops);
        params.setShopId(shopsAsString);
        return execute(params, Object.class);
    }

    @Step("Get Similar products for lmCode={lmCode}, extend={extend}")
    public Response<CatalogSimilarProducts> getSimilarProducts(String lmCode, Extend extend) {
        GetCatalogSimilarProductsReq params = new GetCatalogSimilarProductsReq()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId())
                .setExtend(extend.toString());
        return execute(params, CatalogSimilarProducts.class);
    }

    @Step("Post review")
    public Response<JsonNode> sendReview(ReviewData data) {
        PostCatalogProductReviewCreate params = new PostCatalogProductReviewCreate()
                .jsonBody(data);
        return execute(params, JsonNode.class);
    }

    @Step("Get complementary products")
    public Response<CatalogComplementaryProducts> getComplementaryProducts(String lmCode, String shopId) {
        GetComplementaryProducts params = new GetComplementaryProducts()
                .setShopId(shopId)
                .setLmCode(lmCode)
                .setExtend(Extend.builder().inventory(true).rating(true).logistic(true).build());
        return execute(params, CatalogComplementaryProducts.class);
    }

    @Step("Get product with at least one complementary product")
    public CatalogComplementaryProducts getNotEmptyComplementaryProductData(List<ProductItemData> productList) {
        String userShopId = getUserSessionData().getUserShopId();
        String lmCode;
        for (ProductItemData eachData : productList) {
            lmCode = eachData.getLmCode();
            CatalogComplementaryProducts data = getComplementaryProducts(lmCode, userShopId).asJson();
            List<CatalogProductData> itemsList = data.getItems();
            if (itemsList.size() > 0) {
                data.setParentLmCode(lmCode);
                return data;
            }
        }
        return null;
    }

    @Step("Get product without complementary product")
    public CatalogComplementaryProducts getEmptyComplementaryProductData(List<ProductItemData> productList) {
        String userShopId = getUserSessionData().getUserShopId();
        String lmCode;
        for (ProductItemData eachData : productList) {
            lmCode = eachData.getLmCode();
            CatalogComplementaryProducts data = getComplementaryProducts(lmCode, userShopId).asJson();
            List<CatalogProductData> itemsList = data.getItems();
            if (itemsList.size() == 0) {
                data.setParentLmCode(lmCode);
                return data;
            }
        }
        return null;
    }

}
