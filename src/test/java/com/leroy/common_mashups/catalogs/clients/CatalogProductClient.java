package com.leroy.common_mashups.catalogs.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.common_mashups.catalogs.data.CatalogComplementaryProductsDataV2;
import com.leroy.common_mashups.catalogs.data.CatalogSearchFilter;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV1;
import com.leroy.common_mashups.catalogs.data.CatalogSimilarProductsDataV2;
import com.leroy.common_mashups.catalogs.data.NearestShopsData;
import com.leroy.common_mashups.catalogs.data.NearestShopsDataV2;
import com.leroy.common_mashups.catalogs.data.ProductDataList;
import com.leroy.common_mashups.catalogs.data.ServiceItemDataList;
import com.leroy.common_mashups.catalogs.data.product.CatalogProductData;
import com.leroy.common_mashups.catalogs.data.product.ProductData;
import com.leroy.common_mashups.catalogs.data.product.reviews.CatalogReviewsOfProductList;
import com.leroy.common_mashups.catalogs.data.product.reviews.ReviewData;
import com.leroy.common_mashups.catalogs.data.supply.CatalogSupplierDataOld;
import com.leroy.common_mashups.catalogs.requests.GetCatalogProductReviewsRequest;
import com.leroy.common_mashups.catalogs.requests.GetCatalogProductSalesRequest;
import com.leroy.common_mashups.catalogs.requests.GetCatalogProductSearchRequest;
import com.leroy.common_mashups.catalogs.requests.GetCatalogProductV2Request;
import com.leroy.common_mashups.catalogs.requests.GetCatalogServicesRequest;
import com.leroy.common_mashups.catalogs.requests.GetCatalogSimilarProductsV1Request;
import com.leroy.common_mashups.catalogs.requests.GetCatalogSimilarProductsV2Request;
import com.leroy.common_mashups.catalogs.requests.GetCatalogSupplierSearchRequest;
import com.leroy.common_mashups.catalogs.requests.GetComplementaryProductsRequest;
import com.leroy.common_mashups.catalogs.requests.GetNearestShopsRequest;
import com.leroy.common_mashups.catalogs.requests.GetNomenclatureRequest;
import com.leroy.common_mashups.catalogs.requests.PostCatalogProductReviewCreate;
import com.leroy.common_mashups.catalogs.requests.obsolete.GetCatalogProduct;
import com.leroy.common_mashups.catalogs.requests.obsolete.GetCatalogShops;
import com.leroy.common_mashups.catalogs.requests.obsolete.GetCatalogSupplierRequest;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.sales.SalesDocumentsConst.GiveAwayPoints;
import com.leroy.core.api.BaseMashupClient;
import com.leroy.magmobile.api.data.supply_plan.suppliers.SupplierDataList;
import io.qameta.allure.Step;
import lombok.Builder;
import ru.leroymerlin.qa.core.clients.base.Response;

public class CatalogProductClient extends BaseMashupClient {

    private String oldGatewayUrl;

    @Override
    protected void init() {
        gatewayUrl = EnvConstants.SEARCH_API_HOST;
        oldGatewayUrl = EnvConstants.MAIN_API_HOST;
        jaegerHost = EnvConstants.PRODUCTSEARCH_JAEGER_HOST;
        jaegerService = EnvConstants.PRODUCTSEARCH_JAEGER_SERVICE;
    }

    @Builder
    public static class Extend {

        private boolean rating;
        private boolean logistic;
        private boolean inventory;

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            if (rating) {
                str.append("rating,");
            }
            if (logistic) {
                str.append("logistic,");
            }
            if (inventory) {
                str.append("inventory,");
            }
            if (str.length() > 0) {
                str.setLength(str.length() - 1);
            }
            return str.toString();
        }
    }

    @Step("Get Catalog Product V2 for lmCode={lmCode} with users shopId")
    public Response<CatalogProductData> getProductV2(String lmCode) {
        return getProductV2(lmCode, getUserSessionData().getUserShopId());
    }

    @Step("Get Catalog Product V2 for lmCode={lmCode} and shopId={shopId}")
    public Response<CatalogProductData> getProductV2(String lmCode, String shopId) {
        GetCatalogProductV2Request req = new GetCatalogProductV2Request();
        req.setLmCode(lmCode);
        req.setShopId(shopId);
        return execute(req, CatalogProductData.class);
    }

    @Step("Get Catalog Product V2 with Extend and pointOfGiveAway")
    public Response<CatalogProductData> getProductV2(String lmCode, GiveAwayPoints pointOfGiveAway,
            Extend extend) {
        GetCatalogProductV2Request req = new GetCatalogProductV2Request();
        req.setLmCode(lmCode);
        req.setShopId(getUserSessionData().getUserShopId());
        req.setPointOfGiveAway(pointOfGiveAway.getApiVal());
        req.setExtend(extend.toString());
        return execute(req, CatalogProductData.class);
    }

    @Step("Get Catalog Product for lmCode={lmCode}")
    public Response<ProductData> getProduct(String lmCode) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(getUserSessionData().getUserShopId());
        return execute(req, ProductData.class, oldGatewayUrl);
    }

    @Step("Get Catalog Product for lmCode={lmCode}, pointOfGiveAway={pointOfGiveAway}, extend={extend}")
    public Response<ProductData> getProduct(
            String lmCode, GiveAwayPoints pointOfGiveAway, Extend extend) {
        return getProduct(getUserSessionData().getUserShopId(), lmCode, pointOfGiveAway, extend);
    }

    @Step("Get Catalog Product for lmCode={lmCode}, pointOfGiveAway={pointOfGiveAway}, extend={extend}")
    public Response<ProductData> getProduct(String shopId,
            String lmCode, GiveAwayPoints pointOfGiveAway, Extend extend) {
        GetCatalogProduct req = new GetCatalogProduct();
        req.setLmCode(lmCode);
        req.setShopId(shopId);
        req.setPointOfGiveAway(pointOfGiveAway.getApiVal());
        req.setExtend(extend.toString());
        return execute(req, ProductData.class, oldGatewayUrl);
    }

    @Step("Get Product Reviews for lmCode={lmCode}, pageNumber={pageNumber}, pageSize={pageSize}")
    public Response<CatalogReviewsOfProductList> getProductReviews(String lmCode, int pageNumber,
            int pageSize) {
        GetCatalogProductReviewsRequest params = new GetCatalogProductReviewsRequest()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId())
                .setPageNumber(pageNumber)
                .setPageSize(pageSize);
        return execute(params, CatalogReviewsOfProductList.class);
    }

    @Step("Get nomenclature")
    public Response<Object> getNomenclature() {
        GetNomenclatureRequest req = new GetNomenclatureRequest();
        return execute(req, Object.class);
    }

    @Step("Get Supply Info for lmCode={lmCode}")
    public Response<CatalogSupplierDataOld> getSupplyInfo(String lmCode) {
        GetCatalogSupplierRequest params = new GetCatalogSupplierRequest()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId());
        return execute(params, CatalogSupplierDataOld.class, oldGatewayUrl);
    }

    @Step("Get Product Sales for lmCode={lmCode}")
    public Response<Object> getProductSales(String lmCode, String shopId) {
        GetCatalogProductSalesRequest params = new GetCatalogProductSalesRequest()
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
        return execute(params, Object.class, oldGatewayUrl);
    }

    @Step("Get Similar products for lmCode={lmCode}, shopId={shopId}")
    public Response<CatalogSimilarProductsDataV1> getSimilarProductsV1(String lmCode,
            String shopId) {
        GetCatalogSimilarProductsV1Request params = new GetCatalogSimilarProductsV1Request()
                .setLmCode(lmCode)
                .setShopId(shopId);
        return execute(params, CatalogSimilarProductsDataV1.class);
    }

    @Step("Get Similar products for lmCode={lmCode}, extend={extend}")
    public Response<CatalogSimilarProductsDataV1> getSimilarProductsV1(String lmCode,
            Extend extend) {
        GetCatalogSimilarProductsV1Request params = new GetCatalogSimilarProductsV1Request()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId())
                .setExtend(extend.toString());
        return execute(params, CatalogSimilarProductsDataV1.class);
    }

    @Step("Get Similar products for lmCode={lmCode}")
    public Response<CatalogSimilarProductsDataV2> getSimilarProductsV2(String lmCode,
            Extend extend) {
        GetCatalogSimilarProductsV2Request params = new GetCatalogSimilarProductsV2Request()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId())
                .setExtend(extend.toString());
        return execute(params, CatalogSimilarProductsDataV2.class);
    }

    @Step("Get Similar products for lmCode={lmCode} and user's shopId")
    public Response<CatalogSimilarProductsDataV2> getSimilarProductsV2(String lmCode) {
        GetCatalogSimilarProductsV2Request params = new GetCatalogSimilarProductsV2Request()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId());
        return execute(params, CatalogSimilarProductsDataV2.class);
    }

    @Step("Post review")
    public Response<JsonNode> sendReview(ReviewData data) {
        PostCatalogProductReviewCreate params = new PostCatalogProductReviewCreate();
        params.setLmCode(data.getLmCode());
        params.jsonBody(data);
        return execute(params, JsonNode.class);
    }

    @Step("Get complementary products")
    public Response<CatalogComplementaryProductsDataV2> getComplementaryProducts(String lmCode) {
        GetComplementaryProductsRequest params = new GetComplementaryProductsRequest()
                .setShopId(getUserSessionData().getUserShopId())
                .setLmCode(lmCode)
                .setExtend(Extend.builder().inventory(true).rating(true).logistic(true).build());
        return execute(params, CatalogComplementaryProductsDataV2.class);
    }

    @Step("Search for products")
    public Response<ProductDataList> searchProductsBy(GetCatalogProductSearchRequest params) {
        params.setLdapHeader(getUserSessionData().getUserLdap());
        return execute(params, ProductDataList.class);
    }

    @Step("Search for products")
    public Response<ProductDataList> searchProductsBy(CatalogSearchFilter filters,
            Integer startFrom, Integer pageSize) {
        GetCatalogProductSearchRequest req = new GetCatalogProductSearchRequest();
        req.setLdapHeader(getUserSessionData().getUserLdap());
        req.setShopId(getUserSessionData().getUserShopId());
        if (filters.getDepartmentId() != null) {
            req.setDepartmentId(filters.getDepartmentId());
        }
        if (filters.getHasAvailableStock() != null) {
            req.setHasAvailableStock(filters.getHasAvailableStock());
        }
        if (filters.getTopEM() != null) {
            req.setTopEM(filters.getTopEM());
        }
        if (filters.getBestPrice() != null) {
            req.setBestPrice(filters.getBestPrice());
        }
        if (filters.getTop1000() != null) {
            req.setTop1000(filters.getTop1000());
        }
        if (filters.getLmCode() != null) {
            req.setByLmCode(filters.getLmCode());
        }
        if (filters.getAvs() != null && filters.getAvs()) {
            req.setAvsDate("neq|null");
        }
        if (startFrom != null) {
            req.setStartFrom(startFrom);
        }
        if (pageSize != null) {
            req.setPageSize(pageSize);
        }
        return execute(req, ProductDataList.class);
    }

    @Step("Search for products")
    public Response<ProductDataList> searchProductsBy(CatalogSearchFilter filters) {
        return searchProductsBy(filters, null, null);
    }

    @Step("Search for services")
    public Response<ServiceItemDataList> searchServicesBy(GetCatalogServicesRequest params) {
        params.setLdapHeader(getUserSessionData().getUserLdap());
        return execute(params, ServiceItemDataList.class);
    }

    @Step("Search for suppliers by query={query}, pageSize={pageSize}")
    public Response<SupplierDataList> searchSupplierBy(String query, int pageSize) {
        GetCatalogSupplierSearchRequest params = new GetCatalogSupplierSearchRequest()
                .setQuery(query)
                .setPageSize(pageSize);
        return execute(params, SupplierDataList.class, oldGatewayUrl);
    }

    @Step("Get similar and complement products")
    public Response<CatalogSimilarProductsDataV1> getSimilarProductsV1(String lmCode) {
        return execute(new GetCatalogSimilarProductsV1Request()
                        .setLmCode(lmCode)
                        .setShopId(getUserSessionData().getUserShopId()),
                CatalogSimilarProductsDataV1.class);
    }

    @Step("Get product data")
    public Response<CatalogProductData> getProductData(String lmCode) {
        GetCatalogProductV2Request req = new GetCatalogProductV2Request()
                .setLmCode(lmCode)
                .setShopId(getUserSessionData().getUserShopId());
        return execute(req, CatalogProductData.class);
    }

    @Step("Get stocks and prices in nearest shops for users shopId")
    public Response<NearestShopsData> getNearestShopsInfo(String lmCode) {
        return getNearestShopsInfo(lmCode, getUserSessionData().getUserShopId());
    }

    @Step("Get stocks and prices in nearest shops")
    public Response<NearestShopsData> getNearestShopsInfo(String lmCode, String shopId) {
        GetNearestShopsRequest req = new GetNearestShopsRequest()
                .setVersion("v1")
                .setLmCode(lmCode)
                .setShopId(shopId);
        return execute(req, NearestShopsData.class);
    }

    @Step("Get stocks and prices in nearest shops for users shopId V2")
    public Response<NearestShopsDataV2> getNearestShopsInfoV2(String lmCode) {
        return getNearestShopsInfoV2(lmCode, getUserSessionData().getUserShopId());
    }

    @Step("Get stocks and prices in nearest shops V2")
    public Response<NearestShopsDataV2> getNearestShopsInfoV2(String lmCode, String shopId) {
        GetNearestShopsRequest req = new GetNearestShopsRequest()
                .setVersion("v2")
                .setLmCode(lmCode)
                .setShopId(shopId);
        return execute(req, NearestShopsDataV2.class);
    }
}
